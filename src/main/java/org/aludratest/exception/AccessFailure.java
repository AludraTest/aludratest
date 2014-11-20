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

/** Indicates that the access of a system has failed.
 * @author volker.bergmann@bergmann-it.de */
public final class AccessFailure extends AludraTestException {

    private static final long serialVersionUID = 1L;

    /** Constructor which requires a failure message.
     * @param message the message text to report */
    public AccessFailure(String message) {
        this(message, null);
    }

    /** Constructor with a failure message and the (optional) root cause exception.
     * @param message the message text to report
     * @param cause the (optional) root cause exception */
    public AccessFailure(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public TestStatus getTestStatus() {
        return TestStatus.FAILEDACCESS;
    }

}
