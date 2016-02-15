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
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.aludratest.log4testing.TestCaseLog;
import org.aludratest.log4testing.TestStatus;
import org.aludratest.log4testing.TestStepLog;
import org.aludratest.service.SystemConnectorExchangeTest.PlainTest;
import org.aludratest.service.pseudo.PseudoService;
import org.aludratest.service.util.AbstractSystemConnector;
import org.aludratest.testcase.AludraTestCase;
import org.aludratest.testcase.event.ErrorReport;
import org.aludratest.testcase.event.SystemErrorReporter;

/** Tests the behavior of AludraTest when an exception occurs in a call to {@link SystemConnector#checkForErrors()}.
 * @author Volker Bergmann */
public class SystemConnectorExceptionTest extends AbstractAludraIntegrationTest {

    private static ComponentId<PseudoService> SERVICE_ID = ComponentId.create(PseudoService.class, "localhost");

    /** Test case which uses the AludraTest framework to execute {@link PlainTest} and verifies execution results. */
    @org.junit.Test
    public void testRecurringErrorConnector() {

        // GIVEN a setting with a single threaded execution
        System.setProperty("ALUDRATEST_CONFIG/aludratest/number.of.threads", "1");

        // WHEN performing the RecurringTest
        aludra.run(RecurringTest.class);

        // THEN require the following report characteristics
        TestCaseLog testCase = getTestLog().getLastTestCaseLog();
        TestStepLog lastTestStep = testCase.getLastFailedStep();
        assertEquals(TestStatus.FAILEDAUTOMATION, testCase.getStatus());
        assertTrue("ErrorConnector recursion not recognized",
                lastTestStep.getErrorMessage().contains("Cancelled execution to avoid infinite recursion"));
        Iterator<? extends TestStepLog> stepIterator = testCase.getTestStepGroups().iterator().next().getTestSteps().iterator();
        String rootMessage = "Nothing found";
        assertEquals(rootMessage, stepIterator.next().getErrorMessage());
        assertTrue("ErrorConnector recursion not recognized",
                stepIterator.next().getErrorMessage().contains("Cancelled execution to avoid infinite recursion"));
    }

    @SuppressWarnings("javadoc")
    public static class RecurringTest extends AludraTestCase {
        @org.aludratest.testcase.Test
        public void test() {
            PseudoService service = getService(SERVICE_ID);
            // succeed and fail with connector1
            RecurringConnector connector = new RecurringConnector(service);
            service.setSystemConnector(connector);
            service.perform().fail("pseudoService", "operation2", "locator2");
        }
    }

    /** {@link SystemConnector} implementation which reports a fix {@link ErrorReport}. */
    static class RecurringConnector extends AbstractSystemConnector implements SystemErrorReporter {

        PseudoService service;
        boolean invoked = false;

        public RecurringConnector(PseudoService service) {
            super("system");
            this.service = service;
        }

        /** Reports a single fix {@link ErrorReport}. */
        @Override
        public ErrorReport checkForError() {
            this.invoked = true;
            // cause an exception
            service.perform().fail("pseudoService", "operation2", "locator2");
            return null;
        }
    }

}
