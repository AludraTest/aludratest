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
package org.aludratest.impl.log4testing.data;

import java.util.ArrayList;
import java.util.List;

import org.aludratest.testcase.TestStatus;
import org.aludratest.testcase.event.attachment.Attachment;
import org.joda.time.DateTime;

/** <p>
 * This class stores all the data which corresponds to a test step. Which data is to be stored in a test step is to be decided by
 * test writers themselves. That means that test writers can decide if they want log technical or domain driven commands or for
 * instance which messages will be provided by their implementations of their commands after execution. The starting time of a
 * test step will be automatically set when a new instance of this test step is created.
 * </p>
 * 
 * <p>
 * In addition to all the mentioned information every test step has its unique id which is unique in the context of the JVM in
 * which the test step is created. Ids can change across two executions. This means that the id of a test step can change with the
 * next execution.
 * </p>
 * 
 * <p>
 * {@link TestStepLog}s can provide more information through attachments which itself can be text files, pictures, binary files or
 * something else. Purpose of these attachments is, that more information about the object under test can be provided. Simple
 * messages are not enough sometimes and additional information is needed.<br>
 * Tip: Very helpful
 * </p>
 * 
 * @author Marcel Malitz
 * @author Joerg Langnickel
 * @author Volker Bergmann */
public class TestStepLog extends TestObject {

    private static final String NOT_SET = "Not SET";

    private static final int MILLIS_PER_SECOND = 1000;


    // attributes --------------------------------------------------------------

    private TestStepGroup parent;

    /** Date and time when the execution of this {@link TestStepLog} was started. */
    private DateTime startingTime = null;

    /** Date and time when the execution of this {@link TestStepLog} was finished. */
    private DateTime finishingTime = null;

    /** Command which shall be logged with this {@link TestStepLog}. The command is
     *  usually a test command itself. */
    private String command = null;

    /** Identifier string denoting the service instance on which the test step was executed */
    private String service = null;

    /** A (user) interface element, for example a Button or a Link */
    private String elementType = NOT_SET;

    /** The name of the operation in the Java Classes. */
    private String elementName = NOT_SET;

    /** The technical locator of the element. */
    private String technicalLocator = NOT_SET;

    /** The arguments used in the operation. */
    private String usedArguments = NOT_SET;

    private String errorMessage = "";

    private Throwable error;

    private TestStatus status = TestStatus.PASSED;

    /** List of attachments which provide more information about this {@link TestStepLog}. */
    private List<Attachment> attachments = new ArrayList<Attachment>();

    private String technicalArguments;

    // constructor -------------------------------------------------------------

    /**
     * Constructor.
     * @param parent the group that owns the new step
     */
    protected TestStepLog(TestStepGroup parent) {
        this.status = TestStatus.PASSED;
        this.parent = parent;
        this.error = null;
        start();
    }

    public TestStepLog() {

    }

    // interface ---------------------------------------------------------------

    /**
     * Name of the command to which this {@link TestStepLog} corresponds. The
     * returned command is not more than just a simple name.
     * 
     * @return the command for which this {@link TestStepLog} provides some test
     *         logging information. Returns <code>null</code> if it wasn't set.
     */
    public String getCommand() {
        return command;
    }

    /**
     * Is used to set the name of the command logged by this {@link TestStepLog}.
     * 
     * @param command
     *            is the name of the command which were executed and which shall
     *            be logged with the help of this {@link TestStepLog} object
     * @return the {@link TestStepLog} itself so that it can be used for some other
     *         method calls
     */
    public TestStepLog setCommand(String command) {
        this.command = command;
        return this;
    }

    /** @return the {@link #service}. */
    public String getService() {
        return service;
    }

    /**
     * Sets the {@link #service}.
     * @param service 
     * @return a self-reference to the TestStep for invocation chaining
     */
    public TestStepLog setService(String service) {
        this.service = service;
        return this;
    }

    /**
     * @return the status
     */
    @Override
    public TestStatus getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     * @return a self-reference to the TestStep for invocation chaining
     */
    public TestStepLog setStatus(TestStatus status) {
        this.status = status;
        finish();
        return this;
    }

    /**
     * Adds an attachment and associates it with this test step.
     * @param attachment to be associated with this test step
     * @return a self-reference to the TestStep for invocation chaining
     */
    public TestStepLog addAttachment(Attachment attachment) {
        if (attachment == null) {
            throw new IllegalArgumentException("attachment is null");
        }
        attachments.add(attachment);
        return this;
    }

    /**
     * @return the attachedFiles
     */
    public Iterable<Attachment> getAttachments() {
        return attachments;
    }

    /**
     * 
     * @return the starting Time
     */
    public final TestStepLog start() {
        startingTime = new DateTime();
        return this;
    }

    /**
     * Finishes a TestStep by setting the <code> finishingTime </code>
     * @return a self-reference to the TestStep for invocation chaining
     */
    public TestStepLog finish() {
        finishingTime = new DateTime();
        return this;
    }

    /** @return the finishing time */
    @Override
    public DateTime getFinishingTime() {
        return finishingTime;
    }

    /** @return the {@link #startingTime}  */
    @Override
    public DateTime getStartingTime() {
        return startingTime;
    }

    /** @return the offset of this step's starting time to the owning test case's starting time */
    public int getStartTimeOffsetSeconds() {
        TestCaseLog testCase = getTestCase();
        if (startingTime != null && testCase != null && testCase.getStartingTime() != null) {
            return (int) ((this.startingTime.getMillis() - testCase.getStartingTime().getMillis()) / MILLIS_PER_SECOND);
        }
        return 0;
    }

    /** Checks if the TestStep is failed. */
    @Override
    public boolean isFailed() {
        return (this.status != null && this.status.isFailure());
    }

    /** @return the {@link #elementType} */
    public String getElementType() {
        return elementType;
    }

    /** Sets the {@link #elementType}
     *  @param elementType the element type to set */
    public void setElementType(String elementType) {
        this.elementType = elementType;
    }

    /** @return the {@link #elementName} */
    public String getElementName() {
        return elementName;
    }

    /** Sets the operation.
     *  @param elementName the operation to set */
    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    /** @return the {@link #technicalLocator} */
    public String getTechnicalLocator() {
        return technicalLocator;
    }

    /**
     * Sets the {@link #technicalLocator}
     * @param technicalLocator the technical locator to set
     * @return a self-reference to the TestStep for invocation chaining
     */
    public TestStepLog setTechnicalLocator(String technicalLocator) {
        this.technicalLocator = technicalLocator;
        return this;
    }

    /** @return the {@link #usedArguments} */
    public String getUsedArguments() {
        return usedArguments;
    }

    /**
     * Sets the {@link #usedArguments}
     * @param usedArguments the used arguments to set
     * @return a self-reference to the TestStep for invocation chaining
     */
    public TestStepLog setUsedArguments(String usedArguments) {
        this.usedArguments = usedArguments;
        return this;
    }

    /** @return the {@link #technicalArguments} */
    public String getTechnicalArguments() {
        return technicalArguments;
    }

    /**
     * Sets the {@link #technicalArguments}
     * @param technicalArguments the used arguments to set
     * @return a self-reference to the TestStep for invocation chaining
     */
    public TestStepLog setTechnicalArguments(String technicalArguments) {
        this.technicalArguments = technicalArguments;
        return this;
    }

    /** @return the {@link #errorMessage} */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets the {@link #errorMessage}
     * @param errorMessage the error message to set
     * @return a self-reference to the TestStep for invocation chaining
     */
    public TestStepLog setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    /** @return the {@link #error} */
    public Throwable getError() {
        return error;
    }

    /**
     * Sets the {@link #error}
     * @param error the error to set
     * @return a self-reference to the TestStep for invocation chaining
     */
    public TestStepLog setError(Throwable error) {
        this.error = error;
        return this;
    }

    @Override
    public String toString() {
        return command + " " + errorMessage;
    }

    private TestCaseLog getTestCase() {
        return (parent != null ? parent.getParent() : null);
    }

}
