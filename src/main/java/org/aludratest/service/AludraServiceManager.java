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

import org.aludratest.config.Configurable;

/** The bootstrap for Aludra Components like e.g. services.
 * @author falbrech */
public interface AludraServiceManager {

    /** Creates and configures a service instance.
     * 
     * @param serviceId A characterization of the requested service.
     * @param context Current AludraTest context.
     * @param wrap If <code>true</code>, the service will be wrapped with registered wrappers, otherwise, you will retrieve the
     *            plain service object.
     * 
     * @return the service instance **/
    public <T extends AludraService> T createAndConfigureService(ComponentId<T> serviceId, AludraContext context, boolean wrap);

    /** Selects the default implementor class configured for the requested interface and returns a new instance of it. If the
     * implementor class implements the {@link Configurable} interface, the object is configured before returning it.
     * 
     * @param iface the interface for which to create an implementor instance
     * @return a new instance of the class configured as standard implementor of the interface */
    public <T> T newImplementorInstance(Class<T> iface);

    /** Removes an instantiated singleton from the internal map of singletons, if it has already been instantiated. If it
     * implements the <code>AludraCloseable</code> interface, its <code>close()</code> method is invoked first.
     * 
     * @param iface Component or service interface class of which to remove the instantiated singleton implementation, if any. */
    public void removeSingleton(Class<?> iface);


}
