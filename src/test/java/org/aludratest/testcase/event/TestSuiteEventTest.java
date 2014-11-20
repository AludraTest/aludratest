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
package org.aludratest.testcase.event;

import java.util.HashSet;
import java.util.Set;

import org.aludratest.AludraTest;
import org.aludratest.scheduler.RunnerListener;
import org.aludratest.scheduler.RunnerListenerRegistry;
import org.aludratest.scheduler.RunnerTree;
import org.aludratest.scheduler.node.RunnerGroup;
import org.aludratest.scheduler.node.RunnerLeaf;
import org.aludratest.suite.ParallelTestSuite;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/** Tests parallel / sequential mixed execution of tests, and the associated events.
 * 
 * @author falbrech */
public class TestSuiteEventTest {

    private AludraTest aludraTest;

    // on-off set
    private Set<RunnerGroup> runningGroups = new HashSet<RunnerGroup>();

    // on-off set
    private Set<RunnerLeaf> runningLeafs = new HashSet<RunnerLeaf>();

    // on-only set
    private Set<RunnerGroup> allRunningGroups = new HashSet<RunnerGroup>();

    // on-only set
    private Set<RunnerLeaf> allRunningLeafs = new HashSet<RunnerLeaf>();

    // off-only set
    private Set<RunnerGroup> allStoppedGroups = new HashSet<RunnerGroup>();

    // off-only set
    private Set<RunnerLeaf> allStoppedLeafs = new HashSet<RunnerLeaf>();

    // To avoid AludraTest catching the assertionError fired by a listener
    private String assertionError;

    @Before
    public void initializeAludra() {
        System.setProperty("ALUDRATEST_CONFIG/aludratest/number.of.threads", "3");
        aludraTest = AludraTest.startFramework();
    }

    @After
    public void stopAludra() {
        aludraTest.stopFramework();
    }

    @Test
    public void testMixedExecution() {
        // FIXME this does not work when ALL AludraTest tests are executed, as ParallelTestSuite is used twice.
        // either fix Log4Testing to be non-static, or use another class here (preferred at the moment)

        // get the listener registry
        RunnerListenerRegistry registry = aludraTest.getServiceManager().newImplementorInstance(RunnerListenerRegistry.class);

        TestRunnerListener listener = new TestRunnerListener();
        registry.addRunnerListener(listener);

        aludraTest.run(ParallelTestSuite.class);

        if (assertionError != null) {
            Assert.fail(assertionError);
        }
    }

    private void assertFalse(String message, boolean condition) {
        if (assertionError == null && condition) {
            assertionError = message;
        }
    }

    private void assertTrue(String message, boolean condition) {
        if (assertionError == null && !condition) {
            assertionError = message;
        }
    }

    private class TestRunnerListener implements RunnerListener {

        @Override
        public void startingTestProcess(RunnerTree runnerTree) {
        }

        @Override
        public void startingTestGroup(RunnerGroup runnerGroup) {
            assertFalse("Duplicate start for runner group " + runnerGroup.getName(), runningGroups.contains(runnerGroup));
            assertFalse("Duplicate start for runner group " + runnerGroup.getName(),
                    allRunningGroups.contains(runnerGroup));

            try {
                Thread.sleep((long) (Math.random() * 1000) + 200);
            }
            catch (InterruptedException e) {
            }
            System.out.println("Starting group " + runnerGroup.getName());
            try {
                Thread.sleep((long) (Math.random() * 1000) + 200);
            }
            catch (InterruptedException e) {
            }
            runningGroups.add(runnerGroup);
            allRunningGroups.add(runnerGroup);
        }

        @Override
        public void startingTestLeaf(RunnerLeaf runnerLeaf) {
            assertFalse("Duplicate start for runner leaf " + runnerLeaf.getName(), runningLeafs.contains(runnerLeaf));
            assertFalse("Duplicate start for runner leaf " + runnerLeaf.getName(), allRunningLeafs.contains(runnerLeaf));
            try {
                Thread.sleep((long) (Math.random() * 1000) + 200);
            }
            catch (InterruptedException e) {
            }
            System.out.println("Starting LEAF " + runnerLeaf.getName() + " (" + runnerLeaf.getId() + ")");
            try {
                Thread.sleep((long) (Math.random() * 1000) + 200);
            }
            catch (InterruptedException e) {
            }
            runningLeafs.add(runnerLeaf);
            allRunningLeafs.add(runnerLeaf);
        }

        @Override
        public void finishedTestLeaf(RunnerLeaf runnerLeaf) {
            assertTrue("Finish without start for runner leaf " + runnerLeaf.getName(), runningLeafs.contains(runnerLeaf));
            assertFalse("Duplicate finish for runner leaf " + runnerLeaf.getName(), allStoppedLeafs.contains(runnerLeaf));
            try {
                Thread.sleep((long) (Math.random() * 1000) + 200);
            }
            catch (InterruptedException e) {
            }
            System.out.println("FINISHED LEAF " + runnerLeaf.getName());
            try {
                Thread.sleep((long) (Math.random() * 1000) + 200);
            }
            catch (InterruptedException e) {
            }
            runningLeafs.remove(runnerLeaf);
            allStoppedLeafs.add(runnerLeaf);
        }

        @Override
        public void finishedTestGroup(RunnerGroup runnerGroup) {
            assertTrue("Finish without start for runner group " + runnerGroup.getName(),
                    runningGroups.contains(runnerGroup));
            assertFalse("Duplicate finish for runner group " + runnerGroup.getName(),
                    allStoppedGroups.contains(runnerGroup));


            try {
                Thread.sleep((long) (Math.random() * 1000) + 200);
            }
            catch (InterruptedException e) {
            }
            System.out.println("FINISHED group " + runnerGroup.getName());
            try {
                Thread.sleep((long) (Math.random() * 1000) + 200);
            }
            catch (InterruptedException e) {
            }

            runningGroups.remove(runnerGroup);
            allStoppedGroups.add(runnerGroup);
        }

        @Override
        public void finishedTestProcess(RunnerTree runnerTree) {
            assertTrue("Process finished while group running", runningGroups.isEmpty());
            assertTrue("Process finished while leaf running", runningLeafs.isEmpty());
            assertTrue("Group notifier size mismatch", allRunningGroups.size() == allStoppedGroups.size());
            assertTrue("Leaf notifier size mismatch", allRunningLeafs.size() == allStoppedLeafs.size());
        }

        @Override
        public void newTestStepGroup(RunnerLeaf runnerLeaf, String groupName) {
        }

        @Override
        public void newTestStep(RunnerLeaf runnerLeaf, TestStepInfo testStepInfo) {
        }

    }

}
