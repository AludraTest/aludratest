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
package org.aludratest.testcase.event;

import org.aludratest.testcase.AludraTestCase;
import org.aludratest.testcase.Test;

public class ParallelTest1 extends AludraTestCase {

    static boolean sequentialRunning;

    private void waitForSequentialRunning() {
        if (sequentialRunning) {
            return;
        }

        for (int i = 0; i < 5; i++) {
            if (SequentialTest.running.get() > 0) {
                sequentialRunning = true;
                return;
            }

            try {
                Thread.sleep(100);
            }
            catch (InterruptedException e) {
            }
        }
    }

    @Test
    public void testSomething() {
        waitForSequentialRunning();
    }

    @Test
    public void testSomething2() {
        waitForSequentialRunning();
    }

    @Test
    public void testSomething3() {
        waitForSequentialRunning();
    }

}
