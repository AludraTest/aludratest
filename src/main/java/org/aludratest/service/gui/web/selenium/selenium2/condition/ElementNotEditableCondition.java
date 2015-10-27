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

import java.util.Locale;

import org.aludratest.service.gui.web.selenium.selenium2.LocatorSupport;
import org.aludratest.service.locator.element.GUIElementLocator;
import org.openqa.selenium.WebElement;

public class ElementNotEditableCondition extends ElementEnabledCondition {

    public ElementNotEditableCondition(GUIElementLocator locator, LocatorSupport locatorSupport) {
        super(locator, locatorSupport);
    }

    @Override
    protected WebElement applyOnElement(WebElement element) {
        if (super.applyOnElement(element) == null) {
            // treat "not enabled" as "not editable" -> good here
            return element;
        }

        // element must be text based input - if not, treat as "not editable"
        if (!"input".equalsIgnoreCase(element.getTagName())) {
            return element;
        }

        String tp = element.getAttribute("type");
        // all of these do NOT have a textual input representation
        if (tp == null || tp.toLowerCase(Locale.US).matches("button|checkbox|color|hidden|image|radio|reset|search|submit")) {
            return element;
        }

        String roAttr = element.getAttribute("readonly");
        if (roAttr != null && ("readonly".equals(roAttr) || "true".equals(roAttr))) {
            return element;
        }

        this.message = "Element editable";
        return null;
    }

}
