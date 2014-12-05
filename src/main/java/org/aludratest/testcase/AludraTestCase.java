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

import org.aludratest.service.AludraService;
import org.aludratest.service.ComponentId;

/** Parent class for plain AludraTest test classes.
 * @author Volker Bergmann
 * @author falbrech */
public class AludraTestCase {

    /** The related test context */
    private AludraTestContext context;

    /** Default constructor */
    public AludraTestCase() {
    }

    // interface ---------------------------------------------------------------

    /** Sets the {@link #context}
     * @param context the context to set */
    public void setContext(AludraTestContext context) {
        this.context = context;
    }

    /** Provides a service with the given serviceId.
     *  @param serviceId the {@link ComponentId} of the requested service
     *  @return the service configured for the given serviceId */
    public <T extends AludraService> T getService(ComponentId<T> serviceId) {
        return context.getService(serviceId);
    }

    /** CLoses all services */
    @After
    public void closeServices() {
        context.closeServices();
    }

    /** Closes a service
     *  @param serviceId the {@link ComponentId} of the service to close */
    protected void closeService(ComponentId<?> serviceId) {
        context.closeService(serviceId);
    }

    // logging features for child classes --------------------------------------
    /** Creates a new Test Step Group.
     * @param name of the group to create */
    protected void newTestStepGroup(String name) {
        context.newTestStepGroup(name);
    }

    /** Logs a testing error to the AludraTest logging engine.
     * 
     * @param text Error message to use.
     * 
     * @param status New status to set the test case to. */
    protected void logError(String text, TestStatus status) {
        context.logError(text, status);
    }

    /** Logs a testing error to the AludraTest logging engine.
     * 
     * @param text Error message to use.
     * 
     * @param t Cause of the testing error. */
    protected void logError(String text, Throwable t) {
        context.logError(text, t);
    }

}
