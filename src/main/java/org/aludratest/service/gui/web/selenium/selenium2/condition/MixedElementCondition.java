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

import org.aludratest.service.gui.web.selenium.selenium2.LocatorSupport;
import org.aludratest.service.locator.element.GUIElementLocator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

/** Performs a combination of checks on a web GUI element: It always checks for presence and if it is in foreground and can be
 * configured to check for visibility additionally. If one of the internal checks fails, the failure message is reported in the
 * {@link #message} property.
 * @author Volker Bergmann */
public class MixedElementCondition extends WebElementCondition {

    private ZIndexSupport zIndexSupport;
    private boolean visible;
    private boolean enabled;

    /** Constructor.
     * @param locator a locator for the element to check
     * @param locatorSupport
     * @param visible specifies if the element shall be checked for visibility
     * @param enabled specified if the element shall be checked for enabled state */
    public MixedElementCondition(GUIElementLocator locator, LocatorSupport locatorSupport, boolean visible, boolean enabled) {
        super(locator, locatorSupport);
        this.zIndexSupport = new ZIndexSupport(locatorSupport);
        this.visible = visible;
        this.enabled = enabled;
        this.message = null;
    }

    @Override
    protected WebElement applyOnElement(WebElement element) {
        // check if it is in foreground
        if (!zIndexSupport.isInForeground(element)) {
            this.message = "Element not in foreground";
            return null;
        }
        // check visibility if required
        if (visible && !element.isDisplayed()) {
            // try to scroll to element
            try {
                Actions actions = new Actions(locatorSupport.getDriver());
                actions.moveToElement(LocatorSupport.unwrap(element)).perform();
            }
            catch (Throwable t) {
                // ignore
            }

            if (!element.isDisplayed()) {
                this.message = "Element not visible";
                return null;
            }
        }
        if (enabled && !element.isEnabled()) {
            this.message = "Element not enabled";
            return null;
        }

        return element;
    }

    @Override
    public String toString() {
        return "Foreground position " + (visible ? "and visibility " : "") + " of the element located by " + locator;
    }

}
