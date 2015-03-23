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
import org.databene.commons.Validator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

/** Parent class for {@link ExpectedCondition} implementations that validates a text related to a {@link WebElement} and uses a
 * {@link Validator}. It returns true if and only if the element is found and the validator accepts the value. If one of the
 * internal checks fails, the failure message is reported in the {@link #message} property.
 * @author Volker Bergmann */
public abstract class ValidatingCondition extends BooleanCondition {

    protected final Validator<String> validator;
    protected final String checkedType;

    /** Full constructor.
     * @param locator
     * @param locatorSupport
     * @param validator
     * @param checkedType */
    public ValidatingCondition(GUIElementLocator locator, LocatorSupport locatorSupport, Validator<String> validator,
            String checkedType) {
        super(locator, locatorSupport);
        this.validator = validator;
        this.checkedType = checkedType;
    }

    @Override
    protected Boolean applyOnElement(WebElement element) {
        String actualText = getTextToValidate(element);
        if (!validator.valid(actualText)) {
            this.message = checkedType + " '" + actualText + "' does not match the validator " + validator;
            return false;
        }
        return true;
    }

    protected abstract String getTextToValidate(WebElement element);

}
