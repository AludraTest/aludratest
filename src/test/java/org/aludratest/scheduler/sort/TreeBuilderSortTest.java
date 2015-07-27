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
package org.aludratest.scheduler.sort;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.aludratest.config.impl.AludraTestingTestConfigImpl;
import org.aludratest.impl.log4testing.data.TestLogger;
import org.aludratest.scheduler.AnnotationBasedExecution;
import org.aludratest.scheduler.RunnerTree;
import org.aludratest.scheduler.RunnerTreeBuilder;
import org.aludratest.scheduler.impl.RunnerTreeBuilderImpl;
import org.aludratest.scheduler.node.RunnerGroup;
import org.aludratest.scheduler.node.RunnerNode;
import org.aludratest.service.AbstractAludraServiceTest;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Before;
import org.junit.Test;

public class TreeBuilderSortTest extends AbstractAludraServiceTest {

    @Before
    public void setUp() {
        TestLogger.clear();
    }

    private RunnerTree parseTestFilter(File classDir, String filter, List<String> categories, ClassLoader cl)
            throws ParseException {
        AnnotationBasedExecution exec = new AnnotationBasedExecution(classDir,
                AnnotationBasedExecution.parseFilterString(filter), categories, cl);

        RunnerTreeBuilder builder = aludra.getServiceManager().newImplementorInstance(RunnerTreeBuilder.class);
        assertTrue(builder instanceof RunnerTreeBuilderImpl);
        return builder.buildRunnerTree(exec);
    }

    /** Tests that by default, tree is sorted alphabetically
     * @throws Exception */
    @Test
    public void testTreeSortingDefault() throws Exception {
        // calculate location of test class
        ClassLoader cl = TreeBuilderSortTestClass.class.getClassLoader();
        File classDir = new File(new File("").getAbsoluteFile(), "target/test-classes");
        RunnerTree runnerTree = parseTestFilter(classDir, "testCategory=sorting", Collections.singletonList("testCategory"), cl);

        RunnerGroup root = runnerTree.getRoot();
        List<RunnerNode> children = root.getChildren();
        assertEquals(1, children.size()); // "sorting"
        children = ((RunnerGroup) children.get(0)).getChildren();
        assertEquals(1, children.size()); // class name
        children = ((RunnerGroup) children.get(0)).getChildren();
        assertEquals(3, children.size()); // methods
        String classPrefix = TreeBuilderSortTestClass.class.getName() + ".";
        assertEquals(classPrefix + "anotherTestMethod", children.get(0).getName());
        assertEquals(classPrefix + "coolTestMethod", children.get(1).getName());
        assertEquals(classPrefix + "myTestMethod", children.get(2).getName());
    }

    /** Tests tree sorting with Alphabetic Descending order
     * @throws Exception */
    @Test
    public void testTreeSortingReverse() throws Exception {
        // override configuration property
        AludraTestingTestConfigImpl.getTestInstance().setRunnerTreeSorterName(AlphabeticDescending.class.getSimpleName());

        // calculate location of test class
        ClassLoader cl = TreeBuilderSortTestClass.class.getClassLoader();
        File classDir = new File(new File("").getAbsoluteFile(), "target/test-classes");
        RunnerTree runnerTree = parseTestFilter(classDir, "testCategory=sorting", Collections.singletonList("testCategory"), cl);

        RunnerGroup root = runnerTree.getRoot();
        List<RunnerNode> children = root.getChildren();
        assertEquals(1, children.size()); // "sorting"
        children = ((RunnerGroup) children.get(0)).getChildren();
        assertEquals(1, children.size()); // class name
        children = ((RunnerGroup) children.get(0)).getChildren();
        assertEquals(3, children.size()); // methods
        String classPrefix = TreeBuilderSortTestClass.class.getName() + ".";
        assertEquals(classPrefix + "myTestMethod", children.get(0).getName());
        assertEquals(classPrefix + "coolTestMethod", children.get(1).getName());
        assertEquals(classPrefix + "anotherTestMethod", children.get(2).getName());
    }

    /** Tests tree sorting with custom sorting class
     * @throws Exception */
    @Test
    public void testTreeSortingCustom() throws Exception {
        // override configuration property
        AludraTestingTestConfigImpl.getTestInstance().setRunnerTreeSorterName(TestNodeSorter.class.getName());

        // calculate location of test class
        ClassLoader cl = TreeBuilderSortTestClass.class.getClassLoader();
        File classDir = new File(new File("").getAbsoluteFile(), "target/test-classes");
        RunnerTree runnerTree = parseTestFilter(classDir, "testCategory=sorting", Collections.singletonList("testCategory"), cl);

        RunnerGroup root = runnerTree.getRoot();
        List<RunnerNode> children = root.getChildren();
        assertEquals(1, children.size()); // "sorting"
        children = ((RunnerGroup) children.get(0)).getChildren();
        assertEquals(1, children.size()); // class name
        children = ((RunnerGroup) children.get(0)).getChildren();
        assertEquals(3, children.size()); // methods
        String classPrefix = TreeBuilderSortTestClass.class.getName() + ".";
        assertEquals(classPrefix + "coolTestMethod", children.get(0).getName());
        assertEquals(classPrefix + "anotherTestMethod", children.get(1).getName());
        assertEquals(classPrefix + "myTestMethod", children.get(2).getName());
    }

    /** Negative test case with invalid sorter class. Must fail silently (not throw exceptions) - no other assumptions (order is
     * not defined in this case). But the Log4J Log is also checked for appropriate error message.
     * 
     * @throws Exception */
    @Test
    public void testInvalidSorter() throws Exception {
        SimpleBufferAppender appender = new SimpleBufferAppender();
        Logger log4jLogger = Logger.getLogger(RunnerTreeBuilder.class);
        log4jLogger.addAppender(appender);

        try {
            // override configuration property
            AludraTestingTestConfigImpl.getTestInstance().setRunnerTreeSorterName("BlaBla");

            // calculate location of test class
            ClassLoader cl = TreeBuilderSortTestClass.class.getClassLoader();
            File classDir = new File(new File("").getAbsoluteFile(), "target/test-classes");
            RunnerTree runnerTree = parseTestFilter(classDir, "testCategory=sorting", Collections.singletonList("testCategory"),
                    cl);

            RunnerGroup root = runnerTree.getRoot();
            List<RunnerNode> children = root.getChildren();
            assertEquals(1, children.size()); // "sorting"
            children = ((RunnerGroup) children.get(0)).getChildren();
            assertEquals(1, children.size()); // class name
            children = ((RunnerGroup) children.get(0)).getChildren();
            assertEquals(3, children.size()); // methods
        }
        finally {
            log4jLogger.removeAppender(appender);
        }

        assertTrue(appender.containsMessage("Could not sort runner tree because comparator could not be created."));
        assertTrue(appender.containsThrowableWithMessage(ClassNotFoundException.class, "org.aludratest.scheduler.sort.BlaBla"));
    }

    public static class TestNodeSorter implements Comparator<RunnerNode> {

        private static final List<String> NAME_ORDER = Arrays.asList(new String[] { "coolTestMethod", "anotherTestMethod",
        "myTestMethod" });

        @Override
        public int compare(RunnerNode o1, RunnerNode o2) {
            String n1 = o1.getName();
            n1 = n1.substring(n1.lastIndexOf('.') + 1);
            String n2 = o2.getName();
            n2 = n2.substring(n2.lastIndexOf('.') + 1);

            return NAME_ORDER.indexOf(n1) - NAME_ORDER.indexOf(n2);
        }

    }

    private static class SimpleBufferAppender extends AppenderSkeleton {

        private List<LoggingEvent> events = new ArrayList<LoggingEvent>();

        @Override
        public void close() {
        }

        @Override
        public boolean requiresLayout() {
            return false;
        }

        @Override
        protected void append(LoggingEvent event) {
            events.add(event);
        }

        public boolean containsMessage(String message) {
            for (LoggingEvent event : events) {
                if (message.equals(event.getMessage())) {
                    return true;
                }
            }

            return false;
        }

        public boolean containsThrowableWithMessage(Class<? extends Throwable> errorClass, String message) {
            for (LoggingEvent event : events) {
                if (event.getThrowableInformation() != null && event.getThrowableInformation().getThrowable() != null) {
                    Throwable t = event.getThrowableInformation().getThrowable();
                    if (errorClass.isAssignableFrom(t.getClass())
                            && (message == null ? t.getMessage() == null : message.equals(t.getMessage()))) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

}
