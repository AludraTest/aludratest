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
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;

/** Checks if an element has a selected option. If one of the internal checks fails, the failure message is reported in the
 * {@link #message} property.
 * @author Volker Bergmann */
public class OptionSelected implements ExpectedCondition<String> {

    private final GUIElementLocator locator;
    private final LocatorSupport locatorSupport;
    private String message;

    /** Constructor.
     * @param locator
     * @param locatorSupport */
    public OptionSelected(GUIElementLocator locator, LocatorSupport locatorSupport) {
        this.locator = locator;
        this.locatorSupport = locatorSupport;
        this.message = null;
    }

    /** @return the {@link #message} which has been set if the condition did not match */
    public String getMessage() {
        return message;
    }

    @Override
    public String apply(WebDriver driver) {
        this.message = null;
        WebElement element = null;
        try {
            element = ElementPresence.findElementImmediately(locator, locatorSupport);
        }
        catch (NoSuchElementException e) {
            this.message = "Element not found";
            return null;
        }
        Select select = new Select(element);
        String text = select.getFirstSelectedOption().getText();
        if (text == null) {
            this.message = "No option selected";
            return null;
        }
        return text;
    }

}
