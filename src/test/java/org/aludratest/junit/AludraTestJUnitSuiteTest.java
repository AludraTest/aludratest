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
package org.aludratest.junit;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.aludratest.suite.PlainTestClass;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * Tests the {@link AludraTestJUnitSuite}.
 * @author Volker Bergmann
 */
public class AludraTestJUnitSuiteTest {

    /** Tests the successful execution of a plain AludraTest test class triggered by a JUnit suite. */
    @Test
    public void testSuccessfulClass() {
        System.setProperty(AludraTestJUnitSuite.SUITE_SYSPROP, PlainTestClass.class.getName());
        Result result = JUnitCore.runClasses(TriggerSuite.class);
        assertEquals(3, result.getRunCount());
        assertEquals(0, result.getFailureCount());
    }

    /** Tests execution and failure reporting of a plain AludraTest test class triggered by a JUnit suite. */
    @Test
    public void testFailingClass() {
        System.setProperty(AludraTestJUnitSuite.SUITE_SYSPROP, FailingTestClass.class.getName());
        Result result = JUnitCore.runClasses(TriggerSuite.class);
        assertEquals(1, result.getRunCount());
        assertEquals(1, result.getFailureCount());
        List<Failure> failures = result.getFailures();
        Failure failure = failures.get(0);
        assertEquals("This is a failure!", failure.getMessage());
    }

}
