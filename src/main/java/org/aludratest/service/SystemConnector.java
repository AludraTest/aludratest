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

import java.util.List;

/**
 * Callback interface for systems to provide information asynchronously 
 * like {@link ErrorReport}s or activity statuses. 
 * @author Volker Bergmann
 */
public interface SystemConnector {

    /** @return the name of the system */
    String getName();

    /** 
     * Tells if the system is ready to receive new requests or user actions.
     * @return true if the system is ready to receive 
     *      new requests or user actions, otherwise false */
    boolean isBusy();

    /** Called by the framework to get informations about errors which 
     *  may have occurred asynchronously. Examples are AJAX error pop-ups 
     *  on web pages or delivery failures on messaging systems.
     *  @return a {@link List} of {@link ErrorReport}s or null */
    List<ErrorReport> checkForErrors();

}
