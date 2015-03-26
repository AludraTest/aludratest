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

import org.aludratest.service.gui.component.InputField;
import org.aludratest.util.data.helper.DataMarkerCheck;
import org.databene.commons.Validator;

/** Default implementation of the InputField interface. */
public class InputFieldImpl extends AbstractInputComponent<InputField> implements ValueComponent, InputField {

    private ValueComponentHelper helper = new ValueComponentHelper(this, true);

    /**
     * Enters text in the InputField.
     * If the text is null or marked as null the operation will not be executed
     * If the text is marked as empty it will be replaced with ""
     * @param text the text to enter in the input field
     */
    @Override
    public void enter(String text) {
        if (!DataMarkerCheck.isNull(text)) {
            perform().type(elementType, elementName, getLocator(), DataMarkerCheck.convertIfEmpty(text), taskCompletionTimeout);
        }
    }

    @Override
    public String getText() {
        return perform().getInputFieldValue(elementType, elementName, getLocator());
    }

    @Override
    public void assertTextEquals(String expectedText) {
        helper.assertTextEquals(expectedText);
    }

    @Override
    public void assertTextNotEquals(String expectedText) {
        helper.assertTextNotEquals(expectedText);
    }

    @Override
    public void assertTextContains(String expectedText) {
        helper.assertTextContains(expectedText);
    }

    @Override
    public void assertTextContainsIgnoreCaseTrimmed(String expectedText) {
        helper.assertTextContainsIgnoreCaseTrimmed(expectedText);
    }

    @Override
    public void assertTextEqualsIgnoreCaseTrimmed(String expectedText) {
        helper.assertTextEqualsIgnoreCaseTrimmed(expectedText);
    }

    @Override
    public void assertTextMatches(Validator<String> validator) {
        helper.assertTextMatches(validator);
    }

    @Override
    public void assertValueGreaterThan(String value) {
        helper.assertValueGreaterThan(value);
    }

    @Override
    public void assertValueLessThan(String value) {
        helper.assertValueLessThan(value);
    }

}
