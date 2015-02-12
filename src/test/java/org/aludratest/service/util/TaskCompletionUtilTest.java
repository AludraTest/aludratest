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
package org.aludratest.service.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.aludratest.exception.PerformanceFailure;
import org.junit.Test;

/**
 * Tests the {@link TaskCompletionUtil}.
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public class TaskCompletionUtilTest {

    // testing waitUntilNotBusy ------------------------------------------------

    @Test(expected = PerformanceFailure.class)
    public void testWaitUntilNotBusy_failure() {
        TestSystemConnector connector = new TestSystemConnector("system") {
            @Override
            public boolean isBusyImpl() {
                return true;
            }
        };
        TaskCompletionUtil.waitUntilNotBusy(connector, 1000, 600, "busy");
        assertEquals(2, connector.busyCallCount);
    }

    @Test
    public void testWaitUntilNotBusy_ImmediateSuccess() {
        TestSystemConnector connector = new TestSystemConnector("system") {
            @Override
            public boolean isBusyImpl() {
                return false;
            }
        };
        TaskCompletionUtil.waitUntilNotBusy(connector, 1000, 600, "busy");
        assertEquals(1, connector.busyCallCount);
    }

    @Test
    public void testWaitUntilNotBusy_DelayedSuccess() {
        final long limit = System.currentTimeMillis() + 2000;
        TestSystemConnector connector = new TestSystemConnector("system") {
            @Override
            public boolean isBusyImpl() {
                return (System.currentTimeMillis() < limit);
            }
        };
        TaskCompletionUtil.waitUntilNotBusy(connector, 3000, 600, "busy");
        assertEquals(5, connector.busyCallCount);
    }

    // testing waitForActivityAndCompletion ------------------------------------

    @Test
    public void testWaitForActivityAndCompletion_CompletionBeforePolling() {
        TestSystemConnector connector = new TestSystemConnector("system") {
            @Override
            public boolean isBusyImpl() {
                return false;
            }
        };
        boolean result = TaskCompletionUtil.waitForActivityAndCompletion(connector, "some_action", 600, 2000, 500);
        assertFalse(result);
        assertEquals(2, connector.busyCallCount);
    }

    @Test(expected = PerformanceFailure.class)
    public void testWaitForActivityAndCompletion_DirectStartWithTimeout() {
        TestSystemConnector connector = new TestSystemConnector("system") {
            @Override
            public boolean isBusyImpl() {
                return true;
            }
        };
        TaskCompletionUtil.waitForActivityAndCompletion(connector, "some_action", 2000, 5000, 500);
        assertEquals(3, connector.busyCallCount);
    }

    @Test
    public void testWaitForActivityAndCompletion_DirectStartWithFinish() {
        final long limit = System.currentTimeMillis() + 2000;
        TestSystemConnector connector = new TestSystemConnector("system") {
            @Override
            public boolean isBusyImpl() {
                return System.currentTimeMillis() < limit;
            }
        };
        TaskCompletionUtil.waitForActivityAndCompletion(connector, "some_action", 0, 5000, 500);
        assertEquals(6, connector.busyCallCount);
    }

    @Test
    public void testWaitForActivityAndCompletion_DelayedStartWithFinish() {
        final long start = System.currentTimeMillis() + 1500;
        final long limit = System.currentTimeMillis() + 4000;
        TestSystemConnector connector = new TestSystemConnector("system") {
            @Override
            public boolean isBusyImpl() {
                long time = System.currentTimeMillis();
                return (start < time && time < limit);
            }
        };
        boolean result = TaskCompletionUtil.waitForActivityAndCompletion(connector, "some_action", 2000, 5000, 600);
        assertTrue(result);
        assertEquals(9, connector.busyCallCount);
    }

    @Test(expected = PerformanceFailure.class)
    public void testWaitForActivityAndCompletion_DelayedStartWithTimeout() {
        final long start = System.currentTimeMillis() + 1000;
        TestSystemConnector connector = new TestSystemConnector("system") {
            @Override
            public boolean isBusyImpl() {
                return (start < System.currentTimeMillis());
            }
        };
        TaskCompletionUtil.waitForActivityAndCompletion(connector, "some_action", 2000, 4000, 600);
    }

}
