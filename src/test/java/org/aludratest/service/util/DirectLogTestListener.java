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
package org.aludratest.service.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.aludratest.testcase.TestStatus;
import org.aludratest.testcase.event.InternalTestListener;
import org.aludratest.testcase.event.TestStepInfo;

/** A helper class for AludraTest module integration tests, where checking the logs is required (e.g. for determining the last
 * logged state and error message). Instances of this class hold received test information without passing them to any other
 * instance (especially, not to Log4Testing, so no log is written).
 * 
 * @author falbrech */
public class DirectLogTestListener implements InternalTestListener {

    private String currentTestStepGroup;

    private List<TestStepInfo> currentTestSteps = new ArrayList<TestStepInfo>();

    private TestStepInfo lastFailed;

    private TestStatus status = TestStatus.PASSED;

    @Override
    public void newTestStepGroup(String name) {
        currentTestStepGroup = name;
        currentTestSteps.clear();
    }

    @Override
    public void newTestStep(TestStepInfo testStep) {
        // TODO best would be to copy the object
        currentTestSteps.add(testStep);
        if (testStep.getTestStatus() != null && testStep.getTestStatus().isFailure()) {
            lastFailed = testStep;
            status = testStep.getTestStatus();
        }
    }

    /** Returns the last executed test step.
     * 
     * @return The last executed test step, or <code>null</code> if no test steps have been executed yet. */
    public TestStepInfo getLastTestStep() {
        return currentTestSteps.isEmpty() ? null : currentTestSteps.get(currentTestSteps.size() - 1);
    }

    /** Returns all test steps of the current test step group.
     * 
     * @return All test steps of the current test step group, possibly an empty list. The returned list cannot be modified. */
    public List<TestStepInfo> getTestStepsOfCurrentGroup() {
        return Collections.unmodifiableList(currentTestSteps);
    }

    /** Returns the name of the current test step group.
     * 
     * @return The name of the current test step group, or <code>null</code> if no test step group has been started yet. */
    public String getCurrentTestStepGroup() {
        return currentTestStepGroup;
    }

    /** Returns <code>true</code> if the test case represented by this log can be treated as failed.
     * 
     * @return <code>true</code> if the test case represented by this log can be treated as failed, <code>false</code> otherwise. */
    public boolean isFailed() {
        return lastFailed != null;
    }

    /** Returns the last failed test step.
     * 
     * @return The last failed test step, or <code>null</code> if no test step failed yet. */
    public TestStepInfo getLastFailed() {
        return lastFailed;
    }

    /** Returns the overall status of this test case.
     * 
     * @return The overall status of this test case; will be PASSED if nothing has been executed yet. */
    public TestStatus getStatus() {
        return status;
    }

}
