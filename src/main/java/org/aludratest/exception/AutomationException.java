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
package org.aludratest.exception;

import org.aludratest.testcase.TestStatus;

/** Indicates an error condition that is likely to be the result of a test programmer's fault.
 * @author Volker Bergmann */
public final class AutomationException extends AludraTestException {

    private static final long serialVersionUID = 1L;

    /** Constructor which requires a failure message.
     * @param message the exception message */
    public AutomationException(String message) {
        this(message, null);
    }

    /** Constructor with a failure message and the (optional) root cause exception.
     * @param message the exception message
     * @param cause the root cause of the error */
    public AutomationException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public TestStatus getTestStatus() {
        return TestStatus.FAILEDAUTOMATION;
    }

}
