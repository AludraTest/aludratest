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


/** Abstract parent class for custom {@link AludraService} implementations. It provides the features which are usually implemented
 * equally among different services: Handling of context, test case and system connector.
 * 
 * @author Volker Bergmann */
public abstract class AbstractAludraService implements AludraService {

    /** The context of the service. */
    protected AludraServiceContext aludraServiceContext;

    /** Implements the init() method. Stores the context parameter in the {@link #aludraServiceContext} field which can be accessed
     * by subclasses. Extracts the test case log object from the context object and stores it in the {@link #testCaseLog} field
     * which also can be accessed by subclasses. Afterwards, {@link #initService()} is called. */
    @Override
    public final void init(AludraServiceContext context) {
        this.aludraServiceContext = context;
        initService();
    }

    @Override
    public String getInstanceName() {
        return aludraServiceContext.getInstanceName();
    }

    /**
     * Abstract initialization method to be implemented by child classes for individual initialization operations. Implementors
     * can access the protected <code>aludraServiceContext</code> field to retrieve instances of other services, if required.
     */
    public abstract void initService();

    /** Implementation left empty since this is handled by a dynamic proxy. */
    @Override
    public void setSystemConnector(SystemConnector connector) {
        // This does not need an implementation since it is handled by the wrapper
    }

    @Override
    public String toString() {
        if (aludraServiceContext != null && aludraServiceContext.getInstanceName() != null) {
            return getClass().getSimpleName() + "(" + aludraServiceContext.getInstanceName() + ")";
        }
        else {
            return getClass().getSimpleName();
        }
    }

}
