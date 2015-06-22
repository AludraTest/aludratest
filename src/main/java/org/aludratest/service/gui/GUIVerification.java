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
package org.aludratest.service.gui;

import org.aludratest.impl.log4testing.ElementName;
import org.aludratest.impl.log4testing.ElementType;
import org.aludratest.impl.log4testing.TechnicalLocator;
import org.aludratest.service.Interaction;
import org.aludratest.service.Verification;
import org.aludratest.service.locator.element.GUIElementLocator;
import org.databene.commons.Validator;

/** The interface {@link Verification} provides several methods to verify values in the active screen of the application under
 * test. Every class which implements this interface must assure that a call of one of these methods verifies the expected value
 * with the current in the application under test.<br/>
 * Methods with locators accept two types of locators.
 * <ol>
 * <li>The first type is a by the caller typed locator.</li>
 * <li>The second type is just a String which will be automatically transformed into the default locator. In which locator the
 * String will be transformed depends on the implementation of each method of this interface.</li>
 * </ol>
 * For interactions with the application under test see {@link Interaction}.
 * @author Marcel Malitz
 * @author Volker Bergmann */
public interface GUIVerification extends Verification {

    /** Verifies that one specific GUI element exhibits a text that matches a {@link Validator}.
     * @param elementType Type of the element, depending on the implementation.
     * @param elementName Name of the element.
     * @param locator Locator of the element.
     * @param validator A Validator that decides if the text is correct */
    void assertTextMatches(@ElementType String elementType, @ElementName String elementName,
            @TechnicalLocator GUIElementLocator locator,
            Validator<String> validator);

    /** Verifies for an element identified by a locator that it is visible. Elements not being visible will raise an exception
     * which will be handled by the test framework.
     * @param elementType Type of the element, depending on the implementation.
     * @param elementName Name of the element.
     * @param locator Locator of the element. */
    void assertVisible(@ElementType String elementType, @ElementName String elementName,
            @TechnicalLocator GUIElementLocator locator);

    /** Verifies for an element identified by a locator that it is editable. Read-only elements will raise an exception which will
     * be handled by the test framework. <br>
     * Usually, an "editable" state only applies to text input components, which can have a "read-only" state. Other components
     * are always treated as not editable. Use {@link #assertEnabled(String, String, GUIElementLocator)} to verify the "enabled"
     * state, which applies to most components.
     * 
     * @param elementType Type of the element, depending on the implementation.
     * @param elementName Name of the element.
     * @param locator Locator of the element. */
    void assertEditable(@ElementType String elementType, @ElementName String elementName,
            @TechnicalLocator GUIElementLocator locator);

    /** Verifies for an element identified by a locator that it is <b>not</b> editable. Editable elements will raise an exception
     * which will be handled by the test framework. <br>
     * Usually, an "editable" state only applies to text input components, which can have a "read-only" state. Other components
     * are always treated as not editable. Use {@link #assertNotEnabled(String, String, GUIElementLocator)} to verify the
     * "enabled" state, which applies to most components.
     * @param elementType Type of the element, depending on the implementation.
     * @param elementName Name of the element.
     * @param locator Locator of the element. */
    void assertNotEditable(@ElementType String elementType, @ElementName String elementName,
            @TechnicalLocator GUIElementLocator locator);

    /** Verifies for an element identified by a locator that it is enabled, i.e. not disabled. The "disabled" state is defined by
     * the GUI implementation, but in most GUIs, disabled components are shown in a somewhat "grayed" state and do not accept any
     * user input.
     * @param elementType Type of the element, depending on the implementation.
     * @param elementName Name of the element.
     * @param locator Locator of the element. */
    void assertEnabled(@ElementType String elementType, @ElementName String elementName,
            @TechnicalLocator GUIElementLocator locator);

    /** Verifies for an element identified by a locator that it is <b>not</b> enabled, i.e. disabled. The "disabled" state is
     * defined by the GUI implementation, but in most GUIs, disabled components are shown in a somewhat "grayed" state and do not
     * accept any user input.
     * @param elementType Type of the element, depending on the implementation.
     * @param elementName Name of the element.
     * @param locator Locator of the element. */
    void assertNotEnabled(@ElementType String elementType, @ElementName String elementName,
            @TechnicalLocator GUIElementLocator locator);

    /** Verifies for an element identified by a locator that it is present in the GUI (maybe visible or invisible). Elements not
     * being present will raise an exception which will be handled by the test framework.
     * @param elementType Type of the element, depending on the implementation.
     * @param elementName Name of the element.
     * @param locator Locator of the element. */
    void assertElementPresent(@ElementType String elementType, @ElementName String elementName,
            @TechnicalLocator GUIElementLocator locator);

    /** Verifies for an element identified by a locator that is is checked (e.g. radio buttons or checkboxes). Elements not being
     * checked will raise an exception which will be handled by the test framework.
     * @param elementType Type of the element, depending on the implementation.
     * @param elementName Name of the element.
     * @param locator Locator of the element. */
    void assertChecked(@ElementType String elementType, @ElementName String elementName,
            @TechnicalLocator GUIElementLocator locator);

    /** Verifies for an element identified by a locator that its checked status (e.g. radio buttons or checkboxes) matches the
     * given expected state. Elements having a checked state different than the expected state will raise an exception which will
     * be handled by the test framework.
     * @param elementType Type of the element, depending on the implementation.
     * @param elementName Name of the element.
     * @param expected The expected checked state (<code>true</code> for checked, <code>false</code> for unchecked)
     * @param locator Locator of the element. */
    void assertChecked(@ElementType String elementType, @ElementName String elementName, boolean expected,
            @TechnicalLocator GUIElementLocator locator);

    /** Verifies for an element identified by a locator that it has the input focus. Elements not having the input focus will raise
     * an exception which will be handled by the test framework.
     * @param elementType Type of the element, depending on the implementation.
     * @param elementName Name of the element.
     * @param locator Locator of the element. */
    void assertHasFocus(@ElementType String elementType, @ElementName String elementName,
            @TechnicalLocator GUIElementLocator locator);

    /** Verifies for an element identified by a locator (usually a dropdownbox) that it has the given values (and only these).
     * Elements not having the given values will raise an exception which will be handled by the test framework.
     * @param elementType Type of the element, depending on the implementation.
     * @param elementName Name of the element.
     * @param locator Locator of the element.
     * @param expectedValues Values which are expexted in the element. */
    void assertHasValues(@ElementType String elementType, @ElementName String elementName,
            @TechnicalLocator GUIElementLocator locator,
            String[] expectedValues);

    /** Verifies for an element identified by a locator (usually a dropdownbox) that it has the given labels (and only these).
     * Elements not having the given labels will raise an exception which will be handled by the test framework.
     * @param elementType Type of the element, depending on the implementation.
     * @param elementName Name of the element.
     * @param locator Locator of the element.
     * @param expectedLabels Labels which are expexted in the element. */
    void assertHasLabels(@ElementType String elementType, @ElementName String elementName,
            @TechnicalLocator GUIElementLocator locator,
            String[] expectedLabels);

    /** Verifies for an input element identified by a locator (usually an input field, a button, or similar) that its value matches
     * the given Validator. Elements with an unmatched value will raise an exception which will be handled by the test framework.
     * @param elementType Type of the element, depending on the implementation.
     * @param elementName Name of the element.
     * @param locator Locator of the element.
     * @param validator Validator to validate the value of the element. */
    void assertValueMatches(@ElementType String elementType, @ElementName String elementName,
            @TechnicalLocator GUIElementLocator locator,
            Validator<String> validator);

    /** Verifies for an element identified by a locator (usually a dropdownbox) that it contains the given labels (and possibly
     * more). Elements not containing the given labels will raise an exception which will be handled by the test framework.
     * @param elementType Type of the element, depending on the implementation.
     * @param elementName Name of the element.
     * @param locator Locator of the element.
     * @param labels Labels which are expexted to be contained in the element. */
    void assertContainsLabels(@ElementType String elementType, @ElementName String elementName,
            @TechnicalLocator GUIElementLocator locator,
            String[] labels);

    /** Verifies for an element identified by a locator that it is <b>not</b> present in the GUI (not either visible nor
     * invisible). Elements being present (visible or invisible) will raise an exception which will be handled by the test
     * framework.
     * @param elementType Type of the element, depending on the implementation.
     * @param elementName Name of the element.
     * @param locator Locator of the element. */
    void assertElementNotPresent(@ElementType String elementType, @ElementName String elementName,
            @TechnicalLocator GUIElementLocator locator);

    /** Verifies for a dropdownbox identified by a locator that its selection matches the given Validator. Elements with an
     * unmatching selection will raise an exception which will be handled by the test framework.
     * @param elementType Type of the element, depending on the implementation.
     * @param elementName Name of the element.
     * @param locator Locator of the element.
     * @param validator Validator to validate the selection of the element. */
    void assertDropDownEntrySelectionMatches(@ElementType String elementType, @ElementName String elementName,
            @TechnicalLocator GUIElementLocator locator, Validator<String> validator);

}
