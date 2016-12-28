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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aludratest.exception.AutomationException;
import org.aludratest.exception.FunctionalFailure;
import org.aludratest.exception.TechnicalException;
import org.aludratest.service.Action;
import org.aludratest.service.SystemConnector;
import org.aludratest.service.cmdline.CommandLineCondition;
import org.aludratest.service.cmdline.CommandLineInteraction;
import org.aludratest.service.cmdline.CommandLineService;
import org.aludratest.service.cmdline.CommandLineVerification;
import org.aludratest.testcase.event.attachment.Attachment;
import org.databene.commons.IOUtil;
import org.databene.commons.Validator;

/** Implements all {@link Action} interfaces of the {@link CommandLineService}.
 * @author Volker Bergmann */
public class CommandLineActionImpl implements CommandLineInteraction, CommandLineVerification, CommandLineCondition {

    private CommandLineServiceConfiguration configuration;

    private int lastProcessId;
    private Map<Integer, ProcessWrapper> processes;

    // configuration -----------------------------------------------------------

    /** Constructor with default visibility for preventing external instantiation
     * @param configuration */
    CommandLineActionImpl(CommandLineServiceConfiguration configuration) {
        this.configuration = configuration;
        this.lastProcessId = 0;
        this.processes = new HashMap<Integer, ProcessWrapper>();
    }

    @Override
    public void setSystemConnector(SystemConnector systemConnector) {
        // empty implementation
    }

    // functional interface ----------------------------------------------------

    @Override
    public int create(String processType, String processName, int processTimeout, int responseTimeout, String... command) {
        lastProcessId++;
        int processId = lastProcessId;
        ProcessWrapper process = new ProcessWrapper(processId, processTimeout, responseTimeout, command);
        processes.put(processId, process);
        return processId;
    }

    @Override
    public void start(String processType, String processName, int processId) {
        ProcessWrapper process = getProcess(processId);
        process.start();
    }

    @Override
    public void assertNextLineOfStdOutMatches(String processType, String processName, int processId, Validator<String> validator) {
        String line = readLineOfStdOut(processType, processName, processId);
        if (!validator.valid(line)) {
            ProcessWrapper process = getProcess(processId);
            throw new FunctionalFailure("Standard output of process " + process + " is not accepted by " + validator + ": "
                    + line);
        }
    }

    @Override
    public void assertEmptyStdOut(String processType, String processName, int processId) {
        ProcessWrapper process = getProcess(processId);
        if (process.getStdOut().availableWithinTimeout()) {
            throw new FunctionalFailure("Unexpected output of process " + process + ": " + readLineOfStdOut(process));
        }
    }

    @Override
    public String readLineOfStdOut(String processType, String processName, int processId) {
        ProcessWrapper process = getProcess(processId);
        return readLineOfStdOut(process);
    }

    @Override
    public void assertNextLineOfErrOutMatches(String processType, String processName, int processId, Validator<String> validator) {
        String line = readLineOfErrOut(processType, processName, processId);
        if (!validator.valid(line)) {
            ProcessWrapper process = getProcess(processId);
            throw new FunctionalFailure("Error output of process " + process + " is not accepted by " + validator + ": " + line);
        }
    }

    @Override
    public void assertEmptyErrOut(String processType, String processName, int processId) {
        ProcessWrapper process = getProcess(processId);
        if (process.getErrOut().availableWithinTimeout()) {
            throw new FunctionalFailure("Unexpected error output of process " + process + ": " + readLineOfErrOut(process));
        }
    }

    @Override
    public void assertExitCodeEquals(String processType, String processName, int processId, int expectedValue) {
        ProcessWrapper process = getProcess(processId);
        int actualValue = process.waitUntilFinished();
        if (actualValue != expectedValue) {
            throw new FunctionalFailure("Unexpected exit value. Expected " + expectedValue + ", but encountered " + actualValue);
        }
    }

    @Override
    public void setRelativeWorkingDirectory(String processType, String processName, int processId, String relativeWorkingDirectory) {
        try {
            File workingDirectory = new File(configuration.getBaseDirectory(), relativeWorkingDirectory); // NOSONAR
            workingDirectory = workingDirectory.getCanonicalFile();
            getProcess(processId).setWorkingDirectory(workingDirectory);
        }
        catch (IOException e) {
            throw new AutomationException("Error setting working directory", e);
        }
    }

    @Override
    public void setEnvironmentVariable(String processType, String processName, int processId, String key, String value) {
        getProcess(processId).setEnvironmentVariable(key, value);
    }

    @Override
    public void redirectStdOutTo(String processType, String processName, int processId, OutputStream out) {
        redirect(getProcess(processId).getStdOut(), out);
    }

    @Override
    public void redirectErrOutTo(String processType, String processName, int processId, OutputStream out) {
        redirect(getProcess(processId).getErrOut(), out);
    }

    @Override
    public void redirectStdInFrom(String processType, String processName, int processId, InputStream in) {
        try {
            ProcessWrapper process = getProcess(processId);
            OutputStream stdIn = process.getStdIn();
            IOUtil.transfer(in, stdIn);
            stdIn.flush();
        }
        catch (IOException e) {
            throw new TechnicalException("Error redirecting stdout", e);
        }
    }

    @Override
    public void skipStdOutUntilLineMatches(String processType, String processName, int processId, Validator<String> validator) {
        ProcessWrapper process = getProcess(processId);
        skipUntilOutputMatches(validator, process, process.getStdOut());
    }

    @Override
    public String readLineOfErrOut(String processType, String processName, int processId) {
        ProcessWrapper process = getProcess(processId);
        return readLineOfErrOut(process);
    }

    @Override
    public void skipErrOutUntilLineMatches(String processType, String processName, int processId, Validator<String> validator) {
        ProcessWrapper process = getProcess(processId);
        skipUntilOutputMatches(validator, process, process.getErrOut());
    }

    @Override
    public void enter(String processType, String processName, int processId, String text) {
        getProcess(processId).enter(text);
    }

    @Override
    public int waitUntilFinished(String processType, String processName, int processId) {
        return getProcess(processId).waitUntilFinished();
    }

    @Override
    public void destroy(String processType, String processName, int processId) {
        getProcess(processId).destroy();
    }

    // attachment support ------------------------------------------------------

    @Override
    public List<Attachment> createDebugAttachments() {
        // no attachments supported
        return null; // NOSONAR null is compliant to the interface contract
    }

    @Override
    public List<Attachment> createAttachments(Object object, String title) {
        // no attachments supported
        return null; // NOSONAR null is compliant to the interface contract
    }

    // private helpers ---------------------------------------------------------

    private ProcessWrapper getProcess(int processId) {
        ProcessWrapper process = processes.get(processId);
        if (process == null) {
            throw new AutomationException("Invalid process id: " + processId);
        }
        return process;
    }

    private String readLineOfStdOut(ProcessWrapper process) {
        ProcessOutputReader reader = process.getStdOut();
        try {
            return reader.readLine();
        }
        catch (IOException e) {
            throw new TechnicalException("Error reading stdout of process: " + process, e);
        }
    }

    private String readLineOfErrOut(ProcessWrapper process) {
        try {
            return process.getErrOut().readLine();
        }
        catch (IOException e) {
            throw new TechnicalException("Error reading errout of process " + process, e);
        }
    }

    private void skipUntilOutputMatches(Validator<String> validator, ProcessWrapper process, ProcessOutputReader reader) {
        String line;
        do {
            try {
                if (reader.availableWithinTimeout()) {
                    line = reader.readLine();
                }
                else {
                    // finished output without match
                    throw new FunctionalFailure("Process '" + process + "' did not provide an output on " + reader
                            + " that is accepted by " + validator);
                }
            }
            catch (IOException e) {
                throw new TechnicalException("Error reading " + reader + " of process " + process, e);
            }
        }
        while (!validator.valid(line));
        reader.pushBackLine(line);
    }

    private void redirect(ProcessOutputReader reader, OutputStream out) {
        try {
            reader.redirectTo(out);
        }
        catch (IOException e) {
            throw new TechnicalException("Error redirecting " + reader, e);
        }
    }

}
