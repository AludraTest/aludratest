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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.aludratest.impl.log4testing.data.TestCaseLog;
import org.aludratest.impl.log4testing.data.TestLogger;
import org.aludratest.util.FlowController;
import org.junit.Test;

/**
 * Tests the {@link FlowController}
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public class FlowControllerTest {

    @Test
    public void test() {
        // GIVEN the blank flow controller
        FlowController controller = FlowController.getInstance();
        controller.reset();
        // WHEN a test case is created newly...
        TestCaseLog testCase = TestLogger.getTestCase(getClass());
        // ...THEN it is not supposed to be stopped
        assertFalse(controller.isStopped(testCase));
        // WHEN asking the controller to stop the test case...
        controller.stopTestCaseExecution(testCase);
        // ...THEN it is supposed to register it as stopped.
        assertTrue(controller.isStopped(testCase));
        // WHEN resetting the controller...
        controller.reset();
        // ...THEN the test case shall be executable again 
        assertFalse(controller.isStopped(testCase));
    }

}
