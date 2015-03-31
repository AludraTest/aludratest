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
package org.aludratest.util.poll;

import static org.junit.Assert.assertEquals;

import org.aludratest.exception.PerformanceFailure;
import org.junit.Test;

/** Tests the {@link PollService}.
 * @author Volker Bergmann */
@SuppressWarnings("javadoc")
public class PollServiceTest {

    @Test
    public void testImmediateSuccess() {
        String result = new PollService(1000, 100).poll(new TestTask("SUCCESS", 0, 0));
        assertEquals("SUCCESS", result);
    }

    @Test
    public void testDelayedSuccess() {
        String result = new PollService(2000, 100).poll(new TestTask("SUCCESS", 0, 1000));
        assertEquals("SUCCESS", result);
    }

    @Test(expected = PerformanceFailure.class)
    public void testPollTimeoutWithException() {
        new PollService(2000, 100).poll(new TestTask(new PerformanceFailure("too slow"), 0, 3000));
    }

    @Test
    public void testPollTimeoutWithFallback() {
        String result = new PollService(2000, 100).poll(new TestTask("SUCCESS", 0, 3000));
        assertEquals("FAILURE", result);
    }

    @Test(expected = PerformanceFailure.class)
    public void testTaskInvocationTimeoutWithException() {
        new PollService(2000, 100).poll(new TestTask(new PerformanceFailure("too slow"), 3000, 0));
    }

    @Test
    public void testTaskInvocationTimeoutWithFallback() {
        String result = new PollService(2000, 100).poll(new TestTask("SUCCESS", 3000, 0));
        assertEquals("FAILURE", result);
    }

    @Test(expected = RuntimeException.class)
    public void testInternalException() {
        new PollService(2000, 100).poll(new TestTask(new RuntimeException(), 0, 0));
    }

    class TestTask implements PolledTask<String> {

        private final String result;
        private final long invocationDuration;
        private final long timeToComplete;
        private final RuntimeException exception;

        private long startTime;

        public TestTask(String result, long invocationDuration, long timeToComplete) {
            this(result, invocationDuration, timeToComplete, null);
        }

        public TestTask(RuntimeException result, long invocationDuration, long timeToComplete) {
            this(null, invocationDuration, timeToComplete, result);
        }

        protected TestTask(String result, long invocationDuration, long timeToComplete, RuntimeException exception) {
            this.result = result;
            this.invocationDuration = invocationDuration;
            this.timeToComplete = timeToComplete;
            this.exception = exception;
            this.startTime = 0;
        }

        @Override
        public String run() {
            if (this.startTime == 0) {
                this.startTime = System.currentTimeMillis();
            }
            try {
                Thread.sleep(invocationDuration);
            }
            catch (InterruptedException e) {
                // Nothing to do
            }
            long elapsedTime = System.currentTimeMillis() - startTime;
            if (elapsedTime >= timeToComplete) {
                if (result != null) {
                    return result;
                }
                else {
                    throw exception;
                }
            }
            else {
                return null;
            }
        }

        @Override
        public String timedOut() {
            if (exception != null) {
                throw exception;
            }
            else {
                return "FAILURE";
            }
        }

    }

}
