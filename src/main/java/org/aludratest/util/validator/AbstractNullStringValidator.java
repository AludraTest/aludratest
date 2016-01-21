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

import org.aludratest.util.data.helper.DataMarkerCheck;
import org.databene.commons.Validator;

/**
 * Parent class for AludraTest text validations: 
 * If the validation term is null, empty or equal to 
 * DataConfiguration.NULL_MARKER, it accepts any string.
 * Otherwise it examines the text to validate and, 
 * if null then returns false, otherwise forwards to 
 * the {@link #validImpl(String)} method to be implemented 
 * by child classes. 
 * @author Volker Bergmann
 */
public abstract class AbstractNullStringValidator implements Validator<String> {

    /** The string on which validation is based. */
    protected final String validationTerm;

    /** Constructor
     *  @param validationTerm the validation term */
    public AbstractNullStringValidator(String validationTerm) {
        if (validationTerm == null || DataMarkerCheck.isNull(validationTerm)) {
            this.validationTerm = null;
        } else {
            this.validationTerm = validationTerm;
        }
    }

    /** Implements the {@link Validator} interface. */
    @Override
    public final boolean valid(String text) {
        if (this.validationTerm == null) {
            return true;
        } else if (text == null) {
            return false;
        } else {
            return validImpl(text);
        }
    }

    /** Delegate for string validation to be implemented by child classes. */
    protected abstract boolean validImpl(String text);

    /** Creates a String representation of the validator. */
    @Override
    public String toString() {
        return getClass().getSimpleName() + " (" + description() + ")";
    }

    private String description() {
        if (this.validationTerm == null) {
            return "accepting any text";
        } else {
            return descriptionImpl();
        }
    }

    protected abstract String descriptionImpl();

}
