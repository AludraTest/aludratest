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

import org.aludratest.exception.TechnicalException;
import org.aludratest.service.gui.web.selenium.selenium2.LocatorSupport;
import org.aludratest.service.locator.element.GUIElementLocator;
import org.aludratest.service.locator.option.IndexLocator;
import org.aludratest.service.locator.option.LabelLocator;
import org.aludratest.service.locator.option.OptionLocator;
import org.aludratest.util.DataUtil;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

/** Checks the presence of a drop down entry. If one of the internal checks fails, the failure message is reported in the
 * {@link #message} property.
 * @author Volker Bergmann */
public class DropDownOptionLocatable implements ExpectedCondition<Boolean> {

    private final OptionLocator entryLocator;
    private final AnyDropDownOptions dropDownBoxOptions;
    private String message;

    /** Constructor.
     * @param dropDownLocator
     * @param entryLocator
     * @param locatorSupport
     * @param wrapper */
    public DropDownOptionLocatable(GUIElementLocator dropDownLocator, OptionLocator entryLocator, LocatorSupport locatorSupport) {
        this.dropDownBoxOptions = AnyDropDownOptions.createLabelCondition(dropDownLocator, locatorSupport);
        this.entryLocator = entryLocator;
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
        if (entryLocator instanceof LabelLocator) {
            String expectedLabel = ((LabelLocator) entryLocator).getLabel();
            String mismatch = DataUtil.containsString(expectedLabel, actualLabels);
            if (mismatch.length() == 0) {
                return true;
            }
            else {
                this.message = "The expected labels are not contained in the actual labels. Following Label is missing: "
                        + mismatch;
                return false;
            }

        }
        else if (entryLocator instanceof IndexLocator) {
            Integer requestedIndex = ((IndexLocator) entryLocator).getIndex();
            if (requestedIndex < actualLabels.length) {
                return true;
            }
            else {
                this.message = "The requested index " + requestedIndex + " does not exist";
                return false;
            }
        }
        else {
            throw new TechnicalException("Not a supported drop down option locator type: " + entryLocator.getClass());
        }
    }

    @Override
    public String toString() {
        return "presence of option entry '" + entryLocator + "' in dropDown '" + dropDownBoxOptions.getDropDownLocator() + "'";
    }

}
