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
package org.aludratest.testcase.impl;

import java.util.HashMap;
import java.util.Map;

import org.aludratest.exception.AutomationException;
import org.aludratest.service.AludraService;
import org.aludratest.service.AludraServiceManager;
import org.aludratest.service.ComponentId;
import org.aludratest.testcase.AludraTestContext;
import org.aludratest.testcase.TestStatus;
import org.aludratest.testcase.event.InternalTestListener;
import org.aludratest.testcase.event.TestStepInfo;
import org.aludratest.testcase.event.impl.LogUtil;

public class AludraTestContextImpl implements AludraTestContext {

    private AludraServiceManager serviceManager;

    private InternalTestListener testListener;

    /** A {@link Map} of the services used by the test. */
    private Map<ComponentId<?>, AludraService> nonLoggingServices;

    /** A {@link Map} of the services used by the test. */
    private Map<ComponentId<?>, AludraService> loggingServices;

    public AludraTestContextImpl(InternalTestListener testListener, AludraServiceManager serviceManager) {
        this.testListener = testListener;
        this.serviceManager = serviceManager;
        this.loggingServices = new HashMap<ComponentId<?>, AludraService>();
        this.nonLoggingServices = new HashMap<ComponentId<?>, AludraService>();
    }

    @Override
    public void newTestStepGroup(String name) {
        testListener.newTestStepGroup(name);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends AludraService> T getService(ComponentId<T> serviceId) {
        // look up the requested service in the service map
        T service = (T) loggingServices.get(serviceId);
        if (service == null) {
            try {
                service = serviceManager.createAndConfigureService(serviceId, this, true);
                testListener.newTestStepGroup(serviceId + ": " + service.getDescription());
                loggingServices.put(serviceId, service);
            }
            catch (RuntimeException re) {
                logError("Initialization of service " + serviceId + " failed", re);
                throw re;
            }
        }
        return service;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends AludraService> T getNonLoggingService(ComponentId<T> serviceId) {
        // look up the requested service in the service map
        T service = (T) nonLoggingServices.get(serviceId);
        if (service == null) {
            try {
                // if the service was not found, then retrieve it...
                service = serviceManager.createAndConfigureService(serviceId, this, false);
                // ...and store it in the services map
                nonLoggingServices.put(serviceId, service);
            }
            catch (RuntimeException re) {
                logError("Initialization of service " + serviceId + " failed", re);
                throw re;
            }
        }
        return service;
    }

    @Override
    public <T> T newComponentInstance(Class<T> componentInterface) {
        return serviceManager.newImplementorInstance(componentInterface);
    }

    @Override
    public void closeServices() {
        closeAll(nonLoggingServices);
        closeAll(loggingServices);
    }

    @Override
    public void closeService(ComponentId<?> serviceId) {
        if (!closeService(serviceId, nonLoggingServices) && !closeService(serviceId, loggingServices)) {
            logError("Error closing Service: Service not found: " + serviceId, new AutomationException(
                    "Service could not be found. Please check the ComponentID."));
        }
    }

    @Override
    public void logError(String errorMessage, TestStatus status) {
        LogUtil.logErrorAsNewGroup(this, errorMessage, status);
    }

    @Override
    public void logError(String errorMessage, Throwable t) {
        LogUtil.logErrorAsNewGroup(this, errorMessage, t);
    }

    @Override
    public void fireTestStep(TestStepInfo step) {
        testListener.newTestStep(step);
    }

    // private helper methods --------------------------------------------------

    private void closeAll(Map<ComponentId<?>, AludraService> map) {
        for (AludraService service : map.values()) {
            service.close();
        }
        map.clear();
    }

    private boolean closeService(ComponentId<?> serviceId, Map<ComponentId<?>, AludraService> map) {
        AludraService aludraService = map.get(serviceId);
        if (aludraService != null) {
            map.remove(serviceId);
            aludraService.close();
            return true;
        }
        return false;
    }


}
