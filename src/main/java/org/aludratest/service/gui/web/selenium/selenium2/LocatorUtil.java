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
package org.aludratest.service.gui.web.selenium.selenium2;

import org.aludratest.service.locator.Locator;
import org.aludratest.service.locator.element.CSSLocator;
import org.aludratest.service.locator.element.ElementLocators.ElementLocatorsGUI;
import org.aludratest.service.locator.element.IdLocator;
import org.aludratest.service.locator.element.LabelLocator;
import org.aludratest.service.locator.element.XPathLocator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Maps AludraTest {@link Locator}s to Selenium 2 {@link By} objects.
 * @author Volker Bergmann
 */
public class LocatorUtil {

    /** Private constructor of utility class preventing instantiation by other classes */
    private LocatorUtil() {
    }

    public static WebElement findElement(Locator locator, WebDriver driver) {
        return driver.findElement(by(locator));
    }

    public static By by(Locator locator) {
        if (locator == null) {
            throw new IllegalArgumentException("Locator is null");
        }
        else if (locator instanceof ElementLocatorsGUI) {
            return new ByElementLocators((ElementLocatorsGUI) locator);
        } else if (locator instanceof IdLocator) {
            return By.cssSelector("[id$='" + locator.toString() + "']");
        } else if (locator instanceof CSSLocator) {
            return By.cssSelector(locator.toString());
        } else if (locator instanceof LabelLocator) {
            return By.linkText(locator.toString());
        } else if (locator instanceof XPathLocator) {
            return By.xpath(locator.toString());
        } else {
            throw new UnsupportedOperationException("Unsupported locator type: " + locator.getClass().getName());
        }
    }

}
