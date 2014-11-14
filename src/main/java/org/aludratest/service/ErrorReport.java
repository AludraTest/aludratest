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
package org.aludratest.service;

import org.aludratest.testcase.TestStatus;
import org.databene.commons.Assert;

/**
 * Wraps exception information for exceptions or errors that occurred internally in a system.
 * They are reported by the {@link SystemConnector} interface
 * @author Volker Bergmann
 */
public class ErrorReport {

    /** An optional reference number of the error. */
    private final String referenceNumber;

    /** The error message. */
    private final String message;

    /** An optional error type. */
    private final String errorType;

    /** An optional stack trace. */
    private final String stackTrace;

    /** the test status to report */
    private final TestStatus testStatus;

    /** Constructor which sets the {@link #errorType} to {@link TestStatus#FAILED}. 
     * @param referenceNumber the {@link #referenceNumber}
     * @param message the {@link #message}
     * @param errorType the {@link #errorType}
     * @param stackTrace the {@link #stackTrace} */
    public ErrorReport(String referenceNumber, String message, String errorType, String stackTrace) {
        this(referenceNumber, message, errorType, TestStatus.FAILED, stackTrace);
    }

    /** Constructor which initializes each attribute to the provided parameters.
     * @param referenceNumber the {@link #referenceNumber}
     * @param message the {@link #message}
     * @param errorType the {@link #errorType}
     * @param testStatus the {{@link #testStatus} to report
     * @param stackTrace the {@link #stackTrace} */
    public ErrorReport(String referenceNumber, String message, String errorType, TestStatus testStatus, String stackTrace) {
        this.referenceNumber = referenceNumber;
        this.message = Assert.notNull(message, "message");
        this.errorType = errorType;
        this.testStatus = Assert.notNull(testStatus, "testStatus");
        this.stackTrace = stackTrace;
    }

    /** @return the {@link #referenceNumber} */
    public String getReferenceNumber() {
        return referenceNumber;
    }

    /** @return the {@link #message} */
    public String getMessage() {
        return message;
    }

    /** @return the {@link #errorType} */
    public String getErrorType() {
        return errorType;
    }

    /** @return the {@link #stackTrace} */
    public String getStackTrace() {
        return stackTrace;
    }

    /** @return the {@link #testStatus} */
    public TestStatus getTestStatus() {
        return testStatus;
    }

    @Override
    public String toString() {
        return message;
    }

}
