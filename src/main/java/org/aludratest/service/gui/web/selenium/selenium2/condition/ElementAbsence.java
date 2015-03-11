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

import org.aludratest.service.gui.web.selenium.selenium2.LocatorUtil;
import org.aludratest.service.locator.element.GUIElementLocator;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

/** Checks the absence of an element.
 * @author Volker Bergmann */
public class ElementAbsence implements ExpectedCondition<Boolean> {

    final GUIElementLocator locator;

    /** Constructor.
     * @param locator the {@link GUIElementLocator} of the related GUI element */
    public ElementAbsence(GUIElementLocator locator) {
        this.locator = locator;
    }

    @Override
    public Boolean apply(WebDriver driver) {
        WebElement elem = LocatorUtil.findElementImmediately(locator, driver);
        return (elem == null);
    }

    @Override
    public String toString() {
        return "absence of element located by: " + locator;
    }

}
