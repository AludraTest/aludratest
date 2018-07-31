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
package org.aludratest.scheduler;

import java.util.List;
import java.util.Properties;

import org.aludratest.log4testing.TestCaseLog;
import org.aludratest.log4testing.TestSuiteLog;
import org.aludratest.log4testing.config.InvalidConfigurationException;
import org.aludratest.log4testing.output.LogWriterException;
import org.aludratest.log4testing.output.TestLogWriter;

/** A very simple "log writer" which only stores a reference to the full log in memory. Test classes deriving from
 * AbstractAludraServiceTest can access an instance of this class and query it for the log for a specific test case using
 * {@link #getTestCaseLog(String)}.
 * 
 * @author falbrech
 * @since 3.1.0 */
public class MemoryTestLog implements TestLogWriter {

    private TestSuiteLog rootSuite;

    /** @deprecated Don't use the singleton instance any longer */
    @Deprecated
    public static MemoryTestLog instance;

    /** @deprecated Do not call this directly. Is called by Log4Testing during integration tests. */
    @Deprecated
    public MemoryTestLog() {
        instance = this;
    }

    @Override
    public void init(Properties properties) throws InvalidConfigurationException {
    }

    @Override
    public void startingTestProcess(TestSuiteLog rootSuite) throws LogWriterException {
        this.rootSuite = rootSuite;
    }

    @Override
    public void startingTestSuite(TestSuiteLog suite) throws LogWriterException {
    }

    @Override
    public void startingTestCase(TestCaseLog testCase) throws LogWriterException {
    }

    @Override
    public void finishedTestCase(TestCaseLog testCase) throws LogWriterException {
    }

    @Override
    public void finishedTestSuite(TestSuiteLog suite) throws LogWriterException {
    }

    @Override
    public void finishedTestProcess(TestSuiteLog rootSuite) throws LogWriterException {
    }

    /** Returns the log for a given test case.
     * 
     * @param testCaseName Name of the test case.
     * @return The log for the test case, or <code>null</code> if no matching log was found. */
    public TestCaseLog getTestCaseLog(String testCaseName) {
        return getTestCaseLog(rootSuite, testCaseName);
    }

    /** Returns the root suite representing the overall test execution.
     * 
     * @return The root suite representing the overall test execution, or <code>null</code> if the test process has not yet
     *         started. */
    public TestSuiteLog getRootSuite() {
        return rootSuite;
    }

    /** Returns the last test case log in the tree. This is usually only useful if only ONE test case was to be executed.
     * 
     * @return The last test case log in the tree, or <code>null</code> if no test case is logged in the tree. */
    public TestCaseLog getLastTestCaseLog() {
        return getLastTestCaseLog(rootSuite);
    }

    private TestCaseLog getTestCaseLog(TestSuiteLog start, String testCaseName) {
        for (TestCaseLog log : start.getTestCases()) {
            if (testCaseName.equals(log.getName())) {
                return log;
            }
        }

        for (TestSuiteLog log : start.getChildSuites()) {
            TestCaseLog result = getTestCaseLog(log, testCaseName);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    private TestCaseLog getLastTestCaseLog(TestSuiteLog start) {
        List<? extends TestSuiteLog> suites = start.getChildSuites();
        if (!suites.isEmpty()) {
            return getLastTestCaseLog(suites.get(suites.size() - 1));
        }

        List<? extends TestCaseLog> logs = start.getTestCases();
        if (!logs.isEmpty()) {
            return logs.get(logs.size() - 1);
        }

        return null;

    }

}
