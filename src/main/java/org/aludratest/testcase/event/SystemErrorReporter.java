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
package org.aludratest.testcase.event;

import org.aludratest.service.SystemConnectorInterface;

/** Interface to be implemented by System Connectors to report the error status of a system under test. The AludraTest framework
 * queries this status after each <code>perform()</code> and <code>verify()</code> related operation of service objects which
 * caused an exception, so this is the best way to provide more detailed information about system error state (e.g. an error popup
 * giving more detailed information what's the real cause of the problem).
 * 
 * @author falbrech */
public interface SystemErrorReporter extends SystemConnectorInterface {

    /** Called by the framework to get informations about errors which may have occurred asynchronously. Examples are AJAX error
     * pop-ups on web pages or delivery failures on messaging systems.
     * @return an Error Report or <code>null</code> if there is nothing to report from system side. */
    ErrorReport checkForError();

}
