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

import org.aludratest.impl.log4testing.data.TestCaseLog;
import org.aludratest.impl.log4testing.data.TestLogger;
import org.aludratest.impl.log4testing.data.TestStepLog;
import org.aludratest.impl.log4testing.data.TestSuiteLog;
import org.aludratest.testcase.TestStatus;
import org.junit.Test;

/**
 * Executes some example steps.
 */
@SuppressWarnings("javadoc")
public class Example extends junit.framework.TestCase {

    @Test
    public void test() throws Exception {

        // create test suites and test cases
        TestSuiteLog parentSuite = TestLogger.getTestSuite("My TestSuite");
        TestCaseLog testCase = TestLogger.getTestCase("example.TestCase");
        parentSuite.addTestCase(testCase);

        TestSuiteLog childSuite = TestLogger.getTestSuite("My TestSuite2");
        TestCaseLog testCase2 = TestLogger.getTestCase("example.TestCase2");
        childSuite.addTestCase(testCase2);
        parentSuite.addTestSuite(childSuite);

        // run tests
        testCase.start();
        Thread.sleep(100);
        testCase.newTestStepGroup("Create");
        createOkTestStep(testCase);

        Thread.sleep(100);
        createErrorTestStep(testCase);
        Thread.sleep(100);
        testCase.finish();

        Thread.sleep(100);

        testCase2.start();
        testCase2.newTestStepGroup("Create");
        createOkTestStep(testCase2);
        Thread.sleep(100);
        createErrorTestStep(testCase2);
        Thread.sleep(100);
        testCase2.finish();
    }

    private static TestStepLog createErrorTestStep(TestCaseLog testCase) {
        return testCase.newTestStep(TestStatus.FAILED, "errorCommand", "An error occurred.");
    }

    private static TestStepLog createOkTestStep(TestCaseLog testCase) {
        return testCase.newTestStep(TestStatus.PASSED, "okCommand", "This test step is ok.");
    }

}
