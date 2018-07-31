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
package org.aludratest;

import static org.junit.Assert.assertEquals;

import org.aludratest.impl.AludraTestConstants;
import org.aludratest.testcase.AludraTestCase;

/** Tests the class {@link AludraTest}.
 * @author Volker Bergmann */
@SuppressWarnings("javadoc")
public class AludraTestTest {

    @org.junit.Test
    public void testMain_success() throws Exception {
        int exitCode = AludraTest.runWithCmdLineArgs(new String[] { SuccessfulTestCase.class.getName() });
        assertEquals(AludraTestConstants.EXIT_NORMAL, exitCode);
    }

    @org.junit.Test
    public void testMain_exception() throws Exception {
        int exitCode = AludraTest.runWithCmdLineArgs(new String[] { ExceptionTestCase.class.getName() });
        assertEquals(AludraTestConstants.EXIT_EXECUTION_FAILURE, exitCode);
    }

    public static class SuccessfulTestCase extends AludraTestCase {
        @org.aludratest.testcase.Test
        public void testMethod() {
            System.out.println("Successful test");
        }
    }

    public static class ExceptionTestCase extends AludraTestCase {
        @org.aludratest.testcase.Test
        public void testMethod() {
            System.out.println("Exception test");
            throw new RuntimeException("Exception test");
        }
    }

}
