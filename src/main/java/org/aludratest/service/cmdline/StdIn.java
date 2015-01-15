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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.aludratest.util.data.StringData;

/** Provides access to a process' standard input.
 * @author Volker Bergmann */
public class StdIn {

    private CommandLineProcess<?> process;

    /** Creates a ProcessInput instance for a given process.
     * @param process */
    StdIn(CommandLineProcess<?> process) {
        this.process = process;
    }

    /** Redirects data from a String to the assigned process' standard input.
     * @param in a String that contains the data to be sent to the process
     * @return this */
    public StdIn redirectFrom(StringData in) {
        return redirectFrom(in.getValue().getBytes());
    }

    /** Redirects data from a byte array to the assigned process' standard input.
     * @param in a byte array that contains the data to be sent to the process
     * @return this */
    protected StdIn redirectFrom(byte[] in) {
        return redirectFrom(new ByteArrayInputStream(in));
    }

    /** Redirects data from an InputStream to the assigned process' standard input.
     * @param in the InputStream from which to read the data
     * @return this */
    protected StdIn redirectFrom(InputStream in) {
        process.redirectStdInFrom(in);
        return this;
    }

}
