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

import java.util.concurrent.atomic.AtomicInteger;

import org.aludratest.testcase.AludraTestCase;
import org.aludratest.testcase.Sequential;
import org.aludratest.testcase.Test;

@Sequential
public class SequentialTest extends AludraTestCase {

    static boolean test1;

    static boolean test2;

    static boolean parallel;

    static AtomicInteger running = new AtomicInteger();

    @Test
    public void test1() {
        test1 = true;
        if (running.incrementAndGet() > 1) {
            parallel = true;
        }
        try {
            Thread.sleep(1000);
        }
        catch (InterruptedException e) {
        }
        running.decrementAndGet();
    }

    @Test
    public void test2() {
        test2 = true;
        if (running.incrementAndGet() > 1) {
            parallel = true;
        }
        try {
            Thread.sleep(1000);
        }
        catch (InterruptedException e) {
        }
        running.decrementAndGet();
    }


}
