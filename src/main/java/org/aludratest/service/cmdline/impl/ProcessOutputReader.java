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
package org.aludratest.service.cmdline.impl;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/** Reader class for accessing standard or error output of a process.
 * @author Volker Bergmann */
final class ProcessOutputReader implements Closeable {

    private ProcessWrapper process;
    private final String name;
    private InputStreamWatchDog watchdog;

    private String pushedBackLine;


    // constructor -------------------------------------------------------------

    /** @param in the source
     * @param err */
    ProcessOutputReader(InputStream in, ProcessWrapper process, String name) {
        this.process = process;
        this.name = name;
        this.pushedBackLine = null;
        this.watchdog = new InputStreamWatchDog(in, process, name);
        this.watchdog.start(); // NOSONAR no J2EE compliance needed here
    }

    // interface ---------------------------------------------------------------

    public String readLine() throws IOException {
        if (pushedBackLine != null) {
            String result = this.pushedBackLine;
            this.pushedBackLine = null;
            return result;
        }
        else {
            return watchdog.nextLine();
        }
    }

    /** Pushes back a single line of output into the buffer. On the next call to {@link #readLine()}, it will be provided again.
     * The buffer can only take up one single line, several subsequent calls to this method will fail if the buffer was not
     * cleared in between using {@link #readLine()}.
     * @param line the line to push back */
    public void pushBackLine(String line) {
        if (line != null) {
            throw new IllegalStateException("Tried multiple push backs");
        }
        this.pushedBackLine = line;
    }

    public void redirectTo(OutputStream out) throws IOException {
        this.watchdog.redirectTo(out);
    }

    public boolean availableWithinTimeout() {
        if (this.pushedBackLine != null)
            return true;
        else
            return this.watchdog.availableWithinResponseTimeout();
    }

    @Override
    public void close() throws IOException {
        watchdog.close();
    }

    // java.lang.Object overrides ----------------------------------------------

    @Override
    public String toString() {
        return name;
    }

}
