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
package org.aludratest.junit;

import org.aludratest.exception.TechnicalException;
import org.aludratest.impl.log4testing.data.TestStepLog;
import org.aludratest.scheduler.WrapperFactory;
import org.aludratest.scheduler.node.RunnerLeaf;
import org.aludratest.testcase.TestStatus;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

/**
 * {@link WrapperFactory} implementation which wraps {@link RunnerLeaf}s
 * with a {@link JUnitWrapper} instance in order to report test execution 
 * to the JUnit framework.
 * @author Volker Bergmann
 */
public class JUnitWrapperFactory implements WrapperFactory {

    /** The {@link RunNotifier} provided by the JUnit framework.
     *  It is used to report test execution and results to JUnit. */
    private RunNotifier notifier;

    /** A JUnit-style test class which triggers JUnit execution by 
     *  a RunWith({@link AludraTestJUnitSuite}.class) annotation. */
    private Class<?> testClass;

    /** 
     * Constructor.
     * @param notifier the {@link RunNotifier} object for the JUnit framework.
     * @param testClass the JUnit-conform test class which triggered JUnit execution
     */
    public JUnitWrapperFactory(RunNotifier notifier, Class<?> testClass) {
        this.notifier = notifier;
        this.testClass = testClass;
    }

    /** Wraps the provided {@link RunnerLeaf} with a {@link JUnitWrapper} object. */
    @Override
    public RunnerLeaf wrap(RunnerLeaf leaf) {
        return new JUnitWrapper(leaf);
    }

    /**
     * Proxy class for {@link RunnerLeaf} objects which reports 
     * test execution and results to the JUnit framework.
     * @author Volker Bergmann
     */
    public class JUnitWrapper extends RunnerLeaf {

        /** Constructor which receives the {@link RunnerLeaf} object to be wrapped. 
         *  @param leaf */
        public JUnitWrapper(RunnerLeaf leaf) {
            super(leaf.getName(), leaf.getParent(), leaf, leaf.getLogCase());
        }

        /** Forwards invocations to the wrapped object's run() method 
         *  and sends start, failure and end information of the invocation to JUnit. */
        @Override
        public void run() {
            Description description = JUnitUtil.createDescription(this, testClass);
            if (!logCase.isIgnored()) {
                notifier.fireTestStarted(description);
            }
            try {
                super.run();
                verifyTestSteps();
            } catch (Throwable t) { //NOSONAR
                if (!logCase.isIgnored()) {
                    notifier.fireTestFailure(new Failure(description, t));
                }
            } finally {
                if (logCase.isIgnored()) {
                    notifier.fireTestIgnored(description);
                } else {
                    notifier.fireTestFinished(description);
                }
            }
        }

        /** Checks if an error was reported to log4testing and if it finds one, 
         *  it creates an exception to report the error to the JUnit framework. 
         */
        private void verifyTestSteps() {
            TestStepLog failedStep = getLogCase().getLastFailed();
            if (failedStep != null) {
                TestStatus status = failedStep.getStatus();
                String errorMessage = failedStep.getErrorMessage();
                Throwable error = failedStep.getError();
                // Treat an "expected" failure or performance failure as JUnit "Failure",
                // everything else as "Error"
                if (status == TestStatus.FAILED || status == TestStatus.FAILEDPERFORMANCE) {
                    // JUnit "Failure"
                    throw new AssertionError(errorMessage);
                } else {
                    // JUnit "Error"
                    throw new TechnicalException(errorMessage, error);
                }
            }
        }
    }
}