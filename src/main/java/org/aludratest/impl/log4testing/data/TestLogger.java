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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.aludratest.impl.log4testing.configuration.Log4TestingConfiguration;

/** 
 * Main facade class for log4testing, used for test suite and test case creation.
 * @author Marcel Malitz
 * @author Joerg Langnickel
 * @author Volker Bergmann
 */
public class TestLogger {

    private static final int INITIAL_TESTCASE_CAPACITY = 500;
    private static final int INITIAL_TESTSUITE_CAPACITY = 50;

    private static Map<String, TestSuiteLog> testSuites;
    private static Map<String, TestCaseLog> testCases;

    static {
        Log4TestingConfiguration.getInstance(); // assures that log4testing is initialized
        testSuites = new HashMap<String, TestSuiteLog>(INITIAL_TESTSUITE_CAPACITY);
        testCases = new HashMap<String, TestCaseLog>(INITIAL_TESTCASE_CAPACITY);
    }

    /** Private constructor of utility class preventing instantiation by other classes */
    private TestLogger() {
    }

    public static TestSuiteLog getTestSuite(Class<?> c) {
        return getTestSuite(c.getName());
    }

    public static TestSuiteLog getTestSuite(String name) {
        synchronized (testSuites) {
            TestSuiteLog testSuite = testSuites.get(name);
            if (testSuite == null) {
                testSuite = new TestSuiteLog(name);
                testSuites.put(name, testSuite);
            }
            return testSuite;
        }
    }

    public static TestCaseLog getTestCase(Class<?> c) {
        return getTestCase(c.getName());
    }

    public static TestCaseLog getTestCase(String name) {
        synchronized (testCases) {
            TestCaseLog testCase = testCases.get(name);
            if (testCase == null) {
                testCase = new TestCaseLog(name);
                testCases.put(name, testCase);
            }
            return testCase;
        }
    }

    public static void clear() {
        synchronized (testSuites) {
            testSuites.clear();
        }
        synchronized (testCases) {
            testCases.clear();
        }
    }

    // methods for testing -----------------------------------------------------

    static Collection<TestCaseLog> getTestCases() {
        return testCases.values();
    }

    static Collection<TestSuiteLog> getTestSuites() {
        return testSuites.values();
    }

}
