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

/** Tries to convert a String value to a numeric value and compares it to a given expected value. A String is only considered valid
 * when its absolute difference to the expected value is less than the given tolerance. When the String cannot be parsed as a
 * Double, a Functional Failure is raised.
 * 
 * @author falbrech */
public class NumberStringEqualsValidator extends AbstractNumberStringValidator {

    /** Constructs a new validator.
     * 
     * @param validationTerm String representation of a number, which can be passed to <code>Double.parseDouble()</code>.
     * @param tolerance Tolerance to use for comparison. */
    public NumberStringEqualsValidator(String validationTerm, double tolerance) {
        super(validationTerm, tolerance);
    }

    @Override
    protected boolean validImpl(double validationTerm, double value) {
        return Math.abs(validationTerm - value) < tolerance;
    }

    @Override
    protected String descriptionImpl() {
        return "expects number to be equal to " + validationTerm + " with tolerance " + tolerance;
    }

}
