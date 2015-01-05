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
package org.aludratest.service.cmdline;

import java.io.OutputStream;

import org.databene.commons.Validator;

/** Gives access to a process' errout.
 * @author Volker Bergmann */
public class ErrOut extends Out {

    protected ErrOut(CommandLineProcess<?> process) {
        super(process);
    }

    /** Redirects the process' errout to a stream.
     * @param out
     * @return this */
    @Override
    protected Out redirectTo(OutputStream out) {
        process.redirectErrOutTo(out);
        return this;
    }

    /** Asserts that the validator matches the next line of the output.
     * @param validator the matcher
     * @return this */
    @Override
    protected Out assertNextLineMatches(Validator<String> validator) {
        process.assertNextLineOfErrOutMatches(validator);
        return this;
    }

    /** Skips the output lines until a line matches the validator or the end of the file is reached.
     * @param validator the matcher
     * @return a Line object for the first matching line */
    @Override
    protected Line skipUntilLineMatches(Validator<String> validator) {
        process.skipErrOutUntilLineMatches(validator);
        return new Line(this);
    }

    /** Asserts that the output is empty
     * @return this */
    @Override
    public Out assertEmpty() {
        process.assertEmptyErrOut();
        return this;
    }

}
