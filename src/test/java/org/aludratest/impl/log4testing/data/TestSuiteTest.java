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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestSuite;

import org.aludratest.impl.log4testing.configuration.ConfigurationError;
import org.aludratest.testcase.TestStatus;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the {@link TestSuite} class.
 */
@SuppressWarnings("javadoc")
public class TestSuiteTest {

    private final static String TEST_SUITE_NAME_ONE = "TestSuite One";
    private final static String TEST_SUITE_NAME_TWO = "TestSuite Two";
    private final static String TEST_SUITE_NAME_THREE = "TestSuite Three";
    private final static String TEST_CASE_ONE = "Test Case One";
    private final static String TEST_CASE_TWO = "Test Case Two";

    private TestSuiteLog emptyTestSuiteOne;
    private TestSuiteLog emptyTestSuiteTwo;
    private TestCaseLog emptyTestCaseOne;
    private TestCaseLog emptyTestCaseTwo;

    @Before
    public void init() {
        emptyTestSuiteOne = new TestSuiteLog(TEST_SUITE_NAME_ONE);
        emptyTestSuiteTwo = new TestSuiteLog(TEST_SUITE_NAME_TWO);
        emptyTestCaseOne = new TestCaseLog(TEST_CASE_ONE);
        emptyTestCaseOne.newTestStepGroup(TEST_CASE_ONE);
        emptyTestCaseTwo = new TestCaseLog(TEST_CASE_TWO);
        emptyTestCaseTwo.newTestStepGroup(TEST_CASE_TWO);
    }

    @Test
    public void testClearTestCases() {
        emptyTestSuiteOne.addTestCase(emptyTestCaseOne);
        emptyTestCaseOne.newTestStep().setStatus(TestStatus.PASSED);
        assertTrue(emptyTestSuiteOne.getTestCases().iterator().hasNext());
        assertEquals(1, emptyTestSuiteOne.getNumberOfTestSteps());
        emptyTestSuiteOne.clear();
        assertFalse(emptyTestSuiteOne.getTestCases().iterator().hasNext());
        assertEquals(0, emptyTestSuiteOne.getNumberOfTestSteps());
    }

    @Test
    public void testClearTestSuites() {
        emptyTestSuiteOne.addTestSuite(emptyTestSuiteTwo);
        emptyTestSuiteTwo.addTestCase(emptyTestCaseTwo);
        emptyTestCaseTwo.newTestStep().setStatus(TestStatus.PASSED);
        assertTrue(emptyTestSuiteOne.getTestSuites().iterator().hasNext());
        assertEquals(1, emptyTestSuiteOne.getNumberOfTestSteps());
        emptyTestSuiteOne.clear();
        assertFalse(emptyTestSuiteOne.getTestSuites().iterator().hasNext());
        assertEquals(0, emptyTestSuiteOne.getNumberOfTestSteps());
    }

    @Test
    public void testEmpty() {
        assertFalse(emptyTestSuiteOne.getTestCases().iterator().hasNext());
        assertEquals(0, emptyTestSuiteOne.getNumberOfTestSteps());
    }

    /**
     * This Test tries to add himself to his List of Suites
     */
    @Test(expected = IllegalParentException.class)
    public void testAddTestSuiteSameInstance() {
        final TestSuiteLog testSuite = new TestSuiteLog(TEST_SUITE_NAME_ONE);
        testSuite.addTestSuite(testSuite);
    }

    /**
     * This test tries to add a Suite to another Suite with the same name
     */
    @Test(expected = IllegalParentException.class)
    public void testAddTestSuiteSameName() {
        final TestSuiteLog testSuiteOne = new TestSuiteLog(TEST_SUITE_NAME_ONE);
        final TestSuiteLog testSuiteTwo = new TestSuiteLog(TEST_SUITE_NAME_ONE);
        testSuiteOne.addTestSuite(testSuiteTwo);
    }

    /**
     * This test tries to generate a cycle (TestCaseOne --> TestCaseTwo -->
     * TestCaseThree --> TestCaseOne ...)
     */
    @Test(expected = IllegalParentException.class)
    public void testAddTestSuiteCycleException() {
        final TestSuiteLog testSuiteOne = new TestSuiteLog(TEST_SUITE_NAME_ONE);
        final TestSuiteLog testSuiteTwo = new TestSuiteLog(TEST_SUITE_NAME_TWO);
        final TestSuiteLog testSuiteThree = new TestSuiteLog(TEST_SUITE_NAME_THREE);
        testSuiteOne.addTestSuite(testSuiteTwo);
        testSuiteThree.addTestSuite(testSuiteOne);
        testSuiteTwo.addTestSuite(testSuiteThree);
    }

    @Test(timeout = 500)
    /**
     * Test for performance Issue
     * Adding should be done in under 150 ms
     */
    public void testAddTestSuitePerformance() {
        final int childSuiteCount = 500;
        final int numRootSuites = 50;
        List<TestSuiteLog> rootSuites = createTestSuites(numRootSuites, "E");
        List<TestSuiteLog> addableSuites = createTestSuites(numRootSuites * childSuiteCount, "T");
        Iterator<TestSuiteLog> addableSuitesIter = addableSuites.iterator();
        for (TestSuiteLog parentSuite : rootSuites) {
            for (int i = 0; i < childSuiteCount; i++) {
                final TestSuiteLog childSuite = addableSuitesIter.next();
                parentSuite.addTestSuite(childSuite);
            }
        }
    }

    @Test
    /**
     * Test the functionality of the function GetTestCases which should return only the local
     * TestCases
     */
    public void testGetTestCases() {
        final TestSuiteLog testSuiteOne = new TestSuiteLog(TEST_SUITE_NAME_ONE);
        final TestSuiteLog testSuiteTwo = new TestSuiteLog(TEST_SUITE_NAME_TWO);
        testSuiteTwo.addTestCase(createTestCasewithTestStep("testGetTestCases", 1, 1, 0, 0, false));
        testSuiteOne.addTestSuite(testSuiteTwo);

        Iterable<TestCaseLog> testCaseIter;
        testCaseIter = testSuiteOne.getTestCases();
        // No TestCase is added until now
        for (TestCaseLog testCase4 : testCaseIter) {
            fail("No TestCase should be in TestSuite, but found " + testCase4);
        }
        // Adding some TestCases
        TestCaseLog testCaseOne = createTestCasewithTestStep("testGetTestCases", 1, 1, 0, 0, false);
        TestCaseLog testCaseTwo = createTestCasewithTestStep("testGetTestCases", 1, 1, 0, 0, false);
        TestCaseLog testCaseThree = createTestCasewithTestStep("testGetTestCases", 1, 1, 0, 0, false);
        testSuiteOne.addTestCase(testCaseTwo);
        testSuiteOne.addTestCase(testCaseOne);
        testSuiteOne.addTestCase(testCaseThree);

        testCaseIter = testSuiteOne.getTestCases();
        // Check for every testCase if it is the right one
        int count = 0;
        for (TestCaseLog testCase4 : testCaseIter) {
            count = count + 1;
            switch (count) {
                case 1:
                    assertSame(testCaseTwo, testCase4);
                    break;
                case 2:
                    assertSame(testCaseOne, testCase4);
                    break;
                case 3:
                    assertSame(testCaseThree, testCase4);
                    break;
                default:
                    fail("Wrong count in Loop");
                    break;
            }
        }
        // Check if there are three TestCase found
        assertEquals(3, count);
    }

    @Test
    /**
     * Test the functionality of the function GetTestSuites which should return only the local
     * TestCases
     */
    public void testGetTestSuites() {
        final TestSuiteLog testSuiteOne = new TestSuiteLog(TEST_SUITE_NAME_ONE);
        final TestSuiteLog testSuiteTwo = new TestSuiteLog(TEST_SUITE_NAME_TWO);
        final TestSuiteLog testSuiteThree = new TestSuiteLog(TEST_SUITE_NAME_THREE);
        Iterable<TestSuiteLog> testSuiteIter;

        TestCaseLog testCaseOne = createTestCasewithTestStep("testGetTestSuites1", 1, 0, 0, 1, true);
        testSuiteTwo.addTestCase(testCaseOne);
        TestCaseLog testCaseTwo = createTestCasewithTestStep("testGetTestSuites2", 1, 0, 0, 1, true);
        testSuiteThree.addTestCase(testCaseTwo);

        testSuiteIter = testSuiteOne.getTestSuites();
        // No TestSuite was added until now
        for (TestSuiteLog testSuite1 : testSuiteIter) {
            fail("No TestCase should be in TestSuite, but found " + testSuite1);
        }
        testSuiteOne.addTestSuite(testSuiteTwo);
        testSuiteOne.addTestSuite(testSuiteThree);
        testSuiteIter = testSuiteOne.getTestSuites();

        // Check for every testSuite if it is the right one
        int count = 0;
        for (TestSuiteLog testSuite1 : testSuiteIter) {
            count = count + 1;
            switch (count) {
                case 1:
                    assertSame(testSuiteTwo, testSuite1);
                    break;
                case 2:
                    assertSame(testSuiteThree, testSuite1);
                    break;
                default:
                    fail("Wrong count in Loop");
                    break;
            }
        }
        assertEquals(2, count);
    }

    /** HELP Functions */

    /**
     * Creates TestSuites
     * 
     * @param int = Number of TestSuites
     * @param prefix
     *            = Prefix to add to create uniqueness
     * @return List of TestSuites
     */
    private static List<TestSuiteLog> createTestSuites(int num, final String prefix) {
        final ArrayList<TestSuiteLog> testSuites = new ArrayList<TestSuiteLog>(num);
        for (int i = 0; i < num; ++i) {
            testSuites.add(new TestSuiteLog(prefix + TEST_SUITE_NAME_ONE + i));
        }
        return testSuites;
    }

    /** Creates a TestStep with Status OK */
    private TestStepLog createPassedTestStep(TestCaseLog testCase) {
        return createTestStep(TestStatus.PASSED, "OK", testCase);
    }

    /** Creates a TestStep with Status ERROR */
    private TestStepLog createTestStepWithError(TestCaseLog testCase) {
        return createTestStep(TestStatus.FAILED, "ERROR", testCase);
    }

    /** Creates a TestStep with Status {@link TestStatus#INCONCLUSIVE}  */
    private TestStepLog createTestStepWithException(TestCaseLog testCase) {
        return createTestStep(TestStatus.INCONCLUSIVE, "Inconclusive", testCase);
    }

    /** Encapsulate the creation of the TestSteps
     * @param testCase */
    private TestStepLog createTestStep(TestStatus status, String comment, TestCaseLog testCase) {
        return testCase.newTestStep(status, "Command", "This is the " + comment + " TestStep");
    }

    /**
     * Creates a TestCase with (in the following order)
     * 
     * @param beforeOK
     *            number of TestStep before the TestSteptoAdd with Status OK
     * @param beforeError
     *            number of TestStep before TestSteptoAdd with Status ERROR
     * @param afterOK
     *            number of TestStep after the TestSteptoAdd with Status OK
     * @param afterError
     *            number of TestStep after the TestSteptoAdd with Status ERROR
     * @param testSteptoAdd
     *            the individual TestStep
     * @return
     */
    public TestCaseLog createTestCasewithTestStep(String name, int beforeOK, int beforeError, int afterOK, int afterError, boolean addErrorStep) {
        TestCaseLog testCase = new TestCaseLog("automated TestCase");
        testCase.newTestStepGroup("automated Test Step Group");

        for (int i = 0; i < beforeOK; i++) {
            createPassedTestStep(testCase);
        }

        for (int i = 0; i < beforeError; i++) {
            createTestStepWithError(testCase);
        }

        if (addErrorStep) {
            createTestStepWithException(testCase);
        }

        for (int i = 0; i < afterOK; i++) {
            createPassedTestStep(testCase);
        }

        for (int i = 0; i < afterError; i++) {
            createTestStepWithError(testCase);
        }
        return testCase;
    }

    @Test(expected = ConfigurationError.class)
    public void testMultipleOccurenceOfSameChild() {
        TestSuiteLog suite = TestLogger.getTestSuite("root");
        TestSuiteLog child1 = TestLogger.getTestSuite("child1");
        suite.add(child1);
        TestSuiteLog child2 = TestLogger.getTestSuite("child2");
        suite.add(child2);
        TestCaseLog testCase = TestLogger.getTestCase("testCase");
        child1.add(testCase);
        child2.add(testCase);
    }
}
