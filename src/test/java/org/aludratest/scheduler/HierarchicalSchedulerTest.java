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

import org.aludratest.scheduler.test.DefaultSuite;
import org.aludratest.scheduler.test.ParallelClass;
import org.aludratest.scheduler.test.ParallelSuite;
import org.aludratest.scheduler.test.ParallelSuiteWithSequentialClasses;
import org.aludratest.scheduler.test.ParallelSuiteWithUnconfiguredClasses;
import org.aludratest.scheduler.test.SequentialClass;
import org.aludratest.scheduler.test.SequentialSuite;
import org.aludratest.scheduler.test.SequentialSuiteWithParallelClasses;
import org.aludratest.scheduler.test.UnconfiguredSuiteWithParallelClasses;
import org.junit.Test;

/**
 * Tests various constellations of sequential, default and parallel 
 * suites and sub suites with different pool sizes.
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public class HierarchicalSchedulerTest extends AbstractSchedulerTest {

    @Test
    public void testSequentialClassWithPoolSize4() {
        // GIVEN a parallel class with 4 parallel test methods
        Class<?> suiteClass = SequentialClass.class;

        // WHEN executing the tests with a thread pool size of 4
        executeTests(suiteClass, 4);

        // THEN all must have been invoked sequentially
        assertInvocationCount(4);
        assertSequentialExecution("test1", "test2", "test3", "test4");
    }

    @Test
    public void testParallelClassWithPoolSize4() {
        // GIVEN a parallel class with 4 parallel test methods
        Class<?> suiteClass = ParallelClass.class;

        // WHEN executing the tests with a thread pool size of 4
        executeTests(suiteClass, 4);

        // THEN all must have been invoked concurrently
        assertInvocationCount(4);
        assertParallelExecution("test1", "test2", "test3", "test4");
    }

    @Test
    public void testParallelClassWithPoolSize2() {
        // GIVEN a parallel class with 4 parallel test methods
        Class<?> suiteClass = ParallelClass.class;

        // WHEN executing the tests with a thread pool size of 2
        executeTests(suiteClass, 2);

        // THEN the first two test leafs and the last two test leafs 
        // must have been executed concurrently with each other,...
        assertInvocationCount(4);
        assertParallelExecution("test1", "test2");
        assertParallelExecution("test3", "test4");

        // ...but only two at a time
        assertSequentialExecution("test1", "test3");
        assertSequentialExecution("test2", "test4");
    }

    @Test
    public void testParallelClassWithPoolSize1() {
        // GIVEN a parallel class with 4 parallel test methods
        Class<?> suiteClass = ParallelClass.class;

        // WHEN executing the tests with a thread pool size of 1
        executeTests(suiteClass, 1);

        // THEN all must have been invoked sequentially
        assertInvocationCount(4);
        assertSequentialExecution("test1", "test2", "test3", "test4");
    }

    /**
     * Tests a suite without concurrency configuration 
     * which consists of 2 classes without concurrency configuration 
     * with a thread pool size of 4, 
     * requiring that all tests are executed sequentially.
     */
    @Test
    public void testDefaultSuite() {
        // GIVEN a test suite with tests that use JUnit4's default runner (BlockJUnit4Runner)
        Class<?> suiteClass = DefaultSuite.class;

        // WHEN executing the tests
        executeTests(suiteClass, 1);

        // THEN all must have been invoked sequentially
        assertInvocationCount(4);
        assertSequentialExecution("test1a", "test1b", "test2a", "test2b");
    }

    /**
     * Tests a sequential suite which consists of 2 sequential classes 
     * of which each one has 2 methods with a thread pool size of 4, 
     * requiring that all tests are executed sequentially.
     */
    @Test
    public void testSequentialSuite() {
        // GIVEN a test suite with tests that use JUnit4's default runner (BlockJUnit4Runner)
        Class<?> suiteClass = SequentialSuite.class;

        // WHEN executing the tests
        executeTests(suiteClass, 1);

        // THEN all must have been invoked sequentially
        assertInvocationCount(4);
        assertSequentialExecution("test1a", "test1b", "test2a", "test2b");
    }

    /**
     * Tests a parallel suite which consists of 2 parallel classes 
     * of which each one has 2 methods with a thread pool size of 4, 
     * requiring that all tests are executed concurrently.
     */
    @Test
    public void testParallelSuite() {
        // GIVEN a test suite with tests that use JUnit4's default runner (BlockJUnit4Runner)
        Class<?> suiteClass = ParallelSuite.class;

        // WHEN executing the tests
        executeTests(suiteClass, 4);

        // THEN all must have been invoked sequentially
        assertInvocationCount(4);
        assertParallelExecution("test1a", "test1b", "test2a", "test2b");
    }

    /**
     * Tests a sequential suite which consists of 2 parallel classes 
     * of which each one has 2 methods with a thread pool size of 4, 
     * requiring that classes are executed sequentially, 
     * but the methods of each class concurrently.
     */
    @Test
    public void testSerialSuiteWithParallelClasses() {
        // GIVEN a serial test suite with parallel sub suites
        Class<?> suiteClass = SequentialSuiteWithParallelClasses.class;

        // WHEN executing the tests
        executeTests(suiteClass, 4);

        // THEN the sub suites must have been executed sequentially, 
        // the elements of each sub suite concurrently
        assertInvocationCount(4);
        assertSequentialExecution("test1a", "test2a");
        assertSequentialExecution("test1a", "test2b");
        assertSequentialExecution("test1b", "test2a");
        assertSequentialExecution("test1b", "test2b");
        assertParallelExecution("test1a", "test1b");
        assertParallelExecution("test2a", "test2b");
    }

    /**
     * Tests an unconfigured suite which consists of 2 parallel classes 
     * of which each one has 2 methods. 
     * The test is performed with a thread pool size of 4, 
     * requiring that all methods are executed concurrently.
     */
    @Test
    public void testUnconfiguredSuiteWithParallelClasses() {
        // GIVEN an unconfigured test suite with parallel sub suites
        Class<?> suiteClass = UnconfiguredSuiteWithParallelClasses.class;

        // WHEN executing the tests
        executeTests(suiteClass, 4);

        // THEN the sub suites must have been executed concurrently
        assertParallelExecution("test1a", "test1b", "test2a", "test2b");
    }

    @Test
    public void testParallelSuiteWithSequentialClasses() {
        // GIVEN a parallel test suite with serial sub suites
        Class<?> suiteClass = ParallelSuiteWithSequentialClasses.class;

        // WHEN executing the tests
        executeTests(suiteClass, 4);

        // THEN the sub suites must have been executed concurrently, 
        // the elements of each sub suite sequentially
        assertInvocationCount(4);

        // The parent suite is parallel but not the child suites, 
        // Since the parent's parallelism overwrites children's settings, anything 
        // is executed concurrently
        assertSequentialExecution("test1a", "test1b");
        assertSequentialExecution("test2a", "test2b");
        assertParallelExecution("test1a", "test2a");
        assertParallelExecution("test1b", "test2b");
    }

    @Test
    public void testParallelSuiteWithUnconfiguredClasses() {
        // GIVEN a parallel test suite with serial sub suites
        Class<?> suiteClass = ParallelSuiteWithUnconfiguredClasses.class;

        // WHEN executing the tests
        executeTests(suiteClass, 4);

        // THEN the sub suites must have been executed concurrently, 
        // the elements of each sub suite sequentially
        assertInvocationCount(4);

        // The parent suite is parallel and sets the default for 
        // the unconfigured child suites, so anything 
        // shall be executed concurrently
        assertParallelExecution("test1a", "test1b", "test2a", "test2b");
    }

}
