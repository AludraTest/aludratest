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

import org.databene.commons.Validator;

/** Interface for components carrying a text value (e.g. Labels and InputFields). This interface adds methods for retrieving and
 * validating the component's value.
 * 
 * @author falbrech */
public interface ValueComponent {

    /** Reads the value of this component and returns it as a String without conversion/manipulation.
     * @return the text content of this component. */
    public String getText();

    /** Verifies that this component has the expected text. If the text parameter is <code>null</code> or marked as
     * <code>null</code>, the operation will not be executed. If the text parameter is marked as empty, it will be replaced with
     * "" before verification.
     * 
     * @param expectedText Text to compare this component's text against. */
    public void assertTextEquals(String expectedText);

    /** Verifies that this component does <b>not</b> have the expected text. If the text parameter is <code>null</code> or marked
     * as <code>null</code>, the operation will not be executed. If the text parameter is marked as empty, it will be replaced
     * with "" before verification.
     * 
     * @param expectedText Text to compare this component's text against. */
    public void assertTextNotEquals(String expectedText);

    /** Verifies that this component's text contains the expected text. If the text parameter is <code>null</code> or marked as
     * <code>null</code>, the operation will not be executed. before verification.
     * 
     * @param expectedText Text to search in this component's text. */
    public void assertTextContains(String expectedText);

    /** Verifies that this component's text contains the expected text. It trims both strings and ignores the case. If the text
     * parameter is <code>null</code> or marked as <code>null</code>, the operation will not be executed.
     * 
     * @param expectedText Text to search in this component's text. */
    public void assertTextContainsIgnoreCaseTrimmed(String expectedText);

    /** Verifies that this component's text equals the expected text. It trims both strings and ignores the case. If the text
     * parameter is <code>null</code> or marked as <code>null</code>, the operation will not be executed.
     * 
     * @param expectedText Text to compare this component's text against. */
    public void assertTextEqualsIgnoreCaseTrimmed(String expectedText);

    /** Asserts that this component's text matches the provided {@link Validator}.
     * 
     * @param validator the validator to apply for text verification. */
    public void assertTextMatches(Validator<String> validator);

    /** Verifies that this component's text (interpreted as numeric value) is greater than the specified value (which will be
     * interpreted as numeric value as well). If the parameter is <code>null</code> or marked as <code>null</code>, the operation
     * will not be executed.
     * 
     * @param value Value to interpret as numeric value and to compare the component's text (as numeric value) against. */
    public void assertValueGreaterThan(String value);

    /** Verifies that this component's text (interpreted as numeric value) is lower than the specified value (which will be
     * interpreted as numeric value as well). If the parameter is <code>null</code> or marked as <code>null</code>, the operation
     * will not be executed.
     * 
     * @param value Value to interpret as numeric value and to compare the component's text (as numeric value) against. */
    public void assertValueLessThan(String value);

}
