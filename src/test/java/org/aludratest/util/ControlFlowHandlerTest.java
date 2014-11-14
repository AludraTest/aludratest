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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.aludratest.config.impl.AludraTestingTestConfigImpl;
import org.aludratest.impl.log4testing.data.TestCaseLog;
import org.aludratest.impl.log4testing.data.TestLogger;
import org.aludratest.service.AbstractAludraServiceTest;
import org.aludratest.service.ComponentId;
import org.aludratest.service.ErrorReport;
import org.aludratest.service.SystemConnector;
import org.aludratest.service.util.AbstractSystemConnector;
import org.aludratest.testcase.TestStatus;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the {@link ControlFlowHandler}.
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public class ControlFlowHandlerTest extends AbstractAludraServiceTest {

    @Before
    public void resetFlowController() {
        FlowController.getInstance().reset();
    }

    @Test
    public void testSuccess() throws Exception {
        // GIVEN an object with ControlFlow proxy
        TestCaseLog testCase = TestLogger.getTestCase("TestCase1");
        MyTestInterface proxy = setup(testCase, true);

        // WHEN calling a proxy method which executes normally
        Object result = proxy.copy("Test");

        // THEN the proper value should have been returned and test case is not supposed to be stopped
        assertEquals("Test", result);
        assertFalse(FlowController.getInstance().isStopped(testCase));
    }

    @Test
    public void testExceptionWithoutStop() throws Exception {
        // GIVEN an object with ControlFlow proxy configured to continue execution in case of an exception
        TestCaseLog testCase = TestLogger.getTestCase("TestCase2");
        MyTestInterface proxy = setup(testCase, false);

        // WHEN calling a proxy method which throws an exception
        proxy.error();

        // THEN the test case is not supposed to be stopped
        assertFalse(FlowController.getInstance().isStopped(testCase));
    }

    @Test
    public void testExceptionWithStop() throws Exception {
        // GIVEN an object with ControlFlow proxy configured to stop execution on exceptions
        TestCaseLog testCase = TestLogger.getTestCase("TestCase3");
        MyTestInterface proxy = setup(testCase, true);

        // WHEN calling a proxy method which throws an exception
        proxy.error();

        // THEN the test case is supposed to be stopped...
        assertTrue(FlowController.getInstance().isStopped(testCase));

        // ...and further test steps shall be ignored
        proxy.copy("xyz");
        assertEquals(TestStatus.IGNORED, testCase.getLastTestStep().getStatus());
    }

    // helper methods ----------------------------------------------------------

    private MyTestInterface setup(TestCaseLog testCase, boolean stopOnException) {
        testCase.newTestStepGroup("group1");

        // override configuration property
        AludraTestingTestConfigImpl.getTestInstance().setStopTestCaseOnOtherException(stopOnException);

        MyTestInterface realImpl = new MyTestInterfaceImpl();
        SystemConnector connector = new AbstractSystemConnector("system") {
            @Override
            public List<ErrorReport> checkForErrors() {
                return null;
            }
        };
        ComponentId<MyTestInterface> serviceId = ComponentId.create(MyTestInterface.class, "mod");
        return AludraTestUtil
                .wrapWithControlFlowHandler(realImpl, MyTestInterface.class, serviceId, connector, testCase, context);
    }

    // helper classes ----------------------------------------------------------

    public static interface MyTestInterface {
        public Object copy(Object object);

        public void error() throws Exception;
    }

    public static class MyTestInterfaceImpl implements MyTestInterface {
        @Override
        public Object copy(Object object) {
            return object;
        }

        @Override
        public void error() throws Exception {
            throw new RuntimeException();
        }
    }

}
