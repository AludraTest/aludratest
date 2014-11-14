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
package org.aludratest.impl.log4testing.handler;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

import org.aludratest.exception.AludraTestException;
import org.aludratest.impl.log4testing.AttachParameter;
import org.aludratest.impl.log4testing.AttachResult;
import org.aludratest.impl.log4testing.data.TestCaseLog;
import org.aludratest.impl.log4testing.data.TestStepLog;
import org.aludratest.impl.log4testing.data.attachment.Attachment;
import org.aludratest.impl.log4testing.util.LogUtil;
import org.aludratest.service.Action;
import org.aludratest.service.ComponentId;
import org.aludratest.service.Condition;
import org.aludratest.testcase.TestStatus;
import org.aludratest.util.AludraTestUtil;

/**
 * Wraps a realObject, forwards method calls to it and logs invocation information
 * with log4testing. It is implemented as generic {@link InvocationHandler}
 * for use with Java's dynamic proxy API.
 * @author Volker Bergmann
 */
public class LoggingInvocationHandler implements InvocationHandler {

    /** The real object to forward method invocations to. */
    private Object realObject;

    private ComponentId<?> serviceId;

    /** The log4testing {@link TestCaseLog} to log invocation information to. */
    private TestCaseLog testCase;


    // constructors ------------------------------------------------------------

    /**
     * Constructor
     * @param realObject the real object to wrap
     * @param serviceId
     * @param testCase the log4testing {@link TestCaseLog} to use for logging
     */
    public LoggingInvocationHandler(Object realObject, ComponentId<?> serviceId, TestCaseLog testCase) {
        this.realObject = realObject;
        this.serviceId = serviceId;
        this.testCase = testCase;
    }


    // InvocationHandler interface implementation ------------------------------

    /**
     * Forwards the call to the realObject and logs invocation parameters,
     * return value, exceptions and annotated information with log4testing.
     * @see InvocationHandler#invoke(Object, Method, Object[])
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable { //NOSONAR
        TestStepLog testStepLog = null;
        try {
            if (!Condition.class.isAssignableFrom(method.getDeclaringClass()) && !method.getName().equals("setSystemConnector")) {
                testStepLog = LogUtil.newTestStep(testCase, serviceId, method, args);
                saveParameterAttachments(method, args, testStepLog);
            }
            Object result = method.invoke(realObject, args);
            if (testStepLog != null) {
                testStepLog.setStatus(TestStatus.PASSED);
                saveResultAttachment(method, result, testStepLog);
            }
            return result;
        } catch (Exception e) {
            if (testStepLog != null) {
                try {
                    LogUtil.appendErrorMessage(e.getMessage(), e, null, null, testStepLog);
                    addDebuggingAttachmentsIfUseful(e, testStepLog);
                }
                catch (Exception e1) {
                    LogUtil.log(testCase, serviceId, method, TestStatus.INCONCLUSIVE, e1, args);
                }
            }
            else {
                LogUtil.log(testCase, serviceId, method, TestStatus.INCONCLUSIVE, e, args);
            }
            // rethrow the exception, so that the client code (e.g. ControlFlowHandler) is notified
            throw AludraTestUtil.unwrapInvocationTargetException(e);
        } finally {
            if (testStepLog != null) {
                // in the end, log the arguments once more in case they have been modified
                // (e.g. an ElementLocators instance which was updated in the invoked method)
                LogUtil.logArguments(method, args, testStepLog);
            }
        }
    }



    // private helper methods --------------------------------------------------

    private void saveParameterAttachments(Method method, Object[] args, TestStepLog testStepLog) {
        if (realObject instanceof Action) {
            Annotation[][] allParamAnnos = method.getParameterAnnotations();
            for (int i = 0; i < allParamAnnos.length; i++) {
                Annotation[] singleParamAnnos = allParamAnnos[i];
                for (int j = 0; j < singleParamAnnos.length; j++) {
                    if (singleParamAnnos[j] instanceof AttachParameter) {
                        AttachParameter anno = (AttachParameter) singleParamAnnos[j];
                        createAttachments(args[i], anno.value(), testStepLog);
                    }
                }
            }
        }
    }

    private void saveResultAttachment(Method method, Object result, TestStepLog testStepLog) {
        if (realObject instanceof Action) {
            AttachResult annotation = method.getAnnotation(AttachResult.class);
            if (annotation != null) {
                createAttachments(result, annotation.value(), testStepLog);
            }
        }
    }

    private void createAttachments(Object objectToAttach, String title, TestStepLog testStepLog) {
        List<Attachment> attachments = ((Action) realObject).createAttachments(objectToAttach, title);
        if (attachments != null) {
            for (Attachment attachment : attachments) {
                testStepLog.addAttachment(attachment);
            }
        }
    }

    /**
     * Causes the creation of debugging attachments
     * if the exception is unknown or indicates to do.
     * @author Volker Bergmann
     */
    private void addDebuggingAttachmentsIfUseful(Throwable e, TestStepLog testStep) {
        if (realObject instanceof Action && shouldSaveDebugAttachments(e)) {
            LogUtil.addDebugAttachments((Action) realObject, testStep);
        }
    }

    private boolean shouldSaveDebugAttachments(Throwable e) {
        e = AludraTestUtil.unwrapInvocationTargetException(e); // NOSONAR
        if (!(e instanceof AludraTestException)) {
            return true; // save attachments for unknown exception types
        }
        AludraTestException ae = (AludraTestException) e;
        return ae.shouldSaveDebugAttachments(); // for AludraTestExceptions save attachments only if requested
    }

}
