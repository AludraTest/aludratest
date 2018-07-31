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
package org.aludratest;

/** Exception indicating that a test or test data cannot be parsed for execution. AludraTest cannot execute at all in such a case.
 * 
 * @author falbrech */
public class InvalidTestException extends RuntimeException {

    private static final long serialVersionUID = -5693852153425334998L;

    /** Constructs a new exception with a given message and cause.
     * 
     * @param message Error message.
     * @param cause Cause for the exception. */
    public InvalidTestException(String message, Throwable cause) {
        super(message, cause);
    }

    /** Constructs a new exception with a given cause.
     * @param cause Cause for the exception. */
    public InvalidTestException(Throwable cause) {
        super(cause);
    }

}
