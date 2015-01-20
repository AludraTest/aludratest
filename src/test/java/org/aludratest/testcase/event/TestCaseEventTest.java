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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.aludratest.AludraTest;
import org.aludratest.scheduler.RunnerListener;
import org.aludratest.scheduler.RunnerListenerRegistry;
import org.aludratest.scheduler.RunnerTree;
import org.aludratest.scheduler.node.RunnerGroup;
import org.aludratest.scheduler.node.RunnerLeaf;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.test.testclasses.testcase.ValidTestCaseClass;

@SuppressWarnings("javadoc")
public class TestCaseEventTest {

    private AludraTest aludraTest;

    @Before
    public void initializeAludra() {
        aludraTest = AludraTest.startFramework();
    }

    @After
    public void stopAludra() {
        aludraTest.stopFramework();
    }

    @Test
    public void testStandardEvents() {
        // get the listener registry
        RunnerListenerRegistry registry = aludraTest.getServiceManager().newImplementorInstance(RunnerListenerRegistry.class);
        TestRunnerListener listener = new TestRunnerListener();
        registry.addRunnerListener(listener);
        aludraTest.run(ValidTestCaseClass.class);

        assertEquals(0, listener.phase);
        assertTrue("Runner Tree Leaf event has not been fired", listener.leaf);
        assertFalse("Runner Tree Root Group has not been closed", listener.rootGroup);
    }

    private static class TestRunnerListener implements RunnerListener {

        private int phase;

        private boolean leaf;

        private boolean rootGroup;

        @Override
        public void startingTestProcess(RunnerTree runnerTree) {
            assertEquals(0, phase);
            phase++;
        }

        @Override
        public void startingTestGroup(RunnerGroup runnerGroup) {
            // there are two groups, check detail
            if (runnerGroup.getName().equals(ValidTestCaseClass.class.getName())) {
                assertEquals(1, phase);
                phase++;
                rootGroup = true;
            }
            else if (runnerGroup.getName().endsWith(".test")) {
                assertEquals(2, phase);
                phase++;
            }
            else {
                Assert.fail("Unexpected runner group: " + runnerGroup.getName());
            }
        }

        @Override
        public void startingTestLeaf(RunnerLeaf runnerLeaf) {
            assertEquals(3, phase);
            phase++;
            leaf = true;
        }

        @Override
        public void finishedTestLeaf(RunnerLeaf runnerLeaf) {
            assertEquals(4, phase);
            phase--;
        }

        @Override
        public void finishedTestGroup(RunnerGroup runnerGroup) {
            if (runnerGroup.getName().equals(ValidTestCaseClass.class.getName())) {
                assertEquals(2, phase);
                phase--;
                rootGroup = false;
            }
            else if (runnerGroup.getName().endsWith(".test")) {
                assertEquals(3, phase);
                phase--;
            }
            else {
                Assert.fail("Unexpected runner group: " + runnerGroup.getName());
            }
        }

        @Override
        public void finishedTestProcess(RunnerTree runnerTree) {
            assertEquals(1, phase);
            phase--;
        }

        @Override
        public void newTestStepGroup(RunnerLeaf runnerLeaf, String groupName) {
            // should not be called in this test
            Assert.fail("No test step group should have been called in this test");
        }

        @Override
        public void newTestStep(RunnerLeaf runnerLeaf, TestStepInfo testStepInfo) {
            // should not be called in this test
            Assert.fail("No test step should have been created in this test");
        }

    }
}
