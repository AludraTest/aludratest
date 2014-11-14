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
package org.aludratest.service.impl;

import org.aludratest.impl.log4testing.data.TestCaseLog;
import org.aludratest.service.AludraService;
import org.aludratest.service.AludraServiceContext;
import org.aludratest.service.ComponentId;
import org.aludratest.testcase.AludraTestContext;

public final class AludraServiceContextImpl implements AludraServiceContext {

    private AludraTestContext testContext;

    private String instanceName;

    public AludraServiceContextImpl(AludraTestContext testContext, String instanceName) {
        this.testContext = testContext;
        this.instanceName = instanceName;
    }

    @Override
    public String getInstanceName() {
        return instanceName;
    }

    @Override
    public <T extends AludraService> T getService(Class<T> serviceInterface) {
        ComponentId<T> id = ComponentId.create(serviceInterface, instanceName);
        return testContext.getService(id);
    }

    @Override
    public <T extends AludraService> T getNonLoggingService(Class<T> serviceInterface) {
        ComponentId<T> id = ComponentId.create(serviceInterface, instanceName);
        return testContext.getNonLoggingService(id);
    }

    @Override
    public <T> T newComponentInstance(Class<T> componentInterface) {
        return testContext.newComponentInstance(componentInterface);
    }

    @Override
    public TestCaseLog getTestCaseLog() {
        return testContext.getTestCaseLog();
    }

}
