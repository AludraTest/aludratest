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
package org.aludratest.testcase.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.aludratest.service.SystemConnector;
import org.aludratest.testcase.TestStatus;
import org.aludratest.testcase.event.attachment.Attachment;
import org.databene.commons.Assert;

/**
 * Wraps exception information for exceptions or errors that occurred internally in a system.
 * They are reported by the {@link SystemConnector} interface
 * @author Volker Bergmann
 */
public final class ErrorReport {

    /** An optional reference number of the error. */
    private final String referenceNumber;

    /** The error message. */
    private final String message;

    /** the test status to report */
    private final TestStatus testStatus;

    private final List<Attachment> attachments = new ArrayList<Attachment>();

    /** Constructor which creates an error report with {@link TestStatus#FAILED} status.
     * @param referenceNumber A reference number for the error report.
     * @param message The error message, if any. */
    public ErrorReport(String referenceNumber, String message) {
        this(referenceNumber, message, TestStatus.FAILED);
    }

    /** Constructor which creates an error report with the given attachments.
     * @param referenceNumber A reference number for the error report.
     * @param message The error message, if any.
     * @param attachments The attachments to attach to the report. */
    public ErrorReport(String referenceNumber, String message, Collection<? extends Attachment> attachments) {
        this(referenceNumber, message, TestStatus.FAILED, attachments);
    }

    /** Constructor which creates an error report with the given status.
     * @param referenceNumber A reference number for the error report.
     * @param message The error message, if any.
     * @param testStatus Test status to report. */
    public ErrorReport(String referenceNumber, String message, TestStatus testStatus) {
        this(referenceNumber, message, testStatus, Collections.<Attachment> emptySet());
    }

    /** Constructor which creates an error report with the given status and the given attachments.
     * @param referenceNumber A reference number for the error report.
     * @param message The error message, if any.
     * @param testStatus Test status to report.
     * @param attachments The attachments to attach to the report. */
    public ErrorReport(String referenceNumber, String message, TestStatus testStatus, Collection<? extends Attachment> attachments) {
        this.referenceNumber = referenceNumber;
        this.message = Assert.notNull(message, "message");
        this.testStatus = Assert.notNull(testStatus, "testStatus");
        this.attachments.addAll(attachments);
    }


    /** @return A reference number for this error report. This can be used to lookup the error e.g. in a log file. */
    public String getReferenceNumber() {
        return referenceNumber;
    }

    /** @return The error message for this report. */
    public String getMessage() {
        return message;
    }

    /** @return The test status to report. */
    public TestStatus getTestStatus() {
        return testStatus;
    }

    /** @return The attachments to report. */
    public Iterable<Attachment> getAttachments() {
        return attachments;
    }

    @Override
    public String toString() {
        return message;
    }

}
