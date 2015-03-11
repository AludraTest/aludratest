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

import org.aludratest.service.gui.web.selenium.selenium2.Selenium2Wrapper;
import org.aludratest.service.locator.element.GUIElementLocator;
import org.aludratest.service.locator.option.IndexLocator;
import org.aludratest.service.locator.option.LabelLocator;
import org.aludratest.service.locator.option.OptionLocator;
import org.aludratest.util.DataUtil;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

/** Checks the presence of a drop down entry.
 * @author Volker Bergmann */
public class DropDownEntryPresence implements ExpectedCondition<Boolean> {

    private final GUIElementLocator dropDownLocator;
    private final OptionLocator entryLocator;
    private final Selenium2Wrapper wrapper;

    /** Constructor.
     * @param dropDownLocator
     * @param entryLocator
     * @param wrapper */
    public DropDownEntryPresence(GUIElementLocator dropDownLocator, OptionLocator entryLocator, Selenium2Wrapper wrapper) {
        this.dropDownLocator = dropDownLocator;
        this.entryLocator = entryLocator;
        this.wrapper = wrapper;
    }

    @Override
    public Boolean apply(WebDriver driver) {
        String[] actualLabels = wrapper.getLabels(dropDownLocator);
        if (entryLocator instanceof LabelLocator) {
            String label = ((LabelLocator) entryLocator).getLabel();
            return (DataUtil.containsString(label, actualLabels).length() == 0);
        }
        else {
            return ((entryLocator instanceof IndexLocator) && ((IndexLocator) entryLocator).getIndex() < actualLabels.length);
        }
    }

    @Override
    public String toString() {
        return "presence of option entry '" + entryLocator + "' in dropDown '" + dropDownLocator + "'";
    }

}
