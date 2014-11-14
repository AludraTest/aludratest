/*
 * Copyright (C) 2010-2014 Hamburg Sud and the contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.aludratest.scheduler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.aludratest.exception.TechnicalException;
import org.aludratest.impl.log4testing.data.TestCaseLog;
import org.aludratest.impl.log4testing.data.TestLogger;
import org.aludratest.impl.log4testing.data.TestSuiteLog;
import org.aludratest.scheduler.node.ExecutionMode;
import org.aludratest.scheduler.node.RunnerGroup;
import org.aludratest.scheduler.node.RunnerLeaf;
import org.aludratest.scheduler.node.RunnerNode;
import org.aludratest.scheduler.sort.FallbackComparator;
import org.aludratest.scheduler.sort.NoComparator;
import org.aludratest.scheduler.util.PoolThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Assembles nodes in a tree structure and provides the feature of executing
 * each node's sub nodes either concurrently or sequentially. Each leaf node
 * is executed by a thread of the {@link #parallelExecutorService}, so its
 * size limits the number of concurrent leaf executions.</p>
 * <p>Leaf nodes are formed by objects which implement the {@link Runnable}
 * interface.</p>
 * @see RunnerNode
 * @author Volker Bergmann
 */
public class RunnerTree {

    /** The {@link Logger} of the class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(RunnerTree.class);

    private WrapperFactory wrapperFactory;

    /** The comparator to use for ordering the tree components
     *  (see {@link HieraConfig#sorters()}) */
    private Comparator<RunnerNode> comparator;

    /** The root of the runner tree structure. */
    private RunnerGroup root;

    /** The {@link ExecutorService} to use for concurrent test execution. */
    private ExecutorService parallelExecutorService;

    /** List of the currently pending concurrent tests. */
    // Note: SonarQube recommends List, but I need LinkedList in the implementation
    private volatile LinkedList<Future<Void>> parallelTasks; //NOSONAR

    /** The {@link ExecutorService} to use for sequential test execution. */
    private ExecutorService sequentialExecutorService;

    /** List of the currently pending sequential tests. */
    // Note: SonarQube recommends List, but I need LinkedList in the implementation
    private volatile LinkedList<Future<Void>> sequentialTasks; //NOSONAR

    // constructor -------------------------------------------------------------

    /**
     * Constructor.
     * @param comparator the {@link Comparator} to use for ordering test runners
     */
    public RunnerTree(Comparator<RunnerNode> comparator) {
        this.comparator = (comparator != null ? comparator : new NoComparator());
        this.wrapperFactory = null;
    }

    // interface ---------------------------------------------------------------

    /**
     * Sets the {@link #wrapperFactory} attribute.
     * @param wrapperFactory the new {@link WrapperFactory} to use
     */
    public void setWrapperFactory(WrapperFactory wrapperFactory) {
        this.wrapperFactory = wrapperFactory;
    }

    /**
     * @return the {@link #root} attribute
     */
    public RunnerGroup getRoot() {
        return root;
    }

    /** Creates the tree root.
     *  @param rootName the name of the root node
     *  @param parallel default {@link ExecutionMode} for the root node
     *  @return A {@link RunnerGroup} that has been set as new {@link #root}. */
    public RunnerGroup createRoot(String rootName, boolean parallel) {
        if (root == null) {
            createGroup(rootName, (parallel ? ExecutionMode.PARALLEL : ExecutionMode.SEQUENTIAL), null);
        } else {
            throw new IllegalStateException("Root has already been defined");
        }
        return root;
    }

    /**
     * Creates a {@link RunnerGroup} and adds it as new child to a parent.
     * @param groupName the name of the group to create
     * @param mode the {@link ExecutionMode} of the new group
     * @param parentGroup the parent group
     * @return a {@link RunnerGroup} representing a tree node.
     * 		Missing parent nodes are created on demand.
     */
    public RunnerGroup createGroup(String groupName, ExecutionMode mode, RunnerGroup parentGroup) {
        RunnerGroup group;
        TestSuiteLog logSuite = TestLogger.getTestSuite(groupName);
        group = new RunnerGroup(groupName, mode, parentGroup, noComparator(comparator) ? null : comparator, logSuite);
        if (parentGroup != null) {
            parentGroup.addChild(group);
        } else {
            this.root = group;
        }
        return group;
    }

    /**
     * Adds a {@link java.lang.Runnable} object as leaf to the tree structure,
     * creating parent nodes on demand.
     * @param runnable the object to execute
     * @param nodeName the name of the node to add
     * @param parentGroup the parent group to which to add the new node
     * @return The {@link TestCaseLog} of the new leaf
     */
    public TestCaseLog addLeaf(Runnable runnable, String nodeName, RunnerGroup parentGroup) {
        LOGGER.debug("Adding leaf {} to group {}", new Object[] { runnable, parentGroup });
        if (parentGroup == null) {
            throw new IllegalArgumentException("parentGroup is null");
        }
        TestCaseLog logCase = TestLogger.getTestCase(nodeName);
        RunnerLeaf leaf = new RunnerLeaf(nodeName, parentGroup, new TestCaseRunnable(runnable, logCase), logCase);
        parentGroup.addChild(leaf);
        return logCase;
    }

    /**
     * Traverses the tree hierarchically and executes all nodes according
     * to the configured 'parallel' settings
     * @param parallelThreadPoolSize
     */
    public void performAllTestsAndWait(int parallelThreadPoolSize) {
        if (this.root != null) {
            this.parallelExecutorService = Executors.newFixedThreadPool(parallelThreadPoolSize, new PoolThreadFactory("ParEx"));
            this.parallelTasks = new LinkedList<Future<Void>>();
            this.sequentialExecutorService = Executors.newCachedThreadPool(new PoolThreadFactory("SerEx"));
            this.sequentialTasks = new LinkedList<Future<Void>>();
            if (LOGGER.isDebugEnabled()) {
                debugSubTree(root, "");
            }
            runGroup(root);
            waitForCompletion();
        } else {
            LOGGER.info("No tests to run");
        }
    }

    // private helper methods --------------------------------------------------

    private boolean noComparator(Comparator<RunnerNode> comparator) {
        if (comparator instanceof NoComparator) {
            return true;
        }
        if (!(comparator instanceof FallbackComparator)) {
            return false;
        }
        FallbackComparator fbc = (FallbackComparator) comparator;
        List<Comparator<RunnerNode>> steps = fbc.getSteps();
        return (steps.size() == 1 && steps.get(0) instanceof NoComparator);
    }

    /**
     * Prints the sub tree of a node to a {@link Logger} in DEBUG level.
     * Applied to the root node, it logs the full tree hierarchy
     * @param node
     * @param indent
     */
    public void debugSubTree(RunnerNode node, String indent) {
        LOGGER.debug(indent + node);
        if (node instanceof RunnerGroup) {
            String subIndent = indent + "  ";
            RunnerGroup group = (RunnerGroup) node;
            for (RunnerNode child : group.getChildren()) {
                debugSubTree(child, subIndent);
            }
        }
    }

    /**
     * Waits until all queued tasks have completed.
     */
    private void waitForCompletion() {
        try {
            LOGGER.info("Waiting for test completion.");
            while (!sequentialTasks.isEmpty()) {
                waitForCompletionAndRemoveFromQueue(sequentialTasks.getLast(), sequentialTasks);
            }
            while (!parallelTasks.isEmpty()) {
                waitForCompletionAndRemoveFromQueue(parallelTasks.getFirst(), parallelTasks);
            }
            LOGGER.info("All tests finished");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (!parallelTasks.isEmpty() || !sequentialTasks.isEmpty()) {
                LOGGER.info("Cancelling tasks");
            }
            cancelAll(parallelTasks);
            cancelAll(sequentialTasks);
            shutDown(parallelExecutorService);
            shutDown(sequentialExecutorService);
        }
    }

    /** Shuts down the given {@link ExecutorService} immediately. */
    private void shutDown(ExecutorService executor) {
        LOGGER.debug("Shutting down executor: {}", executor);
        executor.shutdownNow();
    }

    /** Cancels all Tasks contained in the given list. */
    private void cancelAll(LinkedList<Future<Void>> queue) {
        if (!queue.isEmpty()) {
            LOGGER.info("Tasks need to be cancelled: {}", queue);
        }
        while (!queue.isEmpty()) {
            synchronized (queue) {
                if (!queue.isEmpty()) {
                    Future<Void> task = queue.poll();
                    LOGGER.info("Cancelling task: {}", task);
                    task.cancel(true);
                }
            }
        }
    }

    /** Waits for the completion of the given task
     *  and removes it from the queue. */
    private void waitForCompletionAndRemoveFromQueue(Future<Void> task, LinkedList<Future<Void>> queue) throws InterruptedException {
        LOGGER.debug("Waiting for completion of task: {}", task);
        try {
            task.get();
        } catch (ExecutionException e) {
            LOGGER.error("Error executing sequential task", e);
        }
        synchronized (queue) {
            LOGGER.debug("Task completed, removing it from queue: {}", task);
            queue.remove(task);
        }
    }

    /**
     * Executes any group node with all child nodes.
     */
    private List<Future<Void>> runGroup(RunnerGroup group) {
        if (group.isParallel()) {
            return runParallelGroup(group);
        } else {
            return runSequentialGroup(group);
        }
    }

    /**
     * Executes a sequential group node.
     */
    private List<Future<Void>> runSequentialGroup(RunnerGroup group) {
        ArrayList<Future<Void>> futures = new ArrayList<Future<Void>>();
        Runnable groupRunnable = new SequentialGroupRunnable(group);
        Future<Void> future = submitSequentialNode(groupRunnable);
        futures.add(future);
        synchronized (sequentialTasks) {
            sequentialTasks.add(future);
        }
        return futures;
    }

    /**
     * Submits a sequential node to the sequential completion service.
     */
    private Future<Void> submitSequentialNode(Runnable runnable) {
        LOGGER.debug("Submitting sequential node '{}'", runnable);
        Future<Void> future = sequentialExecutorService.<Void> submit(runnable, null);
        LOGGER.debug("Submitted sequential node '{}' as future {}", runnable, future);
        return future;
    }

    /**
     * Executes all direct sub nodes of a group node concurrently.
     */
    private List<Future<Void>> runParallelGroup(RunnerGroup group) {
        if (group.getChildren().size() == 0) {
            group.getLogSuite().startAndFinishEmpty();
            return new ArrayList<Future<Void>>();
        } else {
            ArrayList<Future<Void>> futures = new ArrayList<Future<Void>>();
            for (RunnerNode childNode : group.getChildren()) {
                if (childNode instanceof RunnerGroup) {
                    futures.addAll(runGroup((RunnerGroup) childNode));
                } else if (childNode instanceof RunnerLeaf) {
                    futures.add(submitParallelLeaf((RunnerLeaf) childNode));
                } else {
                    throw new TechnicalException("Not a supported RunnerGroup: " + group.getClass());
                }
            }
            return futures;
        }
    }

    /**
     * Submits a leaf node to be executed concurrently.
     */
    private Future<Void> submitParallelLeaf(RunnerLeaf leaf) {
        LOGGER.debug("Submitting parallel leaf: {}", leaf);
        if (wrapperFactory != null) {
            leaf = wrapperFactory.wrap(leaf);
        }
        Future<Void> future = parallelExecutorService.submit(leaf, null);
        LOGGER.debug("Submitted parallel leaf {} as future {}", leaf, future);
        synchronized (parallelTasks) {
            parallelTasks.add(future);
        }
        return future;
    }

    // helper class ------------------------------------------------------------

    /**
     * Wraps a {@link RunnerGroup} with a {@link Runnable} interface
     * to be executed by an {@link ExecutorService} and execute its
     * children sequentially.
     */
    public class SequentialGroupRunnable implements Runnable {

        private RunnerGroup group;

        protected SequentialGroupRunnable(RunnerGroup group) {
            this.group = group;
        }

        @Override
        public void run() {
            try {
                LOGGER.debug("Starting {}", group);
                if (group.getChildren().size() == 0) {
                    group.getLogSuite().startAndFinishEmpty();
                } else {
                    for (RunnerNode childNode : group.getChildren()) {
                        runChildOfSequentialGroup(childNode);
                    }
                }
            } catch (Exception e) {
                throw new TechnicalException("Error executing " + group, e);
            } finally {
                LOGGER.debug("Finished {}", group);
            }
        }

        /** Runs or submits the given {@link RunnerNode}. */
        private void runChildOfSequentialGroup(RunnerNode childNode) throws InterruptedException, ExecutionException {
            if (childNode instanceof RunnerGroup) {
                // run all child groups...
                List<Future<Void>> futures = runGroup((RunnerGroup) childNode);
                // ...and wait until each one has completed
                for (Future<Void> future : futures) {
                    future.get();
                }
            } else if (childNode instanceof RunnerLeaf) {
                // send the leaf to a thread of the parallel pool...
                Future<Void> future = submitParallelLeaf((RunnerLeaf) childNode);
                // ...and wait until it is finished
                future.get();
            } else {
                throw new TechnicalException("Not a supported RunnerGroup: " + group.getClass());
            }
        }

        /** Creates a string representation of the object. */
        @Override
        public String toString() {
            return getClass().getSimpleName() + ": " + group.getName();
        }
    }

}
