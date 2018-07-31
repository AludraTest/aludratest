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
package org.aludratest.suite;

import org.aludratest.scheduler.Log;
import org.aludratest.testcase.AludraTestCase;
import org.aludratest.testcase.SequentialGroup;
import org.aludratest.testcase.Test;

public class SequentialGroupClass extends AludraTestCase {

    @SequentialGroup(groupName = "test", index = 0)
    @Test
    public void someMethod() throws InterruptedException {
        Log.log("someMethod");
        Thread.sleep(100);
    }

    @SequentialGroup(groupName = "test", index = 2)
    @Test
    public void otherMethod() throws InterruptedException {
        Log.log("otherMethod");
        Thread.sleep(100);
    }

    @SequentialGroup(groupName = "test", index = 1)
    @Test
    public void anotherMethod() throws InterruptedException {
        Log.log("anotherMethod");
        Thread.sleep(100);
    }

    @SequentialGroup(groupName = "test", index = 4)
    @Test
    public void lastMethod() throws InterruptedException {
        Log.log("lastMethod");
        Thread.sleep(100);
    }

}
