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
 * Requires validated strings to be different from the validation term.
 * @author Volker Bergmann
 */
public class NotEqualsValidator extends AbstractNullStringValidator {

    /** Constructor.
     *  @param notExpected the value that is not desired to be encountered */
    public NotEqualsValidator(String notExpected) {
        super(notExpected);
    }

    @Override
    protected boolean validImpl(String text) {
        return !text.equals(validationTerm);
    }

    @Override
    protected String descriptionImpl() {
        return "expects string to be different from '" + validationTerm + "'";
    }

}
