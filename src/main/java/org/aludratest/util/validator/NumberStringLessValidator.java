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
package org.aludratest.util.validator;

/**
 * Requires numbers (formatted as strings) to be less than a reference value.
 * @author Volker Bergmann
 */
public class NumberStringLessValidator extends AbstractNumberStringValidator {

    private final Double expected;

    /** Constructor.
     *  @param validationTerm
     *  @param tolerance */
    public NumberStringLessValidator(String validationTerm, double tolerance) {
        super(validationTerm, tolerance);
        this.expected = parseDouble(validationTerm, false);
    }

    @Override
    protected boolean validImpl(String text) {
        Double actual = parseDouble(text, true);
        return (actual - expected < tolerance);
    }

    @Override
    protected String descriptionImpl() {
        return "expects number to be less than " + validationTerm + " with tolerance " + tolerance;
    }

}
