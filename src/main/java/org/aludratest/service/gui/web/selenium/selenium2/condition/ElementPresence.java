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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

/** Checks an element for presence.
 * @author Volker Bergmann */
public class ElementPresence implements ExpectedCondition<WebElement> {

    private final GUIElementLocator locator;
    private final LocatorSupport locatorSupport;

    /** Constructor.
     * @param locator the {@link GUIElementLocator} of the related GUI element
     * @param locatorSupport */
    public ElementPresence(GUIElementLocator locator, LocatorSupport locatorSupport) {
        this.locator = locator;
        this.locatorSupport = locatorSupport;
    }

    @Override
    public WebElement apply(WebDriver driver) {
        return findElementImmediately(locator, locatorSupport);
    }

    /** Provides the evaluation logic as static public method.
     * @param locator
     * @param locatorSupport
     * @return */
    public static WebElement findElementImmediately(GUIElementLocator locator, LocatorSupport locatorSupport) {
        return locatorSupport.findElementImmediately(locator);
    }

    @Override
    public String toString() {
        return "presence of element located by: " + locator;
    }

}
