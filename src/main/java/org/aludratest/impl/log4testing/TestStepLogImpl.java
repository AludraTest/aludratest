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
package org.aludratest.impl.log4testing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.aludratest.log4testing.AttachmentLog;
import org.aludratest.log4testing.TestCaseLog;
import org.aludratest.log4testing.TestStatus;
import org.aludratest.log4testing.TestStepGroupLog;
import org.aludratest.log4testing.TestStepLog;
import org.aludratest.log4testing.TestStepLogContainer;
import org.joda.time.DateTime;
import org.joda.time.Duration;

/** Implementation of the TestStepLog interface. This class is Thread-safe.
 * 
 * @author falbrech */
public final class TestStepLogImpl extends AbstractNamedTestLogElementImpl implements TestStepLog {

    private TestStepLogContainer parent;

    private volatile String command; // NOSONAR 'volatile' is used here intentionally

    private volatile String service; // NOSONAR 'volatile' is used here intentionally

    private volatile String elementType; // NOSONAR 'volatile' is used here intentionally

    private volatile String elementName; // NOSONAR 'volatile' is used here intentionally

    private volatile String technicalLocator; // NOSONAR 'volatile' is used here intentionally

    private volatile String technicalArguments; // NOSONAR 'volatile' is used here intentionally

    private volatile String usedArguments; // NOSONAR 'volatile' is used here intentionally

    private volatile String result; // NOSONAR 'volatile' is used here intentionally

    private volatile String errorMessage; // NOSONAR 'volatile' is used here intentionally

    private volatile Throwable error; // NOSONAR 'volatile' is used here intentionally

    private volatile String comment; // NOSONAR 'volatile' is used here intentionally

    private volatile TestStatus status; // NOSONAR 'volatile' is used here intentionally

    // optimization - as most steps will never have child steps, only initialize when needed
    private List<TestStepLogImpl> childSteps;

    // same as above
    private List<AttachmentLog> attachments;

    /** Constructs a new TestStepLogImpl object with the given parent group.
     * 
     * @param parent Group containing this test step. The new object will be added to the group automatically. */
    public TestStepLogImpl(TestStepGroupLogImpl parent) {
        // name does not matter
        super("teststep");
        this.parent = parent;
        parent.addTestStep(this);
    }

    /** Constructs a new TestStepLogImpl object with the given parent step.
     * 
     * @param parent Step containing this test step. The new object will be added to the step automatically. */
    public TestStepLogImpl(TestStepLogImpl parent) {
        super("teststep");
        this.parent = parent;
        parent.addChildTestStep(this);
    }

    /** Adds an inner (child) test step log to this test step log.
     * 
     * @param step Test step log to add. Must not be <code>null</code>. */
    public synchronized void addChildTestStep(TestStepLogImpl step) {
        if (step == null) {
            throw new IllegalArgumentException("step must not be null");
        }
        if (childSteps == null) {
            childSteps = new ArrayList<TestStepLogImpl>();
        }
        childSteps.add(step);
    }

    /** Adds an attachment to this test step log.
     * 
     * @param attachment Attachment to add. */
    public synchronized void addAttachment(AttachmentLog attachment) {
        if (attachment == null) {
            throw new IllegalArgumentException("attachment must not be null");
        }
        if (attachments == null) {
            attachments = new ArrayList<AttachmentLog>();
        }
        attachments.add(attachment);
    }

    @Override
    public synchronized List<? extends TestStepLog> getTestSteps() {
        // optimization - as most steps will never have child steps, only initialize when needed
        if (childSteps == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(new ArrayList<TestStepLogImpl>(childSteps));
    }

    @Override
    public Duration getWork() {
        return getDuration();
    }

    @Override
    public TestStatus getStatus() {
        return status;
    }

    /** Sets the status of this test step.
     * 
     * @param status New status for this test step. */
    public void setStatus(TestStatus status) {
        this.status = status;
    }

    @Override
    public int getStartTimeOffsetSeconds() {
        DateTime startTime = getStartTime();
        if (startTime == null) {
            return 0;
        }

        // find start time of test case
        TestCaseLog testCase = findTestCase();
        if (testCase == null) {
            return 0;
        }

        DateTime rootStartTime = testCase.getStartTime();
        return (int) new Duration(rootStartTime, startTime).getStandardSeconds();
    }

    @Override
    public TestStepLogContainer getParent() {
        return parent;
    }

    @Override
    public String getCommand() {
        return command;
    }

    /** Sets the command field of this test step log.
     * 
     * @param command New value for command field. */
    public void setCommand(String command) {
        this.command = intern(command);
    }

    @Override
    public String getService() {
        return service;
    }

    /** Sets the service field of this test step log.
     * 
     * @param service New value for service field. */
    public void setService(String service) {
        this.service = intern(service);
    }

    @Override
    public String getElementType() {
        return elementType;
    }

    /** Sets the elementType field of this test step log.
     * 
     * @param elementType New value for elementType field. */
    public void setElementType(String elementType) {
        this.elementType = intern(elementType);
    }

    @Override
    public String getElementName() {
        return elementName;
    }

    /** Sets the elementName field of this test step log.
     * 
     * @param elementName New value for elementName field. */
    public void setElementName(String elementName) {
        this.elementName = intern(elementName);
    }

    @Override
    public String getTechnicalLocator() {
        return technicalLocator;
    }

    /** Sets the technicalLocator field of this test step log.
     * 
     * @param technicalLocator New value for technicalLocator field. */
    public void setTechnicalLocator(String technicalLocator) {
        // no intern here - will most likely be unique value
        this.technicalLocator = technicalLocator;
    }

    @Override
    public String getTechnicalArguments() {
        return technicalArguments;
    }

    /** Sets the technicalArguments field of this test step log.
     * 
     * @param technicalArguments New value for technicalArguments field. */
    public void setTechnicalArguments(String technicalArguments) {
        // no intern here - will most likely be unique value
        this.technicalArguments = technicalArguments;
    }

    @Override
    public String getUsedArguments() {
        return usedArguments;
    }

    /** Sets the usedArguments field of this test step log.
     * 
     * @param usedArguments New value for usedArguments field. */
    public void setUsedArguments(String usedArguments) {
        // no intern here - will most likely be unique value
        this.usedArguments = usedArguments;
    }

    @Override
    public String getResult() {
        return result;
    }

    /** Sets the result field of this test step log.
     * 
     * @param result New value for result field. */
    public void setResult(String result) {
        this.result = intern(result);
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    /** Sets the errorMessage field of this test step log.
     * 
     * @param errorMessage New value for errorMessage field. */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public Throwable getError() {
        return error;
    }

    /** Sets the error field of this test step log.
     * 
     * @param error New value for error field. */
    public void setError(Throwable error) {
        this.error = error;
    }

    @Override
    public String getComment() {
        return comment;
    }

    /** Sets the comment field of this test step log.
     * 
     * @param comment New value for comment field. */
    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public synchronized List<AttachmentLog> getAttachments() {
        if (attachments == null) {
            return Collections.emptyList();
        }

        return Collections.unmodifiableList(new ArrayList<AttachmentLog>(attachments));
    }

    private TestCaseLog findTestCase() {
        if (parent instanceof TestStepLogImpl) {
            return ((TestStepLogImpl) parent).findTestCase();
        }
        else if (parent instanceof TestStepGroupLog) {
            return ((TestStepGroupLog) parent).getParent();
        }

        return null;
    }

    private static String intern(String value) {
        return value == null ? null : value.intern();
    }

}
