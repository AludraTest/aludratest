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
package org.aludratest.service.gui.web.selenium.selenium2.condition;

import java.text.MessageFormat;

import org.aludratest.service.gui.web.selenium.selenium2.LocatorSupport;
import org.aludratest.service.locator.element.XPathLocator;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Performs z-index calculations with Selenium 2.
 * @author Marcel Malitz
 * @author Volker Bergmann */
public class ZIndexSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZIndexSupport.class);

    private static final int DEFAULT_Z_INDEX = 0;

    private static final String Z_INDEX_SEARCH_XPATH = "//iframe[contains(@style, ''z-index'')])[{0}]";

    private final LocatorSupport locatorSupport;
    private final WebDriver driver;

    // constructor -------------------------------------------------------------

    /** Constructor.
     * @param locatorSupport */
    public ZIndexSupport(LocatorSupport locatorSupport) {
        this.locatorSupport = locatorSupport;
        this.driver = locatorSupport.getDriver();
    }

    // properties --------------------------------------------------------------

    /** @return the {@link #locatorSupport} */
    public LocatorSupport getLocatorSupport() {
        return locatorSupport;
    }

    // operational interface ---------------------------------------------------

    /** Checks if a element is blocked by a modal dialog.
     * @param element the element to be checked
     * @return true if the element is in the foreground, false if the element is in the background or absent */
    public boolean isInForeground(WebElement element) {
        return (getCurrentZIndex(element) >= getMaxZIndex());
    }

    // implementation ----------------------------------------------------------

    /** To get the z-index (defined in the attribute "style") for the operated element. There are 3 possibilities for retrieving
     * z-index: <br/>
     * <ol>
     * <li>If a z-index is defined for this element or its ancestor, then return this value</li>
     * <li>If no z-index is defined for this element and its ancestor, then use the base z-index for this page</li>
     * <li>For an element of the type "LabelLocator", the base z-index will be returned</li>
     * </ol>
     * @param element The element to check.
     * @return current z-Index */
    private int getCurrentZIndex(WebElement element) {
        String zIndex = null;
        try {
            do {
                zIndex = element.getCssValue("z-index");
                element = locatorSupport.getParent(element);
            } while ("auto".equals(zIndex) && element != null);
        } catch (InvalidSelectorException e) {
            // this occurs when having reached the root element
        }
        int value = parseZIndex(zIndex);
        LOGGER.debug("WebElement {} has z index {}", element, value);
        return value;
    }

    /** To get the biggest value of z-index for all of the elements on current page. The element with the biggest value of z-index
     * will be shown in foreground. The elements with the lower value of z-index will be shown in background.
     * @return the biggest value of z-index on current page */
    private int getMaxZIndex() {
        int zIndex = getBaseZIndex();
        int zIndexCount = 0;
        zIndexCount = getZIndexCount();
        for (int i = 0; i < zIndexCount; i++) {
            int tmpzIndex = getzIndex(i + 1);
            zIndex = (tmpzIndex > zIndex) ? tmpzIndex : zIndex;
        }
        return zIndex;
    }

    private int getBaseZIndex() {
        // If it has a default z-Index defined in code, then get its value
        if (getHistoryFrameCount() > 0) {
            return getzIndex(0);
            // If it has not defined a default z-Index in code, then set it to a default value
        } else {
            return DEFAULT_Z_INDEX;
        }
    }

    private int getZIndexCount() {
        return getXPathCount("//iframe[contains(@style, \"z-index\")]");
    }

    private int getHistoryFrameCount() {
        int historyFrameCount = 0;
        historyFrameCount = getXPathCount("//iframe[starts-with(@id, \"history-frame\")]");
        return historyFrameCount;
    }

    private int getzIndex(int index) {
        int tmpzIndex = DEFAULT_Z_INDEX;
        // If a base value is defined in code, it will overwrite the default value
        try {
            String zIndexSearchXPath = MessageFormat.format(Z_INDEX_SEARCH_XPATH, index);
            WebElement element = locatorSupport.findElementImmediately(new XPathLocator(zIndexSearchXPath));
            String tmpElement = (String) executeScript(element.getAttribute("style"), driver, index);
            tmpzIndex = getzIndexFromStyle(tmpElement);
        } catch (InvalidSelectorException e) {
            // This may happen for some elements and needs to be ignored
        }
        return tmpzIndex;
    }

    private int getzIndexFromStyle(String style) {
        if (style.endsWith("undefined")) {
            // a normal element without z-Index defined
            return getBaseZIndex();
        } else {
            // an element with z-Index
            for (String tmp : style.split(";")) {
                if (tmp.trim().startsWith("z-index")) {
                    return Integer.parseInt(tmp.replace("z-index:", "").trim());
                }
            }
        }
        return getBaseZIndex();
    }

    private int parseZIndex(String zIndexString) {
        if ("auto".equals(zIndexString)) {
            return getBaseZIndex();
        } else {
            return Integer.parseInt(zIndexString.trim());
        }
    }

    private int getXPathCount(String xpath) {
        return driver.findElements(By.xpath(xpath)).size();
    }

    private Object executeScript(String script, Object... arguments) {
        return ((JavascriptExecutor) driver).executeScript(script, arguments);
    }

}
