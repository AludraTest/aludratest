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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

import org.aludratest.AludraTest;
import org.aludratest.exception.AludraTestException;
import org.aludratest.impl.log4testing.data.TestCaseLog;
import org.aludratest.impl.log4testing.data.TestStepLog;
import org.aludratest.impl.log4testing.util.LogUtil;
import org.aludratest.service.Action;
import org.aludratest.service.ComponentId;
import org.aludratest.service.ErrorReport;
import org.aludratest.service.SystemConnector;
import org.aludratest.testcase.TestStatus;
import org.aludratest.util.retry.AutoRetry;
import org.databene.commons.Assert;
import org.databene.commons.CollectionUtil;
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

    private ComponentId<?> serviceId;

    /** The current {@link SystemConnector}. */
    private SystemConnector systemConnector;

    private Throwable exceptionUnderErrorChecking;

    /** The log4testing test case to be used for logging. */
    private TestCaseLog testCaseLog;

    /** If set to true, the instance will ignore further invocations after
     *  the occurrence of an exception. */
    private boolean stopOnException;

    /**
     * Constructor which takes the initialization values for all attributes.
     * @param target the target object to forward calls to
     * @param serviceId the {@link ComponentId} related to the target object
     * @param systemConnector an optional {@link SystemConnector} to to provide SUT information
     * @param testCaseLog the test case to log to
     * @param stopOnException a flag that indicates whether to stop on exceptions
     */
    public ControlFlowHandler(Object target, ComponentId<?> serviceId, SystemConnector systemConnector, TestCaseLog testCaseLog,
            boolean stopOnException) {
        // check preconditions
        Assert.notNull(target, "target");
        Assert.notNull(testCaseLog, "testCase");

        // assign values
        this.target = target;
        this.serviceId = serviceId;
        this.systemConnector = systemConnector;
        this.exceptionUnderErrorChecking = null;
        this.testCaseLog = testCaseLog;
        this.stopOnException = stopOnException;
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
            }
            return forwardAndHandleException(method, args);
        } else {
            LogUtil.log(testCaseLog, serviceId, method, TestStatus.IGNORED, null, args);
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
        try {
            return forwardWithRetry(method, args);
        }
        catch (Exception e) { // NOSONAR
            try {
                handleException(e);
            } finally {
                // make sure that test case execution is stopped if configured to do so
                // and also if there is an exception in the call to handleException(e)
                if (stopOnException) {
                    FlowController.getInstance().stopTestCaseExecution(testCaseLog);
                }
            }
            return AludraTestUtil.nullOrPrimitiveDefault(method.getReturnType());
        }
    }

    private void handleException(Exception e) {
        Throwable t = AludraTestUtil.unwrapInvocationTargetException(e);
        if (systemConnector != null && requiresErrorChecking(t)) {
            if (this.exceptionUnderErrorChecking != null) {
                String errorMessage = "An exception occurred while " +
                        "SystemConnector '" + systemConnector + "' " +
                        "examined the cause of another exception. " +
                        "Cancelled execution to avoid infinite recursion.";
                LogUtil.appendErrorInfoToLastStep(errorMessage, t, null, TestStatus.FAILEDAUTOMATION, testCaseLog);
            } else {
                this.exceptionUnderErrorChecking = t;
                try {
                    checkAndLogErrors();
                } finally {
                    this.exceptionUnderErrorChecking = null;
                }
            }
        }
    }

    private void checkAndLogErrors() {
        List<ErrorReport> errors = systemConnector.checkForErrors();
        if (CollectionUtil.isEmpty(errors)) {
            LOGGER.debug("No errors found by system connector {}", systemConnector);
        }
        if (errors != null && errors.size() > 0) {
            for (int i = 0; i < errors.size(); i++) {
                ErrorReport error = errors.get(i);
                LOGGER.debug("System connector {} reported error: {}", systemConnector, error);
                LogUtil.appendErrorInfoToLastStep(error.getMessage(), null, error.getStackTrace(),
                        error.getTestStatus(), testCaseLog);
            }
            if (target instanceof Action) {
                LogUtil.addDebugAttachments((Action) target, testCaseLog.getLastTestStep());
            }
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

    private Object forwardWithRetry(Method method, Object[] args) throws Throwable { //NOSONAR
        int retryCount = 0;
        boolean doRetry;
        Throwable recentException;
        do {
            doRetry = false;
            try {
                return method.invoke(target, args);
            }
            catch (Exception e) { // NOSONAR
                Throwable t = AludraTestUtil.unwrapInvocationTargetException(e);
                recentException = t;

                AutoRetry retry = AludraTest.getInstance().getServiceManager().newImplementorInstance(AutoRetry.class);

                if (retry.matches(method, t, retryCount)) {
                    doRetry = true;
                    retryCount++;
                    TestStepLog testStep = testCaseLog.getLastTestStep();
                    testStep.setStatus(TestStatus.IGNORED);
                    testStep.setComment((testStep.getComment() + " Ignored Exception: " + t).trim());
                }
            }
        } while (doRetry);
        throw recentException;
    }

    /** Uses the {@link FlowController} to find out if further
     *  steps of a given test case shall be executed. */
    private boolean shallContinueTestCaseExecution() {
        return !FlowController.getInstance().isStopped(testCaseLog);
    }

}
