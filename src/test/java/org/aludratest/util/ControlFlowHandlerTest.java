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

import java.util.Collections;
import java.util.List;

import org.aludratest.config.AludraTestConfig;
import org.aludratest.config.impl.AludraTestingTestConfigImpl;
import org.aludratest.service.AbstractAludraService;
import org.aludratest.service.AbstractAludraServiceTest;
import org.aludratest.service.AludraService;
import org.aludratest.service.ComponentId;
import org.aludratest.service.Condition;
import org.aludratest.service.Interaction;
import org.aludratest.service.SystemConnector;
import org.aludratest.service.Verification;
import org.aludratest.service.util.AbstractSystemConnector;
import org.aludratest.testcase.TestStatus;
import org.aludratest.testcase.event.attachment.Attachment;
import org.aludratest.testcase.event.impl.AludraTestUtil;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class ControlFlowHandlerTest extends AbstractAludraServiceTest {

    @Override
    public void prepareTestCase() {
        // register custom configuration implementation
        System.setProperty("ALUDRATEST_CONFIG/aludraservice/" + AludraTestConfig.class.getName(),
                AludraTestingTestConfigImpl.class.getName());

        super.prepareTestCase();

        FlowController.getInstance().reset();
    }

    @Test
    public void testSuccess() throws Exception {
        // GIVEN an object with ControlFlow proxy
        MyTestInterface proxy = setup(true);

        // WHEN calling a proxy method which executes normally
        Object result = proxy.copy("Test");

        // THEN the proper value should have been returned and test case is not supposed to be stopped
        assertEquals("Test", result);
        assertFalse(FlowController.getInstance().isStopped(context));
    }

    @Test
    public void testExceptionWithoutStop() throws Exception {
        // GIVEN an object with ControlFlow proxy configured to continue execution in case of an exception
        MyTestInterface proxy = setup(false);

        // WHEN calling a proxy method which throws an exception
        proxy.error();

        // THEN the test case is not supposed to be stopped
        assertFalse(FlowController.getInstance().isStopped(context));
    }

    @Test
    public void testExceptionWithStop() throws Exception {
        // GIVEN an object with ControlFlow proxy configured to stop execution on exceptions
        MyTestInterface proxy = setup(true);

        // WHEN calling a proxy method which throws an exception
        proxy.error();

        // THEN the test case is supposed to be stopped...
        assertTrue(FlowController.getInstance().isStopped(context));

        // ...and further test steps shall be ignored
        proxy.copy("xyz");
        // force test context to be closed (to write logs)
        context.closeServices();
        assertEquals(TestStatus.IGNORED, getLastTestStep().getTestStatus());
    }

    // helper methods ----------------------------------------------------------

    private MyTestInterface setup(boolean stopOnException) {
        // override configuration property
        AludraTestingTestConfigImpl.getTestInstance().setStopTestCaseOnOtherException(stopOnException);

        MyTestInterface realImpl = new MyTestInterfaceImpl();
        SystemConnector connector = new AbstractSystemConnector("system") {
        };
        ComponentId<MyTestInterface> serviceId = ComponentId.create(MyTestInterface.class, "mod");
        return AludraTestUtil.wrapWithControlFlowHandler(realImpl, MyTestInterface.class, serviceId, connector, context);
    }

    // helper classes ----------------------------------------------------------

    public static interface MyTestInterface extends AludraService {
        public Object copy(Object object);

        public void error() throws Exception;
    }

    public static class MyTestInterfaceImpl extends AbstractAludraService implements MyTestInterface, Interaction, Verification,
    Condition {
        @Override
        public Object copy(Object object) {
            return object;
        }

        @Override
        public void error() throws Exception {
            throw new RuntimeException();
        }

        @Override
        public String getDescription() {
            return "My test interface";
        }

        @Override
        public Interaction perform() {
            return this;
        }

        @Override
        public Verification verify() {
            return this;
        }

        @Override
        public Condition check() {
            return this;
        }

        @Override
        public void close() {
        }

        @Override
        public void initService() {
        }

        @Override
        public List<Attachment> createDebugAttachments() {
            return Collections.emptyList();
        }

        @Override
        public List<Attachment> createAttachments(Object object, String title) {
            return createDebugAttachments();
        }
    }

}
