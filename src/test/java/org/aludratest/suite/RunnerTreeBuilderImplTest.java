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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import junit.framework.AssertionFailedError;

import org.aludratest.PreconditionFailedException;
import org.aludratest.impl.log4testing.data.TestLogger;
import org.aludratest.scheduler.RunnerTree;
import org.aludratest.scheduler.RunnerTreeBuilder;
import org.aludratest.scheduler.impl.RunnerTreeBuilderImpl;
import org.aludratest.scheduler.node.RunnerGroup;
import org.aludratest.scheduler.node.RunnerNode;
import org.aludratest.service.AbstractAludraServiceTest;
import org.aludratest.testcase.AludraTestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the {@link AludraSuiteParser}.
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public class RunnerTreeBuilderImplTest extends AbstractAludraServiceTest {

    @Before
    public void setUp() {
        TestLogger.clear();
    }

    private RunnerTree parseTestClass(Class<?> classToTest) {
        RunnerTreeBuilder builder = aludra.getServiceManager().newImplementorInstance(RunnerTreeBuilder.class);
        assertTrue(builder instanceof RunnerTreeBuilderImpl);
        return builder.buildRunnerTree(classToTest);
    }

    @Test
    public void testAbstractTestClass() {
        try {
            Class<?> testClass = AbstractTestClass.class;
            parseTestClass(testClass);
            throw new AssertionFailedError("Exception expected");
        }
        catch (PreconditionFailedException e) {
            String expectedMessage = "Abstract class not suitable as test class: "
                    + "org.aludratest.suite.RunnerTreeBuilderImplTest$AbstractTestClass";
            assertEquals(expectedMessage, e.getMessage());
        }
    }

    public static abstract class AbstractTestClass extends AludraTestCase {
        @org.aludratest.testcase.Test
        public void test() {
            // just an empty sample
        }
    }

    @Test
    public void testTestClassWithoutParent() {
        try {
            Class<?> testClass = TestClassWithoutParent.class;
            parseTestClass(testClass);
            throw new AssertionFailedError("Exception expected");
        }
        catch (PreconditionFailedException e) {
            String expectedMessage = "Test class does not inherit from org.aludratest.testcase.AludraTestCase: org.aludratest.suite.RunnerTreeBuilderImplTest$TestClassWithoutParent";
            assertEquals(expectedMessage, e.getMessage());
        }
    }

    public static class TestClassWithoutParent {
        @org.aludratest.testcase.Test
        public void test() {
            // just an empty sample
        }
    }

    @Test
    public void testEmptyTestClass() {
        try {
            Class<?> testClass = EmptyTestClass.class;
            parseTestClass(testClass);
            throw new AssertionFailedError("Exception expected");
        }
        catch (PreconditionFailedException e) {
            String expectedMessage = "No @Test methods found in class: "
                    + "org.aludratest.suite.RunnerTreeBuilderImplTest$EmptyTestClass";
            assertEquals(expectedMessage, e.getMessage());
        }
    }

    public static class EmptyTestClass extends AludraTestCase {
        // empty test class for unit test
    }

    /** Tests parsing of a plain AludraTest test class with a {@literal @}Parallel annotation and three methods:
     * <ul>
     * <li>the first one 'plainTest()' without annotation</li>
     * <li>the second one 'sequentialTest()' with a {@literal @}Sequential annotation</li>
     * <li>the third one 'parallelTest()' with a {@literal @}Parallel annotation</li>
     * </ul> */
    @Test
    public void testParallelTestClass() {
        Class<?> testClass = ParallelTestClass.class;
        RunnerTree tree = parseTestClass(testClass);
        checkSuite(testClass, true, tree.getRoot());
    }

    /** Tests parsing of a plain AludraTest test class with a {@literal @}Sequential annotation and three methods:
     * <ul>
     * <li>the first one 'plainTest()' without annotation</li>
     * <li>the second one 'sequentialTest()' with a {@literal @}Sequential annotation</li>
     * <li>the third one 'parallelTest()' with a {@literal @}Parallel annotation</li>
     * </ul> */
    @Test
    public void testSequentialTestClass() {
        Class<?> testClass = SequentialTestClass.class;
        RunnerTree tree = parseTestClass(testClass);
        checkSuite(testClass, false, tree.getRoot());
    }

    /** Tests parsing of a plain AludraTest test class without concurrency annotation and three methods:
     * <ul>
     * <li>the first one 'plainTest()' without annotation</li>
     * <li>the second one 'sequentialTest()' with a {@literal @}Sequential annotation</li>
     * <li>the third one 'parallelTest()' with a {@literal @}Parallel annotation</li>
     * </ul> */
    @Test
    public void testPlainTestClass() {
        Class<?> testClass = PlainTestClass.class;
        RunnerTree tree = parseTestClass(testClass);
        checkSuite(testClass, true, tree.getRoot());
    }

    /** Tests parsing of an AludraTest test suite class without concurrency annotation and three suite component classes:
     * <ul>
     * <li>the first one 'PlainTestClass' with the behavior described in {@link #testPlainTestClass()}</li>
     * <li>the second one 'SequentialTestClass' with the behavior described in {@link #testSequentialTestClass()}</li>
     * <li>the third one 'ParallelTestClass' with the behavior described in {@link #testParallelTestClass()}</li>
     * </ul> */
    @Test
    public void testPlainTestSuite() {
        Class<?> testClass = PlainTestSuite.class;
        RunnerTree tree = parseTestClass(testClass);
        List<RunnerNode> children = tree.getRoot().getChildren();
        assertEquals(3, children.size());
        checkSuite(PlainTestClass.class, true, (RunnerGroup) children.get(0));
        checkSuite(SequentialTestClass.class, false, (RunnerGroup) children.get(1));
        checkSuite(ParallelTestClass.class, true, (RunnerGroup) children.get(2));
    }

    /** Tests parsing of an AludraTest test suite class with a {@literal @}Sequential annotation and three suite component classes:
     * <ul>
     * <li>the first one 'PlainTestClass' with the behavior described in {@link #testPlainTestClass()}</li>
     * <li>the second one 'SequentialTestClass' with the behavior described in {@link #testSequentialTestClass()}</li>
     * <li>the third one 'ParallelTestClass' with the behavior described in {@link #testParallelTestClass()}</li>
     * </ul> */
    @Test
    public void testSequentialTestSuite() {
        Class<?> testClass = SequentialTestSuite.class;
        RunnerTree tree = parseTestClass(testClass);
        List<RunnerNode> children = tree.getRoot().getChildren();
        assertEquals(3, children.size());
        checkSuite(PlainTestClass.class, false, (RunnerGroup) children.get(0));
        checkSuite(SequentialTestClass.class, false, (RunnerGroup) children.get(1));
        checkSuite(ParallelTestClass.class, true, (RunnerGroup) children.get(2));
    }

    /** Tests parsing of an AludraTest test suite class with a {@literal @}Parallel annotation and three suite component classes:
     * <ul>
     * <li>the first one 'PlainTestClass' with the behavior described in {@link #testPlainTestClass()}</li>
     * <li>the second one 'SequentialTestClass' with the behavior described in {@link #testSequentialTestClass()}</li>
     * <li>the third one 'ParallelTestClass' with the behavior described in {@link #testParallelTestClass()}</li>
     * </ul> */
    @Test
    public void testParallelTestSuite() {
        Class<?> testClass = ParallelTestSuite.class;
        RunnerTree tree = parseTestClass(testClass);
        List<RunnerNode> children = tree.getRoot().getChildren();
        assertEquals(3, children.size());
        checkSuite(PlainTestClass.class, true, (RunnerGroup) children.get(0));
        checkSuite(SequentialTestClass.class, false, (RunnerGroup) children.get(1));
        checkSuite(ParallelTestClass.class, true, (RunnerGroup) children.get(2));
    }

    @Test(expected = PreconditionFailedException.class)
    public void testDuplicateChildSuite() {
        Class<?> testClass = DuplicateChildSuite.class;
        parseTestClass(testClass);
    }

    /** Verifies the properties of an AludraTest suite class. */
    private void checkSuite(Class<?> testClass, boolean defaultParallel, RunnerGroup group) {
        List<RunnerNode> children = group.getChildren();
        assertEquals(3, children.size());
        checkTestMethodGroup(testClass, "plainTest", defaultParallel, children, 0);
        checkTestMethodGroup(testClass, "sequentialTest", false, children, 1);
        checkTestMethodGroup(testClass, "parallelTest", true, children, 2);
    }

    /** Verifies the properties of an AludraTest test class. */
    private void checkTestMethodGroup(Class<?> testClass, String methodName, boolean parallel, List<RunnerNode> nodes, int index) {
        RunnerNode childNode = nodes.get(index);
        assertEquals(testClass.getName() + "." + methodName, childNode.getName());
        assertTrue(childNode instanceof RunnerGroup);
        RunnerGroup childGroup = (RunnerGroup) childNode;
        assertEquals(parallel, childGroup.isParallel());
    }

}
