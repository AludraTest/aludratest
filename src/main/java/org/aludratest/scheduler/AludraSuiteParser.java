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

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.aludratest.AludraTest;
import org.aludratest.dict.Data;
import org.aludratest.exception.AutomationException;
import org.aludratest.impl.log4testing.data.TestCaseLog;
import org.aludratest.invoker.AludraTestMethodInvoker;
import org.aludratest.invoker.ErrorReportingInvoker;
import org.aludratest.invoker.TestInvoker;
import org.aludratest.scheduler.node.ExecutionMode;
import org.aludratest.scheduler.node.RunnerGroup;
import org.aludratest.scheduler.node.RunnerNode;
import org.aludratest.testcase.AludraTestCase;
import org.aludratest.testcase.AludraTestContext;
import org.aludratest.testcase.Parallel;
import org.aludratest.testcase.Sequential;
import org.aludratest.testcase.Suite;
import org.aludratest.testcase.Test;
import org.aludratest.testcase.data.TestCaseData;
import org.databene.commons.Assert;
import org.databene.commons.BeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parses AludraTest suites.
 * @author Volker Bergmann
 */
public class AludraSuiteParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(AludraSuiteParser.class);

    private AludraTest aludraTest;

    private final AtomicLong errorCount;

    // parse methods ---------------------------------------------------------------------------------------------------

    /** Constructor.
     * 
     * @param aludraTest the core {@link AludraTest} instance for passing to the {@link AludraTestContext}. */
    public AludraSuiteParser(AludraTest aludraTest) {
        Assert.notNull(aludraTest, "aludraTest");
        this.aludraTest = aludraTest;
        this.errorCount = new AtomicLong();
    }

    /** Parses the resources behind a resource name into a {@link RunnerTree}
     * @param resourceName
     * @return a RunnerTree with the parsed test structure */
    public RunnerTree parse(String resourceName) {
        return parse(resourceName, null);
    }

    /** Parses the resources behind a resource name into a {@link RunnerTree}
     * @param resourceName
     * @param comparator
     * @return a RunnerTree with the parsed test structure */
    public RunnerTree parse(String resourceName, Comparator<RunnerNode> comparator) {
        RunnerTree tree = new RunnerTree(comparator);
        resourceName = resourceName.trim();
        /*
        if (resourceName.endsWith(".xml")) {
        	String relativePath = extractFileName(resourceName, "resources");
        	parseXmlSuite(relativePath, null, tree);
        } else {
         */
        String className;
        if (resourceName.endsWith(".java")) {
            className = deriveFileName(resourceName, "java");
            className = className.substring(0, className.length() - ".java".length());
        } else {
            className = resourceName;
        }
        Class<?> testClass = BeanUtil.forName(className);
        parseTestOrSuiteClass(testClass, null, tree);
        //}
        return tree;
    }

    // suite file parsing ----------------------------------------------------------------------------------------------

    /* The feature to define a suite in an XML document has been disabled to cut effort and costs
    private static void parseXmlSuite(String resourceName, RunnerGroup parentGroup, RunnerTree tree) {
    	try {
    		// parse XML file
    		Document doc = XMLUtil.parse(resourceName);

    		// parse root element
    		Element root = doc.getDocumentElement();
    		assertSuiteElement(root);
    		ExecutionMode mode = parseMode(root);
    		RunnerGroup group = tree.createGroup(resourceName, mode, parentGroup);

    		// parse children
    		NodeList children = root.getChildNodes();
    		for (int i = 0; i < children.getLength(); i++) {
    			Node child = children.item(i);
    			if (child instanceof Element) {
    				parseXmlSuiteChild((Element) child, group, tree);
    			}
    		}
    	} catch (IOException e) {
    		throw new ConfigurationException("Error parsing " + resourceName, e);
    	}
    }

    private static ExecutionMode parseMode(Element root) {
    	String modeConfig = XMLUtil.getAttribute(root, "parallel", false);
    	return (modeConfig != null ? ExecutionMode.valueOf(modeConfig.toUpperCase()) : ExecutionMode.INHERITED);
    }

    private static void parseXmlSuiteChild(Element element, RunnerGroup parentGroup, RunnerTree tree) {
    	assertSuiteElement(element);
    	// parse 'url' config
    	String url = element.getAttribute("url");
    	if (url.startsWith("class://")) {
    		String className = url.substring("class://".length());
    		Class<?> testClass = BeanUtil.forName(className);
    		parseTestOrSuiteClass(testClass, parentGroup, tree);
    	} else if (url.startsWith("suite://")) {
    		String suiteName = url.substring("suite://".length());
    		parseXmlSuite(suiteName, parentGroup, tree);
    	} else
    		throw new ConfigurationException("not a supported URL format: " + url);
    }

    private static void assertSuiteElement(Element element) {
    	String elementName = element.getNodeName();
    	if (!"suite".equals(elementName)) {
    		throw new ConfigurationError("Unexpected element in suite: " + elementName);
    	}
    }
     */

    // class parsing ---------------------------------------------------------------------------------------------------

    /** Derives a file name from a resource name and source directory. */
    private static String deriveFileName(String resourceName, String sourceDir) {
        String dirMarker = "." + sourceDir + ".";
        String baseName = resourceName.replace(File.separatorChar, '.');
        int i = baseName.indexOf(".src.");
        if (i < 0) {
            throw new UnsupportedOperationException("Cannot handle this as class name: " + resourceName);
        }
        i = baseName.indexOf(dirMarker, i + ".src.".length());
        if (i < 0) {
            throw new UnsupportedOperationException("Cannot handle this as class name: " + resourceName);
        }
        i += dirMarker.length();
        return baseName.substring(i);
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

    /** Parses an AludraTest test suite class. */
    private void parseSuiteClass(Class<?> testClass, RunnerGroup parentGroup, RunnerTree tree) {
        LOGGER.debug("Parsing suite class: {}", testClass.getName());
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
        RunnerGroup classGroup = createRunnerGroupForTestClass(testClass, parentGroup, tree);
        for (Method method : testClass.getMethods()) {
            parseMethod(method, testClass, classGroup, tree);
        }
    }

    /** Creates a {@link RunnerGroup} for an AludraTest test class */
    private static RunnerGroup createRunnerGroupForTestClass(Class<?> testClass, RunnerGroup parentGroup, RunnerTree tree) {
        ExecutionMode mode;
        if (testClass.getAnnotation(Parallel.class) != null) {
            mode = ExecutionMode.PARALLEL;
        } else if (testClass.getAnnotation(Sequential.class) != null) {
            mode = ExecutionMode.SEQUENTIAL;
        } else {
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
            } else if (method.getAnnotation(Sequential.class) != null) {
                mode = ExecutionMode.SEQUENTIAL;
            } else {
                mode = ExecutionMode.INHERITED;
            }
            String methodTestSuiteName = createMethodTestSuiteName(testClass, method);
            RunnerGroup methodGroup = tree.createGroup(methodTestSuiteName, mode, classGroup);
            try {
                // iterate through method invocations
                List<TestCaseData> invocationParams = aludraTest.getTestDataSets(method);
                for (TestCaseData data : invocationParams) {
                    if (data.getException() == null) {
                        createTestRunnerForMethodInvocation(method, data.getData(), data.getId(), data.isIgnored(), methodGroup,
                                tree);
                    } else {
                        createTestRunnerForErrorReporting(method, data.getException(), methodGroup, tree);
                    }
                }
            } catch (Exception e) {
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

    /**
     * Creates a test runner for error reporting.
     * @param e The exception that occurred.
     */
    private void createTestRunnerForErrorReporting(Method method, Throwable e, RunnerGroup methodGroup, RunnerTree tree) {
        LOGGER.error("createTestRunnerForErrorReporting('{}', {}, {}, ...)", new Object[] { method, e, methodGroup });
        // create log4testing TestCase name
        String invocationTestCaseName = createMethodTestSuiteName(method.getDeclaringClass(), method) + "_error_" + errorCount.incrementAndGet();
        // Create test object
        TestInvoker invoker = new ErrorReportingInvoker(method, e);
        createRunnerForTestInvoker(invoker, methodGroup, tree, invocationTestCaseName, false);
    }

    private void createRunnerForTestInvoker(TestInvoker invoker, RunnerGroup parentGroup, RunnerTree tree, String testCaseName, boolean ignore) {
        AludraTestRunner runner = new AludraTestRunner(invoker);
        TestCaseLog testCase = tree.addLeaf(runner, testCaseName, parentGroup);
        if (ignore) {
            testCase.ignore();
        }
        runner.setContext(new AludraTestContext(testCase, aludraTest));
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
