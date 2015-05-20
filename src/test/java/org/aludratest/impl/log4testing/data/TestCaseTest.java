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
package org.aludratest.impl.log4testing.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import junit.framework.TestCase;

import org.aludratest.impl.log4testing.data.TestCaseLog;
import org.aludratest.impl.log4testing.data.TestStepLog;
import org.aludratest.impl.log4testing.data.TestSuiteLog;
import org.aludratest.testcase.TestStatus;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the {@link TestCase} class.
 */
@SuppressWarnings("javadoc")
public class TestCaseTest {

    private TestCaseLog testCase;

    @Before
    public void setUp() {
        testCase = new TestCaseLog(TestCaseTest.class.getName());
        assertEquals(TestCaseLog.State.NEW, testCase.state);
        testCase.start();
        testCase.newTestStepGroup(TestCaseTest.class.getName());
        assertEquals(TestCaseLog.State.RUNNING, testCase.state);
    }

    @Test
    public void testDefaultLifecycle() {
        assertEquals(TestCaseLog.State.RUNNING, testCase.state);
        testCase.newTestStep().setStatus(TestStatus.PASSED);
        assertEquals(TestCaseLog.State.RUNNING, testCase.state);
        testCase.finish();
        assertEquals(TestCaseLog.State.FINISHED, testCase.state);
    }

    @Test(expected = IllegalStateException.class)
    public void testStartingTwice() {
        assertEquals(TestCaseLog.State.RUNNING, testCase.state);
        testCase.start();
    }

    @Test(expected = IllegalStateException.class)
    public void testFinishTwice() {
        testCase.newTestStep().setStatus(TestStatus.PASSED);
        testCase.finish();
        testCase.finish();
    }

    @Test
    public void testClear() {
        assertEquals(0, testCase.getNumberOfTestSteps());
        testCase.newTestStep().setStatus(TestStatus.PASSED);
        assertEquals(1, testCase.getNumberOfTestSteps());
        assertEquals(true, testCase.getTestStepGroups().iterator().hasNext());
        testCase.clear();
        assertEquals(false, testCase.getTestStepGroups().iterator().hasNext());
        assertEquals(0, testCase.getNumberOfTestSteps());
    }

    @Test(expected = IllegalStateException.class)
    public void testGetTestStepWithoutTestStepGroup() {
        new TestCaseLog("MyTestCase").newTestStep();
    }

    @Test
    public void testGetFirstTestStep() {
        assertNull(testCase.getFirstTestStep());
        final TestStepLog firstTestStep = testCase.newTestStep();
        assertSame(firstTestStep, testCase.getFirstTestStep());
        testCase.newTestStep();
        assertSame(firstTestStep, testCase.getFirstTestStep());
    }

    @Test
    public void testGetLastTestStep() {
        assertNull(testCase.getLastTestStep());
        final TestStepLog firstTestStep = testCase.newTestStep();
        assertSame(firstTestStep, testCase.getLastTestStep());
        final TestStepLog lastTestStep = testCase.newTestStep();
        assertNotSame(firstTestStep, testCase.getLastTestStep());
        assertSame(lastTestStep, testCase.getLastTestStep());
    }

    @Test
    public void testGetNumberOfTestSteps() {
        assertEquals(0, testCase.getNumberOfTestSteps());
        testCase.newTestStep();
        assertEquals(1, testCase.getNumberOfTestSteps());
        testCase.newTestStep();
        assertEquals(2, testCase.getNumberOfTestSteps());
    }

    @Test
    public void testGetFinishingTime() {
        assertNull(testCase.getFinishingTime());
        final TestStepLog firstTestStep = testCase.newTestStep();
        assertNull(testCase.getFinishingTime());
        firstTestStep.finish();
        testCase.finish();
        assertNotNull(testCase.getFinishingTime());
    }

    @Test
    public void testIsFailed() {
        assertFalse(testCase.isFailed());
        testCase.newTestStep();
        assertFalse(testCase.isFailed());
        final TestStepLog testStep = testCase.newTestStep(TestStatus.FAILED, "", "");
        testCase.finish();
        assertTrue(testCase.isFailed());
        testStep.setStatus(TestStatus.PASSED);
        assertFalse(testCase.isFailed());
        testStep.setStatus(TestStatus.INCONCLUSIVE);
        assertTrue(testCase.isFailed());
        testStep.setStatus(TestStatus.IGNORED);
        assertFalse(testCase.isFailed());
        testStep.setStatus(TestStatus.FAILEDAUTOMATION);
        assertTrue(testCase.isFailed());
        testStep.setStatus(TestStatus.PASSED);
        assertFalse(testCase.isFailed());
    }

    @Test
    public void testFailedPriorityWithoutIgnore() {
        TestSuiteLog suite = new TestSuiteLog("rootSuite");
        TestCaseLog case1 = new TestCaseLog("case1");
        suite.add(case1);
        assertState(TestStatus.PENDING, suite, case1);
        case1.newTestStepGroup("group11");
        case1.newTestStep(TestStatus.PASSED, "command111", "comment111");
        assertState(TestStatus.RUNNING, suite, case1);
        case1.newTestStep(TestStatus.FAILED, "command117", "comment117");
        case1.finish();
        assertState(TestStatus.FAILED, suite, case1);
    }

    @Test
    public void testFailedPriorityWithIgnore() {
        TestSuiteLog suite = new TestSuiteLog("rootSuite");
        TestCaseLog case1 = new TestCaseLog("case1");
        case1.ignore();
        suite.add(case1);
        assertState(TestStatus.PENDING, suite, case1);
        case1.newTestStepGroup("group11");
        case1.newTestStep(TestStatus.PASSED, "command111", "comment111");
        assertState(TestStatus.RUNNING, suite, case1);
        case1.newTestStep(TestStatus.FAILED, "command117", "comment117");
        case1.finish();
        assertState(TestStatus.IGNORED, suite, case1);
    }

    private void assertState(TestStatus status, TestSuiteLog suite, TestCaseLog testCase) {
        assertEquals(status, suite.getStatus());
        assertEquals(status, testCase.getStatus());
    }

}
