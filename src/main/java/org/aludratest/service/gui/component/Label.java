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
import org.databene.commons.Validator;

/**
 * Represents a Label in a GUI.
 * @author Joerg Langnickel
 * @author Volker Bergmann
 */
public class Label extends Element<Label> implements ValueComponent {

    private ValueComponentHelper helper = new ValueComponentHelper(this, false);

    /**
     * Constructor.
     * @param aludraGui the underlying {@link AludraGUI} service instance
     * @param locator a locator for the referenced label
     */
    public Label(AludraGUI aludraGui, Locator locator) {
        super(aludraGui, locator);
    }

    /** Constructor.
     *  @param aludraGui the underlying {@link AludraGUI} service instance
     *  @param locator a locator for the referenced element
     *  @param elementName an explicit name to use for the component */
    public Label(AludraGUI aludraGui, Locator locator, String elementName) {
        super(aludraGui, locator, elementName);
    }

    @Override
    public String getText() {
        return helper.getText();
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
