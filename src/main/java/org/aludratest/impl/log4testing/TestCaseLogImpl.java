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
import org.aludratest.log4testing.TestStatus;
import org.aludratest.log4testing.TestStepGroupLog;
import org.aludratest.log4testing.TestStepLog;
import org.joda.time.Duration;

/** Implementation of the TestCaseLog interface. This class is Thread-safe.
 * 
 * @author falbrech */
public final class TestCaseLogImpl extends AbstractNamedTestLogElementImpl implements TestCaseLog {

    private TestSuiteLogImpl parent;

    private volatile boolean ignored; // NOSONAR 'volatile' is use by purpose here

    private volatile String ignoredReason; // NOSONAR 'volatile' is use by purpose here

    private List<TestStepGroupLogImpl> groups = new ArrayList<TestStepGroupLogImpl>();

    /** Constructs a new TestCaseLogImpl object with the given name and parent suite.
     * 
     * @param name Name of the Test Case.
     * @param parent Parent suite of the Test Case. The new object will be added to the parent automatically. */
    public TestCaseLogImpl(String name, TestSuiteLogImpl parent) {
        super(name);
        this.parent = parent;
        parent.addTestCase(this);
    }

    /** Adds a new test step group log to this test case log.
     * 
     * @param group Test step group log to add. Must not be <code>null</code>. */
    public synchronized void addTestStepGroup(TestStepGroupLogImpl group) {
        if (group == null) {
            throw new IllegalArgumentException("group must not be null");
        }
        groups.add(group);
    }

    @Override
    public Duration getWork() {
        return getDuration();
    }

    @Override
    public TestStatus getStatus() {
        if (getStartTime() == null) {
            return TestStatus.PENDING;
        }

        if (getEndTime() == null) {
            return TestStatus.RUNNING;
        }

        if (ignored) {
            return TestStatus.IGNORED;
        }

        TestStepLog step = getLastFailedStep();
        if (step == null) {
            return TestStatus.PASSED;
        }

        return step.getStatus();
    }

    @Override
    public TestSuiteLogImpl getParent() {
        return parent;
    }

    @Override
    public synchronized boolean isIgnored() {
        return ignored;
    }

    /** Sets the ignored flag for this test case log.
     * 
     * @param ignored New ignored flag. */
    public void setIgnored(boolean ignored) {
        this.ignored = ignored;
    }

    @Override
    public String getIgnoredReason() {
        return ignoredReason;
    }

    /** Sets the reason for ignore for this test case log.
     * 
     * @param ignoredReason The reason for ignore for this test case log. */
    public void setIgnoredReason(String ignoredReason) {
        this.ignoredReason = ignoredReason;
    }

    @Override
    public synchronized List<TestStepGroupLogImpl> getTestStepGroups() {
        return Collections.unmodifiableList(new ArrayList<TestStepGroupLogImpl>(groups));
    }

    @Override
    public TestStepLog getLastFailedStep() {
        // optimized - go backwards
        List<? extends TestStepGroupLog> testGroups = getTestStepGroups();
        for (int i = testGroups.size() - 1; i >= 0; i--) {
            List<? extends TestStepLog> testSteps = testGroups.get(i).getTestSteps();
            for (int s = testSteps.size() - 1; s >= 0; s--) {
                TestStepLog step = testSteps.get(s);
                if (step.getStatus() != null && step.getStatus().isFailure()) {
                    return step;
                }
            }
        }
        return null;
    }

    @Override
    public boolean isFailed() {
        return getLastFailedStep() != null;
    }

    @Override
    public int getNumberOfTestSteps() {
        int counter = 0;
        for (TestStepGroupLog group : getTestStepGroups()) {
            counter += group.getTestSteps().size();
        }

        return counter;
    }

}
