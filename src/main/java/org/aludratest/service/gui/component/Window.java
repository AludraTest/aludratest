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
import org.aludratest.service.locator.window.TitleLocator;

/**
 * Represents a Window/Frame in a GUI.
 * @author Joerg Langnickel
 * @author Volker Bergmann
 */
public class Window extends GUIComponent {

    /** Constructor.
     * @param aludraGui the underlying {@link AludraGUI} service instance
     * @param locator a locator for the referenced window */
    public Window(AludraGUI aludraGui, TitleLocator locator) {
        super(aludraGui, locator);
    }

    /** Constructor.
     * @param aludraGui the underlying {@link AludraGUI} service instance
     * @param locator a locator for the referenced element
     * @param elementName an explicit name to use for the window */
    public Window(AludraGUI aludraGui, TitleLocator locator, String elementName) {
        super(aludraGui, locator, elementName);
    }

    /** @return the locator */
    public TitleLocator getLocator() {
        return (TitleLocator) locator;
    }

    /** Closes all other open windows. */
    public void closeOthers() {
        perform().closeOtherWindows(elementType, elementName, getLocator());
    }

}
