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
package org.aludratest.service.gui.web.selenium.selenium2;

import org.aludratest.exception.AludraTestException;
import org.aludratest.service.gui.web.WebGUICondition;
import org.aludratest.service.gui.web.selenium.selenium1.Selenium1Condition;
import org.aludratest.service.locator.element.GUIElementLocator;
import org.aludratest.service.locator.window.WindowLocator;
import org.aludratest.util.DataUtil;
import org.databene.commons.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Provides the {@link WebGUICondition} feature set using Selenium 2.
 * @author Marcel Malitz
 * @author Joerg Langnickel
 * @author Volker Bergmann */
public class Selenium2Condition extends AbstractSelenium2Action implements WebGUICondition {

    private final static Logger LOGGER = LoggerFactory.getLogger(Selenium1Condition.class);

    /** Constructor.
     * @param seleniumWrapper the Selenium2Wrapper to use */
    public Selenium2Condition(Selenium2Wrapper seleniumWrapper) {
        super(seleniumWrapper);
    }

    @Override
    public boolean isElementPresent(String elementType, String elementName, GUIElementLocator locator) {
        return isElementPresent(elementType, elementName, locator, getTimeout());
    }

    @Override
    public boolean isElementPresent(String elementType, String elementName, GUIElementLocator locator, long timeout) {
        try {
            wrapper.waitUntilPresent(locator, timeout);
            return true;
        }
        catch (AludraTestException e) {
            return false;
        }
        catch (Exception e) { // NOSONAR
            LOGGER.error("Unexpected exception during check which will be ignored", e);
            return false;
        }
    }

    @Override
    public boolean isElementVisible(String elementType, String operation, GUIElementLocator locator) {
        return isElementVisible(elementType, operation, locator, getTimeout());
    }

    @Override
    public boolean isElementVisible(String elementType, String operation, GUIElementLocator locator, long timeout) {
        try {
            wrapper.waitUntilVisible(locator, timeout);
            return true;
        }
        catch (AludraTestException e) {
            return false;
        }
        catch (Exception e) { // NOSONAR
            LOGGER.error("Unexpected exception during check which will be ignored", e);
            return false;
        }
    }

    @Override
    public boolean isElementEnabled(String elementType, String operation, GUIElementLocator locator) {
        return isElementEnabled(elementType, operation, locator, getTimeout());
    }

    @Override
    public boolean isElementEnabled(String elementType, String operation, GUIElementLocator locator, long timeout) {
        try {
            wrapper.waitUntilClickable(locator, timeout);
            return true;
        }
        catch (AludraTestException e) {
            return false;
        }
        catch (Exception e) { // NOSONAR
            LOGGER.error("Unexpected exception during check which will be ignored", e);
            return false;
        }
    }

    @Override
    public boolean isElementNotPresent(String elementType, String operation, GUIElementLocator locator) {
        return isElementNotPresent(elementType, operation, locator, getTimeout());
    }

    @Override
    public boolean isElementNotPresent(String elementType, String operation, GUIElementLocator locator, long timeout) {
        try {
            wrapper.waitUntilElementNotPresent(locator, timeout);
            return true;
        }
        catch (AludraTestException e) {
            return false;
        }
        catch (Exception e) { // NOSONAR
            LOGGER.error("Unexpected exception during check which will be ignored", e);
            return false;
        }
    }

    @Override
    public boolean isWindowOpen(String elementType, String operation, WindowLocator locator) {
        try {
            wrapper.selectWindow(locator);
            return true;
        }
        catch (AludraTestException e) {
            return false;
        }
        catch (Exception e) { // NOSONAR
            LOGGER.error("Unexpected exception during check which will be ignored", e);
            return false;
        }
    }

    @Override
    public boolean isElementPresentandInForeground(String elementType, String operation, GUIElementLocator locator) {
        try {
            wrapper.waitUntilPresent(locator, getTimeout());
            wrapper.waitUntilInForeground(locator, 10);
            return true;
        }
        catch (AludraTestException e) {
            return false;
        }
        catch (Exception e) { // NOSONAR
            LOGGER.error("Unexpected exception during check which will be ignored", e);
            return false;
        }
    }

    @Override
    public boolean isElementChecked(String elementType, String elementName, GUIElementLocator locator) {
        try {
            wrapper.waitUntilPresent(locator, getTimeout());
            return wrapper.isChecked(locator);
        }
        catch (AludraTestException e) {
            return false;
        }
        catch (Exception e) { // NOSONAR
            LOGGER.error("Unexpected exception during check which will be ignored", e);
            return false;
        }
    }

    @Override
    public boolean containsLabels(String elementType, String elementName, GUIElementLocator locator, String... labels) {
        return checkLabels(labels, locator, true);
    }

    @Override
    public boolean equalsLabels(String elementType, String elementName, GUIElementLocator locator, String... labels) {
        return checkLabels(labels, locator, false);
    }

    private boolean checkLabels(String[] labels, GUIElementLocator elementLocator, boolean contains) {
        final String[] actualLabels = wrapper.getLabels(elementLocator);
        final String mismatches;
        if (contains) {
            mismatches = DataUtil.containsStrings(labels, actualLabels);
        }
        else {
            mismatches = DataUtil.expectEqualArrays(labels, actualLabels);
        }
        return StringUtil.isEmpty(mismatches);
    }

}
