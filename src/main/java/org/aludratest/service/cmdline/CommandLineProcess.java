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

import java.io.InputStream;
import java.io.OutputStream;

import org.aludratest.util.data.IntData;
import org.aludratest.util.data.StringData;
import org.databene.commons.ArrayFormat;
import org.databene.commons.SystemInfo;
import org.databene.commons.Validator;

/** Business delegate class for creating and accessing a command line process.
 * @param <E> The child class, to be specified when inheriting the CommandLineProcess class
 * @author Volker Bergmann */
public final class CommandLineProcess<E extends CommandLineProcess<E>> {

    private final CommandLineService service;
    private final String processType;
    private final String processName;
    private final String[] commands;
    private final int processId;

    /** Creates a {@link CommandLineProcess} instance.
     * @param processType the process type
     * @param processName the process name
     * @param service the underlying {@link CommandLineService}
     * @param processTimeout the maximum time to wait for process termination
     * @param responseTimeout the maximum time to wait for process response
     * @param commands the commands used to start the process */
    public CommandLineProcess(String processType, String processName, CommandLineService service, int processTimeout,
            int responseTimeout, String... commands) {
        this.processType = processType;
        this.processName = processName;
        this.service = service;
        this.commands = commands;
        this.processId = service.perform().create(processType, processName, processTimeout, responseTimeout, commands);
        setRelativeWorkingDirectory(".");
    }

    /** Sets the working directory of the process
     * @param directoryPath the path of the directory to be used
     * @return this */
    @SuppressWarnings("unchecked")
    public E setRelativeWorkingDirectory(String directoryPath) {
        service.perform().setRelativeWorkingDirectory(processType, processName, processId, directoryPath);
        return (E) this;
    }

    /** Sets an environment variable for the process.
     * @param key
     * @param value
     * @return this */
    @SuppressWarnings("unchecked")
    public E setEnvironmentVariable(String key, String value) {
        service.perform().setEnvironmentVariable(processType, processName, processId, key, value);
        return (E) this;
    }

    /** Starts the process.
     * @return this */
    @SuppressWarnings("unchecked")
    public E start() {
        service.perform().start(processType, processName, processId);
        return (E) this;
    }

    /** @return the {@link StdIn} for processing the process' standard input */
    public StdIn stdIn() {
        return new StdIn(this);
    }

    /** @return the {@link Out} for the process' standard output */
    public Out stdOut() {
        return new StdOut(this);
    }

    /** @return the {@link Out} for the process' error output */
    public Out errOut() {
        return new ErrOut(this);
    }

    /** Enters the provided text and appends a line feed.
     * @param text the text to enter
     * @return this */
    @SuppressWarnings("unchecked")
    public E enterLine(String text) {
        String LF = SystemInfo.getLineSeparator();
        service.perform().enter(processType, processName, processId, text + LF);
        return (E) this;
    }

    /** Sends the given text to the process' stdin.
     * @param text
     * @return this */
    @SuppressWarnings("unchecked")
    public E enter(StringData text) {
        service.perform().enter(processType, processName, processId, text.getValue());
        return (E) this;
    }

    /** Waits for the process to finish.
     * @return this */
    @SuppressWarnings("unchecked")
    public E waitUntilFinished() {
        service.perform().waitUntilFinished(processType, processName, processId);
        return (E) this;
    }

    /** Waits for the process to finish and asserts an exit value.
     * @param expectedValue
     * @return */
    @SuppressWarnings("unchecked")
    public E assertExitValue(IntData expectedValue) {
        service.verify().assertExitCodeEquals(processType, processName, processId, expectedValue.getValue());
        return (E) this;
    }

    /** Kills the process
     * @return this */
    @SuppressWarnings("unchecked")
    public E destroy() {
        service.perform().destroy(processType, processName, processId);
        return (E) this;
    }

    // package-visible delegation methods --------------------------------------

    void redirectStdInFrom(InputStream in) {
        service.perform().redirectStdInFrom(processType, processName, processId, in);
    }

    void redirectStdOutTo(OutputStream out) {
        service.perform().redirectStdOutTo(processType, processName, processId, out);
    }

    void assertEmptyStdOut() {
        service.verify().assertEmptyStdOut(processType, processName, processId);
    }

    void assertNextLineOfStdOutMatches(Validator<String> validator) {
        service.verify().assertNextLineOfStdOutMatches(processType, processName, processId, validator);
    }

    void skipStdOutUntilLineMatches(Validator<String> validator) {
        service.perform().skipStdOutUntilLineMatches(processType, processName, processId, validator);
    }

    void redirectErrOutTo(OutputStream out) {
        service.perform().redirectErrOutTo(processType, processName, processId, out);
    }

    void assertEmptyErrOut() {
        service.verify().assertEmptyErrOut(processType, processName, processId);
    }

    void assertNextLineOfErrOutMatches(Validator<String> validator) {
        service.verify().assertNextLineOfErrOutMatches(processType, processName, processId, validator);
    }

    void skipErrOutUntilLineMatches(Validator<String> validator) {
        service.perform().skipErrOutUntilLineMatches(processType, processName, processId, validator);
    }

    // java.lang.Object overrides ----------------------------------------------

    @Override
    public String toString() {
        return ArrayFormat.format(" ", commands);
    }

}
