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

import org.aludratest.service.AttachResult;
import org.aludratest.service.ElementName;
import org.aludratest.service.ElementType;
import org.aludratest.service.Interaction;
import org.aludratest.service.TechnicalLocator;
import org.databene.commons.ObjectNotFoundException;

/**
 * Pseudo interaction interface for testing.
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public interface PseudoInteraction extends Interaction {

    /** A service that is expected to succeed. */
    String succeed(
            @ElementType String elementType,
            @ElementName String elementName,
            @TechnicalLocator String locator);

    /** A service that is expected to succeed and of which the result shall be logged in an attachment. */
    @AttachResult("Result")
    String succeedWithAttachments(@ElementType String elementType, @ElementName String elementName,
            @TechnicalLocator String locator);

    /** A service which is expected to raise an {@link ObjectNotFoundException}. */
    void fail(
            @ElementType String elementType,
            @ElementName String elementName,
            @TechnicalLocator String locator);

    void throwException(RuntimeException exception);

}
