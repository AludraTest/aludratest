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

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;

import org.aludratest.impl.log4testing.ElementName;
import org.aludratest.impl.log4testing.ElementType;
import org.aludratest.impl.log4testing.TechnicalLocator;
import org.aludratest.impl.log4testing.data.TestCaseLog;
import org.aludratest.impl.log4testing.data.TestLogger;
import org.aludratest.impl.log4testing.data.TestStepLog;
import org.aludratest.impl.log4testing.handler.LoggingInvocationHandler;
import org.aludratest.service.ComponentId;
import org.junit.Test;

/**
 * Tests the {@link LoggingInvocationHandler}.
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public class LoggingInvocationHandlerTest {

    @Test
    public void test() throws Throwable {
        TestCaseLog testCase = TestLogger.getTestCase(getClass().getName() + "_1");
        testCase.newTestStepGroup("group1");
        Dummy dummy = new Dummy();
        ComponentId<?> serviceId = ComponentId.create(Dummy.class, "dMod");
        LoggingInvocationHandler handler = new LoggingInvocationHandler(dummy, serviceId, testCase);
        Class<?>[] argTypes = new Class[] { String.class, String.class, String.class, int.class };
        Method method = Dummy.class.getMethod("process", argTypes);
        handler.invoke(dummy, method, new Object[] { "theName", "theType", "theLoc", 1 });
        assertEquals(1, dummy.value);
        TestStepLog lastTestStep = testCase.getLastTestStep();
        assertEquals("process", lastTestStep.getCommand());
        assertEquals("theType", lastTestStep.getElementType());
        assertEquals("theName", lastTestStep.getElementName());
        assertEquals("theLoc", lastTestStep.getTechnicalLocator());
        assertEquals("1", lastTestStep.getUsedArguments());
        assertEquals("", lastTestStep.getTechnicalArguments());
        System.out.println(lastTestStep);
    }

    static class Dummy {
        public int value = 0;

        public void process(
                @ElementName String name, 
                @ElementType String type, 
                @TechnicalLocator String loc, 
                int value) {
            this.value = value;
        }
    }

}
