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

import java.util.ArrayList;
import java.util.List;

import org.aludratest.testcase.TestStatus;
import org.joda.time.DateTime;

/**
 * A TestCase provides a List of TestSteps which are combined in this TestCase
 * Context of a TestCase: A TestCase contains many TestSteps, which will be
 * performed if this TestCase is used. So a TestCase aggregates a bunch of
 * TestSteps in his TestStepGroups.
 */
public class TestCaseLog extends TestSuiteLogComponent {

    private boolean ignored;

    State state;

    private DateTime startingTime;
    private DateTime finishingTime;

    /** List of TestStepGroups which contains the TestSteps */
    private List<TestStepGroup> groups = new ArrayList<TestStepGroup>();

    /** Constructor which just calls the superclass
     *  @param name Name of the TestCase */
    protected TestCaseLog(String name) {
        super(name);
        this.state = State.NEW;
    }

    // TestStepGroup handling --------------------------------------------------

    /** @return the TestStepGroups Array as Iterable */
    public List<TestStepGroup> getTestStepGroups() {
        return groups;
    }

    /** Creates a new TestStepGroup with the given name
     *  @param name Name of the TestStepGroup
     *  @return TestStepGroup with the given name */
    public TestStepGroup newTestStepGroup(String name) {
        TestStepGroup testStepGroup = new TestStepGroup(name, this);
        startIfNecessary();
        groups.add(testStepGroup);
        return testStepGroup;
    }

    // TestStep handling -------------------------------------------------------

    /**
     * Creates a new TestStep and adds it to the current {@link TestStepGroup}.
     * Don't forget to call {@link #newTestStepGroup(String)} before you add the
     * first TestStep.
     * @return TestStep which was created
     * @throws IllegalStateException if no TestStepGroup exists
     */
    public TestStepLog newTestStep() {
        return newTestStep(null, null, null);
    }

    /**
     * Creates a new TestStep.
     * @param status TestStatus of the TestStep
     * @param command Command of the TestStep
     * @param comment Comment of the TestSTep
     * @return TestStep which was created
     */
    public TestStepLog newTestStep(TestStatus status, String command, String comment) {
        if (groups.isEmpty()) {
            throw new IllegalStateException("Tried to add a TestStep to a TestCase without creating a TestStepGroup first.");
        }
        TestStepGroup currentGroup = groups.get(groups.size() - 1);
        TestStepLog testStep = currentGroup.newTestStep(status, command, comment);
        testStep.start();
        return testStep;
    }


    // TestCase result retrieval -----------------------------------------------

    /** Returns the number of all TestSteps in the TestStepGroups */
    @Override
    public int getNumberOfTestSteps() {
        int numberOfTestSteps = getNumberOfTestSteps(groups);
        return numberOfTestSteps;
    }

    /** Returns the total number of failed TestSteps in the TestStepGroups
     *  @return the total number of failed TestSteps in the TestStepGroups */
    public int getNumberOfFailedTestSteps() {
        int result = 0;
        for (TestStepGroup group : groups) {
            result += group.getNumberOfFailedTestSteps();
        }
        return result;
    }

    /** @return the last test step that has a failure state. */
    public TestStepLog getLastFailed() {
        for (int i = groups.size() - 1; i >= 0; i--) {
            TestStepGroup group = groups.get(i);
            TestStepLog lastFailed = group.getLastFailed();
            if (lastFailed != null) {
                return lastFailed;
            }
        }
        return null;
    }

    /** @return the first TestStep of the first TestStepGroup */
    public TestStepLog getFirstTestStep() {
        if (groups.size() > 0) {
            return groups.get(0).getFirstTestStep();
        } else {
            return null;
        }
    }

    /** @return the last TestStep of the last TestStepGroup */
    public TestStepLog getLastTestStep() {
        if (groups.size() > 0) {
            return groups.get(groups.size() - 1).getLastTestStep();
        } else {
            return null;
        }
    }

    // lifecycle operations ----------------------------------------------------

    /** Marks the test case to be ignored */
    public void ignore() {
        this.ignored = true;
    }

    /** Tells if the test shall be ignored
     *  @return true if the test shall be ignored, otherwise false */
    public boolean isIgnored() {
        return ignored;
    }

    /** @return true if the test case is ignored and passed, otherwise false */
    public boolean isIgnoredAndPassed() {
        return (ignored && this.state == State.FINISHED && getLastFailed() == null);
    }

    /** @return true if the test case is ignored and failed, otherwise false */
    public boolean isIgnoredAndFailed() {
        return (ignored && this.state == State.FINISHED && getLastFailed() != null);
    }

    /** Sets the test case to RUNNING state and notifies the parent suite. */
    public void start() {
        assertState(State.NEW);
        this.startingTime = new DateTime();
        this.state = State.RUNNING;
        if (parent != null) {
            parent.startingChild(this);
        }
    }

    /** Sets the test case to FINISHED state and notifies the parent suite. */
    public void finish() {
        assertState(State.RUNNING);
        this.finishingTime = new DateTime();
        this.state = State.FINISHED;
        if (parent != null) {
            parent.finishedChild(this);
        }
    }

    /** Tells if the test case has finished. */
    @Override
    public boolean isFinished() {
        return (this.finishingTime != null);
    }

    /** Resets the test case. */
    public void clear() {
        groups.clear();
        this.state = State.NEW;
    }

    /** @return an Iterable of {@link TestStepGroup}s of this {@link TestCaseLog} */
    public Iterable<TestStepGroup> getTestSuites() {
        return groups;
    }

    /** Returns the time point at which the test case was started. */
    @Override
    public DateTime getStartingTime() {
        return startingTime;
    }

    /** Returns the time point at which the test case has started. */
    @Override
    public DateTime getFinishingTime() {
        return finishingTime;
    }

    /** Returns the the {@link TestStatus} of the test case. */
    @Override
    public TestStatus getStatus() {
        switch (this.state) {
            case NEW:
                return TestStatus.PENDING;
            case RUNNING:
                return TestStatus.RUNNING;
            case FINISHED:
                if (ignored) {
                    return TestStatus.IGNORED;
                } else if (getLastFailed() == null) {
                    return TestStatus.PASSED;
                } else {
                    return getLastFailed().getStatus();
                }
            default:
                throw new IllegalStateException("Not a supported state: " + state);
        }
    }

    /** Checks if the test case is in state RUNNING. If yes, it finishes,
     *  otherwise it checks if the state change is legal and sets the state to RUNNING. */
    private void startIfNecessary() {
        switch (this.state) {
            case NEW:
                start();
                break;
            case RUNNING:
                break;
            case FINISHED:
                throw new IllegalStateException("TestCase already finished");
            default:
                throw new IllegalStateException("Not a supported state: " + state);
        }
    }

    /** Asserts that the test case is in a certain life cycle state.
     * If yes, it returns silently, otherwise an {@link IllegalStateException} is thrown. */
    private void assertState(State state) {
        if (this.state != state) {
            throw new IllegalStateException("Expected state " + state + ", but found " + this.state);
        }
    }

    /** The possible life cycle states of the test case. */
    static enum State {
        NEW, RUNNING, FINISHED
    }

}
