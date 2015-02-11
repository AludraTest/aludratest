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

import org.aludratest.service.AludraContext;
import org.aludratest.service.AludraService;
import org.aludratest.service.AludraServiceContext;
import org.aludratest.service.ComponentId;

public final class AludraServiceContextImpl implements AludraServiceContext {

    private AludraContext delegate;

    private String instanceName;

    public AludraServiceContextImpl(AludraContext delegate, String instanceName) {
        this.delegate = delegate;
        this.instanceName = instanceName;
    }

    @Override
    public String getInstanceName() {
        return instanceName;
    }

    @Override
    public <T extends AludraService> T getService(Class<T> serviceInterface) {
        ComponentId<T> id = ComponentId.create(serviceInterface, instanceName);
        return delegate.getService(id);
    }

    @Override
    public <T extends AludraService> T getNonLoggingService(Class<T> serviceInterface) {
        ComponentId<T> id = ComponentId.create(serviceInterface, instanceName);
        return delegate.getNonLoggingService(id);
    }

    @Override
    public <T> T newComponentInstance(Class<T> componentInterface) {
        return delegate.newComponentInstance(componentInterface);
    }

    @Override
    public <T extends AludraService> T getNonLoggingService(ComponentId<T> serviceId) {
        return delegate.getNonLoggingService(serviceId);
    }

    @Override
    public <T extends AludraService> T getService(ComponentId<T> serviceId) {
        return delegate.getService(serviceId);
    }

}
