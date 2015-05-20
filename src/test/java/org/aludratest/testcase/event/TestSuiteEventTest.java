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
import org.aludratest.scheduler.AbstractRunnerListener;
import org.aludratest.scheduler.RunnerListenerRegistry;
import org.aludratest.scheduler.RunnerTree;
import org.aludratest.scheduler.node.RunnerGroup;
import org.aludratest.scheduler.node.RunnerLeaf;
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
        System.setProperty("ALUDRATEST_CONFIG/aludratest/number.of.threads", "6");
        aludraTest = AludraTest.startFramework();
    }

    @After
    public void stopAludra() {
        aludraTest.stopFramework();
        System.getProperties().remove("ALUDRATEST_CONFIG/aludratest/number.of.threads");
    }

    @Test
    public void testMixedExecution() {
        // get the listener registry
        RunnerListenerRegistry registry = aludraTest.getServiceManager().newImplementorInstance(RunnerListenerRegistry.class);

        TestRunnerListener listener = new TestRunnerListener();
        registry.addRunnerListener(listener);

        // prepare test case classes (markers)
        SequentialTest.parallel = false;
        SequentialTest.test1 = false;
        SequentialTest.test2 = false;
        SequentialTest.running.set(0);
        ParallelTest1.sequentialRunning = false;

        aludraTest.run(ParallelSuite.class);

        if (assertionError != null) {
            Assert.fail(assertionError);
        }

        Assert.assertTrue("SequentialTest tests should have been executed parallel to ParallelTest1",
                ParallelTest1.sequentialRunning);
        Assert.assertTrue(SequentialTest.test1);
        Assert.assertTrue(SequentialTest.test2);
        Assert.assertFalse(SequentialTest.parallel);
        Assert.assertEquals(0, SequentialTest.running.get());
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

    private class TestRunnerListener extends AbstractRunnerListener {

        @Override
        public void startingTestGroup(RunnerGroup runnerGroup) {
            assertFalse("Duplicate start for runner group " + runnerGroup.getName(), runningGroups.contains(runnerGroup));
            assertFalse("Duplicate start for runner group " + runnerGroup.getName(),
                    allRunningGroups.contains(runnerGroup));

            runningGroups.add(runnerGroup);
            allRunningGroups.add(runnerGroup);

            // simulate a bad listener, wasting time (must not influence notification correctness)
            try {
                Thread.sleep((long) (Math.random() * 100));
            }
            catch (InterruptedException e) {
            }
        }

        @Override
        public void startingTestLeaf(RunnerLeaf runnerLeaf) {
            assertFalse("Duplicate start for runner leaf " + runnerLeaf.getName(), runningLeafs.contains(runnerLeaf));
            assertFalse("Duplicate start for runner leaf " + runnerLeaf.getName(), allRunningLeafs.contains(runnerLeaf));

            runningLeafs.add(runnerLeaf);
            allRunningLeafs.add(runnerLeaf);

            try {
                Thread.sleep((long) (Math.random() * 100));
            }
            catch (InterruptedException e) {
            }

        }

        @Override
        public void finishedTestLeaf(RunnerLeaf runnerLeaf) {
            assertTrue("Finish without start for runner leaf " + runnerLeaf.getName(), runningLeafs.contains(runnerLeaf));
            assertFalse("Duplicate finish for runner leaf " + runnerLeaf.getName(), allStoppedLeafs.contains(runnerLeaf));

            runningLeafs.remove(runnerLeaf);
            allStoppedLeafs.add(runnerLeaf);

            try {
                Thread.sleep((long) (Math.random() * 100));
            }
            catch (InterruptedException e) {
            }
        }

        @Override
        public void finishedTestGroup(RunnerGroup runnerGroup) {
            synchronized (runningGroups) {
                assertTrue("Finish without start for runner group " + runnerGroup.getName(), runningGroups.contains(runnerGroup));
                assertFalse("Duplicate finish for runner group " + runnerGroup.getName(), allStoppedGroups.contains(runnerGroup));

                try {
                    Thread.sleep((long) (Math.random() * 100));
                }
                catch (InterruptedException e) {
                }

                runningGroups.remove(runnerGroup);
                allStoppedGroups.add(runnerGroup);
            }
        }

        @Override
        public void finishedTestProcess(RunnerTree runnerTree) {
            assertTrue("Process finished while group running", runningGroups.isEmpty());
            assertTrue("Process finished while leaf running", runningLeafs.isEmpty());
            assertTrue("Group notifier size mismatch", allRunningGroups.size() == allStoppedGroups.size());
            assertTrue("Leaf notifier size mismatch", allRunningLeafs.size() == allStoppedLeafs.size());
        }

    }

}
