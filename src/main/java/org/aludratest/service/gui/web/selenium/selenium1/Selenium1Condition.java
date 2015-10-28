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
    public boolean isElementPresent(String elementType, String operation, GUIElementLocator locator) {
        try {
            wrapper.waitForElement(locator);
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
        try {
            wrapper.waitForVisible(locator);
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
        try {
            wrapper.waitForEnabled(locator);
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
    public boolean isElementEditable(String elementType, String elementName, GUIElementLocator locator) {
        try {
            wrapper.waitForEditable(locator);
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
    public boolean isElementEditable(String elementType, String elementName, GUIElementLocator locator, long timeout) {
        try {
            wrapper.waitForEditable(locator, timeout);
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
        try {
            wrapper.waitForElementNotPresent(locator);
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
    public boolean isElementPresent(String elementType, String operation, GUIElementLocator locator, long timeout) {
        try {
            wrapper.waitForElement(locator, timeout);
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
    public boolean isElementVisible(String elementType, String operation, GUIElementLocator locator, long timeout) {
        try {
            wrapper.waitForVisible(locator, timeout);
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
    public boolean isElementEnabled(String elementType, String operation, GUIElementLocator locator, long timeout) {
        try {
            wrapper.waitForEnabled(locator, timeout);
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
    public boolean isElementNotPresent(String elementType, String operation, GUIElementLocator locator, long timeout) {
        try {
            wrapper.waitForElementNotPresent(locator, timeout);
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
            wrapper.waitForElement(locator);
            wrapper.waitForInForeground(locator);
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
            wrapper.waitForElement(locator);
            wrapper.waitForInForeground(locator);
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
        CheckLabelCondition condition = new CheckLabelCondition(true, false, labels, wrapper, locator);
        wrapper.retryUntilTimeout(condition);
        return StringUtil.isEmpty(condition.getMismatches());
    }

    @Override
    public boolean containsValues(String elementType, String elementName, GUIElementLocator locator, String... values) {
        CheckValuesCondition condition = new CheckValuesCondition(true, false, values, wrapper, locator);
        wrapper.retryUntilTimeout(condition);
        return StringUtil.isEmpty(condition.getMismatches());
    }

    @Override
    public boolean hasLabels(String elementType, String elementName, GUIElementLocator locator, boolean checkOrder,
            String... labels) {
        CheckLabelCondition condition = new CheckLabelCondition(false, checkOrder, labels, wrapper, locator);
        wrapper.retryUntilTimeout(condition);
        return StringUtil.isEmpty(condition.getMismatches());
    }

    @Override
    public boolean hasValues(String elementType, String elementName, GUIElementLocator locator, boolean checkOrder,
            String... values) {
        CheckValuesCondition condition = new CheckValuesCondition(false, checkOrder, values, wrapper, locator);
        wrapper.retryUntilTimeout(condition);
        return StringUtil.isEmpty(condition.getMismatches());
    }

}
