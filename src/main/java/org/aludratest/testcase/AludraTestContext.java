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
package org.aludratest.testcase;

import org.aludratest.service.AludraContext;
import org.aludratest.service.ComponentId;
import org.aludratest.testcase.event.TestStepInfo;

/** Test context provided to AludraTestCase instances in order to access information necessary for initialization and framework
 * interaction.
 * @author Volker Bergmann
 * @author falbrech */
public interface AludraTestContext extends AludraContext {

    /** CLoses all services which were opened by this context. */
    public void closeServices();

    /** Closes a service.
     * @param serviceId the {@link ComponentId} of the service to close */
    public void closeService(ComponentId<?> serviceId);

    /** Starts a new group of test steps with the given name.
     * @param name of the group to create */
    public void newTestStepGroup(String name);

    /** Reports an execution failure to the framework.
     * @param errorMessage the error message assigned to the failure.
     * @param status the {@link TestStatus} indicating the failure. <code>status.isFailure()</code> should be <code>true</code>. */
    public void logError(String errorMessage, TestStatus status);

    /** Reports an execution error (a kind of "uncaught" exception) to the framework.
     * @param errorMessage the error message assigned to the error. Can be the message of the exception.
     * @param t the {@link Throwable} to be reported. */
    public void logError(String errorMessage, Throwable t);

    /** Adds a test step to this context. Call {@link #fireTestSteps()} to fire all added test steps at once and reset the internal
     * list of test steps.
     * 
     * @param step Step to add to the internal list of steps of this context. */
    public void addTestStep(TestStepInfo step);

    /** Fires the whole internal list of test steps. Implementations will know where to fire it to. Afterwards, the internal list
     * will be empty, so a second call to this method without adding a new test step has no effect. */
    public void fireTestSteps();

}
