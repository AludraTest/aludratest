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
 * Expects the validated strings to be equal to the search term ignoring case.
 * @author Volker Bergmann
 */
public class EqualsIgnoreCaseTrimmedValidator extends AbstractNullStringValidator {

    /** Constructor.
     *  @param expectedTrimmed the required sub string */
    public EqualsIgnoreCaseTrimmedValidator(String expectedTrimmed) {
        super(expectedTrimmed);
    }

    @Override
    public boolean validImpl(String text) {
        return text.trim().equalsIgnoreCase(validationTerm.trim());
    }

    @Override
    protected String descriptionImpl() {
        return "expecting '" + validationTerm.trim() + "' after trimming, ignoring case";
    }

}
