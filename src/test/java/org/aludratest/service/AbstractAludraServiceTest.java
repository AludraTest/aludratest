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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.aludratest.AludraTest;
import org.aludratest.testcase.AludraTestContext;
import org.aludratest.testcase.TestStatus;
import org.aludratest.testcase.event.InternalTestListener;
import org.aludratest.testcase.event.TestStepInfo;
import org.aludratest.testcase.impl.AludraTestContextImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

/** Parent class for test cases which test or use {@link AludraService} implementations. Create instances of services using
 * {@link #getLoggingService(Class, String)} and {@link #getService(Class, String)}. You can access the logged test step groups
 * and test steps using the methods of this base class.
 * 
 * @author Volker Bergmann
 * @author falbrech */
@SuppressWarnings("javadoc")
public abstract class AbstractAludraServiceTest {

    protected AludraTestContext context;

    private String currentTestStepGroup;

    private List<TestStepInfo> testSteps = new ArrayList<TestStepInfo>();

    private TestStepInfo lastFailedTestStep;

    private TestStatus status;

    private AludraTest framework;

    @Before
    public void prepareTestCase() {
        status = TestStatus.PASSED;

        // startup framework for IoC container, but do not reveal it to child classes
        this.framework = AludraTest.startFramework();

        InternalTestListener listener = new InternalTestListener() {
            @Override
            public void newTestStepGroup(String name) {
                currentTestStepGroup = name;
                testSteps.clear();
                lastFailedTestStep = null;
            }

            @Override
            public void newTestStep(TestStepInfo testStep) {
                testSteps.add(testStep);
                status = testStep.getTestStatus();
                if (status.isFailure()) {
                    lastFailedTestStep = testStep;
                }
            }
        };

        context = new AludraTestContextImpl(listener, framework.getServiceManager());
    }

    @After
    public void closeTestCase() {
        framework.stopFramework();
    }

    @SuppressWarnings("unchecked")
    public <T extends AludraService, U extends T> U getLoggingService(Class<T> interfaceClass, String moduleName) {
        framework.getServiceManager().createAndConfigureService(ComponentId.create(interfaceClass, moduleName), context, true);
        U service = (U) this.context.getService(ComponentId.create(interfaceClass, moduleName));
        return service;
    }

    public <T extends AludraService> T getService(Class<T> interfaceClass, String moduleName) {
        return this.context.<T> getNonLoggingService(ComponentId.create(interfaceClass, moduleName));
    }

    /** Returns the last logged status. Note that this is AludraTest's internal TestStatus type, <b>not</b> the Log4Testing type!
     * 
     * @return The last logged status, or <code>PASSED</code> if no logging has yet occurred. */
    protected final TestStatus getLoggedStatus() {
        return status;
    }

    protected final String getCurrentTestStepGroupName() {
        return currentTestStepGroup;
    }

    protected final List<TestStepInfo> getTestSteps() {
        return Collections.unmodifiableList(testSteps);
    }

    protected final TestStepInfo getLastTestStep() {
        return testSteps.isEmpty() ? null : testSteps.get(testSteps.size() - 1);
    }

    protected final TestStepInfo getLastFailedTestStep() {
        return lastFailedTestStep;
    }

    protected final void assertNotFailed() {
        Assert.assertFalse("Unexpected failure", getLoggedStatus().isFailure());
    }

}
