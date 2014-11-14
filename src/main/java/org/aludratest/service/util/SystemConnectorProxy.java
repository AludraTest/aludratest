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
package org.aludratest.service.util;

import java.util.List;

import org.aludratest.service.ErrorReport;
import org.aludratest.service.SystemConnector;
import org.aludratest.util.ControlFlowHandler;

/** Permits separation of an {@link SystemConnector} reference and its implementation. This is used by the
 * {@link ControlFlowHandler} in order to enable dynamic change of the SystemConnector for existing proxy instances.
 * @author Volker Bergmann */
public class SystemConnectorProxy implements SystemConnector {

    /** The real connector to call */
    private SystemConnector realConnector;

    /** Constructor expecting the {@link #realConnector}.
     * @param realConnector the real connector to use */
    public SystemConnectorProxy(SystemConnector realConnector) {
        this.realConnector = realConnector;
    }

    /** Sets/changes the {@link #realConnector}.
     * @param realConnector the real connector to use */
    public void setRealSystemConnector(SystemConnector realConnector) {
        this.realConnector = realConnector;
    }

    @Override
    public String getName() {
        return (realConnector != null ? realConnector.getName() : null);
    }

    /** Forwards the invocation to the {@link #realConnector}. */
    @Override
    public boolean isBusy() {
        return (realConnector != null ? realConnector.isBusy() : false);
    }

    /** Forwards the invocation to the {@link #realConnector}. */
    @Override
    public List<ErrorReport> checkForErrors() {
        return (realConnector != null ? realConnector.checkForErrors() : null);
    }


    // java.lang.Object overrides ----------------------------------------------

    @Override
    public boolean equals(Object obj) {
        return realConnector.equals(obj);
    }

    @Override
    public int hashCode() {
        return realConnector.hashCode();
    }

    @Override
    public String toString() {
        return (realConnector != null ? realConnector.toString() : "null");
    }

}
