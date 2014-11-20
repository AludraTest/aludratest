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
package org.aludratest.impl.log4testing.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.aludratest.impl.log4testing.ElementName;
import org.aludratest.impl.log4testing.ElementType;
import org.aludratest.impl.log4testing.TechnicalArgument;
import org.aludratest.impl.log4testing.TechnicalLocator;
import org.aludratest.impl.log4testing.data.TestCaseLog;
import org.aludratest.impl.log4testing.data.TestStepLog;
import org.aludratest.service.Action;
import org.aludratest.service.ComponentId;
import org.aludratest.testcase.TestStatus;
import org.aludratest.testcase.event.attachment.Attachment;
import org.aludratest.util.AludraTestUtil;
import org.databene.commons.ArrayUtil;
import org.databene.commons.CollectionUtil;
import org.databene.commons.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides logging related functionality.
 * @author Volker Bergmann
 */
public class LogUtil {

    /** The slf4j logger of the class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(LogUtil.class);

    /** Holds the XHTML code for a line break: &lt;BR/&gt; */
    private static final String BR = "<BR/>";

    /** Reads the line feed property from the virtual machine. */
    public static final String LF = System.getProperty("line.separator");

    /** Private constructor of utility class preventing instantiation by other classes */
    private LogUtil() {
    }

    /**
     * Logs a method invocation to log4testing.
     * @param testCase the log4testing {@link TestCaseLog} to log the information to
     * @param service the {@link ComponentId} of the related service
     * @param method the invoked method
     * @param status the log4testing {@link TestStatus} to assign with the logged information
     * @param result the result value or returned or the exception thrown by the method
     * @param args the method invocation parameters used
     */
    public static void log(TestCaseLog testCase, ComponentId<?> service, Method method, TestStatus status, Object result,
            Object[] args) {
        TestStepLog testStepLog = newTestStep(testCase, service, method, args);
        log(testStepLog, method, status, result);
    }

    /**
     * Creates a new {@link TestStepLog} for the given {@link TestCaseLog}.
     * @param testCase
     * @param service
     * @param method
     * @param args
     * @return the new {@link TestStepLog}
     */
    public static TestStepLog newTestStep(TestCaseLog testCase, ComponentId<?> service, Method method, Object[] args) {
        TestStepLog testStepLog = testCase.newTestStep();
        testStepLog.setCommand(method.getName());
        testStepLog.setService(service.toString());
        logArguments(method, args, testStepLog);
        return testStepLog;
    }

    /**
     * Logs a service invocation to log4testing.
     * @param ts the {@link TestStepLog} to log the information to
     * @param method the invoked method
     * @param status the {@link TestStatus} to assign with the logged information
     * @param result the result value or returned or the exception thrown by the method
     */
    public static void log(TestStepLog ts, Method method, TestStatus status, Object result) {
        if (result instanceof Throwable) {
            appendErrorMessage(((Throwable) result).getMessage(), (Throwable) result, null, null, ts);
        } else {
            ts.setStatus(status);
        }
    }

    /** Appends the errorMessage to the last test step.
     *  If no test step exists, one is created dynamically.
     *  @param messageToAppend the message to append
     *  @param error
     *  @param commentToAppend
     *  @param status the TestStatus to set; if null, it is ignored
     *  @param testCaseLog the test case log to which to append the step */
    public static void appendErrorInfoToLastStep(String messageToAppend, Throwable error, String commentToAppend,
            TestStatus status, TestCaseLog testCaseLog) {
        TestStepLog step = getOrCreateLastStep(testCaseLog);
        appendErrorMessage(messageToAppend, error, commentToAppend, status, step);
    }

    /** Appends the errorMessage to the last test step.
     *  If no test step exists, one is created dynamically.
     *  @param messageToAppend the message to append
     *  @param error
     *  @param commentToAppend
     *  @param status the TestStatus to set; if null, it is ignored
     *  @param testStepLog the test step log to which to append the step */
    public static void appendErrorMessage(String messageToAppend, Throwable error, String commentToAppend,
            TestStatus status, TestStepLog testStepLog) {
        // unwrap exception
        Throwable realError = AludraTestUtil.unwrapInvocationTargetException(error);
        if (StringUtil.isEmpty(messageToAppend) && realError != null) {
            messageToAppend = realError.getMessage();
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Exception occurred: " + messageToAppend, realError);
        }
        else {
            LOGGER.info("Exception occurred: " + messageToAppend);
        }

        appendMessageText(messageToAppend, testStepLog);
        appendComment(commentToAppend, testStepLog);
        setTestStatus(status, realError, testStepLog);
        appendStackTrace(realError, testStepLog);
    }

    /** Collects the debugging information available for the action (in form of attachments) and adds it to the log4testing log.
     * 
     * @param action Action to extract attachments from.
     * @param testStep Log to attach the attachments to. */
    public static void addDebugAttachments(Action action, TestStepLog testStep) {
        List<Attachment> attachments = action.createDebugAttachments();
        if (attachments != null) {
            for (Attachment attachment : attachments) {
                if (attachment == null) {
                    throw new IllegalArgumentException("Action " + action + " returned a null debug attachment");
                }
                testStep.addAttachment(attachment);
            }
        } else {
            LOGGER.debug("addDebugAttachments() returned null on {}", action);
        }
    }

    /**
     * Processes the invocation parameters, recognizing element type/name, locators and remaining arguments
     * @param method the invoked method
     * @param args the invocation arguments
     * @param testStepLog the test step log to update with the argument information
     */
    public static void logArguments(Method method, Object[] args, TestStepLog testStepLog) {
        // initialize all values to defaults
        String elementType = "";
        String elementName = "";
        String locator = "";
        ArrayList<Object> technicalArgs = new ArrayList<Object>();
        ArrayList<Object> unconsumedArgs = new ArrayList<Object>();

        if (args != null) {
            // check for the presence of a Logging annotation and modify values accordingly
            Annotation[][] paramsAnnos = method.getParameterAnnotations();
            for (int i = 0; i < args.length; i++) {
                Annotation[] paramAnnos = paramsAnnos[Math.min(i, paramsAnnos.length - 1)];
                if (!ArrayUtil.isEmpty(paramAnnos)) {
                    for (Annotation paramAnno : paramAnnos) {
                        if (paramAnno instanceof ElementName) {
                            elementName = String.valueOf(args[i]);
                        } else if (paramAnno instanceof ElementType) {
                            elementType = String.valueOf(args[i]);
                        } else if (paramAnno instanceof TechnicalLocator) {
                            locator = String.valueOf(args[i]);
                        } else if (paramAnno instanceof TechnicalArgument) {
                            technicalArgs.add(args[i]);
                        } else {
                            unconsumedArgs.add(args[i]);
                        }
                    }
                } else {
                    unconsumedArgs.add(args[i]);
                }
            }
        }
        testStepLog.setElementType(elementType);
        testStepLog.setElementName(elementName);
        testStepLog.setTechnicalLocator(locator);
        testStepLog.setTechnicalArguments(formatList(technicalArgs));
        testStepLog.setUsedArguments(formatList(unconsumedArgs));
    }

    // private helper methods --------------------------------------------------

    private static String formatList(List<Object> list) {
        if (CollectionUtil.isEmpty(list)) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(list.get(i));
        }
        return builder.toString();
    }

    private static void appendMessageText(String messageToAppend, TestStepLog testStepLog) {
        if (!StringUtil.isEmpty(messageToAppend)) {
            String errorMessage = StringUtil.nullToEmpty(testStepLog.getErrorMessage());
            if (!StringUtil.isEmpty(errorMessage)) {
                errorMessage += BR + BR;
            }
            errorMessage += messageToAppend;
            testStepLog.setErrorMessage(errorMessage);
        }
    }

    private static void setTestStatus(TestStatus status, Throwable realError, TestStepLog testStepLog) {
        if (status != null) {
            testStepLog.setStatus(status);
        } else if (realError != null) {
            testStepLog.setStatus(AludraTestUtil.getTestStatus(realError));
        } else if (testStepLog.getStatus() != null && !testStepLog.getStatus().isFailure()) {
            LOGGER.error("Neither exception nor TestStatus was provided to the logger, " +
                    "setting status to " + TestStatus.INCONCLUSIVE);
            testStepLog.setStatus(TestStatus.INCONCLUSIVE);
        }
    }

    private static void appendStackTrace(Throwable realError, TestStepLog testStepLog) {
        if (realError != null) {
            testStepLog.setError(realError);
            String stackTrace = renderStackTrace(realError);
            appendComment(stackTrace, testStepLog);
        }
    }

    private static void appendComment(String commentToAppend, TestStepLog testStepLog) {
        if (!StringUtil.isEmpty(commentToAppend)) {
            String comment = StringUtil.nullToEmpty(testStepLog.getComment());
            if (!StringUtil.isEmpty(comment)) {
                comment += BR + BR;
            }
            comment += commentToAppend;
            testStepLog.setComment(comment);
        }
    }

    /**
     * Renders an exception stack trace as String.
     * @param e the exception of which to render the stack trace
     * @return a String containing the stack trace
     */
    private static String renderStackTrace(Throwable e) {
        StringBuilder builder = new StringBuilder(e.getClass().getName())
        .append(": ").append(e.getMessage()).append(BR).append(LF);
        StackTraceElement[] stackTrace = e.getStackTrace();
        for (StackTraceElement element : stackTrace) {
            builder.append("&nbsp;&nbsp;&nbsp;&nbsp;at&nbsp;").append(element.toString()).append(BR).append(LF);
        }
        if (e.getCause() != null && e.getCause() != e) {
            builder.append(renderStackTrace(e.getCause()));
        }
        return builder.toString();
    }

    private static TestStepLog getOrCreateLastStep(TestCaseLog testCaseLog) {
        TestStepLog lastTestStep = testCaseLog.getLastTestStep();
        if (lastTestStep == null) {
            // if no TestStep exists, create one for logging the exception
            testCaseLog.newTestStepGroup("Error");
            lastTestStep = testCaseLog.newTestStep();
            lastTestStep.setCommand("Error report");
        }
        return lastTestStep;
    }

}
