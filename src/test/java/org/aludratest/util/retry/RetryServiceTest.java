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
package org.aludratest.util.retry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.concurrent.Callable;

import org.junit.Test;

/** Tests the {@link RetryService}.
 * @author Volker Bergmann */
@SuppressWarnings("javadoc")
public class RetryServiceTest {

    @Test
    public void testImmediateSuccess() throws Throwable {
        Target target = new Target();
        String result = RetryService.call(target, null, 0);
        assertEquals("DONE", result);
        assertEquals(1, target.invocationCount);
    }

    @Test
    public void testRetryWithSuccess() throws Throwable {
        Target target = new Target(new IOException(), new IOException());
        String result = RetryService.call(target, IOException.class, 2);
        assertEquals("DONE", result);
        assertEquals(3, target.invocationCount);
    }

    @Test(expected = IOException.class)
    public void testRetryWithoutSuccess() throws Throwable {
        Target target = new Target(new IOException(), new IOException(), new IOException());
        String result = RetryService.call(target, IOException.class, 2);
        assertEquals("DONE", result);
        assertEquals(3, target.invocationCount);
    }

    @Test
    public void testOtherException() {
        Target target = new Target(new IllegalArgumentException());
        try {
            RetryService.call(target, IOException.class, 1);
            // The previous call should have caused an IllegalArgumentException that leads to the catch clause.
            // If we arrive here, that did not happen.
            fail("The expected IllegalArgumentException did not occur");
        }
        catch (IllegalArgumentException e) {
            // this is the expected outcome
            assertEquals(1, target.invocationCount);
        }
        catch (Throwable e) {
            // oops another exception occurred
            fail("Encountered an unexpected exception type: " + e.getClass());
        }
    }

    public class Target implements Callable<String> {

        Exception[] exceptionsToThrow;
        int invocationCount;

        public Target(Exception... exceptionsToThrow) {
            this.exceptionsToThrow = exceptionsToThrow;
            this.invocationCount = 0;
        }

        @Override
        public String call() throws Exception {
            invocationCount++;
            if (invocationCount - 1 < exceptionsToThrow.length) {
                Exception exception = exceptionsToThrow[invocationCount - 1];
                exception.fillInStackTrace();
                throw exception;
            }
            return "DONE";
        }

    }

}
