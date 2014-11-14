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
package org.aludratest.service.gui.web.selenium;

import org.aludratest.config.InternalComponent;

/**
 * Abstraction of a service that provides server resources to AludraTest.
 * @author Volker Bergmann
 */
@InternalComponent(singleton = true)
public interface SeleniumResourceService {

    /** Acquires a server and reserves it for the client.
     * @return the URL of the server (could be host name only, or host:port). */
    String acquire();

    /** Releases a server and makes it available to other clients again.
     * @param server The URL of the server to release, as retrieved from {@link #acquire()}. */
    void release(String server);

    /** @return the number of resources available */
    int size();

    /**
     * @return the number of configured hosts
     */
    int getHostCount();

}
