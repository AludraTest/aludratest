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

import java.util.Arrays;

import org.aludratest.service.gui.web.selenium.selenium2.LocatorSupport;
import org.aludratest.service.locator.element.GUIElementLocator;
import org.aludratest.util.DataUtil;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

/** Checks a drop down box for the presence of required values.
 * @author Volker Bergmann
 * @author falbrech */
public class DropDownBoxOptionValuesPresence implements ExpectedCondition<Boolean> {

    private final AnyDropDownOptions dropDownBoxOptions;
    private final String[] expectedValues;
    private final boolean contained;
    private final boolean checkOrder;
    private String message;

    /** Constructor.
     * @param dropDownLocator
     * @param expectedValues
     * @param contained
     * @param checkOrder
     * @param locatorSupport
     * @param wrapper */
    public DropDownBoxOptionValuesPresence(GUIElementLocator dropDownLocator, String[] expectedValues, boolean contained,
            boolean checkOrder, LocatorSupport locatorSupport) {
        this.dropDownBoxOptions = AnyDropDownOptions.createValueCondition(dropDownLocator, locatorSupport);
        this.expectedValues = expectedValues;
        this.contained = contained;
        this.checkOrder = checkOrder;
    }

    /** @return the {@link #message} which has been set if the condition did not match */
    public String getMessage() {
        return message;
    }

    @Override
    public Boolean apply(WebDriver driver) {
        String[] actualValues = null;
        try {
            actualValues = dropDownBoxOptions.apply(driver);
        }
        catch (StaleElementReferenceException e) {
            // 'actualLabels' remains null and this case is handled in the following 'if' clause
        }
        if (actualValues == null) {
            this.message = dropDownBoxOptions.getMessage();
            return false;
        }
        if (contained) {
            String mismatches = DataUtil.containsStrings(expectedValues, actualValues);
            if (mismatches.length() == 0) {
                return true;
            }
            else {
                this.message = "The expected values are not contained in the actual values. Following Value(s) is/are missing: "
                        + mismatches;
                return false;
            }
        }
        else {
            String mismatches;
            // only in this case, checkOrder matters
            if (checkOrder) {
                mismatches = DataUtil.expectEqualArrays(expectedValues, actualValues);
            }
            else {
                mismatches = DataUtil.expectEqualArraysIgnoreOrder(expectedValues, actualValues);
            }
            if (mismatches.length() == 0) {
                return true;
            }

            this.message = "The actual values are not equal to the expected ones. " + mismatches;
            return false;
        }
    }

    @Override
    public String toString() {
        return "presence of the expected values [" + Arrays.toString(expectedValues) + "] in the drop down box located by "
                + dropDownBoxOptions.getDropDownLocator();
    }

}
