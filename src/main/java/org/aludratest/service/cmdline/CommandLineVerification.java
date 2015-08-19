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

import org.aludratest.service.ElementName;
import org.aludratest.service.ElementType;
import org.aludratest.service.TechnicalLocator;
import org.aludratest.service.Verification;
import org.databene.commons.Validator;

/** {@link Verification} interface for the {@link CommandLineService}.
 * @author Volker Bergmann */
public interface CommandLineVerification extends Verification {

    /** Asserts that the next line of the specified process' standard output is accepted by the {@link Validator}.
     * @param processType the process type
     * @param processName the process name
     * @param processId the internal id of the process
     * @param validator */
    void assertNextLineOfStdOutMatches(@ElementType String processType, @ElementName String processName,
            @TechnicalLocator int processId, Validator<String> validator);

    /** Asserts that the specified process has no more standard output.
     * @param processType the process type
     * @param processName the process name
     * @param processId the internal id of the process */
    void assertEmptyStdOut(@ElementType String processType, @ElementName String processName, @TechnicalLocator int processId);

    /** Asserts that the next line of the specified process' error output is accepted by the {@link Validator}.
     * @param processType the process type
     * @param processName the process name
     * @param processId the internal id of the process
     * @param validator */
    void assertNextLineOfErrOutMatches(@ElementType String processType, @ElementName String processName,
            @TechnicalLocator int processId, Validator<String> validator);

    /** Asserts that the specified process has no more error output.
     * @param processType the process type
     * @param processName the process name
     * @param processId the internal id of the process */
    void assertEmptyErrOut(@ElementType String processType, @ElementName String processName, @TechnicalLocator int processId);

    /** Asserts that the referenced process' exit value equals the expected value.
     * @param processType the process type
     * @param processName the process name
     * @param processId the internal id of the process
     * @param expectedValue */
    void assertExitCodeEquals(@ElementType String processType, @ElementName String processName, @TechnicalLocator int processId,
            int expectedValue);

}
