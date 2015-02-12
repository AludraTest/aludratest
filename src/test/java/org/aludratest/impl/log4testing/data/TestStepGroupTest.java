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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.Iterator;

import org.aludratest.impl.log4testing.data.TestCaseLog;
import org.aludratest.impl.log4testing.data.TestStepGroup;
import org.aludratest.impl.log4testing.data.TestStepLog;
import org.aludratest.impl.log4testing.data.TestSuiteLog;
import org.aludratest.testcase.TestStatus;
import org.joda.time.DateTime;
import org.junit.Test;

/**
 * Tests the {@link TestStepGroup} class.
 */
@SuppressWarnings("javadoc")
public class TestStepGroupTest {

    @Test
    public void testClear() {
        TestStepGroup group = new TestStepGroup("test", null);
        assertEquals(0, group.getNumberOfTestSteps());
        createOKTestStep(group);
        assertEquals(1, group.getNumberOfTestSteps());
    }

    @Test
    public void testGetFirstTestStep() {
        TestStepGroup testStepGroup = createEmptyTestStepGroup();
        assertNull(testStepGroup.getFirstTestStep());
        TestStepGroup group = createTestStepGroupwithTestSteps(0, 0, 12, 133, TestStatus.INCONCLUSIVE);
        assertEquals(TestStatus.INCONCLUSIVE, group.getFirstTestStep().getStatus());
    }

    @Test
    public void testGetLastTestStep() {
        assertNull(createEmptyTestStepGroup().getLastTestStep());
        TestStepGroup group = createTestStepGroupwithTestSteps(11, 22, 0, 0, TestStatus.PASSED);
        assertEquals(TestStatus.PASSED, group.getLastTestStep().getStatus());
    }

    @Test
    public void testGetNumberOfTestSteps() {
        assertEquals(createEmptyTestStepGroup().getNumberOfTestSteps(), 0);
        assertEquals(createTestStepGroupwithTestSteps(22, 33, 44, 1111, TestStatus.PASSED).getNumberOfTestSteps(), 1211);
    }

    @Test
    public void testAddTestStep() {
        TestStepGroup testStepGroup = createEmptyTestStepGroup();
        TestStepLog newTestStep = createErrorTestStep(testStepGroup);
        assertEquals(newTestStep, testStepGroup.getTestSteps().iterator().next());
    }

    @Test
    public void testGetTestSteps() {
        assertFalse(createEmptyTestStepGroup().getTestSteps().iterator().hasNext());
        
        TestStepGroup group = createEmptyTestStepGroup();
        TestStepLog okTestStep = createOKTestStep(group);
        TestStepLog errorTestStep = createErrorTestStep(group);
        TestStepLog exceptionTestStep = createExceptionTestStep(group);

        Iterator<TestStepLog> testStepGroupIter = group.getTestSteps().iterator();
        assertSame(okTestStep, testStepGroupIter.next());
        assertSame(errorTestStep, testStepGroupIter.next());
        assertSame(exceptionTestStep, testStepGroupIter.next());
        assertFalse(testStepGroupIter.hasNext());
    }

    @Test
    public void testGetStartingTime() {
        TestStepGroup testStepGroupNew = createEmptyTestStepGroup();
        assertNull(testStepGroupNew.getStartingTime());
        DateTime startingTime = new DateTime();
        createOKTestStep(testStepGroupNew);
        if (!((testStepGroupNew.getStartingTime() == startingTime) || (testStepGroupNew.getStartingTime().isBefore(startingTime.plusMillis(100))))) {
            fail("Starting time not Equal");
        }

    }

    @Test
    public void testGetFinishingTime() {
        TestStepGroup testStepGroupNew = createEmptyTestStepGroup();
        // Creation of one TestStep because setStatus calls finish()
        testStepGroupNew.newTestStep();
        assertNull(testStepGroupNew.getFinishingTime());

        DateTime finishingTime = new DateTime();
        createErrorTestStep(testStepGroupNew).finish();

        if (!((testStepGroupNew.getFinishingTime() == finishingTime) || (testStepGroupNew.getFinishingTime().isBefore(finishingTime.plusMillis(100))))) {
            fail("Finishing time not Equal");
        }
    }

    @Test
    public void testIsFailed() {
        TestStepGroup group = createEmptyTestStepGroup();
        assertEquals(false, group.isFailed());
        createErrorTestStep(group);
        assertEquals(true, group.isFailed());
    }

    /**
     * Tests the functions SetName and GetName
     */
    @Test
    public void testSetGetName() {
        TestStepGroup testStepGroup = new TestStepGroup("Test1", null);
        testStepGroup.setName("Test2");
        assertEquals(testStepGroup.getName(), "Test2");
    }

    /**
     * Tests the inherited method GetNumberOfTestSteps(Iterable<? extends TestStepContainer> iterable)
     */
    @Test
    public void testGetNumberOfTestStepsIterableOfQextendsTestStepContainer() {

        // TestStepGroup which is creating the contact to the function
        TestSuiteLog testSuite = new TestSuiteLog("TestSuite1");
        TestCaseLog testCase = new TestCaseLog("TestCase1");

        TestStepGroup group = testCase.newTestStepGroup("TestStepGroup1");
        assertEquals(0, group.getNumberOfTestSteps(testCase.getTestStepGroups()));
        testSuite.addTestCase(testCase);
        assertEquals(0, group.getNumberOfTestSteps(testSuite.getTestCases().iterator().next().getTestStepGroups()));

        createOKTestStep(group);
        createErrorTestStep(group);
        createErrorTestStep(group);
        createErrorTestStep(group);

        assertEquals(4, group.getNumberOfTestSteps(testCase.getTestStepGroups()));
        assertEquals(4, group.getNumberOfTestSteps(testSuite.getTestCases().iterator().next().getTestStepGroups()));
    }

    @Test
    public void testGetDuration() {
        TestStepGroup testStepGroup = createEmptyTestStepGroup();
        assertEquals(testStepGroup.getDuration().toPeriod().getSeconds(), 0);
        TestStepLog newTestStep = testStepGroup.newTestStep();
        newTestStep.finish();
        if (testStepGroup.getDuration().toPeriod().getSeconds() >= 1)
            fail("Error in GetDuration");
    }

    /**
     * Tests the functions SetComment, GetComment and ClearComment
     */
    @Test
    public void testSetGetComment() {
        String comment = "comment";
        TestStepGroup testStepGroup1 = new TestStepGroup("TEST", null);
        assertNull(testStepGroup1.getComment());
        testStepGroup1.setComment(comment);
        assertEquals(testStepGroup1.getComment(), comment);
    }


    // private helper methods --------------------------------------------------

    /**
     * Creates a new Empty TestStepGroup with no TestStep
     * @return the empty TestStepGroup
     */
    private TestStepGroup createEmptyTestStepGroup() {
        return new TestStepGroup("TestStepGroup 1", null);

    }

    /**
     * Creates a TestStepGroup with
     * @param beforeOk count of TestSteps which are ok before the TestSteptoAdd is added
     * @param beforefailed count of TestSteps which are failed(TestStatus Error) before the TestSteptoAdd is added
     * @param afterOK count of TestSteps which are ok after the TestSteptoAdd is added
     * @param afterfailed count of TestSteps which are failed(TestStatus Error) after the TestSteptoAdd is added
     * @param testStepToAdd TestStep which will be added in the middle of the before and after TestSteps
     * @return TestStepGroup with the TestSteps
     */
    private TestStepGroup createTestStepGroupwithTestSteps(int beforeOk, int beforefailed, int afterOK, int afterfailed, 
            TestStatus testStatusToAdd) {
        TestStepGroup group = new TestStepGroup("TestStepGroup 1", null);

        for (int i = 0; i < beforeOk; i++) {
            createOKTestStep(group);
        }

        for (int i = 0; i < beforefailed; i++) {
            createErrorTestStep(group);
        }

        group.newTestStep(testStatusToAdd, "", "");

        for (int i = 0; i < afterOK; i++) {
            createOKTestStep(group);
        }

        for (int i = 0; i < afterfailed; i++) {
            createErrorTestStep(group);
        }

        return group;
    }

    /**
     * Creates a TestStep with Status ERROR
     * @return the Error TestStep
     */
    private TestStepLog createErrorTestStep(TestStepGroup group) {
        return group.newTestStep(TestStatus.FAILED, "Command", "Comment from Error TestStep");
    }

    /**
     * Creates a TestStep with Status Exception
     * @return the Exception TestStep
     */
    private TestStepLog createExceptionTestStep(TestStepGroup group) {
        return group.newTestStep(TestStatus.INCONCLUSIVE, "Command", "Comment from Exception TestStep");
    }

    /**
     * Creates a TestStep with Status OK
     * @param group 
     * @return the OK TestStep
     */
    private TestStepLog createOKTestStep(TestStepGroup group) {
        return group.newTestStep(TestStatus.PASSED, "Command", "Comment from OK TestStep");
    }

}
