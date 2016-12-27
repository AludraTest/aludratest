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

import org.aludratest.service.ElementName;
import org.aludratest.service.ElementType;
import org.aludratest.service.Interaction;
import org.aludratest.service.TechnicalArgument;
import org.aludratest.service.TechnicalLocator;
import org.databene.commons.Validator;

/** Provides methods for interacting with the command line.
 * @author Volker Bergmann */
public interface CommandLineInteraction extends Interaction {

    /** Creates a process.
     * @param processType the process type
     * @param processName the process name
     * @param processTimeout the maximum time to wait for process termination
     * @param responseTimeout the maximum time to wait for process response
     * @param command the command line tokens to use for creating the process
     * @return the processId */
    int create(@ElementType String processType, @ElementName String processName, @TechnicalArgument int processTimeout,
            @TechnicalArgument int responseTimeout, String... command);

    /** Sets the working directory of the specified process.
     * @param processType the process type
     * @param processName the process name
     * @param processId the internal id of the process
     * @param relativeWorkingDirectory the path of the process' working directory relative to the configuration's base.directory setting */
    void setRelativeWorkingDirectory(@ElementType String processType, @ElementName String processName,
            @TechnicalLocator int processId, String relativeWorkingDirectory);

    /** Sets an environment variable of the specified process.
     * @param processType the process type
     * @param processName the process name
     * @param processId the internal id of the process
     * @param key the key of the environment variable to set
     * @param value the value to set for the environment variable */
    void setEnvironmentVariable(@ElementType String processType, @ElementName String processName,
            @TechnicalLocator int processId, String key, String value);

    /** Redirects the standard output of the referenced process to the provided {@link OutputStream}.
     * @param processType the process type
     * @param processName the process name
     * @param processId the internal id of the process
     * @param out the {@link OutputStream} to use for receiving stdout output */
    void redirectStdOutTo(@ElementType String processType, @ElementName String processName, @TechnicalLocator int processId,
            OutputStream out);

    /** Redirects the error output of the referenced process to the provided {@link OutputStream}.
     * @param processType the process type
     * @param processName the process name
     * @param processId the internal id of the process
     * @param out the {@link OutputStream} to use for receiving stderr output */
    void redirectErrOutTo(@ElementType String processType, @ElementName String processName, @TechnicalLocator int processId,
            OutputStream out);

    /** Redirects provided {@link OutputStream}'s output to the standard input of the referenced process.
     * @param processType the process type
     * @param processName the process name
     * @param processId the internal id of the process
     * @param in the {@link InputStream} to use as data source for feeding the process */
    void redirectStdInFrom(@ElementType String processType, @ElementName String processName, @TechnicalLocator int processId,
            InputStream in);

    /** Starts the referenced process.
     * @param processType the process type
     * @param processName the process name
     * @param processId the internal id of the process */
    void start(@ElementType String processType, @ElementName String processName, @TechnicalLocator int processId);

    /** Reads the next line of the referenced process' standard output.
     * @param processType the process type
     * @param processName the process name
     * @param processId the internal id of the process
     * @return the line read from stdout */
    String readLineOfStdOut(@ElementType String processType, @ElementName String processName, @TechnicalLocator int processId);

    /** Skips the references process' standard output lines until a line matches the validator or the no more output is available.
     * @param processType the process type
     * @param processName the process name
     * @param processId the internal id of the process
     * @param validator the validator to use */
    void skipStdOutUntilLineMatches(@ElementType String processType, @ElementName String processName,
            @TechnicalLocator int processId, Validator<String> validator);

    /** Reads the next line of the referenced process' error output.
     * @param processType the process type
     * @param processName the process name
     * @param processId the internal id of the process
     * @return the read stderr output */
    String readLineOfErrOut(@ElementType String processType, @ElementName String processName, @TechnicalLocator int processId);

    /** Skips lines of the refernced process' error output until a line matches the Validator.
     * @param processType the process type
     * @param processName the process name
     * @param processId the internal id of the process
     * @param validator the validator to use */
    void skipErrOutUntilLineMatches(@ElementType String processType, @ElementName String processName,
            @TechnicalLocator int processId, Validator<String> validator);

    /** Enters a text into the referenced process' standard input.
     * @param processType the process type
     * @param processName the process name
     * @param processId the internal id of the process
     * @param text the text to enter */
    void enter(@ElementType String processType, @ElementName String processName, @TechnicalLocator int processId, String text);

    /** Waits until the references process has finished.
     * @param processType the process type
     * @param processName the process name
     * @param processId the internal id of the process
     * @return the process' exit value */
    int waitUntilFinished(@ElementType String processType, @ElementName String processName, @TechnicalLocator int processId);

    /** Destroys the referenced process.
     * @param processType the process type
     * @param processName the process name
     * @param processId the internal id of the process */
    void destroy(@ElementType String processType, @ElementName String processName, @TechnicalLocator int processId);

}
