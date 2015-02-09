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

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import org.aludratest.impl.log4testing.ElementName;
import org.aludratest.impl.log4testing.ElementType;
import org.aludratest.impl.log4testing.TechnicalArgument;
import org.aludratest.impl.log4testing.TechnicalLocator;
import org.aludratest.service.Interaction;
import org.databene.commons.Validator;

/** Provides methods for interacting with the command line.
 * @author Volker Bergmann */
public interface CommandLineInteraction extends Interaction {

    /** Creates a process.
     * @param processType the process type
     * @param processName the process name
     * @param timeout
     * @param command
     * @return the processId */
    int create(@ElementType String processType, @ElementName String processName, @TechnicalArgument int timeout,
            String... command);

    /** Sets the working directory of the specified process.
     * @param processType the process type
     * @param processName the process name
     * @param processId the internal id of the process
     * @param directory */
    void setWorkingDirectory(@ElementType String processType, @ElementName String processName, @TechnicalLocator int processId,
            File directory);

    /** Sets an environment variable of the specified process.
     * @param processType the process type
     * @param processName the process name
     * @param processId the internal id of the process
     * @param key
     * @param value */
    void setEnvironmentVariable(@ElementType String processType, @ElementName String processName,
            @TechnicalLocator int processId, String key, String value);

    /** Redirects the standard output of the referenced process to the provided {@link OutputStream}.
     * @param processType the process type
     * @param processName the process name
     * @param processId the internal id of the process
     * @param out */
    void redirectStdOutTo(@ElementType String processType, @ElementName String processName, @TechnicalLocator int processId,
            OutputStream out);

    /** Redirects the error output of the referenced process to the provided {@link OutputStream}.
     * @param processType the process type
     * @param processName the process name
     * @param processId the internal id of the process
     * @param out */
    void redirectErrOutTo(@ElementType String processType, @ElementName String processName, @TechnicalLocator int processId,
            OutputStream out);

    /** Redirects provided {@link OutputStream}'s output to the standard input of the referenced process.
     * @param processType the process type
     * @param processName the process name
     * @param processId the internal id of the process
     * @param in */
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
     * @return */
    String readLineOfStdOut(@ElementType String processType, @ElementName String processName, @TechnicalLocator int processId);

    /** Skips the references process' standard output lines until a line matches the validator or the no more output is available.
     * @param processType the process type
     * @param processName the process name
     * @param processId the internal id of the process
     * @param validator */
    void skipStdOutUntilLineMatches(@ElementType String processType, @ElementName String processName,
            @TechnicalLocator int processId, Validator<String> validator);

    /** Reads the next line of the referenced process' error output.
     * @param processType the process type
     * @param processName the process name
     * @param processId the internal id of the process
     * @return */
    String readLineOfErrOut(@ElementType String processType, @ElementName String processName, @TechnicalLocator int processId);

    /** Skips lines of the refernced process' error output until a line matches the Validator.
     * @param processType the process type
     * @param processName the process name
     * @param processId the internal id of the process
     * @param validator */
    void skipErrOutUntilLineMatches(@ElementType String processType, @ElementName String processName,
            @TechnicalLocator int processId, Validator<String> validator);

    /** Enters a text into the referenced process' standard input.
     * @param processType the process type
     * @param processName the process name
     * @param processId the internal id of the process
     * @param text */
    void enter(@ElementType String processType, @ElementName String processName, @TechnicalLocator int processId, String text);

    /** Waits until the references process has finished.
     * @param processType the process type
     * @param processName the process name
     * @param processId the internal id of the process
     * @return */
    int waitUntilFinished(@ElementType String processType, @ElementName String processName, @TechnicalLocator int processId);

    /** Destroys the referenced process.
     * @param processType the process type
     * @param processName the process name
     * @param processId the internal id of the process */
    void destroy(@ElementType String processType, @ElementName String processName, @TechnicalLocator int processId);

}
