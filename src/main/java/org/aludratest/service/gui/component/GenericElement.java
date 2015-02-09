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
package org.aludratest.service.gui.component;

import org.aludratest.service.gui.AludraGUI;
import org.aludratest.service.locator.Locator;

/**
 * GUI element without a specific assignment to a certain GUI component type.
 * @author Volker Bergmann
 */
public class GenericElement extends Element {

    /**
     * Constructor.
     * @param aludraGui the underlying AludraGUI instance
     * @param locator a {@link Locator} for localizing the associated GUI component
     */
    public GenericElement(AludraGUI aludraGui, Locator locator) {
        super(aludraGui, locator);
    }

    /**
     * Constructor.
     * @param aludraGui the underlying AludraGUI instance
     * @param locator a {@link Locator} for localizing the associated GUI component
     * @param elementName the name by which to log element access
     */
    public GenericElement(AludraGUI aludraGui, Locator locator, String elementName) {
        super(aludraGui, locator, elementName);
    }
    
}
