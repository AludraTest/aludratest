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
 * Represents a Group of TestSteps which could be identified by the name.
 *  
 * @author Marcel Malitz
 * @author Volker Bergmann
 */
public class TestStepGroup extends TestStepContainer {

    /**
     * Array which contains all TestSteps
     */
    private List<TestStepLog> testSteps = new ArrayList<TestStepLog>();
    private TestCaseLog parent;

    /** Constructor.
     *  @param name the name of the test step group 
     *  @param parent the parent {@link TestCaseLog} */
    protected TestStepGroup(String name, TestCaseLog parent) {
        super(name);
        this.parent = parent;
    }

    /** @return the {@link #parent} */
    public TestCaseLog getParent() {
        return parent;
    }

    /** @return the number of TestSteps */
    @Override
    public int getNumberOfTestSteps() {
        return testSteps.size();
    }

    /** @return the number of Failed TestSteps  */
    public int getNumberOfFailedTestSteps() {
        int numberOfFailed = 0;
        for (TestStepLog testStep : testSteps) {
            if (testStep.isFailed()) {
                ++numberOfFailed;
            }
        }
        return numberOfFailed;
    }

    /**
     * Adds a TestStep to the Array of TestSteps
     * @return a reference to the TestStepGroup itself
     */
    public TestStepLog newTestStep() {
        TestStepLog testStep = new TestStepLog(this);
        this.testSteps.add(testStep);
        return testStep;
    }

    /**
     * Creates a new test step and adds it to this group.
     * @param status
     * @param command
     * @param comment
     * @return the new test step
     */
    public TestStepLog newTestStep(TestStatus status, String command, String comment) {
        TestStepLog testStep = newTestStep();
        testStep.setCommand(command);
        testStep.setComment(comment);
        testStep.setStatus(status);
        return testStep;
    }

    public void newTestStep(TestStepLog testStep) {
        this.testSteps.add(testStep);
        testStep.start();
        testStep.finish();
    }

    /** @return the {@link #testSteps}  */
    public List<TestStepLog> getTestSteps() {
        return testSteps;
    }

    /** @return the last failed step of the group. */
    public TestStepLog getLastFailed() {
        for (int i = testSteps.size() - 1; i >= 0; i--) {
            TestStepLog step = testSteps.get(i);
            if (step.isFailed()) {
                return step;
            }
        }
        return null;
    }

    /** Returns the first TestStep 
     *  @return the first TestStep*/
    public TestStepLog getFirstTestStep() {
        return getTestStep(0);
    }

    /** Returns the last TestStep 
     *  @return the last TestStep */
    public TestStepLog getLastTestStep() {
        return getTestStep(testSteps.size() - 1);
    }

    /**
     * Returns the TestStep at the given position
     * @param index the position of the TestStep
     * @return TestStep at the Position or <code> null </code> if not found
     */
    public TestStepLog getTestStep(int index) {
        if (testSteps.size() > index && index >= 0) {
            return testSteps.get(index);
        } else {
            return null;
        }
    }

    @Override
    public DateTime getStartingTime() {
        if (testSteps.size() > 0) {
            return testSteps.get(0).getStartingTime();
        } else {
            return null;
        }
    }

    @Override
    public DateTime getFinishingTime() {
        if (testSteps.size() > 0) {
            return testSteps.get(testSteps.size() - 1).getFinishingTime();
        } else {
            return null;
        }
    }

    @Override
    public TestStatus getStatus() {
        TestStepLog lastFailed = getLastFailed();
        if (lastFailed != null) {
            return lastFailed.getStatus();
        } else if (testSteps.size() == 0) {
            return TestStatus.PASSED;
        } else if (testSteps.get(0).getStatus().equals(TestStatus.IGNORED)) {
            return TestStatus.IGNORED;
        } else {
            return TestStatus.PASSED;
        }
    }

}
