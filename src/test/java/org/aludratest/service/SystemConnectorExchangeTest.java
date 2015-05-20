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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.aludratest.config.impl.AludraTestingTestConfigImpl;
import org.aludratest.impl.log4testing.data.TestCaseLog;
import org.aludratest.impl.log4testing.data.TestLogger;
import org.aludratest.impl.log4testing.data.TestStepGroup;
import org.aludratest.service.pseudo.PseudoInteraction;
import org.aludratest.service.pseudo.PseudoService;
import org.aludratest.service.util.AbstractSystemConnector;
import org.aludratest.testcase.AludraTestCase;
import org.aludratest.testcase.TestStatus;
import org.aludratest.testcase.event.ErrorReport;
import org.aludratest.testcase.event.SystemErrorReporter;
import org.aludratest.testcase.event.attachment.StringAttachment;

/** Verifies that SystemConnectors can be exchanged dynamically on a service.
 * @author Volker Bergmann */
public class SystemConnectorExchangeTest extends AbstractAludraServiceTest {

    private static ComponentId<PseudoService> SERVICE_ID = ComponentId.create(PseudoService.class, "localhost");

    private static final String LF = "\n";

    /** The connector instance to be applied first */
    private static final PlainConnector connector1 = new PlainConnector();

    /** Another connector instance to replace the {@link #connector1} later */
    private static final PlainConnector connector2 = new PlainConnector();

    private static TestCaseLog testCase = null;

    /** Test case which uses the AludraTest framework to execute {@link PlainTest} and verifies execution results. */
    @org.junit.Test
    public void testExchangingErrorConnectors() {
        // storing previous settings
        AludraTestingTestConfigImpl cfg = AludraTestingTestConfigImpl.getTestInstance();
        boolean stopInterExc = cfg.isStopTestCaseOnOtherException();
        boolean stopOtherExc = cfg.isStopTestCaseOnInteractionException();
        try {

            // GIVEN a configuration that does not stop on errors
            cfg.setStopTestCaseOnInteractionException(false);
            cfg.setStopTestCaseOnOtherException(false);
            System.setProperty("ALUDRATEST_CONFIG/aludratest/number.of.threads", "1");

            // WHEN running the PlainTest
            aludra.run(PlainTest.class);

            // THEN the following reporting characteristics are expected
            assertTrue("ErrorConnector #1 has not been called", connector1.invoked);
            assertFalse("ErrorConnector #2 has been called despite previous error", connector2.invoked);
            assertEquals(TestStatus.FAILED, testCase.getStatus());
            TestStepGroup group1 = testCase.getTestStepGroups().get(0);
            assertEquals("succeed", group1.getTestStep(0).getCommand());
            String message2 = group1.getTestStep(1).getErrorMessage();
            assertTrue(message2.startsWith("Nothing found"));
            String message3 = group1.getTestStep(2).getErrorMessage();
            assertTrue(message3.startsWith("The system refuses to coooperate "));
        }
        finally {
            // restore original settings
            cfg.setStopTestCaseOnInteractionException(stopInterExc);
            cfg.setStopTestCaseOnOtherException(stopOtherExc);
        }
    }

    /** An {@link AludraTestCase} test which calls the succeed() and fail() methods on the {@link PseudoInteraction}. */
    public static class PlainTest extends AludraTestCase {
        @SuppressWarnings("javadoc")
        @org.aludratest.testcase.Test
        public void test() {
            testCase = TestLogger.getTestCase(getClass().getName() + ".test-0");
            PseudoService service = getService(SERVICE_ID);
            // succeed and fail with connector1
            service.setSystemConnector(connector1);
            service.perform().succeed("pseudoService", "operation1", "locator1");
            service.perform().fail("pseudoService", "operation2", "locator2");
            // fail with connector2
            service.setSystemConnector(connector2);
            service.perform().fail("pseudoService", "operation2", "locator2");
        }
    }

    /** {@link SystemConnector} implementation which reports a fix {@link ErrorReport}. */
    static class PlainConnector extends AbstractSystemConnector implements SystemErrorReporter {

        public PlainConnector() {
            super("system");
        }

        boolean invoked = false;

        /** Reports a single fix {@link ErrorReport}. */
        @Override
        public ErrorReport checkForError() {
            this.invoked = true;
            // testCase.newTestStep(TestStatus.PASSED, "system connector action", "...");
            String errorMessage = "The system refuses to coooperate for personal reasons";
            String stackTrace = "com.example.ExampleError: I'll provide this info to anybody but you" + LF
                    + "at com.example.Blabla.fail(ThePseudoInteraction.java:22)" + LF
                    + "at com.example.Blabla.invoke0(Native Method)" + LF
                    + "at com.example.NativeMethodAccessorImpl.invoke(Unknown Source)" + LF
                    + "at com.example.DelegatingMethodAccessorImpl.invoke(Unknown Source)";
            return new ErrorReport("123", errorMessage, Collections.singleton(new StringAttachment("Server Stack Trace",
                    stackTrace, "txt")));
        }

    }

}
