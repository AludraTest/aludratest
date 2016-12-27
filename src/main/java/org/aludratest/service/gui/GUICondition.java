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

import org.aludratest.service.Condition;
import org.aludratest.service.ElementName;
import org.aludratest.service.ElementType;
import org.aludratest.service.TechnicalArgument;
import org.aludratest.service.TechnicalLocator;
import org.aludratest.service.locator.element.GUIElementLocator;
import org.aludratest.service.locator.window.WindowLocator;

/**
 * Extends the {@link Condition} interface providing
 * GUI related features.
 * @author Marcel Malitz
 * @author Volker Bergmann
 */
public interface GUICondition extends Condition {
    /** Determines that the specified element is somewhere on the page in a given timeout
     * @param elementType the element type to log
     * @param elementName the element name to log
     * @param locator Locator of the element
     * @return <code>true</code> if the element is present, otherwise <code>false</code> */
    boolean isElementPresent(
            @ElementType String elementType,
            @ElementName String elementName,
            @TechnicalLocator GUIElementLocator locator);

    /** Determines if the specified element is visible.
     * @param elementType the element type to log
     * @param elementName the element name to log
     * @param locator Locator of the element
     * @return <code>true</code> if the element is visible, otherwise <code>false</code> */
    boolean isElementVisible(
            @ElementType String elementType,
            @ElementName String elementName,
            @TechnicalLocator GUIElementLocator locator);

    /** Determines whether the specified input element is enabled, ie hasn't been disabled.
     * @param elementType the element type to log
     * @param elementName the element name to log
     * @param locator Locator of the element
     * @return <code>true</code> if the element is enabled, otherwise <code>false</code> */
    boolean isElementEnabled(
            @ElementType String elementType,
            @ElementName String elementName,
            @TechnicalLocator GUIElementLocator locator);

    /** Determines whether the specified input element is editable, i.e. is not disabled and would accept user text input.
     * @param elementType the element type to log.
     * @param elementName the element name to log
     * @param locator Locator of the element.
     * @return <code>true</code> if the element is editable, <code>false</code> otherwise. */
    boolean isElementEditable(@ElementType String elementType, @ElementName String elementName,
            @TechnicalLocator GUIElementLocator locator);

    /** Determines whether the specified input element is editable within the given timeout, i.e. is not disabled and would accept
     * user text input.
     * @param elementType the element type to log
     * @param elementName the element name to log
     * @param locator Locator of the element.
     * @param timeout the maximum number of milliseconds to wait for the element to reach the desired state
     * @return <code>true</code> if the element is editable, <code>false</code> otherwise. */
    boolean isElementEditable(@ElementType String elementType, @ElementName String elementName,
            @TechnicalLocator GUIElementLocator locator, @TechnicalArgument long timeout);

    /** Determines that the specified element is not on the page
     * @param elementType the element type to log
     * @param elementName the element name to log
     * @param locator Locator of the element
     * @return <code>true</code> if the element is not present, otherwise <code>false</code> */
    boolean isElementNotPresent(
            @ElementType String elementType,
            @ElementName String elementName,
            @TechnicalLocator GUIElementLocator locator);

    /** Determines that the specified element is somewhere on the page in a given timeout
     * @param elementType the element type to log
     * @param elementName the element name to log
     * @param locator Locator of the element
     * @param timeout the maximum number of milliseconds to wait for the element to reach the desired state
     * @return <code>true</code> if the element is present, otherwise <code>false</code> */
    boolean isElementPresent(
            @ElementType String elementType,
            @ElementName String elementName,
            @TechnicalLocator GUIElementLocator locator,
            @TechnicalArgument long timeout);

    /** Determines if the specified element is visible within timeout
     * @param elementType the element type to log
     * @param elementName the element name to log
     * @param locator Locator of the element
     * @param timeout the maximum number of milliseconds to wait for the element to reach the desired state
     * @return <code>true</code> if the element is visible, otherwise <code>false</code> */
    boolean isElementVisible(
            @ElementType String elementType,
            @ElementName String elementName,
            @TechnicalLocator GUIElementLocator locator,
            @TechnicalArgument long timeout);

    /** Determines whether the specified input element is enabled in the over given timeout, ie hasn't been disabled.
     * @param elementType the element type to log
     * @param elementName the element name to log
     * @param locator Locator of the element
     * @param timeout the maximum number of milliseconds to wait for the element to reach the desired state
     * @return <code>true</code> if the element is enabled, otherwise <code>false</code> */
    boolean isElementEnabled(
            @ElementType String elementType,
            @ElementName String elementName,
            @TechnicalLocator GUIElementLocator locator,
            @TechnicalArgument long timeout);

    /** Determines that the specified element is not on the page within timeout
     * @param elementType the element type to log
     * @param elementName the element name to log
     * @param locator Locator of the element
     * @param timeout the maximum number of milliseconds to wait for the element to reach the desired state
     * @return <code>true</code> if the element is not present, otherwise <code>false</code> */
    boolean isElementNotPresent(
            @ElementType String elementType,
            @ElementName String elementName,
            @TechnicalLocator GUIElementLocator locator,
            @TechnicalArgument long timeout);

    /** Determines if a specified window is open
     * @param elementType the element type to log
     * @param elementName the element name to log
     * @param locator Locator of the element
     * @return <code>true</code> if the window is open, otherwise <code>false</code> */
    boolean isWindowOpen(@ElementType String elementType, @ElementName String elementName, @TechnicalLocator WindowLocator locator);

    /** Determines that the specified element is somewhere on the page and in foreground
     * @param elementType the element type to log
     * @param elementName the element name to log
     * @param locator Locator of the element
     * @return <code>true</code> if the element is present and in foreground, otherwise <code>false</code> */
    boolean isElementPresentandInForeground(
            @ElementType String elementType,
            @ElementName String elementName,
            @TechnicalLocator GUIElementLocator locator);

    /** Determines if the specified element is checked. This normally only applies to checkboxes.
     * @param elementType the element type to log
     * @param elementName the element name to log
     * @param locator Locator of the element.
     * @return <code>true</code> if the element is checked, <code>false</code> otherwise. */
    boolean isElementChecked(@ElementType String elementType, @ElementName String elementName,
            @TechnicalLocator GUIElementLocator locator);

    /** Determines if the speficied element contains the given labels (and possibly more).
     * @param elementType the element type to log
     * @param elementName the element name to log
     * @param locator Locator of the element.
     * @param labels Labels which are expected in the element.
     * @return <code>true</code> if the labels were found in the element, <code>false</code> otherwise. */
    boolean containsLabels(@ElementType String elementType, @ElementName String elementName,
            @TechnicalLocator GUIElementLocator locator,
            @TechnicalArgument String... labels);

    /** Determines if the speficied element contains the given labels (and only these), optionally with checking the order.
     * @param elementType the element type to log
     * @param elementName the element name to log
     * @param locator Locator of the element.
     * @param checkOrder If <code>true</code>, order of elements is important, otherwise, it is ignored.
     * @param labels Labels which are expected in the element.
     * @return <code>true</code> if the labels equal all labels found in the element, <code>false</code> otherwise. */
    boolean hasLabels(@ElementType String elementType, @ElementName String elementName,
            @TechnicalLocator GUIElementLocator locator, boolean checkOrder, String... labels);

    /** Determines if the speficied element contains the given values (and possibly more).
     * @param elementType the element type to log
     * @param elementName the element name to log
     * @param locator Locator of the element.
     * @param values Values which are expected in the element.
     * @return <code>true</code> if the values were found in the element, <code>false</code> otherwise. */
    boolean containsValues(@ElementType String elementType, @ElementName String elementName,
            @TechnicalLocator GUIElementLocator locator, @TechnicalArgument String... values);

    /** Determines if the speficied element contains the given values (and only these), optionally with checking the order.
     * @param elementType the element type to log
     * @param elementName the element name to log
     * @param locator Locator of the element.
     * @param checkOrder If <code>true</code>, order of elements is important, otherwise, it is ignored.
     * @param values Values which are expected in the element.
     * @return <code>true</code> if the values equal all values found in the element, <code>false</code> otherwise. */
    boolean hasValues(@ElementType String elementType, @ElementName String elementName,
            @TechnicalLocator GUIElementLocator locator, boolean checkOrder, String... values);

}
