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

import org.aludratest.testcase.AludraTestContext;
import org.aludratest.testcase.impl.AludraTestContextImpl;
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
        AludraTestContext context = new AludraTestContextImpl(null, null);
        // ...THEN it is not supposed to be stopped
        assertFalse(controller.isStopped(context));
        // WHEN asking the controller to stop the test case...
        controller.stopTestCaseExecution(context);
        // ...THEN it is supposed to register it as stopped.
        assertTrue(controller.isStopped(context));
        // WHEN resetting the controller...
        controller.reset();
        // ...THEN the test case shall be executable again
        assertFalse(controller.isStopped(context));
    }

}
