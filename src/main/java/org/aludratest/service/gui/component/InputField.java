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

import org.aludratest.service.gui.component.impl.ValueComponent;

/**
 * Represents an input field in a GUI.
 * @author Joerg Langnickel
 * @author Volker Bergmann
 */
public interface InputField extends InputComponent<InputField>, ValueComponent {

    /**
     * Enters text in the InputField.
     * If the text is null or marked as null the operation will not be executed
     * If the text is marked as empty it will be replaced with ""
     * @param text the text to enter in the input field
     */
    public void enter(String text);

    /** Asserts that the element is editable, i.e. text can be entered. */
    public void assertEditable();

    /** Asserts that the element is not editable, i.e. no text can be entered. Disabled controls are also treated as
     * "not editable". */
    public void assertNotEditable();

}
