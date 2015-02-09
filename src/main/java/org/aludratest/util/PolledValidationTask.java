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
package org.aludratest.util;

import org.aludratest.exception.FunctionalFailure;
import org.aludratest.util.poll.PolledTask;
import org.databene.commons.Validator;

/**
 * {@link PolledTask} implementation for polling a {@link Provider} until it provides
 * a value accepted by a {@link Validator} (or a timeout occurs)
 * @author Volker Bergmann
 */
public class PolledValidationTask implements PolledTask<String> {

    private Provider<String> provider;
    private Validator<String> validator;
    private String actualText;

    /** Constructor
     * @param provider the provider of the data to validate.
     * @param validator Validator to validate the provided data. */
    public PolledValidationTask(Provider<String> provider, Validator<String> validator) {
        this.provider = provider;
        this.validator = validator;
    }

    @Override
    public String run() {
        this.actualText = provider.getValue();
        return (validator.valid(this.actualText) ? this.actualText : null);
    }

    @Override
    public String timedOut() {
        throw new FunctionalFailure(provider.getName() + " '" + actualText + "'" +
                " does not match the validator " + validator);
    }

    @Override
    public String toString() {
        return "Expecting text that matches " + validator;
    }

}
