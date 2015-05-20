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

import org.aludratest.exception.FunctionalFailure;
import org.databene.commons.Validator;

/** Provides access to the standard output of a process.
 * @author Volker Bergmann */
public class StdOut extends Out {

    protected StdOut(CommandLineProcess<?> process) {
        super(process);
    }

    /** Redirects the output to a stream.
     * @param out the target stream
     * @return this */
    @Override
    public Out redirectTo(OutputStream out) {
        process.redirectStdOutTo(out);
        return this;
    }

    /** Asserts that the validator matches the next line of the output
     * @param validator the matcher
     * @return this */
    @Override
    public Out assertNextLineMatches(Validator<String> validator) {
        process.assertNextLineOfStdOutMatches(validator);
        return this;
    }

    /** Skips the output lines until a line matches the validator or the end of the file is reached.
     * @param validator the matcher
     * @return a Line object for the first matching line */
    @Override
    public Line skipUntilLineMatches(Validator<String> validator) {
        process.skipStdOutUntilLineMatches(validator);
        return new Line(this);
    }

    /** Asserts the output to be empty.
     * @return this
     * @throws a {@link FunctionalFailure} if output is found */
    @Override
    public Out assertEmpty() {
        process.assertEmptyStdOut();
        return this;
    }

}
