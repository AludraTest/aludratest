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
package org.aludratest.scheduler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.aludratest.AludraTest;
import org.aludratest.LocalTestCase;
import org.aludratest.impl.log4testing.data.TestLogger;
import org.junit.Before;

/**
 * Abstract parent class for tests which verify scheduling behavior.
 * @author Volker Bergmann
 */
public abstract class AbstractSchedulerTest extends LocalTestCase {

    /** Resets the {@link TestLogger}, the {@link Log} start time and removes all entries
     *  before each test. */
    @Before
    public void resetLogs() {
        TestLogger.clear();
        Log.reset();
    }

    /** Parses the given test class and
     *  executes its tests with the given pool size. */
    protected void executeTests(Class<?> suiteClass, int poolSize) {
        System.setProperty("ALUDRATEST_CONFIG/aludratest/number.of.threads", "" + poolSize);
        AludraTest aludraTest = AludraTest.startFramework();

        RunnerTreeBuilder builder = aludraTest.getServiceManager().newImplementorInstance(RunnerTreeBuilder.class);
        RunnerTree tree = builder.buildRunnerTree(suiteClass);

        AludraTestRunner runner = aludraTest.getServiceManager().newImplementorInstance(AludraTestRunner.class);
        runner.runAludraTests(tree);
        aludraTest.stopFramework();
    }

    /** Asserts the given test invocation count.
     *  This assumes that each test invocation
     *  causes exactly one {@link Log} entry */
    protected void assertInvocationCount(int count) {
        List<Log.Entry> entries = Log.getEntries();
        System.out.println(entries);
        assertEquals(count, entries.size());
    }

    /** Asserts sequential execution of the given tests.
     *  As a precondition, each test must exhibit an
     *  execution time of 500 milliseconds.
     *  The assertion then asserts that the time between
     *  each test invocation is at least 470 milliseconds. */
    protected void assertSequentialExecution(String... actions) {
        Log.Entry[] invocations = logEntries(actions);
        long latestInvocation = invocations[0].millis;
        for (int i = 1; i < invocations.length; i++) {
            Log.Entry invocation = invocations[i];
            long offset = invocation.millis - latestInvocation;
            String assertionMessage;
            if (offset >= 0) {
                assertionMessage = "invocation " + invocation.action + " is assumed to be sequential to " + invocations[i - 1].action + ", but occured only " + offset + " ms later";
            } else {
                assertionMessage = "invocation " + invocation.action + " is assumed to happen after " + invocations[i - 1].action + ", but occured " + Math.abs(offset) + " ms earlier";
            }
            assertTrue(assertionMessage, offset > 470);
            latestInvocation = invocation.millis;
        }
    }

    /** Asserts parallel execution of the given tests.
     *  As a precondition, each test must exhibit an
     *  execution time of 500 milliseconds.
     *  The assertion then asserts that the time between
     *  each test invocation is at most 300 milliseconds. */
    protected void assertParallelExecution(String... actions) {
        Log.Entry[] invocations = logEntries(actions);
        long latestInvocation = invocations[0].millis;
        for (int i = 1; i < invocations.length; i++) {
            Log.Entry invocation = invocations[i];
            long offset = invocation.millis - latestInvocation;
            String assertionMessage = "invocation " + invocation.action + " is assumed to be parallel to " + invocations[i - 1].action + ", but occured " + offset + " ms later";
            assertTrue(assertionMessage, offset < 300);
            latestInvocation = invocation.millis;
        }
    }

    /** Retrieves the {@link Log} entries for all given actions. */
    protected Log.Entry[] logEntries(String... actions) {
        Log.Entry[] invocations = new Log.Entry[actions.length];
        for (int i = 0; i < actions.length; i++)
            invocations[i] = Log.getEntry(actions[i]);
        return invocations;
    }

}
