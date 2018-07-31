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

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.aludratest.util.data.ByteArrayData;
import org.aludratest.util.data.StringData;
import org.databene.commons.Validator;

/** Common parent class for the output streams of a process: stdout and errout.
 * @author Volker Bergmann */
public abstract class Out {

    final CommandLineProcess<?> process;

    // construction ------------------------------------------------------------

    protected Out(CommandLineProcess<?> process) {
        this.process = process;
    }

    // operational interface ---------------------------------------------------

    /** Redirects the process' output to a stream.
     * @param textOutput a buffer to receive process text output
     * @return this */
    public Out redirectTo(StringData textOutput) {
        ByteArrayData buffer = new ByteArrayData();
        redirectTo(buffer);
        textOutput.setValue(new String(buffer.getValue())); // NOSONAR the default system charset has to be used here
        return this;
    }

    /** Redirects the process' stdout to a stream.
     * @param byteOutput a byte buffer to receive process data output
     * @return this */
    public Out redirectTo(ByteArrayData byteOutput) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        redirectTo(buffer);
        byteOutput.setValue(buffer.toByteArray());
        return this;
    }

    /** Redirects the output to a stream.
     * @param out the target stream
     * @return this */
    public abstract Out redirectTo(OutputStream out);

    /** @return the next line available in this stream */
    public Line nextLine() {
        return new Line(this);
    }

    /** Asserts that the validator matches the next line of the output.
     * @param validator the matcher
     * @return this */
    public abstract Out assertNextLineMatches(Validator<String> validator);

    /** Skips the output lines until a line matches the validator or the end of the file is reached.
     * @param validator the matcher
     * @return a Line object for the first matching line */
    public abstract Line skipUntilLineMatches(Validator<String> validator);

    /** Asserts that the output stream is empty
     * @return this */
    public abstract Out assertEmpty();

}
