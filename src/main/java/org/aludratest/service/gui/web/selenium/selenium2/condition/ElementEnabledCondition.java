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

public class ElementEnabledCondition extends WebElementCondition {

    private boolean enabled = true;

    public ElementEnabledCondition(GUIElementLocator locator, LocatorSupport locatorSupport, boolean enabled) {
        this(locator, locatorSupport);
        this.enabled = enabled;
    }

    public ElementEnabledCondition(GUIElementLocator locator, LocatorSupport locatorSupport) {
        super(locator, locatorSupport);
    }

    @Override
    protected WebElement applyOnElement(WebElement element) {
        if (enabled && !element.isEnabled()) {
            this.message = "Element not enabled";
            return null;
        }
        if (!enabled && element.isEnabled()) {
            this.message = "Element enabled";
            return null;
        }

        return element;
    }

}
