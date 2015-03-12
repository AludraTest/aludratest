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
import org.aludratest.util.DataUtil;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

/** Checks a drop down box for the presence of required entries.
 * @author Volker Bergmann */
public class DropDownBoxOptionLabelsPresence implements ExpectedCondition<Boolean> {

    private final AnyDropDownOptions dropDownBoxOptions;
    private final String[] expectedLabels;
    private final boolean contained;
    private String message;

    /** Constructor.
     * @param dropDownLocator
     * @param expectedLabels
     * @param contained
     * @param locatorSupport
     * @param wrapper */
    public DropDownBoxOptionLabelsPresence(GUIElementLocator dropDownLocator, String[] expectedLabels, boolean contained,
            LocatorSupport locatorSupport) {
        this.dropDownBoxOptions = AnyDropDownOptions.createLabelCondition(dropDownLocator, locatorSupport);
        this.expectedLabels = expectedLabels;
        this.contained = contained;
    }

    /** @return the {@link #message} which has been set if the condition did not match */
    public String getMessage() {
        return message;
    }

    @Override
    public Boolean apply(WebDriver driver) {
        String[] actualLabels = null;
        try {
            actualLabels = dropDownBoxOptions.apply(driver);
        }
        catch (StaleElementReferenceException e) {
            // 'actualLabels' remains null and this case is handled in the following 'if' clause
        }
        if (actualLabels == null) {
            this.message = dropDownBoxOptions.getMessage();
            return false;
        }
        if (contained) {
            String mismatches = DataUtil.containsStrings(expectedLabels, actualLabels);
            if (mismatches.length() == 0) {
                return true;
            }
            else {
                this.message = "The expected labels are not contained in the actual labels. Following Label(s) is/are missing: "
                        + mismatches;
                return false;
            }
        }
        else {
            String mismatches = DataUtil.expectEqualArrays(expectedLabels, actualLabels);
            if (mismatches.length() == 0) {
                return true;
            } else {
                this.message = "The actual labels are not equal to the expected ones. As follows the unequal pairs "
                        + "(expected!=actual): " + mismatches;
                return false;
            }
        }
    }

}
