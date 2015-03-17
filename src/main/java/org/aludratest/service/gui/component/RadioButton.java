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
import org.aludratest.service.locator.element.GUIElementLocator;
import org.aludratest.util.data.helper.DataMarkerCheck;

/**
 * Represents a radio button on a GUI.
 * @author Joerg Langnickel
 * @author Volker Bergmann
 */
public class RadioButton extends InputComponent<RadioButton> {

    /**
     * Constructor.
     * @param aludraGui the underlying {@link AludraGUI} service instance
     * @param locator a locator for the referenced radio button
     */
    public RadioButton(AludraGUI aludraGui, GUIElementLocator locator) {
        super(aludraGui, locator);
    }

    /** Constructor.
     *  @param aludraGui the underlying {@link AludraGUI} service instance
     *  @param locator a locator for the referenced element
     *  @param elementName an explicit name to use for the component */
    public RadioButton(AludraGUI aludraGui, GUIElementLocator locator, String elementName) {
        super(aludraGui, locator, elementName);
    }

    /** Selects this radio button. */
    public void select() {
        perform().selectRadiobutton(elementType, elementName, getLocator(), taskCompletionTimeout);
    }

    /** Selects this radio button if and only if the passed string parameter has the value <code>"true"</code>. In every other
     * case, no action is performed.
     * 
     * @param value String parameter to indicate if a select operation shall be performed on this radio button. */
    public void select(String value) {
        if (!DataMarkerCheck.isNull(value)) {
            if (Boolean.parseBoolean(value)) {
                select();
            }
        }
    }

    /** Asserts that the radio button is checked */
    public void assertChecked() {
        verify().assertChecked(elementType, elementName, getLocator());
    }

    /** Asserts that this Radio button is in the expected state, passed by expected string. If the expected string is null or
     * marked as null, no operation will be executed.
     * 
     * @param expected <code>"true"</code> or <code>"false"</code>, or <code>null</code> or marked as null to not perform any
     *            assertion. */
    public void assertChecked(String expected) {
        if (!DataMarkerCheck.isNull(expected)) {
            verify().assertChecked(elementType, elementName, Boolean.parseBoolean(expected), getLocator());
        }
    }

}
