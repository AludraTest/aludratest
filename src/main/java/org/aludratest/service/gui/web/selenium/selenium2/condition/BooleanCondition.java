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

/** Parent class for {@link ExpectedCondition} implementations that rely on the presence of a {@link WebElement} and return a
 * boolean value. If one of the internal checks fails, the failure message is reported in the {@link #message} property.
 * @author Volker Bergmann */
public abstract class BooleanCondition extends AbstractElementCondition<Boolean> {

    /** Full constructor
     * @param locator
     * @param locatorSupport */
    public BooleanCondition(GUIElementLocator locator, LocatorSupport locatorSupport) {
        super(locator, locatorSupport);
    }

    @Override
    public final Boolean apply(WebDriver driver) {
        this.message = null;
        WebElement element = findElementImmediately();
        if (element == null) {
            return false;
        }
        return applyOnElement(element);
    }

    protected abstract Boolean applyOnElement(WebElement element);

}
