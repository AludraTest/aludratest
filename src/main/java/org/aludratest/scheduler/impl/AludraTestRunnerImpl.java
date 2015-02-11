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
package org.aludratest.scheduler.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.aludratest.config.AludraTestConfig;
import org.aludratest.exception.TechnicalException;
import org.aludratest.invoker.TestInvoker;
import org.aludratest.scheduler.AludraTestRunner;
import org.aludratest.scheduler.RunStatus;
import org.aludratest.scheduler.RunnerListenerRegistry;
import org.aludratest.scheduler.RunnerTree;
import org.aludratest.scheduler.node.RunnerGroup;
import org.aludratest.scheduler.node.RunnerLeaf;
import org.aludratest.scheduler.node.RunnerNode;
import org.aludratest.scheduler.util.CommonRunnerLeafAttributes;
import org.aludratest.scheduler.util.PoolThreadFactory;
import org.aludratest.service.AludraServiceManager;
import org.aludratest.testcase.AludraTestContext;
import org.aludratest.testcase.event.InternalTestListener;
import org.aludratest.testcase.event.TestStepInfo;
import org.aludratest.testcase.event.impl.LogUtil;
import org.aludratest.testcase.impl.AludraTestContextImpl;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(role = AludraTestRunner.class)
public class AludraTestRunnerImpl implements AludraTestRunner {

    /** The {@link Logger} of the class. Intentional use of AludraTestRunner.class to allow logging configuration independent from
     * implementation. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AludraTestRunner.class);

    /** The {@link ExecutorService} to use for concurrent test execution. */
    private ExecutorService parallelExecutorService;

    /** List of the currently pending concurrent tests. */
    // Note: SonarQube recommends List, but I need LinkedList in the implementation
    private volatile LinkedList<Future<Void>> parallelTasks; // NOSONAR

    /** The {@link ExecutorService} to use for sequential test execution. */
    private ExecutorService sequentialExecutorService;

    /** List of the currently pending sequential tests. */
    // Note: SonarQube recommends List, but I need LinkedList in the implementation
    private volatile LinkedList<Future<Void>> sequentialTasks; // NOSONAR

    @Requirement
    private RunnerListenerRegistry listenerRegistry;

    @Requirement
    private AludraTestConfig aludraConfig;

    @Requirement
    private AludraServiceManager serviceManager;

    @Override
    public void runAludraTests(RunnerTree runnerTree) {
        int poolSize = aludraConfig.getNumberOfThreads();

        if (runnerTree.getRoot() != null) {
            parallelExecutorService = Executors.newFixedThreadPool(poolSize, new PoolThreadFactory("ParEx"));
            parallelTasks = new LinkedList<Future<Void>>();
            sequentialExecutorService = Executors.newCachedThreadPool(new PoolThreadFactory("SerEx"));
            sequentialTasks = new LinkedList<Future<Void>>();
            if (LOGGER.isDebugEnabled()) {
                debugSubTree(runnerTree.getRoot(), "");
            }

            // fire start process event
            listenerRegistry.fireStartingTestProcess(runnerTree);
            try {
                runGroup(runnerTree.getRoot());
                waitForCompletion();
            }
            finally {
                listenerRegistry.fireFinishedTestProcess(runnerTree);
            }
        }
        else {
            LOGGER.info("No tests to run");
        }

    }

    /** Executes any group node with all child nodes. */
    private List<Future<Void>> runGroup(RunnerGroup group) {
        if (group.isParallel()) {
            return runParallelGroup(group);
        }
        else {
            return runSequentialGroup(group);
        }
    }

    /** Prints the sub tree of a node to a {@link Logger} in DEBUG level. Applied to the root node, it logs the full tree hierarchy
     * @param node
     * @param indent */
    private void debugSubTree(RunnerNode node, String indent) {
        LOGGER.debug(indent + node);
        if (node instanceof RunnerGroup) {
            String subIndent = indent + "  ";
            RunnerGroup group = (RunnerGroup) node;
            for (RunnerNode child : group.getChildren()) {
                debugSubTree(child, subIndent);
            }
        }
    }

    /** Waits until all queued tasks have completed. */
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
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        finally {
            if (!parallelTasks.isEmpty() || !sequentialTasks.isEmpty()) {
                LOGGER.info("Cancelling tasks");
            }
            cancelAll(parallelTasks);
            cancelAll(sequentialTasks);
            shutDown(parallelExecutorService);
            shutDown(sequentialExecutorService);
        }
    }

    /** Waits for the completion of the given task and removes it from the queue. */
    private void waitForCompletionAndRemoveFromQueue(Future<Void> task, LinkedList<Future<Void>> queue)
            throws InterruptedException {
        LOGGER.debug("Waiting for completion of task: {}", task);
        try {
            task.get();
        }
        catch (ExecutionException e) {
            LOGGER.error("Error executing sequential task", e);
        }
        synchronized (queue) {
            LOGGER.debug("Task completed, removing it from queue: {}", task);
            queue.remove(task);
        }
    }

    /** Executes a sequential group node. */
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

    /** Submits a sequential node to the sequential completion service. */
    private Future<Void> submitSequentialNode(Runnable runnable) {
        LOGGER.debug("Submitting sequential node '{}'", runnable);
        Future<Void> future = sequentialExecutorService.<Void> submit(runnable, null);
        LOGGER.debug("Submitted sequential node '{}' as future {}", runnable, future);
        return future;
    }

    /** Executes all direct sub nodes of a group node concurrently.
     * @param group Group to create and submit callables for.
     * @return A list of the submitted futures. */
    private List<Future<Void>> runParallelGroup(RunnerGroup group) {
        if (group.isEmpty()) {
            listenerRegistry.fireStartingTestGroup(group);
            listenerRegistry.fireFinishedTestGroup(group);
            return new ArrayList<Future<Void>>();
        }
        else {
            ArrayList<Future<Void>> futures = new ArrayList<Future<Void>>();
            for (RunnerNode childNode : group.getChildren()) {
                if (childNode instanceof RunnerGroup) {
                    futures.addAll(runGroup((RunnerGroup) childNode));
                }
                else if (childNode instanceof RunnerLeaf) {
                    futures.add(submitParallelLeaf((RunnerLeaf) childNode));
                }
                else {
                    throw new TechnicalException("Not a supported RunnerNode: " + childNode.getClass());
                }
            }
            return futures;
        }
    }

    /** Submits a leaf node to be executed concurrently. */
    private Future<Void> submitParallelLeaf(RunnerLeaf leaf) {
        LOGGER.debug("Submitting parallel leaf: {}", leaf);
        Future<Void> future = parallelExecutorService.submit(new RunnerLeafRunnable(leaf));
        LOGGER.debug("Submitted parallel leaf {} as future {}", leaf, future);
        synchronized (parallelTasks) {
            parallelTasks.add(future);
        }
        return future;
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

    /** Wraps a {@link RunnerGroup} with a {@link Runnable} interface to be executed by an {@link ExecutorService} and execute its
     * children sequentially. */
    private class SequentialGroupRunnable implements Runnable {

        private RunnerGroup group;

        protected SequentialGroupRunnable(RunnerGroup group) {
            this.group = group;
        }

        @Override
        public void run() {
            try {
                LOGGER.debug("Starting {}", group);
                if (group.isEmpty()) {
                    listenerRegistry.fireStartingTestGroup(group);
                    listenerRegistry.fireFinishedTestGroup(group);
                }
                else {
                    for (RunnerNode childNode : group.getChildren()) {
                        runChildOfSequentialGroup(childNode);
                    }
                }
            }
            catch (Exception e) {
                throw new TechnicalException("Error executing " + group, e);
            }
            finally {
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
            }
            else if (childNode instanceof RunnerLeaf) {
                // send the leaf to a thread of the parallel pool...
                Future<Void> future = submitParallelLeaf((RunnerLeaf) childNode);
                // ...and wait until it is finished
                future.get();
            }
            else {
                throw new TechnicalException("Not a supported RunnerGroup: " + group.getClass());
            }
        }

        /** Creates a string representation of the object. */
        @Override
        public String toString() {
            return getClass().getSimpleName() + ": " + group.getName();
        }
    }

    /* Needed for RunnerLeafRunnable */
    private static Object runStatusSemaphore = new Object();

    private class RunnerLeafRunnable implements Callable<Void> {

        private RunnerLeaf leaf;

        public RunnerLeafRunnable(RunnerLeaf leaf) {
            this.leaf = leaf;
        }

        @Override
        public Void call() {
            TestInvoker testInvoker = leaf.getTestInvoker();
            LOGGER.debug("Starting " + testInvoker);

            InternalTestListener listener = new InternalTestListener() {
                @Override
                public void newTestStepGroup(String name) {
                    listenerRegistry.fireNewTestStepGroup(leaf, name);
                }

                @Override
                public void newTestStep(TestStepInfo testStep) {
                    listenerRegistry.fireNewTestStep(leaf, testStep);
                }
            };

            // set context
            AludraTestContext context = new AludraTestContextImpl(listener, serviceManager);
            testInvoker.setContext(context);

            // mark as ignored, if leaf is ignored
            boolean ignore = Boolean.TRUE.equals(leaf.getAttribute(CommonRunnerLeafAttributes.IGNORE))
                    && aludraConfig.isIgnoreEnabled();

            // rename current Thread for logging purposes
            String oldName = Thread.currentThread().getName();
            Thread.currentThread().setName("RunnerLeaf " + leaf.getId());

            try {
                // check tree upwards if group is just starting
                RunnerGroup group = leaf.getParent();
                List<RunnerGroup> toFire = new ArrayList<RunnerGroup>();
                synchronized (runStatusSemaphore) {
                    while (group != null && group.getRunStatus() == RunStatus.WAITING) {
                        toFire.add(0, group);
                        group = group.getParent();
                    }
                    leaf.setRunStatus(RunStatus.RUNNING);
                }

                for (RunnerGroup g : toFire) {
                    listenerRegistry.fireStartingTestGroup(g);
                }

                listenerRegistry.fireStartingTestLeaf(leaf);
                testInvoker.invoke();
                LOGGER.debug("Finished " + testInvoker);
                Thread.currentThread().setName(oldName);
            }
            catch (Throwable t) {
                // new test step group, to be sure
                LogUtil.logErrorAsNewGroup(listenerRegistry, leaf, t, ignore);
            }
            finally {
                Thread.currentThread().setName(oldName);


                // check tree upwards for finished groups
                List<RunnerGroup> toFire = new ArrayList<RunnerGroup>();
                synchronized (runStatusSemaphore) {
                    leaf.setRunStatus(RunStatus.FINISHED);
                    RunnerGroup group = leaf.getParent();
                    while (group != null && group.getRunStatus() == RunStatus.FINISHED) {
                        toFire.add(group);
                        group = group.getParent();
                    }
                }

                listenerRegistry.fireFinishedTestLeaf(leaf);
                for (RunnerGroup group : toFire) {
                    listenerRegistry.fireFinishedTestGroup(group);
                }
            }

            return null;
        }

    }

}
