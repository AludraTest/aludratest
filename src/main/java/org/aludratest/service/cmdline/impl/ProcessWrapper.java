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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.aludratest.exception.AutomationException;
import org.aludratest.exception.PerformanceFailure;
import org.aludratest.util.poll.PollService;
import org.aludratest.util.poll.PolledTask;
import org.databene.commons.IOUtil;
import org.databene.commons.SystemInfo;

/** Wraps a Java {@link ProcessBuilder}, uses it to configure and create a {@link Process} instance and gives the user control of
 * the process.
 * @author Volker Bergmann */
public class ProcessWrapper {

    private static final String LINEFEED = SystemInfo.getLineSeparator();

    private static final int DEFAULT_PROCESS_TERMINATION_POLLING_INTERVAL = 300;

    private final int processId;
    private final int processTimeout;
    private final int responseTimeout;

    private ProcessBuilder builder;
    private Process process;
    private OutputStream stdIn;
    private ProcessOutputReader stdOut;
    private ProcessOutputReader errOut;

    private ProcessState state;

    // constructor -------------------------------------------------------------

    /** Creates a {@link ProcessWrapper} instance.
     * @param processId the process id to assign
     * @param processTimeout the maximum time to wait for process termination
     * @param responseTimeout the maximum time to wait for process response
     * @param command the command tokens to send to the shell */
    public ProcessWrapper(int processId, int processTimeout, int responseTimeout, String... command) {
        this.processId = processId;
        this.processTimeout = processTimeout;
        this.responseTimeout = responseTimeout;
        this.builder = new ProcessBuilder(command);
        this.process = null;
        this.stdIn = null;
        this.stdOut = null;
        this.errOut = null;
        this.state = ProcessState.CREATED;
    }

    // configuration -----------------------------------------------------------

    /** @return the {@link #processId}, a numerical id value assigned upon construction */
    public int getProcessId() {
        return processId;
    }

    /** @return the command tokes used in construction */
    public List<String> getCommand() {
        return builder.command();
    }

    /** @return the {@link #processTimeout} */
    public int getProcessTimeout() {
        return processTimeout;
    }

    /** @return the {@link #processTimeout} */
    public int getResponseTimeout() {
        return responseTimeout;
    }

    /** Sets an environment variable of the process. This has to happen before invocation of the {@link #start()} method.
     * @param key the name of the environment variable
     * @param value the value of the environment variable */
    public void setEnvironmentVariable(String key, String value) {
        assertState(ProcessState.CREATED);
        this.builder.environment().put(key, value);
    }

    /** Sets the working directory of the process. This has to happen before invocation of the {@link #start()} method.
     * @param directory the working directory to set. */
    public void setWorkingDirectory(File directory) {
        assertState(ProcessState.CREATED);
        this.builder.directory(directory);
    }

    // operational interface ---------------------------------------------------

    /** Starts the process.
     * @throws IOException */
    public void start() throws IOException {
        assertState(ProcessState.CREATED);
        this.process = builder.start();
        this.stdOut = new ProcessOutputReader(process.getInputStream(), this, "stdout");
        this.errOut = new ProcessOutputReader(process.getErrorStream(), this, "stderr");
        this.stdIn = process.getOutputStream();
        this.state = ProcessState.RUNNING;
    }

    /** Tells if the process is running.
     * @return true if the process is running, otherwise false */
    public boolean isRunning() {
        return (state != ProcessState.CREATED && getExitValue() == null);
    }

    /** Tells if the process has finished.
     * @return true if the process has finished, otherwise false */
    public boolean hasFinished() {
        return (getExitValue() != null);
    }

    /** @return a {@link BufferedWriter} for accessing stdin */
    public OutputStream getStdIn() {
        return this.stdIn;
    }

    /** @return a {@link BufferedReader} for accessing stdout */
    public ProcessOutputReader getStdOut() {
        assertRunningOrFinished("getStdOut()");
        return this.stdOut;
    }

    /** @return a {@link BufferedWriter} for accessing errout */
    public ProcessOutputReader getErrOut() {
        assertRunningOrFinished("getErrOut()");
        return this.errOut;
    }

    /** Enters the provided text into the process' stdin and appends a line feed.
     * @param text the text to enter */
    public void enterLine(String text) {
        enter(text);
        enter(LINEFEED);
    }

    /** Enters the provided text into the process' stdin.
     * @param text the text to enter */
    public void enter(String text) {
        try {
            stdIn.write(text.getBytes());
            stdIn.flush();
        }
        catch (IOException e) {
            throw new AutomationException("Error entering text '" + text + "'", e);
        }
    }

    /** Waits until the process has finished or the provided timeout is exceeded.
     * @throws PerformanceException if the timeout is exceeded before the process has finished
     * @return the process' exit value */
    public int waitUntilFinished() {
        PollService poller = new PollService(processTimeout, DEFAULT_PROCESS_TERMINATION_POLLING_INTERVAL);
        return poller.poll(new WaitUntilFinishedTask());
    }

    /** @return the exit value of the process if it has finished, otherwise null */
    public Integer getExitValue() {
        try {
            int exitValue = process.exitValue();
            if (this.state == ProcessState.RUNNING) {
                this.state = ProcessState.FINISHED;
            }
            return exitValue; // returns the exit value if the process has already finished
        }
        catch (IllegalThreadStateException e) {
            return null; // returns null if the process is still running
        }
    }

    /** Kills the process. */
    public void destroy() {
        IOUtil.close(this.errOut);
        IOUtil.close(this.stdOut);
        IOUtil.close(this.stdIn);
        process.destroy();
    }

    // java.lang.Object overrides ----------------------------------------------

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        List<String> tokens = getCommand();
        for (int i = 0; i < tokens.size(); i++) {
            if (i > 0) {
                builder.append(' ');
            }
            builder.append(formatToken(tokens.get(i)));
        }
        return builder.toString();
    }

    // private helpers ---------------------------------------------------------

    private void assertState(ProcessState assertion) {
        if (this.state != assertion) {
            throw new AutomationException("Encountered unexpected status: '" + this.state + "' while expecting '" + assertion
                    + "'");
        }
    }

    private void assertRunningOrFinished(String operation) {
        if (this.state != ProcessState.RUNNING && this.state != ProcessState.FINISHED) {
            throw new AutomationException("Process must be in state " + ProcessState.RUNNING + " or " + ProcessState.RUNNING
                    + " when calling " + operation + ", but was in state " + this.state);
        }
    }

    private String formatToken(String token) {
        return (token.contains(" ") ? '"' + token + '"' : token);
    }

    private enum ProcessState {
        CREATED, RUNNING, FINISHED
    }


    /** PolledTask implementation that queries a process' exit value until the process has finished or a timeout occurs. */
    public class WaitUntilFinishedTask implements PolledTask<Integer> {

        @Override
        public Integer run() {
            return getExitValue();
        }

        @Override
        public Integer timedOut() {
            throw new PerformanceFailure("Process did not finish within the timeout of " + processTimeout + " ms: "
                    + ProcessWrapper.this.toString());
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + "[" + ProcessWrapper.this + "]";
        }
    }

}
