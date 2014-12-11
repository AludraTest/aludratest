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
package org.aludratest.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

import org.aludratest.exception.AludraTestException;
import org.aludratest.impl.log4testing.AttachParameter;
import org.aludratest.impl.log4testing.AttachResult;
import org.aludratest.service.Action;
import org.aludratest.service.AludraService;
import org.aludratest.service.ComponentId;
import org.aludratest.service.SystemConnector;
import org.aludratest.testcase.AludraTestContext;
import org.aludratest.testcase.TestStatus;
import org.aludratest.testcase.event.ErrorReport;
import org.aludratest.testcase.event.SystemErrorReporter;
import org.aludratest.testcase.event.attachment.Attachment;
import org.aludratest.testcase.event.impl.TestStepInfoBean;
import org.aludratest.util.retry.AutoRetry;
import org.databene.commons.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wraps an object and intercepts method invocations:
 * By default, the calls are forwarded to the wrapped object,
 * but when an exception occurred, the {@link ControlFlowHandler}
 * will ignore further calls if the property 'stopOnException'
 * is 'true'.
 * @author Volker Bergmann
 */
public class ControlFlowHandler implements InvocationHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ControlFlowHandler.class);

    /** The wrapped object to forward invocations to. */
    private Object target;

    private ComponentId<? extends AludraService> serviceId;

    /** The current {@link SystemConnector}. */
    private SystemConnector systemConnector;

    private Throwable exceptionUnderErrorChecking;

    private TestStepInfoBean stepUnderErrorChecking;

    private AludraTestContext testContext;

    /** If set to true, the instance will ignore further invocations after
     *  the occurrence of an exception. */
    private boolean stopOnException;

    private boolean logTestSteps;

    /** Constructor which takes the initialization values for all attributes.
     * @param target the target object to forward calls to
     * @param serviceId the {@link ComponentId} related to the target object
     * @param systemConnector an optional {@link SystemConnector} to to provide SUT information
     * @param testContext the test context to log to
     * @param stopOnException a flag that indicates whether to stop on exceptions
     * @param logTestSteps If <code>true</code>, method invocations will be fired to the test context as new test step. */
    public ControlFlowHandler(Object target, ComponentId<? extends AludraService> serviceId, SystemConnector systemConnector,
            AludraTestContext testContext, boolean stopOnException, boolean logTestSteps) {
        // check preconditions
        Assert.notNull(target, "target");
        Assert.notNull(testContext, "testContext");

        // assign values
        this.target = target;
        this.serviceId = serviceId;
        this.systemConnector = systemConnector;
        this.testContext = testContext;
        this.stopOnException = stopOnException;
        this.logTestSteps = logTestSteps;
    }


    // InvocationHandler interface implementation ------------------------------

    /**
     * Handles the actual invocation. First it checks if calls shall be ignored.
     * If yes, then an according log entry is written, otherwise the method
     * {@link #forwardAndHandleException(Method, Object[])} is called to call the wrapped object.
     * @see InvocationHandler#invoke(Object, Method, Object[])
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable { //NOSONAR
        if (shallContinueTestCaseExecution()) {
            if (method.getName().equals("setSystemConnector")) {
                this.systemConnector = (SystemConnector) args[0];
                return null;
            }
            else {
                return forwardAndHandleException(method, args);
            }
        } else {
            if (logTestSteps) {
                TestStepInfoBean testStep = new TestStepInfoBean();
                testStep.setServiceId(serviceId);
                testStep.setCommandNameAndArguments(method, args);
                testStep.setTestStatus(TestStatus.IGNORED);
                testContext.fireTestStep(testStep);
            }
            return AludraTestUtil.nullOrPrimitiveDefault(method.getReturnType());
        }
    }


    // private helpers ---------------------------------------------------------

    /**
     * Performs the forwarding of the invocation to the wrapped object.
     * If an exception occurred and {@link #stopOnException} is true,
     * then the {@link FlowController} is called to prevent further
     * test step executions and null or an valid replacement is returned
     * as method return value
     * (@see {@link AludraTestUtil#nullOrPrimitiveDefault(Class)}).
     */
    private Object forwardAndHandleException(Method method, Object[] args) throws Throwable { //NOSONAR
        TestStepInfoBean testStep = new TestStepInfoBean();
        TestStepInfoBean[] outArr = new TestStepInfoBean[1];
        try {
            testStep.setCommandNameAndArguments(method, args);
            if (logTestSteps) {
                // attach parameters, if applicable
                attachAttachableParameters(testStep, method, args);
            }
            Object result = forwardWithRetry(method, args, testStep, outArr);
            testStep = outArr[0];
            if (logTestSteps) {
                attachResultIfAttachable(testStep, method, result);
                testContext.fireTestStep(testStep);
            }
            return result;
        }
        catch (Exception e) { // NOSONAR
            testStep = outArr[0];
            try {
                handleException(e, testStep);
            } finally {
                // make sure that test case execution is stopped if configured to do so
                // and also if there is an exception in the call to handleException(e)
                if (stopOnException) {
                    FlowController.getInstance().stopTestCaseExecution(testContext);
                }
            }
            return AludraTestUtil.nullOrPrimitiveDefault(method.getReturnType());
        }
    }

    private void attachResultIfAttachable(TestStepInfoBean testStep, Method method, Object result) {
        if (!(target instanceof Action)) {
            return;
        }
        Action action = (Action) target;

        AttachResult attachResult = method.getAnnotation(AttachResult.class);
        if (attachResult != null) {
            List<Attachment> attachments = action.createAttachments(result, attachResult.value());
            for (Attachment attachment : attachments) {
                testStep.addAttachment(attachment);
            }
        }
    }

    private void attachAttachableParameters(TestStepInfoBean testStep, Method method, Object[] args) {
        if (!(target instanceof Action)) {
            return;
        }
        Action action = (Action) target;

        Annotation[][] annots = method.getParameterAnnotations();

        for (int i = 0; i < annots.length; i++) {
            Annotation[] paramAnnots = annots[i];
            for (Annotation a : paramAnnots) {
                if (a.annotationType() == AttachParameter.class) {
                    List<Attachment> attachments = action.createAttachments(args[i], ((AttachParameter) a).value());
                    for (Attachment attachment : attachments) {
                        testStep.addAttachment(attachment);
                    }
                }
            }
        }
    }

    private void handleException(Exception e, TestStepInfoBean currentTestStep) {
        Throwable t = AludraTestUtil.unwrapInvocationTargetException(e);
        setErrorAndStatus(currentTestStep, e);
        if (systemConnector != null && requiresErrorChecking(t)) {
            if (this.exceptionUnderErrorChecking != null) {
                testContext.fireTestStep(stepUnderErrorChecking);
                // marker for inner call
                stepUnderErrorChecking = null;
                String errorMessage = "An exception occurred while " + "SystemConnector '" + systemConnector + "' "
                        + "examined the cause of another exception. " + "Cancelled execution to avoid infinite recursion.";
                currentTestStep.setErrorMessage(errorMessage);
                currentTestStep.setError(t);
                currentTestStep.setTestStatus(TestStatus.FAILEDAUTOMATION);
                testContext.fireTestStep(currentTestStep);
            } else {
                this.exceptionUnderErrorChecking = t;
                this.stepUnderErrorChecking = currentTestStep;
                try {
                    checkAndLogErrors(currentTestStep);
                } finally {
                    this.exceptionUnderErrorChecking = null;
                    this.stepUnderErrorChecking = null;
                }
            }
        }
        else {
            testContext.fireTestStep(currentTestStep);
        }

    }

    private void checkAndLogErrors(TestStepInfoBean testStep) {
        ErrorReport error = checkForError(systemConnector);
        if (error == null) {
            LOGGER.debug("No errors found by system connector {}", systemConnector);
            // assert no recursion error occurred
            if (stepUnderErrorChecking != null) {
                testContext.fireTestStep(testStep);
            }
        }
        else {
            LOGGER.debug("System connector {} reported error: {}", systemConnector, error);

            // ignore from now on
            FlowController.getInstance().stopTestCaseExecution(testContext);
            testContext.fireTestStep(testStep);

            TestStepInfoBean sysConnError = new TestStepInfoBean();
            sysConnError.copyBaseInfoFrom(testStep);
            sysConnError.setError(null);
            sysConnError.setErrorMessage(error.getMessage());

            for (Attachment a : error.getAttachments()) {
                sysConnError.addAttachment(a);
            }
            sysConnError.setTestStatus(error.getTestStatus());

            if (target instanceof Action) {
                Action action = (Action) target;
                for (Attachment attachment : action.createDebugAttachments()) {
                    sysConnError.addAttachment(attachment);
                }
            }

            testContext.fireTestStep(sysConnError);
        }
    }

    private boolean requiresErrorChecking(Throwable t) {
        // Check only for some sub classes of AludraTestException
        if (!(t instanceof AludraTestException)) {
            return false;
        }
        TestStatus status = AludraTestUtil.getTestStatus(t);
        return (status == TestStatus.FAILED || status == TestStatus.FAILEDAUTOMATION);
    }

    private ErrorReport checkForError(SystemConnector connector) {
        SystemErrorReporter reporter = connector.getConnector(SystemErrorReporter.class);
        return reporter != null ? reporter.checkForError() : null;
    }

    private Object forwardWithRetry(Method method, Object[] args, TestStepInfoBean testStep, TestStepInfoBean[] outTestStep)
            throws Throwable { // NOSONAR
        int retryCount = 0;
        boolean doRetry;
        Throwable recentException;
        TestStepInfoBean currentStep = testStep;
        outTestStep[0] = currentStep;
        do {
            doRetry = false;
            try {
                return method.invoke(target, args);
            }
            catch (Exception e) { // NOSONAR
                Throwable t = AludraTestUtil.unwrapInvocationTargetException(e);
                recentException = t;

                AutoRetry retry = testContext.newComponentInstance(AutoRetry.class);

                if (retry.matches(method, t, retryCount)) {
                    doRetry = true;
                    retryCount++;

                    currentStep.setTestStatus(TestStatus.IGNORED);
                    currentStep.setErrorMessage("Ignored Exception: " + t.getMessage());
                    currentStep.setError(t);
                    testContext.fireTestStep(currentStep);
                    currentStep = new TestStepInfoBean();
                    currentStep.copyBaseInfoFrom(testStep);
                    outTestStep[0] = currentStep;
                }
            }
        } while (doRetry);
        throw recentException;
    }

    /** Uses the {@link FlowController} to find out if further
     *  steps of a given test case shall be executed. */
    private boolean shallContinueTestCaseExecution() {
        return !FlowController.getInstance().isStopped(testContext);
    }

    private void setErrorAndStatus(TestStepInfoBean testStep, Throwable t) {
        t = AludraTestUtil.unwrapInvocationTargetException(t);
        testStep.setError(t);
        testStep.setErrorMessage(t.getMessage());
        if (t instanceof AludraTestException) {
            testStep.setTestStatus(((AludraTestException) t).getTestStatus());
            // also extract attachments, if applicable
            if (((AludraTestException) t).shouldSaveDebugAttachments() && (target instanceof Action)) {
                Action action = (Action) target;
                List<Attachment> attachments = action.createDebugAttachments();
                if (attachments != null) {
                    for (Attachment attachment : attachments) {
                        testStep.addAttachment(attachment);
                    }
                }
            }
        }
        else {
            testStep.setTestStatus(TestStatus.INCONCLUSIVE);
        }
    }

}
