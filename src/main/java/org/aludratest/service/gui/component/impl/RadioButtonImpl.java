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
package org.aludratest.service.gui.component.impl;

import org.aludratest.service.gui.component.RadioButton;
import org.aludratest.util.data.helper.DataMarkerCheck;

/** Default implementation of the RadioButton interface. */
public class RadioButtonImpl extends AbstractElement<RadioButton> implements RadioButton {

    @Override
    public void select() {
        perform().selectRadiobutton(elementType, elementName, getLocator(), taskCompletionTimeout);
    }

    @Override
    public void select(String value) {
        if (!DataMarkerCheck.isNull(value) && Boolean.parseBoolean(value)) {
            select();
        }
    }

    @Override
    public void assertChecked() {
        verify().assertChecked(elementType, elementName, getLocator());
    }

    @Override
    public void assertChecked(String expected) {
        if (!DataMarkerCheck.isNull(expected)) {
            verify().assertChecked(elementType, elementName, Boolean.parseBoolean(expected), getLocator());
        }
    }

    @Override
    public boolean isChecked() {
        return check().isElementChecked(elementType, elementName, getLocator());
    }
}
