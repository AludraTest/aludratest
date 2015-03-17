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

/** Represents a checkbox in a GUI.
 * @author Joerg Langnickel
 * @author Volker Bergmann */
public class Checkbox extends InputComponent<Checkbox> {

    /** Constructor.
     * @param aludraGui the underlying {@link AludraGUI} service instance
     * @param locator a locator for the referenced checkbox */
    public Checkbox(AludraGUI aludraGui, GUIElementLocator locator) {
        super(aludraGui, locator);
    }

    /** Constructor.
     * @param aludraGui the underlying {@link AludraGUI} service instance
     * @param locator a locator for the referenced element
     * @param elementName an explicit name to use for the component */
    public Checkbox(AludraGUI aludraGui, GUIElementLocator locator, String elementName) {
        super(aludraGui, locator, elementName);
    }

    /** Selects or deselects a Checkbox due to overgiven String If the text is null or marked as null the operation is not
     * executed.
     * @param selectString */
    public void select(String selectString) {
        if (!DataMarkerCheck.isNull(selectString)) {
            Boolean select = Boolean.parseBoolean(selectString);
            if (select) {
                select();
            }
            else {
                deselect();
            }
        }
    }

    /** Selects the checkbox. */
    public void select() {
        perform().selectCheckbox(elementType, elementName, getLocator(), taskCompletionTimeout);
    }

    /** Unselects the checkbox. */
    public void deselect() {
        perform().deselectCheckbox(elementType, elementName, getLocator(), taskCompletionTimeout);
    }

    /** Asserts that the checkbox is checked. */
    public void assertChecked() {
        verify().assertChecked(elementType, elementName, getLocator());
    }

    /** Asserts that the checkbox is in the in the state expected by the passed text If the text is null or marked as null the
     * operation is not executed.
     * @param text <code>"true"</code> or <code>"false"</code>. Anything else (non-null) will be interpreted as <code>false</code> */
    public void assertChecked(String text) {
        if (!DataMarkerCheck.isNull(text)) {
            Boolean expected = Boolean.parseBoolean(text);
            verify().assertChecked(elementType, elementName, expected, getLocator());
        }
    }

    /** Returns if the checkbox is currently checked or not.
     * 
     * @return <code>true</code> if the checkbox is currently checked (has a checkmark in its box), <code>false</code> otherwise. */
    public boolean isChecked() {
        return check().isElementChecked(elementType, elementName, getLocator());
    }

}
