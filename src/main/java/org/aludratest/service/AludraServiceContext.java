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


/**
 * Interface for AludraService contexts. Each service will get such a context during its initialization. The context can be used
 * to retrieve other contexts, or to get the name of the current instance of the service, if any.
 * 
 * @author falbrech
 * 
 */
public interface AludraServiceContext extends AludraContext {

    /**
     * Returns the name of the current service instance, if any.
     * 
     * @return The name of the current service instance, if any. May be <code>null</code> if no instance name is set.
     */
    public String getInstanceName();

    /** Gets the service for the given service interface. The service is ensured to be configured and initialized. Calls to the
     * service will be logged in the test case log.
     * 
     * @param serviceInterface Class of the service interface to retrieve (e.g. <code>FileService.class</code>).
     * 
     * @return A ready-to-use service instance. */
    public <T extends AludraService> T getService(Class<T> serviceInterface);

    /** Gets the service for the given service interface. The service is ensured to be configured and initialized.
     * 
     * @param serviceInterface Class of the service interface to retrieve (e.g. <code>FileService.class</code>).
     * 
     * @return A ready-to-use service instance. */
    public <T extends AludraService> T getNonLoggingService(Class<T> serviceInterface);

}
