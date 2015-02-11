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
package org.aludratest.service;

/** Common interface for AludraTest Contexts. An AludraTest context is able to return arbitrary component instances for a given
 * interface class.
 * @author falbrech */
public interface AludraContext {

    /** Creates a new instance of the given component class. If the component is a configurable object, it is configured before it
     * is returned.
     * 
     * @param componentInterface Component interface.
     * 
     * @return An object of a class implementing the component interface. */
    public <T> T newComponentInstance(Class<T> componentInterface);

    /** Provides a service with the given component ID for use within this context. The service will <b>not</b> be wrapped with
     * registered service wrappers, so operations on this service will not be logged or otherwise noticed.
     * 
     * @param serviceId Service ID for the service.
     * 
     * @return The service object for use. */
    public <T extends AludraService> T getNonLoggingService(ComponentId<T> serviceId);

    /** Provides a service with the given component ID for use within this context. The service will be wrapped with registered
     * service wrappers, so operations on this service will be logged.
     * 
     * @param serviceId Service ID for the service.
     * 
     * @return The service object for use. */
    public <T extends AludraService> T getService(ComponentId<T> serviceId);

}
