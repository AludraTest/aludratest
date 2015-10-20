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

import org.aludratest.exception.FunctionalFailure;
import org.aludratest.util.data.helper.DataMarkerCheck;

/**
 * Parent class for validators that check strings which contain numbers.
 * @author Volker Bergmann
 */
public abstract class AbstractNumberStringValidator extends AbstractNullStringValidator {

    protected final double tolerance;
    private final Double expected;

    /** Constructor.
     *  @param validationTerm the reference value
     *  @param tolerance the numerical tolerance to be accepted */
    public AbstractNumberStringValidator(String validationTerm, double tolerance) {
        super(validationTerm);
        this.tolerance = tolerance;
        this.expected = parseDouble(validationTerm);
    }

    @Override
    protected final boolean validImpl(String text) {
        if (expected == null) {
            return true;
        }

        Double actual = parseDouble(text);
        return actual != null && validImpl(expected.doubleValue(), actual.doubleValue());
    }

    protected abstract boolean validImpl(double validationTerm, double value);

    protected static Double parseDouble(String value) {
        if (value == null || DataMarkerCheck.isNull(value)) {
            return null;
        }
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            throw new FunctionalFailure("Unable to parse '" + value + "' as numeric value.");
        }
    }

}
