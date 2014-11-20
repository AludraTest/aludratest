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
package org.aludratest.scheduler.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.aludratest.PreconditionFailedException;
import org.aludratest.dict.Data;
import org.aludratest.exception.AutomationException;
import org.aludratest.invoker.AludraTestMethodInvoker;
import org.aludratest.invoker.ErrorReportingInvoker;
import org.aludratest.invoker.TestInvoker;
import org.aludratest.scheduler.RunnerTree;
import org.aludratest.scheduler.RunnerTreeBuilder;
import org.aludratest.scheduler.node.ExecutionMode;
import org.aludratest.scheduler.node.RunnerGroup;
import org.aludratest.scheduler.node.RunnerLeaf;
import org.aludratest.scheduler.util.CommonRunnerLeafAttributes;
import org.aludratest.testcase.AludraTestCase;
import org.aludratest.testcase.Parallel;
import org.aludratest.testcase.Sequential;
import org.aludratest.testcase.Suite;
import org.aludratest.testcase.Test;
import org.aludratest.testcase.data.TestCaseData;
import org.aludratest.testcase.data.TestDataProvider;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.databene.commons.BeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(role = RunnerTreeBuilder.class, instantiationStrategy = "per-lookup")
public class RunnerTreeBuilderImpl implements RunnerTreeBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(RunnerTreeBuilder.class);

    private final AtomicLong errorCount = new AtomicLong();

    private final AtomicInteger nextLeafId = new AtomicInteger();

    /** Used to trace added files and detect recursions & multi-uses of classes */
    private Set<Class<?>> addedClasses = new HashSet<Class<?>>();

    @Requirement
    private TestDataProvider testDataProvider;

    @Override
    public RunnerTree buildRunnerTree(Class<?> suiteOrTestClass) {
        RunnerTree tree = new RunnerTree();
        parseTestOrSuiteClass(suiteOrTestClass, null, tree);
        return tree;
    }

    /** Parses an AludraTest test class or suite. */
    private void parseTestOrSuiteClass(Class<?> testClass, RunnerGroup parentGroup, RunnerTree tree) {
        // check test class type
        if (isTestSuiteClass(testClass)) {
            parseSuiteClass(testClass, parentGroup, tree);
        }
        else {
            assertTestClass(testClass);
            parseTestClass(testClass, parentGroup, tree);
        }
    }

    private boolean isTestSuiteClass(Class<?> testClass) {
        return (testClass.getAnnotation(Suite.class) != null);
    }

    private void assertTestClass(Class<?> testClass) {
        if ((testClass.getModifiers() & Modifier.ABSTRACT) == Modifier.ABSTRACT) {
            throw new AutomationException("Abstract class not suitable as test class: " + testClass.getName());
        }
        else if (!AludraTestCase.class.isAssignableFrom(testClass)) {
            throw new AutomationException("Test class " + testClass.getName() + " does not inherit from "
                    + AludraTestCase.class.getName());
        }
        else if (testMethodCount(testClass) == 0) {
            throw new AutomationException("No @Test methods found in class " + testClass.getName());
        }
    }

    private int testMethodCount(Class<?> testClass) {
        int count = 0;
        for (Method method : testClass.getMethods()) {
            if (method.getAnnotation(Test.class) != null) {
                count++;
            }
        }
        return count;
    }

    private void checkAddTestClass(Class<?> clazz) {
        if (addedClasses.contains(clazz)) {
            throw new PreconditionFailedException("The class " + clazz
                    + " is used in more than one test suite, or part of a test suite recursion.");
        }
        addedClasses.add(clazz);
    }

    /** Parses an AludraTest test suite class. */
    private void parseSuiteClass(Class<?> testClass, RunnerGroup parentGroup, RunnerTree tree) {
        LOGGER.debug("Parsing suite class: {}", testClass.getName());
        checkAddTestClass(testClass);
        addedClasses.add(testClass);
        Suite suite = testClass.getAnnotation(Suite.class);
        if (suite == null) {
            throw new IllegalArgumentException("Class has no @Suite annotation");
        }
        RunnerGroup group = createRunnerGroupForTestClass(testClass, parentGroup, tree);
        for (Class<?> component : suite.value()) {
            parseTestOrSuiteClass(component, group, tree);
        }
    }

    /** Parses an AludraTest test class. */
    private void parseTestClass(Class<?> testClass, RunnerGroup parentGroup, RunnerTree tree) {
        LOGGER.debug("Parsing test class: {}", testClass.getName());
        checkAddTestClass(testClass);
        RunnerGroup classGroup = createRunnerGroupForTestClass(testClass, parentGroup, tree);
        for (Method method : testClass.getMethods()) {
            parseMethod(method, testClass, classGroup, tree);
        }
    }

    /** Creates a {@link RunnerGroup} for an AludraTest test class */
    private RunnerGroup createRunnerGroupForTestClass(Class<?> testClass, RunnerGroup parentGroup, RunnerTree tree) {
        ExecutionMode mode;
        if (testClass.getAnnotation(Parallel.class) != null) {
            mode = ExecutionMode.PARALLEL;
        }
        else if (testClass.getAnnotation(Sequential.class) != null) {
            mode = ExecutionMode.SEQUENTIAL;
        }
        else {
            mode = ExecutionMode.INHERITED;
        }
        RunnerGroup group = tree.createGroup(testClass.getName(), mode, parentGroup);
        return group;
    }

    /** Parses a method */
    private void parseMethod(Method method, Class<?> testClass, RunnerGroup classGroup, RunnerTree tree) {
        if (method.getAnnotation(Test.class) != null) {
            LOGGER.debug("Parsing test class method: {}", method);
            ExecutionMode mode;
            if (method.getAnnotation(Parallel.class) != null) {
                mode = ExecutionMode.PARALLEL;
            }
            else if (method.getAnnotation(Sequential.class) != null) {
                mode = ExecutionMode.SEQUENTIAL;
            }
            else {
                mode = ExecutionMode.INHERITED;
            }
            String methodTestSuiteName = createMethodTestSuiteName(testClass, method);
            RunnerGroup methodGroup = tree.createGroup(methodTestSuiteName, mode, classGroup);
            try {
                // iterate through method invocations
                List<TestCaseData> invocationParams = testDataProvider.getTestDataSets(method);
                for (TestCaseData data : invocationParams) {
                    if (data.getException() == null) {
                        createTestRunnerForMethodInvocation(method, data.getData(), data.getId(), data.isIgnored(), methodGroup,
                                tree);
                    }
                    else {
                        createTestRunnerForErrorReporting(method, data.getException(), methodGroup, tree);
                    }
                }
            }
            catch (Exception e) {
                createTestRunnerForErrorReporting(method, e, methodGroup, tree);
            }
        }
    }

    /** Creates a test runner for a single method invocation */
    private void createTestRunnerForMethodInvocation(Method method, Data[] args, String testInfo, boolean ignore,
            RunnerGroup methodGroup, RunnerTree tree) {
        // create log4testing TestCase
        String invocationTestCaseName = createInvocationTestCaseName(testInfo, methodGroup.getName());
        // Create test object
        @SuppressWarnings("unchecked")
        AludraTestCase testObject = BeanUtil.newInstance((Class<? extends AludraTestCase>) method.getDeclaringClass());
        TestInvoker invoker = new AludraTestMethodInvoker(testObject, method, args);
        createRunnerForTestInvoker(invoker, methodGroup, tree, invocationTestCaseName, ignore);
    }

    /** Creates a test runner for error reporting.
     * @param e The exception that occurred. */
    private void createTestRunnerForErrorReporting(Method method, Throwable e, RunnerGroup methodGroup, RunnerTree tree) {
        LOGGER.error("createTestRunnerForErrorReporting('{}', {}, {}, ...)", new Object[] { method, e, methodGroup });
        // create log4testing TestCase name
        String invocationTestCaseName = createMethodTestSuiteName(method.getDeclaringClass(), method) + "_error_"
                + errorCount.incrementAndGet();
        // Create test object
        TestInvoker invoker = new ErrorReportingInvoker(method, e);
        createRunnerForTestInvoker(invoker, methodGroup, tree, invocationTestCaseName, false);
    }

    private void createRunnerForTestInvoker(TestInvoker invoker, RunnerGroup parentGroup, RunnerTree tree, String testCaseName, boolean ignore) {
        RunnerLeaf leaf = tree.addLeaf(nextLeafId.incrementAndGet(), invoker, testCaseName, parentGroup);
        if (ignore) {
            leaf.setAttribute(CommonRunnerLeafAttributes.IGNORE, Boolean.valueOf(ignore));
        }
    }

    /** Creates a test case name for a test method. */
    private static String createMethodTestSuiteName(Class<?> testClass, Method method) {
        return testClass.getName() + '.' + method.getName();
    }

    /** Creates a test case name for a test method invocation. */
    private static String createInvocationTestCaseName(String testInfo, String methodTestSuiteName) {
        return methodTestSuiteName + '-' + testInfo;
    }

}
