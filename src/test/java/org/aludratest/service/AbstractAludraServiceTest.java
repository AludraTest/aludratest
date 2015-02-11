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

import org.aludratest.AludraTest;
import org.aludratest.config.AludraTestConfig;
import org.aludratest.config.impl.AludraTestingTestConfigImpl;
import org.aludratest.impl.log4testing.data.TestCaseLog;
import org.aludratest.impl.log4testing.data.TestLogger;
import org.aludratest.impl.log4testing.data.TestSuiteLog;
import org.aludratest.service.util.DirectLogTestListener;
import org.aludratest.testcase.AludraTestContext;
import org.aludratest.testcase.impl.AludraTestContextImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

/**
 * Parent class for test cases which test or use {@link AludraService} implementations.
 * 
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public abstract class AbstractAludraServiceTest {

    private static int count = 0;

    protected AludraTest aludra;
    protected TestSuiteLog testSuite;
    protected TestCaseLog testCase;
    protected AludraTestContext context;

    @Before
    public void prepareTestCase() {
        System.setProperty("ALUDRATEST_CONFIG/aludraservice/" + AludraTestConfig.class.getName(),
                AludraTestingTestConfigImpl.class.getName());

        this.aludra = AludraTest.startFramework();
        String baseName = getClass().getName() + "-" + (++count);
        this.testSuite = TestLogger.getTestSuite(baseName + "-suite");
        this.testCase = TestLogger.getTestCase(baseName + "-test");
        this.testSuite.add(testCase);

        this.context = new AludraTestContextImpl(new DirectLogTestListener(testCase), aludra.getServiceManager());

        // inject our own configuration object to be able to override configs for test, and initialize it
        Assert.assertTrue(context.newComponentInstance(AludraTestConfig.class) instanceof AludraTestingTestConfigImpl);
    }

    @After
    public void closeTestCase() {
        try {
            testCase.finish();
        }
        catch (Throwable t) {
            // ignore here (could be a test case without using testCase)
        }
        aludra.stopFramework();
    }

    @SuppressWarnings("unchecked")
    public <T extends AludraService, U extends T> U getLoggingService(Class<T> interfaceClass, String moduleName) {
        U service = (U) this.context.getService(ComponentId.create(interfaceClass, moduleName));
        this.testCase.newTestStepGroup("group1");
        return service;
    }

    public <T extends AludraService> T getService(Class<T> interfaceClass, String moduleName) {
        return this.context.<T> getNonLoggingService(ComponentId.create(interfaceClass, moduleName));
    }

    protected void assertNotFailed() {
        Assert.assertFalse("Unexpected failure", testCase.isFailed());
    }

}
