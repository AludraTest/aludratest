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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.aludratest.exception.PerformanceFailure;
import org.aludratest.exception.TechnicalException;
import org.aludratest.util.poll.PollService;
import org.aludratest.util.poll.PolledTask;
import org.databene.commons.IOUtil;
import org.databene.commons.SystemInfo;

/** Reader class for accessing standard or error output of a process.
 * @author Volker Bergmann */
class ProcessOutputReader {

    private static final int DEFAULT_POLLING_INTERVAL = 300;

    private final static String LF = SystemInfo.getLineSeparator();

    private ProcessWrapper process;
    private final String name;
    private final InputStream in;
    private String buffer;
    private boolean timedOut;


    /** @param in the source
     * @param err */
    ProcessOutputReader(InputStream in, ProcessWrapper process, String name) {
        this.in = in;
        this.process = process;
        this.name = name;
        this.buffer = null;
        this.timedOut = false;
    }

    public String readLine() throws IOException {
        if (buffer != null) {
            String result = this.buffer;
            this.buffer = null;
            return result;
        }
        else {
            waitUntilAvailable();
            return readLineFromInputStream();
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
        this.buffer = line;
    }

    public void redirectTo(OutputStream out) throws IOException {
        IOUtil.transfer(this.in, out);
        out.flush();
    }

    public boolean waitUntilAvailable() {
        if (!availableWithinTimeout()) {
            throw new PerformanceFailure("Process '" + process + "' did not provide expected output within the timeout of "
                    + process.getTimeout() + " ms");
        }
        return true;
    }

    public boolean availableWithinTimeout() {
        PollService poller = new PollService(process.getTimeout(), DEFAULT_POLLING_INTERVAL);
        WaitUntilReadyTask task = new WaitUntilReadyTask();
        return poller.poll(task);
    }

    public boolean availableImmediately() {
        try {
            return (!timedOut && (this.buffer != null || this.in.available() > 0));
        }
        catch (IOException e) {
            throw new TechnicalException("Error checking if " + this + " is available", e);
        }
    }

    // java.lang.Object overrides ----------------------------------------------

    @Override
    public String toString() {
        return name;
    }

    // private helpers ---------------------------------------------------------

    private String readLineFromInputStream() throws IOException {
        StringBuilder builder = new StringBuilder();
        // read chars until the first character of the line separator
        int c;
        while ((c = in.read()) != -1 && c != LF.charAt(0)) {
            builder.append((char) c);
        }
        if (c != -1) {
            // if the line separator consists of multiple characters, assume the following characters match and skip them
            for (int i = 1; i < LF.length(); i++) {
                c = in.read();
            }
        }
        return builder.toString();
    }


    public class WaitUntilReadyTask implements PolledTask<Boolean> {
        @Override
        public Boolean run() {
            return (availableImmediately() ? true : null);
        }

        @Override
        public Boolean timedOut() {
            timedOut = true;
            return false;
        }
    }

}
