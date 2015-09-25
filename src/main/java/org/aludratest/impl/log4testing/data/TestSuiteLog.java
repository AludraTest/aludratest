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
import java.util.concurrent.atomic.AtomicInteger;

import org.aludratest.impl.log4testing.observer.TestObserver;
import org.aludratest.impl.log4testing.observer.TestObserverManager;
import org.aludratest.testcase.TestStatus;
import org.joda.time.DateTime;
import org.joda.time.Duration;

/**
 * Class which represents a TestSuite. 
 * A TestSuite can contain TestCases and/or other TestSuites.
 * 
 * @author Marcel Malitz
 * @author Volker Bergmann
 */
public class TestSuiteLog extends TestSuiteLogComponent {

    /** Life cycle state of the test suite. */
    private TestSuiteState state;

    /** Contains all TestCases of the TestSuite. */
    private List<TestCaseLog> testCases = new ArrayList<TestCaseLog>();

    /** testSuites contains all testSuites of the TestSuite. */
    private List<TestSuiteLog> testSuites = new ArrayList<TestSuiteLog>();

    /** containers contains all TestSuites and TestCases. */
    private List<TestSuiteLogComponent> components = new ArrayList<TestSuiteLogComponent>();

    /** Counts the child components which are queued for execution. */
    private AtomicInteger pendingChildCount;

    /** The time point at which the suite has started execution. */
    private DateTime startingTime;

    /** The time point at which the suite has finished. */
    private DateTime finishingTime;

    /** Constructor.
     *  @param name the name of the TestSuite */
    protected TestSuiteLog(String name) {
        super(name);
        this.state = TestSuiteState.NEW;
        this.pendingChildCount = new AtomicInteger();
        this.startingTime = null;
        this.finishingTime = null;
    }

    /** @return the suite's child components */
    public List<TestSuiteLogComponent> getComponents() {
        return components;
    }

    /** Adds a child component to the test suite. 
     *  @param child the {@link TestSuiteLogComponent} to add */
    public void add(TestSuiteLogComponent child) {
        if (child == null) {
            throw new IllegalArgumentException("child is null");
        } else if (child instanceof TestSuiteLog) {
            addTestSuite((TestSuiteLog) child);
        } else if (child instanceof TestCaseLog) {
            addTestCase((TestCaseLog) child);
        } else {
            throw new IllegalArgumentException("Not a supported TestSuite component: " + child.getClass());
        }

    }

    /** Adds a TestSuite to the testSuites and to the containers
     *  @param childSuite the TestSuite to add */
    public void addTestSuite(TestSuiteLog childSuite) {
        if (childSuite == null) {
            throw new IllegalArgumentException("childSuite is null");
        }
        beUnderConstruction();
        checkChildSuiteToAdd(childSuite);
        childSuite.setParent(this);
        testSuites.add(childSuite);
        components.add(childSuite);
        pendingChildCount.incrementAndGet();
    }

    /** Adds a TestCase to testCases and to containers
     *  @param testCase */
    public void addTestCase(TestCaseLog testCase) {
        if (testCase == null) {
            throw new IllegalArgumentException("testCase is null");
        }
        beUnderConstruction();
        testCase.setParent(this);
        testCases.add(testCase);
        components.add(testCase);
        pendingChildCount.incrementAndGet();
    }

    /** @return the complete number of failed test steps aggregated recursively 
     *  over the complete sub tree of child components. */
    public int getNumberOfFailedTestCases() {
        int count = 0;
        for (TestSuiteLog child : testSuites) {
            count += child.getNumberOfFailedTestCases();
        }
        for (TestCaseLog child : testCases) {
            if (child.isFailed()) {
                count++;
            }
        }
        return count;
    }

    /** @return the recursive count of passed test cases */
    public int getNumberOfPassedTestCases() {
        return countTestCasesInStatus(TestStatus.PASSED);
    }

    /** @return the complete number of functionally failed test cases aggregated recursively 
     *  over the complete sub tree of child components. */
    public int getNumberOfFunctionallyFailedTestCases() {
        return countTestCasesInStatus(TestStatus.FAILED);
    }

    /** @return the complete number of technically failed test cases aggregated recursively 
     *  over the complete sub tree of child components. */
    public int getNumberOfAutomationFailedTestCases() {
        return countTestCasesInStatus(TestStatus.FAILEDAUTOMATION);
    }

    /** @return the complete number of inconclusive test cases aggregated recursively 
     *  over the complete sub tree of child components. */
    public int getNumberOfInconclusiveTestCases() {
        return countTestCasesInStatus(TestStatus.INCONCLUSIVE);
    }

    /** @return the complete number of nonresposive test cases aggregated recursively 
     *  over the complete sub tree of child components. */
    public int getNumberOfFailedPerformanceTestCases() {
        return countTestCasesInStatus(TestStatus.FAILEDPERFORMANCE);
    }

    /** @return the complete number of test cases with access failures, 
     *      aggregated recursively over the complete sub tree of child components. */
    public int getNumberOfFailedAccessTestCases() {
        return countTestCasesInStatus(TestStatus.FAILEDACCESS);
    }

    /** Aggregates the complete number of test cases with status 
     *  {@link TestStatus#IGNORED} recursively 
     *  over the complete sub tree of child components. 
     *  @return the number of ignored test cases */
    public int getNumberOfIgnoredTestCases() {
        return countTestCasesInStatus(TestStatus.IGNORED);
    }

    /** @return the complete number of test cases aggregated recursively 
     *  over the complete sub tree of child components. */
    public int getNumberOfTestCases() {
        int count = 0;
        for (int j = 0; j < testSuites.size(); j++) {
            count = count + testSuites.get(j).getNumberOfTestCases();
        }
        return testCases.size() + count;
    }

    /** @return the number of direct child suites */
    public int getNumberOfChildSuites() {
        return testSuites.size();
    }

    /** @return the recursive count of test cases that were ignored and have passed. */
    public int getNumberOfIgnoredAndPassedTestCases() {
        int count = 0;
        for (TestSuiteLog child : testSuites) {
            count += child.getNumberOfIgnoredAndPassedTestCases();
        }
        for (TestCaseLog child : testCases) {
            if (child.isIgnoredAndPassed()) {
                count++;
            }
        }
        return count;
    }

    /** @return the recursive count of test cases that were ignored and have failed. */
    public int getNumberOfIgnoredAndFailedTestCases() {
        int count = 0;
        for (TestSuiteLog child : testSuites) {
            count += child.getNumberOfIgnoredAndFailedTestCases();
        }
        for (TestCaseLog child : testCases) {
            if (child.isIgnoredAndFailed()) {
                count++;
            }
        }
        return count;
    }

    /** @return the complete number of test steps aggregated recursively 
     *  over the complete sub tree of child components. */
    @Override
    public int getNumberOfTestSteps() {
        return getNumberOfTestSteps(components);
    }

    /** @return the List of testCases as Iterable */
    public Iterable<TestCaseLog> getTestCases() {
        return testCases;
    }

    /**  @return the List of TestSuites as Iterable */
    public Iterable<TestSuiteLog> getTestSuites() {
        return testSuites;
    }

    /** Resets the state of the test suite to empty and uninitialized */
    public void clear() {
        components.clear();
        testCases.clear();
        testSuites.clear();
        this.state = TestSuiteState.NEW;
    }

    /** @return the time point at which the first child component started. */
    @Override
    public DateTime getStartingTime() {
        return startingTime;
    }

    /** @return the time point at which the last child component started. */
    @Override
    public DateTime getFinishingTime() {
        return finishingTime;
    }

    /** @return the aggregate sum of all test case durations reported by all recursive child components. */
    public Duration getWork() {
        long millis = 0;
        for (TestSuiteLog childSuite : testSuites) {
            millis += childSuite.getWork().getMillis();
        }
        for (TestCaseLog childCase : testCases) {
            millis += childCase.getDuration().getMillis();
        }
        return new Duration(millis);
    }

    /** @return the status of the test suite */
    @Override
    public TestStatus getStatus() {
        TestStatus status = TestStatus.PASSED;
        for (int i = 0; i < components.size(); i++) {
            TestStepContainer tsc = components.get(i);
            if (tsc.getStatus().ordinal() < status.ordinal()) {
                status = tsc.getStatus();
            }
        }
        return status;
    }

    // callback methods for life cycle management -------------------------------------

    /** Callback method invoked by each child component, when the child is starting.
     *  The method propagates state information to {@link TestObserver}s using the 
     *  {@link TestObserverManager}. 
     *  @param child */
    public void startingChild(TestSuiteLogComponent child) {
        // check state precondition
        switch (state) {
            case NEW:
                throw new IllegalStateException("Child started in empty test suite");
            case UNDER_CONSTRUCTION:
                // set state and startingTime appropriately
                this.startingTime = new DateTime();
                this.state = TestSuiteState.RUNNING;
                // notify parent
                if (parent != null) {
                    parent.startingChild(this);
                }
                // notify test observers
                notifySuiteStarting();
                break;
            case RUNNING:
                break;
            case FINISHED:
                throw new IllegalStateException("Child " + child + " started" + " in finished suite: " + this);
            default:
                throw new IllegalStateException("Not a supported state: " + state);
        }
        // send notifications
        if (child instanceof TestCaseLog) {
            TestObserverManager.getInstance().notifyStartingTestCase((TestCaseLog) child);
        }
    }

    /** Callback method invoked by each child component, when the child has finished.
     *  The method propagates state information to {@link TestObserver}s using the 
     *  {@link TestObserverManager}. 
     *  @param child the {@link TestSuiteLogComponent} to add */
    public void finishedChild(TestSuiteLogComponent child) {
        // check state precondition
        switch (state) {
            case NEW:
                throw new IllegalStateException("Child finished in empty test suite");
            case UNDER_CONSTRUCTION:
                throw new IllegalStateException("Child finished in empty test suite");
            case RUNNING:
                break;
            case FINISHED:
                throw new IllegalStateException("TestSuite has already been finished");
            default:
                throw new IllegalStateException("Not a supported state: " + state);
        }

        // notify observers in case of a test case
        if (child instanceof TestCaseLog) {
            TestObserverManager.getInstance().notifyFinishedTestCase((TestCaseLog) child);
        }

        // update pendingChildCount 
        int pending = pendingChildCount.decrementAndGet();

        // if all children have finished, set this suites state to closed and 
        // inform observers as well as the (optional) parent suite
        if (pending == 0) {
            // set state
            this.state = TestSuiteState.FINISHED;
            this.finishingTime = new DateTime();
            notifySuiteFinished();
            if (parent != null) {
                parent.finishedChild(this);
            }
        }
    }

    /** Handles an empty test suite by starting and immediately closing it. */
    public void startAndFinishEmpty() {
        // check preconditions
        if (getNumberOfTestCases() > 0) {
            throw new IllegalStateException("Called startAndFinishEmpty() on a suite that is not empty: " + this);
        }

        // start the suite
        this.startingTime = new DateTime();
        this.state = TestSuiteState.RUNNING;
        if (parent != null) {
            parent.startingChild(this);
        }
        notifySuiteStarting();

        // finish the suite
        state = TestSuiteState.FINISHED;
        this.finishingTime = new DateTime();
        notifySuiteFinished();
        if (parent != null) {
            parent.finishedChild(this);
        }
    }

    /** Tells if the test suite was finished. */
    @Override
    public boolean isFinished() {
        return (this.state == TestSuiteState.FINISHED);
    }

    // private methods ---------------------------------------------------------

    /** Sends a notification that the test suite was started. */
    private void notifySuiteStarting() {
        TestObserverManager oberverManager = TestObserverManager.getInstance();
        if (parent == null) {
            oberverManager.notifyStartingTestProcess(this);
        }
        oberverManager.notifyStartingTestSuite(this);
    }

    /** Sends a notification that the test suite has started. */
    private void notifySuiteFinished() {
        TestObserverManager.getInstance().notifyFinishedTestSuite(this);
        if (parent == null) {
            TestObserverManager.getInstance().notifyFinishedTestProcess(this);
        }
    }

    /** assumes that the lifecycle state is {@link TestSuiteState#UNDER_CONSTRUCTION} 
     *  and throws an {@link IllegalStateException} if that is not the case. */
    private void beUnderConstruction() {
        switch (state) {
        case NEW:
            state = TestSuiteState.UNDER_CONSTRUCTION;
            break;
        case UNDER_CONSTRUCTION:
            break;
        default:
            throw new IllegalStateException("No children may be added after starting a suite");
        }
    }

    /** Avoids that a TestSuite could add itself and avoids that a circle between
     * some TestSuites can be created. */
    private void checkChildSuiteToAdd(TestSuiteLog childSuite) {
        TestSuiteLog tmp = this;
        do {
            if (tmp.getName().equals(childSuite.getName())) {
                throw new IllegalParentException();
            }
            tmp = tmp.getParent();
        } while (tmp != null);
    }

    /** Aggregates the complete number of test cases that have the specified status 
     *  recursively over the complete sub tree of child components. */
    private int countTestCasesInStatus(TestStatus status) {
        int count = 0;
        for (int i = 0; i < components.size(); i++) {
            TestStepContainer tsc = components.get(i);
            if (tsc instanceof TestSuiteLog) {
                count = count + ((TestSuiteLog) tsc).countTestCasesInStatus(status);
            } else if (tsc.getStatus().equals(status)) {
                count++;
            }
        }
        return count;
    }

    /** States enumeration for the test suite life cycle. */
    static enum TestSuiteState {
        NEW, UNDER_CONSTRUCTION, RUNNING, FINISHED
    }

}
