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

import java.util.HashMap;
import java.util.Map;

import org.aludratest.AludraTest;
import org.aludratest.impl.log4testing.data.TestCaseLog;
import org.aludratest.impl.log4testing.data.TestStepLog;
import org.aludratest.impl.log4testing.util.LogUtil;
import org.aludratest.service.AludraContext;
import org.aludratest.service.AludraService;
import org.aludratest.service.ComponentId;

/**
 * Test context provided to {@link AludraService} implementors
 * in order to access information necessary for initialization
 * and framework interaction.
 * @author Volker Bergmann
 */
public final class AludraTestContext implements AludraContext {

    /** The test case to log test execution info. */
    private TestCaseLog testCaseLog;

    private AludraTest aludraTest;

    /** A {@link Map} of the services used by the test. */
    private Map<ComponentId<?>, AludraService> nonLoggingServices;

    /** A {@link Map} of the services used by the test. */
    private Map<ComponentId<?>, AludraService> loggingServices;

    /** Constructor.
     *  @param testCase The test case to log test execution info
     *  @param aludra */
    public AludraTestContext(TestCaseLog testCase, AludraTest aludra) {
        this.testCaseLog = testCase;
        this.aludraTest = aludra;
        this.loggingServices = new HashMap<ComponentId<?>, AludraService>();
        this.nonLoggingServices = new HashMap<ComponentId<?>, AludraService>();
    }

    /** @return the {@link #testCaseLog} */
    public TestCaseLog getTestCaseLog() {
        return testCaseLog;
    }

    /** Creates a new test step group in the underlying test case log.
     *  @param name of the group to create */
    public void newTestStepGroup(String name) {
        testCaseLog.newTestStepGroup(name);
    }

    /** Provides a service with the given serviceId.
     *  @param serviceId the {@link ComponentId} of the requested service
     *  @return the service configured for the given serviceId */
    @SuppressWarnings("unchecked")
    public <T extends AludraService> T getService(ComponentId<T> serviceId) {
        // look up the requested service in the service map
        T service = (T) loggingServices.get(serviceId);
        if (service == null) {
            service = LogUtil.<T, T> wrapWithAludraProxy(getNonLoggingService(serviceId), serviceId, testCaseLog, this);
            loggingServices.put(serviceId, service);
        }
        return service;
    }

    /** Provides a service with the given serviceId.
     *  @param serviceId the {@link ComponentId} of the requested service
     *  @return the service configured for the given serviceId */
    @SuppressWarnings("unchecked")
    public <T extends AludraService> T getNonLoggingService(ComponentId<T> serviceId) {
        // look up the requested service in the service map
        T service = (T) nonLoggingServices.get(serviceId);
        if (service == null) {
            // if the service was not found, then retrieve it...
            service = aludraTest.getServiceManager().createAndConfigureService(serviceId, this);
            // ...and store it in the services map
            nonLoggingServices.put(serviceId, service);
        }
        return service;
    }

    @Override
    public <T> T newComponentInstance(Class<T> componentInterface) {
        return aludraTest.getServiceManager().newImplementorInstance(componentInterface);
    }

    /** CLoses all services */
    public void closeServices() {
        closeAll(nonLoggingServices);
        closeAll(loggingServices);
    }

    /** Closes a service
     *  @param serviceId the {@link ComponentId} of the service to close */
    public void closeService(ComponentId<?> serviceId) {
        closeService(serviceId, nonLoggingServices);
        closeService(serviceId, loggingServices);
    }

    /** Logs a text with 'info' level
     *  @param text to log */
    public void logInfo(String text) {
        TestStepLog testStep = newTestStep();
        testStep.setComment(text);
    }

    /** logs a text with 'error' level
     *  @param errorMessage the error message to log
     *  @param status the {@link TestStatus} to assign to the test step */
    public void logError(String errorMessage, TestStatus status) {
        LogUtil.appendErrorInfoToLastStep(errorMessage, null, null, status, testCaseLog);
    }

    /** Logs a text and an exception with 'error' level
     *  @param errorMessage the error message to log
     *  @param t the {@link Throwable} to be reported */
    public void logError(String errorMessage, Throwable t) {
        LogUtil.appendErrorInfoToLastStep(errorMessage, t, null, null, testCaseLog);
    }

    // private helper methods --------------------------------------------------

    private TestStepLog newTestStep() {
        return testCaseLog.newTestStep();
    }

    private void closeAll(Map<ComponentId<?>, AludraService> map) {
        for (AludraService service : map.values()) {
            service.close();
        }
        map.clear();
    }

    private void closeService(ComponentId<?> serviceId, Map<ComponentId<?>, AludraService> map) {
        AludraService aludraService = map.get(serviceId);
        if (aludraService != null) {
            map.remove(serviceId);
            aludraService.close();
        } else {
            logError("Error closing Service: Service not found: " + serviceId + ". "
                    + "Service could not be found. Please check the ComponentID.", TestStatus.FAILEDAUTOMATION);
        }
    }

}
