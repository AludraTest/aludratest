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

import org.aludratest.service.SystemConnector;
import org.aludratest.service.SystemConnectorInterface;

/**
 * Abstract empty implementation of the {@link SystemConnector} interface.
 * @author Volker Bergmann
 */
public abstract class AbstractSystemConnector implements SystemConnector {

    private final String name;

    /** Constructor
     *  @param name the name to use for the system connector */
    public AbstractSystemConnector(String name) {
        this.name = name;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends SystemConnectorInterface> T getConnector(Class<T> interfaceClass) {
        if (interfaceClass.isAssignableFrom(getClass())) {
            return (T) this;
        }
        return null;
    }

    @Override
    public String toString() {
        return name;
    }
}
