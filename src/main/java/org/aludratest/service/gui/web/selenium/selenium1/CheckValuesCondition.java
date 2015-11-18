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
package org.aludratest.service.gui.web.selenium.selenium1;

import org.aludratest.service.gui.web.selenium.ConditionCheck;
import org.aludratest.service.locator.element.GUIElementLocator;
import org.aludratest.util.DataUtil;
import org.databene.commons.StringUtil;

/** Package visible helper class for both Selenium1Condition and Selenium1Verification. */
class CheckValuesCondition implements ConditionCheck {
    private boolean contains;
    private boolean checkOrder;
    private String[] values;
    private String mismatches;
    private SeleniumWrapper wrapper;
    private GUIElementLocator elementLocator;

    CheckValuesCondition(boolean contains, boolean checkOrder, String[] values, SeleniumWrapper wrapper,
            GUIElementLocator elementLocator) {
        this.contains = contains;
        this.checkOrder = checkOrder;
        this.values = (values != null ? values.clone() : null);
        this.wrapper = wrapper;
        this.elementLocator = elementLocator;
    }

    @Override
    public boolean eval() {
        String[] actualValues = wrapper.getValues(elementLocator);
        if (contains) {
            mismatches = DataUtil.containsStrings(values, actualValues);
        } else {
            if (checkOrder) {
                mismatches = DataUtil.expectEqualArrays(values, actualValues);
            }
            else {
                mismatches = DataUtil.expectEqualArraysIgnoreOrder(values, actualValues);
            }
        }
        return (StringUtil.isEmpty(mismatches));
    }

    public String getMismatches() {
        return mismatches;
    }
}