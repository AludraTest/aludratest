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
package org.aludratest.service.gui.web.selenium.selenium1;

import org.aludratest.exception.AludraTestException;
import org.aludratest.service.gui.web.WebGUICondition;
import org.aludratest.service.locator.Locator;
import org.aludratest.service.locator.element.GUIElementLocator;
import org.aludratest.service.locator.window.WindowLocator;
import org.databene.commons.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Selenium1 implementation of {@link WebGUICondition}
 * 
 * @author Marcel Malitz
 * @author Joerg Langnickel
 * @author Volker Bergmann
 */
public class Selenium1Condition extends AbstractSeleniumAction implements WebGUICondition {

    private final static Logger LOGGER = LoggerFactory.getLogger(Selenium1Condition.class);

    /**
     * Constructor
     * 
     * @param seleniumWrapper
     *            - wrapper of Selenium on which the conditions will be executed
     */
    public Selenium1Condition(SeleniumWrapper seleniumWrapper) {
        super(seleniumWrapper);
    }

    @Override
    public boolean isElementPresent(String elementType, String operation, Locator locator) {
        final GUIElementLocator elementObject = assertGUIElementLocator(locator);
        try {
            wrapper.waitForElement(elementObject);
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
    public boolean isElementVisible(String elementType, String operation, Locator locator) {
        final GUIElementLocator elementObject = assertGUIElementLocator(locator);
        try {
            wrapper.waitForVisible(elementObject);
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
    public boolean isElementEnabled(String elementType, String operation, Locator locator) {
        final GUIElementLocator elementObject = assertGUIElementLocator(locator);
        try {
            wrapper.waitForEnabled(elementObject);
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
    public boolean isElementNotPresent(String elementType, String operation, Locator locator) {
        final GUIElementLocator elementObject = assertGUIElementLocator(locator);
        try {
            wrapper.waitForElementNotPresent(elementObject);
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
    public boolean isElementPresent(String elementType, String operation, Locator locator, long timeout) {
        final GUIElementLocator elementObject = assertGUIElementLocator(locator);
        try {
            wrapper.waitForElement(elementObject, timeout);
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
    public boolean isElementVisible(String elementType, String operation, Locator locator, long timeout) {
        final GUIElementLocator elementObject = assertGUIElementLocator(locator);
        try {
            wrapper.waitForVisible(elementObject, timeout);
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
    public boolean isElementEnabled(String elementType, String operation, Locator locator, long timeout) {
        final GUIElementLocator elementObject = assertGUIElementLocator(locator);
        try {
            wrapper.waitForEnabled(elementObject, timeout);
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
    public boolean isElementNotPresent(String elementType, String operation, Locator locator, long timeout) {
        final GUIElementLocator elementObject = assertGUIElementLocator(locator);
        try {
            wrapper.waitForElementNotPresent(elementObject, timeout);
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
    public boolean isWindowOpen(String elementType, String operation, Locator locator) {
        final WindowLocator windowObject = getDefaultWindowLocator(wrapper, locator);
        try {
            wrapper.selectWindow(windowObject);
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
    public boolean isElementPresentandInForeground(String elementType, String operation, Locator locator) {
        final GUIElementLocator elementObject = assertGUIElementLocator(locator);
        try {
            wrapper.waitForElement(elementObject);
            wrapper.waitForInForeground(elementObject);
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
    public boolean isElementChecked(String elementType, String elementName, Locator locator) {
        final GUIElementLocator elementObject = assertGUIElementLocator(locator);
        try {
            wrapper.waitForElement(elementObject);
            wrapper.waitForInForeground(elementObject);
            return wrapper.isChecked(elementObject);
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
    public boolean containsLabels(String elementType, String elementName, Locator locator, String... labels) {
        GUIElementLocator elementLocator = assertGUIElementLocator(locator);
        CheckLabelCondition condition = new CheckLabelCondition(true, labels, wrapper, elementLocator);
        wrapper.retryUntilTimeout(condition);
        return StringUtil.isEmpty(condition.getMismatches());
    }

    @Override
    public boolean equalsLabels(String elementType, String elementName, Locator locator, String... labels) {
        GUIElementLocator elementLocator = assertGUIElementLocator(locator);
        CheckLabelCondition condition = new CheckLabelCondition(false, labels, wrapper, elementLocator);
        wrapper.retryUntilTimeout(condition);
        return StringUtil.isEmpty(condition.getMismatches());
    }
}
