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

import java.util.ArrayList;
import java.util.List;

import org.aludratest.service.locator.element.ElementLocators;
import org.aludratest.service.locator.element.ElementLocators.ElementLocatorsGUI;
import org.aludratest.service.locator.element.GUIElementLocator;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

/**
 * Implements Selenium 2's {@link By} interface
 * wrapping an {@link ElementLocators} instance.
 * @author Volker Bergmann
 */
public class ByElementLocators extends By {

    private ElementLocatorsGUI elementLocators;

    /** Constructor.
     *  @param elementLocators an {@link ElementLocators} */
    public ByElementLocators(ElementLocatorsGUI elementLocators) {
        this.elementLocators = elementLocators;
    }

    @Override
    public List<WebElement> findElements(SearchContext context) {
        List<WebElement> result = new ArrayList<WebElement>();
        // assume that the ElementLocators' pointer attribute already has been set correctly.
        // Do NOT fallback to try everything else again, as we would have gotten a new
        // ByElementLocators object by the LocatorUtil class.
        GUIElementLocator usedOption = elementLocators.getUsedOption();
        if (usedOption != null) {
            result.add(context.findElement(LocatorUtil.by(usedOption)));
            return result;
        }

        // ... and try all alternatives
        for (GUIElementLocator alternative : elementLocators) {
            try {
                WebElement element = context.findElement(LocatorUtil.by(alternative));
                // if the element is found, update the pointer and return the element to the caller
                elementLocators.setUsedOption(alternative);
                result.add(element);
                return result;
            } catch (NoSuchElementException e) {
                // if any single locator cannot be found, simply ignore it
                // and loop to the next option
            }
        }
        // if all alternatives have failed, throw a NoSuchElementException
        throw new NoSuchElementException("No element found for locator " + elementLocators);
    }

}
