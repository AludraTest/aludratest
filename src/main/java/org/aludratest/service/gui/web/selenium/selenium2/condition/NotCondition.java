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

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

/** Negates the given condition for every call to <code>apply()</code>.
 * 
 * @author falbrech */
public class NotCondition implements ExpectedCondition<Boolean> {

    private ExpectedCondition<Boolean> condition;

    /** Creates a new negating condition object.
     * 
     * @param condition Condition to negate. */
    public NotCondition(ExpectedCondition<Boolean> condition) {
        this.condition = condition;
    }

    @Override
    public Boolean apply(WebDriver input) {
        Boolean value = condition.apply(input);
        return value == null ? Boolean.TRUE : Boolean.valueOf(!value.booleanValue());
    }

    @Override
    public String toString() {
        return "not (" + condition.toString() + ")";
    }
}
