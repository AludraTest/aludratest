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
package org.aludratest.util.timeout;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

/** Tests the {@link TimeoutService}.
 * @author Volker Bergmann */
@SuppressWarnings("javadoc")
public class TimeoutServiceTest {

    @Test
    public void testPerformant() throws Exception {
        String result = TimeoutService.call(new Target(0), 4000);
        assertEquals("DONE", result);
    }

    @Test(expected = TimeoutException.class)
    public void testTooSlow() throws Exception {
        TimeoutService.call(new Target(4000), 1000);
    }

    @Test(expected = IOException.class)
    public void testException() throws Exception {
        Callable<String> target = new Callable<String>() {
            @Override
            public String call() throws Exception {
                throw new IOException();
            }
        };
        String result = TimeoutService.call(target, 4000);
        assertEquals("DONE", result);
    }

    // helpers -----------------------------------------------------------------

    public class Target implements Callable<String> {
        private long timeout;

        public Target(long timeout) {
            this.timeout = timeout;
        }

        @Override
        public String call() throws Exception {
            Thread.sleep(timeout);
            return "DONE";
        }

    }

}
