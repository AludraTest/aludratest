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
package org.aludratest.testcase.event.impl;

import org.aludratest.service.AludraContext;
import org.aludratest.service.AludraService;
import org.aludratest.service.ComponentId;
import org.aludratest.service.ServiceWrapper;
import org.aludratest.testcase.AludraTestContext;
import org.codehaus.plexus.component.annotations.Component;

@Component(role = ServiceWrapper.class, hint = "aludra")
public class DefaultAludraServiceWrapper implements ServiceWrapper {

    @Override
    public <T extends AludraService> T wrap(T serviceObject, ComponentId<T> serviceId, AludraContext testContext) {
        if (!(testContext instanceof AludraTestContext)) {
            return serviceObject;
        }

        return AludraTestUtil.wrapWithInvocationHandler(serviceId.getInterfaceClass(), new AludraServiceInvocationHandler(
                serviceObject, serviceId, (AludraTestContext) testContext));
    }

}
