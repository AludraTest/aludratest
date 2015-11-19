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
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.aludratest.config.AludraTestConfig;
import org.aludratest.invoker.TestInvoker;
import org.aludratest.scheduler.AludraTestRunner;
import org.aludratest.scheduler.RunStatus;
import org.aludratest.scheduler.RunnerListenerRegistry;
import org.aludratest.scheduler.RunnerTree;
import org.aludratest.scheduler.node.RunnerGroup;
import org.aludratest.scheduler.node.RunnerLeaf;
import org.aludratest.scheduler.node.RunnerNode;
import org.aludratest.scheduler.util.CommonRunnerLeafAttributes;
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

/** Default implementation of the AludraTestRunner component interface. This implementation uses the <code>ExecutionPlan</code>
 * class to build an ordered list of test cases to execute, including their preconditions. As long as the execution plan is not
 * empty, it is asked for the next test case of the ordered list which has all of its preconditions fulfilled, and executes it
 * using a <code>ThreadPoolExecutor</code>. When the Thread Pool is full, the runner waits for a running test case to become
 * finished before submitting the next one.
 * 
 * @author falbrech
 * 
 * @see ExecutionPlan */
@Component(role = AludraTestRunner.class)
public class AludraTestRunnerImpl implements AludraTestRunner {

    /** The {@link Logger} of the class. Intentional use of AludraTestRunner.class to allow logging configuration independent from
     * implementation. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AludraTestRunner.class);

    /** The {@link ExecutorService} to use for test execution. */
    private ThreadPoolExecutor executorService;

    /** The execution plan for all tests. */
    private ExecutionPlan executionPlan;

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
            if (LOGGER.isDebugEnabled()) {
                debugSubTree(runnerTree.getRoot(), "");
            }

            executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(poolSize);

            executionPlan = new ExecutionPlan();
            executionPlan.buildExecutionPlan(runnerTree);

            // fire start process event
            listenerRegistry.fireStartingTestProcess(runnerTree);

            try {
                while (!executionPlan.isEmpty()) {
                    // wait for an execution slot to become available
                    while (executorService.getActiveCount() >= poolSize) {
                        synchronized (executorService) {
                            try {
                                executorService.wait(5000);
                            }
                            catch (InterruptedException e) {
                                return;
                            }
                        }
                    }

                    // wait for a runner leaf to become available (all dependencies fulfilled)
                    RunnerLeaf nextLeaf;
                    while ((nextLeaf = executionPlan.getNextExecutableLeaf()) == null && !executionPlan.isEmpty()) {
                        synchronized (executorService) {
                            try {
                                executorService.wait(5000);
                            }
                            catch (InterruptedException e) {
                                return;
                            }
                        }
                    }

                    executorService.submit(new RunnerLeafRunnable(nextLeaf));
                }
            }
            finally {
                listenerRegistry.fireFinishedTestProcess(runnerTree);
                executorService.shutdown();
                try {
                    executorService.awaitTermination(1, TimeUnit.MINUTES);
                }
                catch (InterruptedException e) {
                    return;
                }
            }
        }
        else {
            LOGGER.info("No tests to run");
        }
    }

    private void executeEmptyGroups(RunnerGroup group) {
        if (group.getChildren().isEmpty()) {
            listenerRegistry.fireStartingTestGroup(group);
            listenerRegistry.fireFinishedTestGroup(group);
        }

        for (RunnerNode node : group.getChildren()) {
            if (node instanceof RunnerGroup) {
                executeEmptyGroups((RunnerGroup) node);
            }
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
                    // find all empty groups and fire start and finish for them
                    executeEmptyGroups(g);
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

                try {
                    listenerRegistry.fireFinishedTestLeaf(leaf);
                    for (RunnerGroup group : toFire) {
                        listenerRegistry.fireFinishedTestGroup(group);
                    }
                }
                finally {
                    executionPlan.removeFinishedRunnerLeaf(leaf);
                    synchronized (executorService) {
                        executorService.notify();
                    }
                }
            }

            return null;
        }

    }

}
