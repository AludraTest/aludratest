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

/**
 * Root class of all AludraTest exceptions.
 * @author Marcel Malitz
 * @author Volker Bergmann
 */
public abstract class AludraTestException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    /** Constructor.
     *  @param message the exception message */
    protected AludraTestException(String message) {
        this(message, null);
    }

    /** Constructor.
     *  @param message the exception message
     *  @param cause the root cause of the error */
    protected AludraTestException(String message, final Throwable cause) {
        super(message, cause);
    }

    /** Tells the framework if it should save a screenshot when this exception occurs. The default implementation returns true.
     * Subclasses can override to deactivate debug attachments for their specific type of exception
     * @return <code>true</code> if a screenshot shall be created, otherwise <code>false</code> */
    public boolean shouldSaveDebugAttachments() {
        return true;
    }

    /** Provides the {@link TestStatus} which is appropriate for this exception
     * @return the associated {@link TestStatus} */
    public abstract TestStatus getTestStatus();

}
