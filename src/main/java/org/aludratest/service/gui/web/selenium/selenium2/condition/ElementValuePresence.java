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

/** Checks an element for the presence of a 'value' attribute. If one of the internal checks fails, the failure message is reported
 * in the {@link #message} property.
 * @author Volker Bergmann */
public class ElementValuePresence extends StringCondition {

    /** Constructor.
     * @param locator a locator of the element to be examined
     * @param locatorSupport */
    public ElementValuePresence(GUIElementLocator locator, LocatorSupport locatorSupport) {
        super(locator, locatorSupport);
    }

    @Override
    protected String applyOnElement(WebElement element) {
        String value = element.getAttribute("value");
        if (value == null) {
            this.message = "Value not set";
            return null;
        }
        return value;
    }

    @Override
    public String toString() {
        return "presence of a value attribute in the element located by " + locator;
    }

}
