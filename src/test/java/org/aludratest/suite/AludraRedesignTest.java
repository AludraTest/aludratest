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

import org.aludratest.AludraTest;
import org.aludratest.AludraTestTest;
import org.aludratest.testcase.AludraTestCase;
import org.junit.Assert;

/**
 * Tests the redesign of AludraTest.
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public class AludraRedesignTest {

    @org.junit.Test
    public void test() {
        // prepare Aludra
        System.setProperty("ALUDRATEST_CONFIG/aludratest/number.of.threads", "1");
        AludraTestTest.setInstance(null);
        AludraTest aludra = new AludraTest();

        // run the test case
        aludra.run(MyAludraTest.class);
        Assert.assertTrue(MyAludraTest.executed);
    }

    public static class MyAludraTest extends AludraTestCase {

        private static boolean executed;

        @org.aludratest.testcase.Test
        public void test() {
            // System.out.println("Performing MyAludraTest.test()");
            executed = true;
        }

    }

}
