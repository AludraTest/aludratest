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
import org.aludratest.impl.log4testing.TechnicalArgument;
import org.aludratest.impl.log4testing.TechnicalLocator;
import org.aludratest.service.Condition;
import org.aludratest.service.locator.Locator;

/**
 * Extends the {@link Condition} interface providing
 * GUI related features.
 * @author Marcel Malitz
 * @author Volker Bergmann
 */
public interface GUICondition extends Condition {
    /**
     * Determines that the specified element is somewhere on the page in a given timeout
     */
    boolean isElementPresent(
            @ElementType String elementType,
            @ElementName String elementName,
            @TechnicalLocator Locator locator);

    /**
     * Determines if the specified element is visible.
     */
    boolean isElementVisible(
            @ElementType String elementType,
            @ElementName String elementName,
            @TechnicalLocator Locator locator);

    /**
     * Determines whether the specified input element is editable, ie hasn't been disabled.
     */
    boolean isElementEnabled(
            @ElementType String elementType,
            @ElementName String elementName,
            @TechnicalLocator Locator locator);

    /**
     * Determines that the specified element is not on the page
     */
    boolean isElementNotPresent(
            @ElementType String elementType,
            @ElementName String elementName,
            @TechnicalLocator Locator locator);

    /**
     * Determines that the specified element is somewhere on the page in a given timeout
     */
    boolean isElementPresent(
            @ElementType String elementType,
            @ElementName String elementName,
            @TechnicalLocator Locator locator,
            @TechnicalArgument long timeout);

    /**
     * Determines if the specified element is visible within timeout
     */
    boolean isElementVisible(
            @ElementType String elementType,
            @ElementName String elementName,
            @TechnicalLocator Locator locator,
            @TechnicalArgument long timeout);

    /**
     * Determines whether the specified input element is editable in the over given timeout, ie hasn't been disabled.
     */
    boolean isElementEnabled(
            @ElementType String elementType,
            @ElementName String elementName,
            @TechnicalLocator Locator locator,
            @TechnicalArgument long timeout);

    /**
     *  Determines that the specified element is not on the page within timeout
     */
    boolean isElementNotPresent(
            @ElementType String elementType,
            @ElementName String elementName,
            @TechnicalLocator Locator locator,
            @TechnicalArgument long timeout);

    /**
     * Determines if a specified window is open
     */
    boolean isWindowOpen(
            @ElementType String elementType,
            @ElementName String elementName,
            @TechnicalLocator Locator locator);

    /**
     * Determines that the specified element is somewhere on the page and in foreground
     */
    boolean isElementPresentandInForeground(
            @ElementType String elementType,
            @ElementName String elementName,
            @TechnicalLocator Locator locator);

    /** Determines if the specified element is checked. This normally only applies to checkboxes.
     * 
     * @param elementType Type of the element.
     * @param elementName Name of the element.
     * @param locator Locator of the element.
     * 
     * @return <code>true</code> if the element is checked, <code>false</code> otherwise. */
    boolean isElementChecked(@ElementType String elementType, @ElementName String elementName, @TechnicalLocator Locator locator);

    /**
     * Determines if the speficied element contains the given labels (and possibly more).
     * 
     * @param elementType Type of the element.
     * @param elementName Name of the element.
     * @param locator Locator of the element.
     * @param labels Labels which are expected in the element.
     * 
     * @return <code>true</code> if the labels were found in the element, <code>false</code> otherwise.
     */
    boolean containsLabels(@ElementType String elementType, @ElementName String elementName, @TechnicalLocator Locator locator,
            @TechnicalArgument String... labels);

    /** Determines if the speficied element contains the given labels (and only these), in the same order as specified.
     * 
     * @param elementType Type of the element.
     * @param elementName Name of the element.
     * @param locator Locator of the element.
     * @param labels Labels which are expected in the element.
     * 
     * @return <code>true</code> if the labels equal all labels found in the element, <code>false</code> otherwise. */
    boolean equalsLabels(@ElementType String elementType, @ElementName String elementName, @TechnicalLocator Locator locator,
            @TechnicalArgument String... labels);

}
