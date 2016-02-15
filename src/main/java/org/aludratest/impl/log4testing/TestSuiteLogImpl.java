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
package org.aludratest.impl.log4testing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.aludratest.log4testing.TestCaseLog;
import org.aludratest.log4testing.TestLogElement;
import org.aludratest.log4testing.TestStatus;
import org.aludratest.log4testing.TestSuiteLog;
import org.aludratest.log4testing.TestSuiteStatistics;
import org.aludratest.log4testing.util.DefaultTestSuiteStatistics;
import org.joda.time.Duration;

/** Implementation of the TestSuiteLog interface. This implementation is Thread-safe.
 * 
 * @author falbrech */
public final class TestSuiteLogImpl extends AbstractNamedTestLogElementImpl implements TestSuiteLog {

    private TestSuiteLogImpl parent;

    private List<TestSuiteLogImpl> childSuites = new ArrayList<TestSuiteLogImpl>();

    private List<TestCaseLogImpl> testCases = new ArrayList<TestCaseLogImpl>();

    /** Constructs a new TestSuiteLogImpl object with the given name.
     * 
     * @param name Name of the suite. */
    public TestSuiteLogImpl(String name) {
        super(name);
    }

    /** Constructs a new TestSuiteLogImpl object with the given name and the given parent.
     * 
     * @param name Name of the suite.
     * @param parent Parent of the suite. This suite will add itself to the parent. */
    public TestSuiteLogImpl(String name, TestSuiteLogImpl parent) {
        this(name);
        this.parent = parent;
        parent.addChildSuite(this);
    }

    /** Adds a child suite log to this suite.
     * 
     * @param suite Suite log to add, must not be <code>null</code>. */
    public synchronized void addChildSuite(TestSuiteLogImpl suite) {
        if (suite == null) {
            throw new IllegalArgumentException("suite must not be null");
        }
        childSuites.add(suite);
    }

    /** Adds a test case log to this suite.
     * 
     * @param testCase Test Case log to add, must not be <code>null</code>. */
    public synchronized void addTestCase(TestCaseLogImpl testCase) {
        if (testCase == null) {
            throw new IllegalArgumentException("testCase must not be null");
        }
        testCases.add(testCase);
    }

    @Override
    public Duration getWork() {
        // sum up durations of test cases and work of child suites.
        Duration result = new Duration(0);
        for (TestSuiteLog suite : getChildSuites()) {
            result = result.plus(suite.getWork());
        }

        for (TestCaseLog testCase : getTestCases()) {
            result = result.plus(testCase.getDuration());
        }

        return result;
    }

    @Override
    public TestStatus getStatus() {
        List<TestLogElement> children = new ArrayList<TestLogElement>(testCases);
        children.addAll(childSuites);
        return getStatus(children);
    }

    @Override
    public TestSuiteLogImpl getParent() {
        return parent;
    }

    @Override
    public synchronized List<? extends TestSuiteLog> getChildSuites() {
        return Collections.unmodifiableList(new ArrayList<TestSuiteLogImpl>(childSuites));
    }

    @Override
    public synchronized List<? extends TestCaseLog> getTestCases() {
        return Collections.unmodifiableList(new ArrayList<TestCaseLogImpl>(testCases));
    }

    @Override
    public TestSuiteStatistics gatherStatistics() {
        return DefaultTestSuiteStatistics.create(this);
    }

}
