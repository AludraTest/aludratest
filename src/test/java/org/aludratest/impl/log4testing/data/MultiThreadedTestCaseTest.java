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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.aludratest.impl.log4testing.data.TestCaseLog;
import org.aludratest.impl.log4testing.data.TestLogger;
import org.aludratest.impl.log4testing.data.TestStepLog;
import org.aludratest.impl.log4testing.data.TestSuiteLog;
import org.aludratest.impl.log4testing.observer.ObserverTestUtil;
import org.aludratest.impl.log4testing.observer.PendingTestsObserver;
import org.aludratest.impl.log4testing.observer.TestObserverManager;
import org.aludratest.impl.log4testing.output.SuitePrinter;
import org.aludratest.testcase.TestStatus;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the {@link TestSuiteLog}'s state management and event handling for thread safety.
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public class MultiThreadedTestCaseTest {

    @Before
    public void clear() {
        TestLogger.clear();
        ObserverTestUtil.removeAllTestObservers();
        TestObserverManager.getInstance().addObserver(new PendingTestsObserver());
    }

    @Test
    public void testFlatStructureWithSlowTests() throws Exception {
        performTest(0, 0, 5, 500);
    }

    @Test
    public void testNested() throws Exception {
        performTest(2, 3, 3, 100);
    }

    @Test
    public void testComplexHighlyConcurrent() throws Exception {
        performTest(3, 4, 20, 0);
    }

    private void performTest(int hierarchyDepth, int childSuiteCount, int childCaseCount, int sleepTime) throws InterruptedException {
        TestSuiteLog root = new TestSuiteLog("root");
        List<TestCaseLog> testCases = createTestCases(root, hierarchyDepth, childSuiteCount, childCaseCount, new ArrayList<TestCaseLog>());
        SuitePrinter.print(root);
        List<Thread> threads = createThreads(testCases, sleepTime);
        runThreadsAndWait(threads);
        for (TestCaseLog testCase : testCases) {
            assertTrue(testCase.isFinished());
            assertFalse(testCase.getLastTestStep().getErrorMessage(), testCase.isFailed());
        }
    }

    private List<TestCaseLog> createTestCases(TestSuiteLog parent, int depth, int childSuiteCount, int childCaseCount, List<TestCaseLog> testCases) {
        for (int i = 0; i < childCaseCount; i++) {
            TestCaseLog childCase = TestLogger.getTestCase(parent.getName() + "-case_" + (i + 1));
            parent.add(childCase);
            testCases.add(childCase);
        }
        for (int i = 0; i < childSuiteCount; i++) {
            TestSuiteLog childSuite = TestLogger.getTestSuite(parent.getName() + "-suite_" + (i + 1));
            parent.add(childSuite);
            if (depth > 0)
                createTestCases(childSuite, depth - 1, childSuiteCount, childCaseCount, testCases);
        }
        return testCases;
    }

    private List<Thread> createThreads(List<TestCaseLog> testCases, int sleepTime) {
        List<Thread> threads = new ArrayList<Thread>(testCases.size());
        for (TestCaseLog testCase : testCases) {
            threads.add(new TestCaseThread(testCase, sleepTime));
        }
        return threads;
    }

    private void runThreadsAndWait(List<Thread> threads) throws InterruptedException {
        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
    }

    static class TestCaseThread extends Thread implements Thread.UncaughtExceptionHandler {

        private TestCaseLog testCase;
        private long sleepTime;

        private TestCaseThread(TestCaseLog testCase, long sleepTime) {
            this.testCase = testCase;
            this.sleepTime = sleepTime;
            setUncaughtExceptionHandler(this);
        }

        @Override
        public void run() {
            try {
                //System.out.println("Starting " + testCase);
                testCase.start();
                testCase.newTestStepGroup("g1");
                testCase.newTestStep();
                Thread.sleep(sleepTime);
                testCase.finish();
                //System.out.println("Finished " + testCase);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void uncaughtException(Thread thread, Throwable throwable) {
            TestStepLog lastStep = testCase.getLastTestStep();
            lastStep.setStatus(TestStatus.FAILEDAUTOMATION);
            lastStep.setErrorMessage("Exception occurred: " + throwable.toString());
        }
    }
}
