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

import org.aludratest.exception.AccessFailure;
import org.aludratest.exception.AutomationException;

/** Common interface for all services that can be tested by AludraTest. Service implementors must provide a default constructor
 * (public and without parameters) and need to initialize when the framework calls the service's
 * {@link #init(AludraServiceContext)} method.
 * 
 * @author Volker Bergmann */
public interface AludraService extends AludraCloseable {

    /** This method is called by the framework when the service is about to be used. The service can use the provided context
     * parameter to retrieve instances of other services, and can also store the context parameter reference for later use.
     * 
     * @param context Service context which can be used to access other services
     * 
     * @throws AutomationException if the service is misconfigured
     * @throws AccessFailure if a required system cannot be connected */
    void init(AludraServiceContext context);

    /** Returns the instance name which applies to this service instance. Could be <code>null</code> in some cases (for some
     * service types), so be careful to handle this case.
     * 
     * @return The instance name which applies to this service instance, or <code>null</code> if not set. */
    String getInstanceName();

    /** Provides a textual description of the most important configuration information to be logged when the service is configured.
     * @return a textual description of the most important configuration information */
    String getDescription();

    /** Called by test code to create a service specific child class of the {@link Interaction} interface. Note that a service
     * interfaces may override this method's signature to declare a more specific Interaction child interface with additional
     * features.
     * @return the service's {@link Interaction} object */
    Interaction perform();

    /** Called by test code to create a service specific child class of the {@link Verification} interface. Note that a service
     * interfaces may override this method's signature to declare a more specific Verification child interface with additional
     * features.
     * @return the service's {@link Verification} object */
    Verification verify();

    /** Called by test code to create a service specific child class of the {@link Condition} interface. Note that a service
     * interfaces may override this method's signature to declare a more specific Condition child interface with additional
     * features.
     * @return the service's {@link Condition} object */
    Condition check();

    /** Allows the client code to inject an {@link SystemConnector} for providing informations of a SUT asynchronously.
     * @param connector the connector to apply */
    void setSystemConnector(SystemConnector connector);

}
