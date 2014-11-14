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
package org.aludratest.service.pseudo;

import java.util.ArrayList;
import java.util.List;

import org.aludratest.exception.FunctionalFailure;
import org.aludratest.exception.TechnicalException;
import org.aludratest.impl.log4testing.data.attachment.Attachment;
import org.aludratest.impl.log4testing.data.attachment.StringAttachment;
import org.aludratest.service.SystemConnector;

/**
 * Pseudo interaction implementation for testing.
 * @author Volker Bergmann
 */
public class ThePseudoInteraction implements PseudoInteraction {
    
    private SystemConnector systemConnector;

    @Override
    public List<Attachment> createAttachments(Object object, String label) {
        throw new TechnicalException("Not supported");
    }
    
    /** Creates a simple text file in replacement of real debugging information. */
    @Override
    public List<Attachment> createDebugAttachments() {
        List<Attachment> result = new ArrayList<Attachment>();
        result.add(new StringAttachment("debugInfoTitle", "debugInfoToBeSaved", "txt"));
        return result;
    }

    /** Empty service method implementation. */
    @Override
    public void succeed(String elementType, String elementName, String locator) {
        // nothing to do
    }

    /** Service method which throws an {@link ObjectNotFoundException}. */
    @Override
    public void fail(String elementType, String elementName, String locator) {
        throw new FunctionalFailure("Nothing found");
    }

    @Override
    public void throwException(RuntimeException exception) {
        throw exception;
    }

    /** @return the {@link #systemConnector} */
    public SystemConnector getSystemConnector() {
        return systemConnector;
    }
    
    @Override
    public void setSystemConnector(SystemConnector systemConnector) {
        this.systemConnector = systemConnector;
    }

}
