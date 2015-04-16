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

/** A validator which checks that the string to validate ends with a given substring. If the string is equal to the expected start,
 * this is also treated as valid.
 * 
 * @author falbrech */
public class EndsWithValidator extends AbstractNullStringValidator {

    /** Constructor
     * @param expectedEnd the expected string at the end of the strings to validate. */
    public EndsWithValidator(String expectedEnd) {
        super(expectedEnd);
    }

    @Override
    protected boolean validImpl(String text) {
        return text.endsWith(validationTerm);
    }

    @Override
    protected String descriptionImpl() {
        return "expecting string to end with '" + validationTerm + "'";
    }

}
