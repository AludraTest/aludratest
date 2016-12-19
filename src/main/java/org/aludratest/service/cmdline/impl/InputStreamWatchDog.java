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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.aludratest.exception.PerformanceFailure;
import org.aludratest.exception.TechnicalException;
import org.aludratest.util.poll.PollService;
import org.aludratest.util.poll.PolledTask;
import org.databene.commons.IOUtil;
import org.databene.commons.SystemInfo;
import org.databene.commons.array.ByteArray;

/** Watches and collects a process' output.
 * @author Volker Bergmann */
public class InputStreamWatchDog extends Thread {

    private static final int DEFAULT_POLLING_INTERVAL = 300;

    private final static String LF = SystemInfo.getLineSeparator();

    private InputStream in;
    private ProcessWrapper process;
    private String name;
    private ByteArray buffer;
    private int pos;
    private boolean timedOut;

    InputStreamWatchDog(InputStream in, ProcessWrapper process, String name) {
        this.in = in;
        this.process = process;
        this.name = name;
        this.buffer = new ByteArray(5000);
        this.pos = 0;
        this.timedOut = false;
        setDaemon(true);
        setPriority(MIN_PRIORITY);
    }

    public String nextLine() throws IOException {
        if (!bufferedTextAvailable()) {
            if (process.isRunning()) {
                // expect process output
                waitUntilAvailable();
            }
            else if (!availableWithinResponseTimeout()) {
                // perform another check if process output was pending
                // (necessary for Windows)
                return null;
            }
        }
        return readLineFromBuffer();
    }

    public boolean waitUntilAvailable() {
        if (!availableWithinResponseTimeout()) {
            throw new PerformanceFailure("Process '" + process
                    + "' did not provide expected output within the response timeout of " + process.getResponseTimeout() + " ms");
        }
        return true;
    }

    boolean availableWithinResponseTimeout() {
        PollService poller = new PollService(process.getResponseTimeout(), DEFAULT_POLLING_INTERVAL);
        WaitUntilTextAvailable task = new WaitUntilTextAvailable();
        return poller.poll(task);
    }

    private boolean bufferedTextAvailable() {
        synchronized (buffer) {
            return (!timedOut && pos < buffer.length());
        }
    }

    @Override
    public void run() {
        try {
            int c;
            while ((c = in.read()) >= 0) {
                synchronized (buffer) {
                    buffer.add((byte) c);
                }
            }
        }
        catch (IOException e) {
            throw new TechnicalException("Error reading " + name + " stream of process " + process, e);
        }
    }

    public void redirectTo(OutputStream out) throws IOException {
        if (!bufferedTextAvailable()) {
            if (process.isRunning()) {
                // expect process output
                waitUntilAvailable();
            }
            else if (!availableWithinResponseTimeout()) {
                // perform another check if process output was pending
                // (necessary for Windows)
                return;
            }
        }
        InputStream bufferStream;
        synchronized (buffer) {
            bufferStream = new ByteArrayInputStream(this.buffer.getBytes(), this.pos, this.buffer.length() - this.pos);
        }
        IOUtil.transfer(bufferStream, out);
        out.flush();
    }

    private String readLineFromBuffer() throws IOException {
        synchronized (buffer) {
            StringBuilder builder = new StringBuilder();
            // read chars until the first character of the line separator
            int c = -1;
            while (pos < buffer.length()) {
                c = buffer.get(pos++);
                if (c != LF.charAt(0)) {
                    builder.append((char) c);
                }
                else {
                    // if the line separator consists of multiple characters,
                    // assume the following characters match and skip them
                    for (int i = 1; i < LF.length() && pos < buffer.length() && c != -1; i++) {
                        c = buffer.get(pos);
                        pos++;
                    }
                    break;
                }
            }
            return builder.toString();
        }
    }

    public void close() {
        IOUtil.close(this.in); // this also cancels in.read() in the run method's loop
    }

    class WaitUntilTextAvailable implements PolledTask<Boolean> {
        @Override
        public Boolean run() {
            return (bufferedTextAvailable() ? true : null); // NOSONAR
        }

        @Override
        public Boolean timedOut() {
            timedOut = true;
            return false;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + "[" + process + "]";
        }
    }

}
