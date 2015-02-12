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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.Collection;

import org.aludratest.impl.log4testing.data.TestCaseLog;
import org.aludratest.impl.log4testing.data.TestLogger;
import org.aludratest.impl.log4testing.data.TestSuiteLog;
import org.junit.Test;

/**
 * Tests the {@link TestLogger}.
 */
@SuppressWarnings("javadoc")
public class TestLoggerTest {

    private static final String TEST_SUITE_NAME = "My TestSuite";
    private static final String TEST_CASE_NAME = "My TestCase";

    private void assertSameAndNotNull(Object object, final Object sameObject) {
        assertNotNull(object);
        assertSame(object, sameObject);
    }

    @Test
    public void testGetTestSuiteString() {
        final TestSuiteLog testSuite = TestLogger.getTestSuite(TEST_SUITE_NAME);
        final TestSuiteLog sameTestSuite = TestLogger.getTestSuite(TEST_SUITE_NAME);
        assertSameAndNotNull(testSuite, sameTestSuite);
    }

    @Test
    public void testGetTestSuiteClassOfQ() {
        final TestSuiteLog testSuite = TestLogger.getTestSuite(Integer.class);
        final TestSuiteLog sameTestSuite = TestLogger.getTestSuite(Integer.class);
        assertSameAndNotNull(testSuite, sameTestSuite);
    }

    @Test
    public void testGetTestSuiteObject() {
        final TestSuiteLog testSuite = TestLogger.getTestSuite(new String());
        final TestSuiteLog sameTestSuite = TestLogger.getTestSuite(new String());
        assertSameAndNotNull(testSuite, sameTestSuite);
    }

    @Test
    public void testGetTestSuites() {
        final Collection<TestSuiteLog> testSuites = TestLogger.getTestSuites();
        final Collection<TestSuiteLog> sameTestSuites = TestLogger.getTestSuites();
        assertSameAndNotNull(testSuites, sameTestSuites);
    }

    @Test
    public void testGetTestCaseString() {
        final TestCaseLog testCase = TestLogger.getTestCase(TEST_CASE_NAME);
        final TestCaseLog sameTestCase = TestLogger.getTestCase(TEST_CASE_NAME);
        assertSameAndNotNull(testCase, sameTestCase);
    }

    @Test
    public void testGetTestCaseClassOfQ() {
        final TestCaseLog testCase = TestLogger.getTestCase(Integer.class);
        final TestCaseLog sameTestCase = TestLogger.getTestCase(Integer.class);
        assertSameAndNotNull(testCase, sameTestCase);
    }

    @Test
    public void testGetTestCases() {
        final Collection<TestCaseLog> testCases = TestLogger.getTestCases();
        final Collection<TestCaseLog> sameTestCases = TestLogger.getTestCases();
        assertSameAndNotNull(testCases, sameTestCases);
    }

    @Test
    public void testGetTestCaseObject() {
        final TestCaseLog testCase = TestLogger.getTestCase(new String());
        final TestCaseLog sameTestCase = TestLogger.getTestCase(new String());
        assertSameAndNotNull(testCase, sameTestCase);
    }

}
