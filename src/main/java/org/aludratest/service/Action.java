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

import org.aludratest.impl.log4testing.data.attachment.Attachment;

/** Parent interface for all service actions which can provide file-based information useful for debugging.
 * @author Joerg Langnickel
 * @author Volker Bergmann */
public interface Action {

    /** Creates attachments containing whichever information the service finds helpful for debugging the current state.
     * @return a list of {@link Attachment}s which contains the debugging information */
    public List<Attachment> createDebugAttachments();

    /** Creates zero, one or more attachments which represent the provided object.
     * @param object the object to save
     * @param title the title to use for the object
     * @return a list of {@link Attachment}s or null */
    public List<Attachment> createAttachments(Object object, String title);

    /** Injects a SystemConnector
     * @param systemConnector the SystemConnector to use */
    public void setSystemConnector(SystemConnector systemConnector);

}
