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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.aludratest.PreconditionFailedException;
import org.aludratest.config.AludraTestConfig;
import org.aludratest.dict.Data;
import org.aludratest.invoker.AludraTestMethodInvoker;
import org.aludratest.invoker.ErrorReportingInvoker;
import org.aludratest.invoker.TestInvoker;
import org.aludratest.scheduler.AnnotationBasedExecution;
import org.aludratest.scheduler.RunnerTree;
import org.aludratest.scheduler.RunnerTreeBuilder;
import org.aludratest.scheduler.TestClassFilter;
import org.aludratest.scheduler.node.ExecutionMode;
import org.aludratest.scheduler.node.RunnerGroup;
import org.aludratest.scheduler.node.RunnerLeaf;
import org.aludratest.scheduler.node.RunnerNode;
import org.aludratest.scheduler.sort.Alphabetic;
import org.aludratest.scheduler.sort.RunnerTreeSortUtil;
import org.aludratest.scheduler.util.CommonRunnerLeafAttributes;
import org.aludratest.testcase.AludraTestCase;
import org.aludratest.testcase.Parallel;
import org.aludratest.testcase.Sequential;
import org.aludratest.testcase.SequentialGroup;
import org.aludratest.testcase.Suite;
import org.aludratest.testcase.Test;
import org.aludratest.testcase.data.TestCaseData;
import org.aludratest.testcase.data.TestDataProvider;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.databene.commons.BeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Default implementation of the RunnerTreeBuilder component interface.
 * 
 * @author falbrech */
@Component(role = RunnerTreeBuilder.class, instantiationStrategy = "per-lookup")
public class RunnerTreeBuilderImpl implements RunnerTreeBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(RunnerTreeBuilder.class);

    private Class<?> initializer;

    private final AtomicLong errorCount = new AtomicLong();

    private final AtomicInteger nextLeafId = new AtomicInteger();

    /** Used to trace added files and detect recursions & multi-uses of classes */
    private Set<Class<?>> addedClasses = new HashSet<Class<?>>();

    /** Map Class -> Assertion Error for classes where an assertion failed */
    private Map<Class<?>, String> assertionErrorClasses = new HashMap<Class<?>, String>();

    @Requirement
    private TestDataProvider testDataProvider;

    @Requirement
    private AludraTestConfig aludraConfig;

    @Override
    public RunnerTree buildRunnerTree(Class<?> suiteOrTestClass) {
        RunnerTree tree = new RunnerTree();
        parseTestOrSuiteClass(suiteOrTestClass, null, tree);
        if (!assertionErrorClasses.isEmpty()) {
            // concatenate all exceptions
            Iterator<Map.Entry<Class<?>, String>> iter = assertionErrorClasses.entrySet().iterator();
            throw concatAssertionExceptions(iter, null);
        }

        return tree;
    }

    @Override
    public RunnerTree buildRunnerTree(AnnotationBasedExecution executionConfig) {
        this.initializer = executionConfig.getInitializer();

        // find all class files matching the filter
        List<Class<? extends AludraTestCase>> testClasses;

        File searchRoot = executionConfig.getJarOrClassRoot();
        if (searchRoot.isDirectory()) {
            testClasses = findMatchingClassesInFolder(searchRoot, "", executionConfig.getFilter(),
                    executionConfig.getClassLoader());
        }
        else if (searchRoot.isFile()) {
            try {
                testClasses = findMatchingClassesInJar(searchRoot, executionConfig.getFilter(), executionConfig.getClassLoader());
            }
            catch (IOException e) {
                throw new PreconditionFailedException("Could not search JAR file " + searchRoot.getAbsolutePath()
                        + " for test classes", e);
            }
        }
        else {
            throw new PreconditionFailedException("Unknown file type for class root " + searchRoot.getAbsolutePath());
        }

        // construct the RunnerTree
        RunnerTree tree = new RunnerTree();
        RunnerGroup root = tree.createRoot("All Tests", true);

        Class<?> initializerClass = executionConfig.getInitializer();
        if (initializerClass != null) {
            parseTestOrSuiteClass(initializerClass, root, tree);
        }

        CategoryBuilder categoryBuilder;
        if (executionConfig.getGroupingAttributes().isEmpty()) {
            String commonPackageRoot = getCommonPackageRoot(testClasses);
            categoryBuilder = new CategoryBuilder(commonPackageRoot);
        }
        else {
            categoryBuilder = new CategoryBuilder(executionConfig.getGroupingAttributes());
        }

        for (Class<? extends AludraTestCase> clz : testClasses) {
            parseTestClass(clz, categoryBuilder.getParentRunnerGroup(tree, clz), tree);
        }

        // sort tree according to sort configuration
        String sortClassName = aludraConfig.getRunnerTreeSorterName();
        if (sortClassName == null) {
            sortClassName = Alphabetic.class.getName();
        }

        if (!sortClassName.contains(".")) {
            sortClassName = Alphabetic.class.getPackage().getName() + "." + sortClassName;
        }

        try {
            Class<?> clz = Class.forName(sortClassName);
            @SuppressWarnings("unchecked")
            Comparator<RunnerNode> comparator = (Comparator<RunnerNode>) clz.newInstance();
            RunnerTreeSortUtil.sortTree(tree, comparator);
        }
        catch (Exception e) {
            LOGGER.error("Could not sort runner tree because comparator could not be created.", e);
        }

        return tree;
    }

    @SuppressWarnings("unchecked")
    private List<Class<? extends AludraTestCase>> findMatchingClassesInFolder(File folder, String packagePrefix,
            TestClassFilter filter, ClassLoader classLoader) {
        List<Class<? extends AludraTestCase>> result = new ArrayList<Class<? extends AludraTestCase>>();
        File[] children = folder.listFiles();
        for (File file : children) {
            if (file.isDirectory()) {
                result.addAll(findMatchingClassesInFolder(file,
                        packagePrefix + ("".equals(packagePrefix) ? "" : ".") + file.getName(), filter, classLoader));
            }
            else if (file.isFile() && (file.getName().endsWith(".class") || file.getName().endsWith(".java"))) {
                String className = packagePrefix + "." + file.getName().substring(0, file.getName().lastIndexOf('.'));
                try {
                    Class<?> clz;
                    if (classLoader != null) {
                        clz = classLoader.loadClass(className);
                    }
                    else {
                        clz = Class.forName(className);
                    }
                    if (AludraTestCase.class.isAssignableFrom(clz) && !result.contains(clz)
                            && filter.matches((Class<? extends AludraTestCase>) clz)) {
                        result.add((Class<? extends AludraTestCase>) clz);
                    }
                }
                catch (Throwable t) {
                    // ignore that class
                }
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private List<Class<? extends AludraTestCase>> findMatchingClassesInJar(File jarFile, TestClassFilter filter,
            ClassLoader classLoader)
                    throws IOException {
        Pattern classPattern = Pattern.compile("(.+/|)([^/]+)\\.class");

        List<Class<? extends AludraTestCase>> result = new ArrayList<Class<? extends AludraTestCase>>();

        JarFile jf = new JarFile(jarFile);
        Enumeration<JarEntry> entries = jf.entries();
        while (entries.hasMoreElements()) {
            JarEntry je = entries.nextElement();
            Matcher m = classPattern.matcher(je.getName());
            if (m.matches()) {
                String pkgName = m.group(1).replace('/', '.');
                String className = m.group(2);
                if (!"".equals(pkgName)) {
                    className = pkgName + "." + className;
                }
                try {
                    Class<?> clz;
                    if (classLoader != null) {
                        clz = classLoader.loadClass(className);
                    }
                    else {
                        clz = Class.forName(className);
                    }
                    if (AludraTestCase.class.isAssignableFrom(clz) && filter.matches((Class<? extends AludraTestCase>) clz)) {
                        result.add((Class<? extends AludraTestCase>) clz);
                    }
                }
                catch (Throwable t) {
                    // ignore that class
                }
            }
        }

        return result;
    }

    private String getCommonPackageRoot(List<Class<? extends AludraTestCase>> testClasses) {
        if (testClasses.isEmpty()) {
            return "";
        }
        String commonPrefix = testClasses.get(0).getName();
        for (Class<?> clz : testClasses) {
            String cn = clz.getName();
            commonPrefix = getCommonPrefix(commonPrefix, cn);
            if ("".equals(commonPrefix)) {
                // totally unequal
                return commonPrefix;
            }
        }

        if (commonPrefix.contains(".")) {
            commonPrefix = commonPrefix.substring(0, commonPrefix.lastIndexOf('.'));
        }
        else {
            return "";
        }

        return commonPrefix;
    }

    private String getCommonPrefix(String s1, String s2) {
        int i;
        for (i = 0; i < s1.length() && i < s2.length() && s1.charAt(i) == s2.charAt(i); i++)
            ;
        return s1.substring(0, i);
    }

    private PreconditionFailedException concatAssertionExceptions(Iterator<Map.Entry<Class<?>, String>> iterator,
            PreconditionFailedException cause) {
        if (!iterator.hasNext()) {
            return cause;
        }
        Map.Entry<Class<?>, String> entry = iterator.next();
        String msg = entry.getValue() + ": " + entry.getKey().getName();
        PreconditionFailedException ex = cause == null ? new PreconditionFailedException(msg) : new PreconditionFailedException(
                msg, cause);
        return concatAssertionExceptions(iterator, ex);
    }

    private boolean isTestSuiteClass(Class<?> testClass) {
        return (testClass.getAnnotation(Suite.class) != null);
    }

    private boolean assertTestClass(Class<?> testClass) {
        if ((testClass.getModifiers() & Modifier.ABSTRACT) == Modifier.ABSTRACT) {
            assertionErrorClasses.put(testClass, "Abstract class not suitable as test class");
            return false;
        }
        else if (!AludraTestCase.class.isAssignableFrom(testClass)) {
            assertionErrorClasses.put(testClass, "Test class does not inherit from " + AludraTestCase.class.getName());
            return false;
        }
        else if (testMethodCount(testClass) == 0) {
            assertionErrorClasses.put(testClass, "No @Test methods found in class");
            return false;
        }

        return true;
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

    private boolean checkAddTestClass(Class<?> clazz) {
        if (addedClasses.contains(clazz)) {
            if (!clazz.equals(initializer)) {
                throw new PreconditionFailedException("The class " + clazz
                        + " is used in more than one test suite, or part of a test suite recursion.");
            }
            else {
                return false;
            }
        }
        else {
            addedClasses.add(clazz);
            return true;
        }
    }

    /** Parses an AludraTest test class or suite.
     * @param testClass the test or suite class to parse
     * @param parentGroup the RunnerGroup into which to insert the test
     * @param tree the RunnerTree that holds the test hierarchy */
    public void parseTestOrSuiteClass(Class<?> testClass, RunnerGroup parentGroup, RunnerTree tree) {
        // check test class type
        if (isTestSuiteClass(testClass)) {
            parseSuiteClass(testClass, parentGroup, tree);
        }
        else {
            if (assertTestClass(testClass)) {
                parseTestClass(testClass, parentGroup, tree);
            }
        }
    }

    /** Parses an AludraTest test suite class. */
    private void parseSuiteClass(Class<?> testClass, RunnerGroup parentGroup, RunnerTree tree) {
        LOGGER.debug("Parsing suite class: {}", testClass.getName());
        if (checkAddTestClass(testClass)) {
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
    }

    /** Parses an AludraTest test class. */
    private void parseTestClass(Class<?> testClass, RunnerGroup parentGroup, RunnerTree tree) {
        LOGGER.debug("Parsing test class: {}", testClass.getName());
        if (checkAddTestClass(testClass)) {
            RunnerGroup classGroup = createRunnerGroupForTestClass(testClass, parentGroup, tree);
            for (Method method : testClass.getMethods()) {
                parseMethod(method, testClass, classGroup, tree);
            }
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

        addSequentialGroupAttributes(group, testClass);

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
                        createTestRunnerForMethodInvocation(method, data.getData(), data.getId(), data.isIgnored(),
                                data.getIgnoredReason(), methodGroup, tree);
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
            String ignoredReason, RunnerGroup methodGroup, RunnerTree tree) {
        // create log4testing TestCase
        String invocationTestCaseName = createInvocationTestCaseName(testInfo, methodGroup.getName());
        // Create test object
        @SuppressWarnings("unchecked")
        AludraTestCase testObject = BeanUtil.newInstance((Class<? extends AludraTestCase>) method.getDeclaringClass());
        TestInvoker invoker = new AludraTestMethodInvoker(testObject, method, args);
        createRunnerForTestInvoker(invoker, methodGroup, tree, invocationTestCaseName, ignore, ignoredReason);
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
        createRunnerForTestInvoker(invoker, methodGroup, tree, invocationTestCaseName, false, null);
    }

    private void createRunnerForTestInvoker(TestInvoker invoker, RunnerGroup parentGroup, RunnerTree tree, String testCaseName,
            boolean ignore, String ignoredReason) {
        RunnerLeaf leaf = tree.addLeaf(nextLeafId.incrementAndGet(), invoker, testCaseName, parentGroup);

        if (ignore) {
            leaf.setAttribute(CommonRunnerLeafAttributes.IGNORE, Boolean.valueOf(ignore));
            if (ignoredReason != null) {
                leaf.setAttribute(CommonRunnerLeafAttributes.IGNORE_REASON, ignoredReason);
            }
        }
    }

    private void addSequentialGroupAttributes(RunnerGroup group, Class<?> testClass) {
        SequentialGroup annot = testClass.getAnnotation(SequentialGroup.class);
        if (annot != null) {
            group.setAttribute(CommonRunnerLeafAttributes.SEQUENTIAL_GROUP_NAME, annot.groupName());
            group.setAttribute(CommonRunnerLeafAttributes.SEQUENTIAL_GROUP_INDEX, Integer.valueOf(annot.index()));
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

    private static class CategoryBuilder {

        private String removePackagePrefix;

        private List<String> categoryOrder;

        public CategoryBuilder(List<String> categoryOrder) {
            this.categoryOrder = categoryOrder;
        }

        public CategoryBuilder(String removePackagePrefix) {
            this.removePackagePrefix = removePackagePrefix;
            this.categoryOrder = Collections.emptyList();
        }

        public RunnerGroup getParentRunnerGroup(RunnerTree tree, Class<? extends AludraTestCase> clazz) {
            if (!categoryOrder.isEmpty()) {
                List<String> categories = new ArrayList<String>();
                StringBuilder prefix = new StringBuilder();
                for (String cat : categoryOrder) {
                    String catVal = TestAttributeUtil.getTestAttributes(clazz).get(cat);
                    if (catVal == null) {
                        catVal = cat + " unknown";
                    }
                    categories.add(prefix + catVal);
                    prefix.append(catVal).append(".");
                }
                return forceGetRunnerGroup(tree, categories);
            }
            else {
                String className = clazz.getName();
                if (!"".equals(removePackagePrefix) && className.startsWith(removePackagePrefix)) {
                    className = className.substring(removePackagePrefix.length());
                    if (className.startsWith(".")) {
                        className = className.substring(1);
                    }
                }

                // remove class itself
                if (className.contains(".")) {
                    className = className.substring(0, className.lastIndexOf('.'));
                }
                else {
                    // no package available
                    return tree.getRoot();
                }

                List<String> groups = new ArrayList<String>();
                int i = 0;
                while (i < className.length()) {
                    int nextIndex = className.indexOf('.', i);
                    if (nextIndex == -1) {
                        groups.add(className);
                        break;
                    }
                    groups.add(className.substring(0, nextIndex));
                    i = nextIndex + 1;
                }

                return forceGetRunnerGroup(tree, groups);
            }
        }

        private RunnerGroup forceGetRunnerGroup(RunnerTree tree, List<String> pathSegments) {
            RunnerGroup group = tree.getRoot();

            for (String seg : pathSegments) {
                boolean found = false;
                for (RunnerNode node : group.getChildren()) {
                    if (node instanceof RunnerGroup && seg.equals(node.getName())) {
                        group = (RunnerGroup) node;
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    // TODO here would be the place to select execution mode based on whatever information
                    group = tree.createGroup(seg, ExecutionMode.PARALLEL, group);
                }
            }

            return group;
        }
    }

}
