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

import java.util.List;

import org.aludratest.service.gui.web.selenium.selenium2.LocatorSupport;
import org.aludratest.service.locator.element.GUIElementLocator;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;

/** Collects specific property values of all options of a drop down box. If one of the internal checks fails, the failure message
 * is reported in the {@link #message} property.
 * @author Volker Bergmann */
public class AnyDropDownOptions implements ExpectedCondition<String[]> {

    /** constant for an HTML option's 'value' property. */
    public static final String DROPDOWN_OPTION_VALUE_PROPERTY = "value";

    /** constant for an HTML option's 'text' property, a.k.a. 'label' value. */
    public static final String DROPDOWN_OPTION_LABEL_PROPERTY = "text";

    private final GUIElementLocator dropDownLocator;
    private final String propertyName;
    private final LocatorSupport locatorSupport;

    private String message;

    /** Constructor.
     * @param dropDownLocator
     * @param propertyName
     * @param locatorSupport */
    public AnyDropDownOptions(GUIElementLocator dropDownLocator, String propertyName, LocatorSupport locatorSupport) {
        this.dropDownLocator = dropDownLocator;
        this.propertyName = propertyName;
        this.locatorSupport = locatorSupport;
        this.message = null;
    }

    /** @return the {@link #dropDownLocator} */
    public GUIElementLocator getDropDownLocator() {
        return dropDownLocator;
    }

    /** @return the {@link #message} which has been set if the condition did not match */
    public String getMessage() {
        return message;
    }

    @Override
    public String[] apply(WebDriver driver) {
        WebElement element = null;
        try {
            element = locatorSupport.findElementImmediately(dropDownLocator);
        }
        catch (NoSuchElementException e) {
            this.message = "Element not found";
            return null;
        }
        Select select = new Select(element);
        List<WebElement> options = select.getOptions();
        String[] values = new String[options.size()];
        for (int i = 0; i < options.size(); i++) {
            values[i] = options.get(i).getAttribute(propertyName);
        }
        return values;
    }

    /** Creates an instance that provides the options' labels.
     * @param dropDownLocator
     * @param locatorSupport
     * @return */
    public static AnyDropDownOptions createLabelCondition(GUIElementLocator dropDownLocator, LocatorSupport locatorSupport) {
        return new AnyDropDownOptions(dropDownLocator, DROPDOWN_OPTION_LABEL_PROPERTY, locatorSupport);
    }

    /** Creates an instance that provides the options' value contents.
     * @param dropDownLocator
     * @param locatorSupport
     * @return */
    public static AnyDropDownOptions createValueCondition(GUIElementLocator dropDownLocator, LocatorSupport locatorSupport) {
        return new AnyDropDownOptions(dropDownLocator, DROPDOWN_OPTION_VALUE_PROPERTY, locatorSupport);
    }

    @Override
    public String toString() {
        return "Availability of " + propertyName + " values for the options of the drop down box located by " + dropDownLocator;
    }

}
