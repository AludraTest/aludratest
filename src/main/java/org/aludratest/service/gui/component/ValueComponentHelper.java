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

import org.aludratest.util.data.helper.DataMarkerCheck;
import org.aludratest.util.validator.ContainsIgnoreCaseTrimmedValidator;
import org.aludratest.util.validator.ContainsValidator;
import org.aludratest.util.validator.EqualsIgnoreCaseTrimmedValidator;
import org.aludratest.util.validator.EqualsValidator;
import org.aludratest.util.validator.NotEqualsValidator;
import org.aludratest.util.validator.NumberStringGreaterValidator;
import org.aludratest.util.validator.NumberStringLessValidator;
import org.databene.commons.Validator;

/** Helper class to implement the ValueComponent interface for a GUIComponent (pseudo-multi-inheritance)
 * 
 * @author falbrech */
class ValueComponentHelper implements ValueComponent {

    private Element<?> component;

    private boolean value;

    ValueComponentHelper(Element<?> component, boolean value) {
        this.component = component;
        this.value = value;
    }

    @Override
    public String getText() {
        if (value) {
            return component.perform().getInputFieldValue(component.elementType, component.elementName, component.getLocator());
        }
        return component.perform().getText(component.elementType, component.elementName, component.getLocator());
    }

    @Override
    public void assertTextEquals(String expectedText) {
        if (!DataMarkerCheck.isNull(expectedText)) {
            if (value) {
                component.verify().assertValueMatches(component.elementType, component.elementName, component.getLocator(),
                        new EqualsValidator(expectedText));
            }
            else {
                component.verify().assertTextMatches(component.elementType, component.elementName, component.getLocator(),
                        new EqualsValidator(expectedText));
            }
        }
    }

    @Override
    public void assertTextNotEquals(String expectedText) {
        if (!DataMarkerCheck.isNull(expectedText)) {
            if (value) {
                component.verify().assertValueMatches(component.elementType, component.elementName, component.getLocator(),
                        new NotEqualsValidator(expectedText));
            }
            else {
                component.verify().assertTextMatches(component.elementType, component.elementName, component.getLocator(),
                        new NotEqualsValidator(expectedText));
            }
        }
    }

    @Override
    public void assertTextContains(String expectedText) {
        if (!DataMarkerCheck.isNull(expectedText)) {
            if (value) {
                component.verify().assertValueMatches(component.elementType, component.elementName, component.getLocator(),
                        new ContainsValidator(expectedText));
            }
            else {
                component.verify().assertTextMatches(component.elementType, component.elementName, component.getLocator(),
                        new ContainsValidator(expectedText));
            }
        }
    }

    @Override
    public void assertTextContainsIgnoreCaseTrimmed(String expectedText) {
        if (!DataMarkerCheck.isNull(expectedText)) {
            if (value) {
                component.verify().assertValueMatches(component.elementType, component.elementName, component.getLocator(),
                        new ContainsIgnoreCaseTrimmedValidator(expectedText));
            }
            else {
                component.verify().assertTextMatches(component.elementType, component.elementName, component.getLocator(),
                        new ContainsIgnoreCaseTrimmedValidator(expectedText));
            }
        }
    }

    @Override
    public void assertTextEqualsIgnoreCaseTrimmed(String expectedText) {
        if (!DataMarkerCheck.isNull(expectedText)) {
            if (value) {
                component.verify().assertValueMatches(component.elementType, component.elementName, component.getLocator(),
                        new EqualsIgnoreCaseTrimmedValidator(expectedText));
            }
            else {
                component.verify().assertTextMatches(component.elementType, component.elementName, component.getLocator(),
                        new EqualsIgnoreCaseTrimmedValidator(expectedText));
            }
        }
    }

    @Override
    public void assertTextMatches(Validator<String> validator) {
        if (value) {
            component.verify()
                    .assertValueMatches(component.elementType, component.elementName, component.getLocator(), validator);
        }
        else {
            component.verify().assertTextMatches(component.elementType, component.elementName, component.getLocator(), validator);
        }
    }

    @Override
    public void assertValueGreaterThan(String value) {
        if (!DataMarkerCheck.isNull(value)) {
            if (this.value) {
                component.verify().assertValueMatches(component.elementType, component.elementName, component.getLocator(),
                        new NumberStringGreaterValidator(value, getNumericTolerance()));
            }
            else {
                component.verify().assertTextMatches(component.elementType, component.elementName, component.getLocator(),
                        new NumberStringGreaterValidator(value, getNumericTolerance()));
            }
        }
    }

    @Override
    public void assertValueLessThan(String value) {
        if (!DataMarkerCheck.isNull(value)) {
            if (this.value) {
                component.verify().assertValueMatches(component.elementType, component.elementName, component.getLocator(),
                        new NumberStringLessValidator(value, getNumericTolerance()));
            }
            else {
                component.verify().assertTextMatches(component.elementType, component.elementName, component.getLocator(),
                        new NumberStringLessValidator(value, getNumericTolerance()));
            }
        }
    }

    private double getNumericTolerance() {
        return DataMarkerCheck.getNumericTolerance();
    }

}
