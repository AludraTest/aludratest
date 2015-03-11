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

import org.aludratest.service.locator.element.GUIElementLocator;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

/** Performs a combination of checks on a web GUI element: It always checks for presence and if it is in foreground and can be
 * configured to check for visibility and/or clickability additionally. If one of the internal checks fails, the failure message
 * is reported in the {@link #message} property.
 * @author Volker Bergmann */
public class ElementCondition implements ExpectedCondition<WebElement> {

    private GUIElementLocator locator;
    private boolean visible;
    private boolean enabled;
    private String message;

    /** Constructor.
     * @param locator a locator for the element to check
     * @param visible specifies if the element shall be checked for visibility
     * @param enabled specifies if the element shall be check if enabled (clickable) */
    public ElementCondition(GUIElementLocator locator, boolean visible, boolean enabled) {
        this.locator = locator;
        this.visible = visible;
        this.enabled = enabled;
        this.message = null;
    }

    /** @return the {@link #message} which has been set if the condition did not match */
    public String getMessage() {
        return message;
    }

    @Override
    public WebElement apply(WebDriver driver) {
        this.message = null;
        // find element
        WebElement element = ElementPresence.findElementImmediately(locator, driver);
        if (element == null) {
            this.message = "Element not found";
            return null;
        }
        // check if it is in foreground
        if (!ZIndexSupport.isInForeground(element, driver)) {
            this.message = "Element not in foreground";
            return null;
        }
        // check visibility if required
        if (visible && !element.isDisplayed()) {
            this.message = "Element not visible";
            return null;
        }
        // check if it is enabled (clickable or editable)
        if (enabled && !ElementClickable.isClickable(element)) {
            this.message = "Element not editable";
            return null;
        }
        return element;
    }

}
