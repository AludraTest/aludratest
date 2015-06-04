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

import java.util.Collections;
import java.util.List;

import org.aludratest.service.locator.element.ElementLocators;
import org.aludratest.service.locator.element.ElementLocators.ElementLocatorsGUI;
import org.aludratest.service.locator.element.GUIElementLocator;
import org.aludratest.service.locator.element.IdLocator;
import org.aludratest.service.locator.element.XPathLocator;
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
        GUIElementLocator usedOption = elementLocators.getUsedOption();
        if (usedOption != null) {
            return Collections.singletonList(context.findElement(LocatorSupport.by(usedOption)));
        }

        // build an XPath OR string to have only one XPath for invocation, if possible
        String xpath = buildXPathLocator(elementLocators);
        if (xpath == null) {
            return Collections.singletonList(iterativeFindElement(context));
        }

        WebElement element = context.findElement(By.xpath(xpath));
        String id = element.getAttribute("id");
        if (id != null && !"".equals(id)) {
            elementLocators.setUsedOption(new IdLocator(id));
        }

        return Collections.singletonList(element);
    }

    private String buildXPathLocator(ElementLocatorsGUI locators) {
        StringBuilder sb = new StringBuilder();
        for (GUIElementLocator locator : locators) {
            if (sb.length() > 0) {
                sb.append("|");
            }
            if (locator instanceof XPathLocator) {
                sb.append(locator.toString());
            }
            else if (locator instanceof IdLocator) {
                sb.append("//*[@id='" + locator.toString() + "']");
            }
            else if (locator instanceof ElementLocatorsGUI) {
                // recursive element
                String xpath = buildXPathLocator((ElementLocatorsGUI) locator);
                if (xpath == null) {
                    return null;
                }
                sb.append(xpath);
            }
            else {
                // unsupported locator type, fallback to slow method
                return null;
            }
        }

        return sb.toString();
    }

    private WebElement iterativeFindElement(SearchContext context) {
        // try all alternatives
        for (GUIElementLocator alternative : elementLocators) {
            try {
                WebElement element = context.findElement(LocatorSupport.by(alternative));
                // if the element is found, update the pointer and return the element to the caller
                elementLocators.setUsedOption(alternative);
                return element;
            } catch (NoSuchElementException e) {
                // if any single locator cannot be found, simply ignore it
                // and loop to the next option
            }
        }

        throw new NoSuchElementException("No element found for locator " + elementLocators);
    }

}
