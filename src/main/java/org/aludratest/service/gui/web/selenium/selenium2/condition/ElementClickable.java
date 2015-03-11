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

import java.util.Set;

import org.aludratest.service.locator.element.GUIElementLocator;
import org.databene.commons.CollectionUtil;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

/** Checks if a web GUI element exists and is clickable (editable). If one of these checks fails, the failure message is reported
 * in the {@link #message} property.
 * @author Volker Bergmann */
public class ElementClickable implements ExpectedCondition<WebElement> {

    private static final Set<String> EDITABLE_ELEMENTS = CollectionUtil.toSet("input", "textarea", "select", "a");

    private final GUIElementLocator locator;

    private String message;

    /** Constructor.
     * @param locator the {@link GUIElementLocator} of the related GUI element */
    public ElementClickable(GUIElementLocator locator) {
        this.locator = locator;
        this.message = null;
    }

    /** @return the {@link #message} which has been set if the condition did not match */
    public String getMessage() {
        return message;
    }

    @Override
    public WebElement apply(WebDriver driver) {
        this.message = null;
        WebElement element = ElementPresence.findElementImmediately(locator, driver);
        if (element == null) {
            this.message = "Element not found";
            return null;
        }
        if (!isClickable(element)) {
            this.message = "Element not clickable";
            return null;
        }
        return element;
    }

    /** Provides the evaluation logic as static public method.
     * @param element the element to check
     * @return true if the element is clickable, otherwise false */
    public static boolean isClickable(WebElement element) {
        if (!element.isEnabled()) {
            return false;
        }
        String tagName = element.getTagName().toLowerCase();
        if (!EDITABLE_ELEMENTS.contains(tagName)) {
            return false;
        }
        if ("input".equals(tagName) && "true".equals(element.getAttribute("readonly"))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "clickability of element located by: " + locator;
    }

}
