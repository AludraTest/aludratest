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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.aludratest.scheduler.impl.FilterParser;

/** Configuration class for invoking {@link RunnerTreeBuilder#buildRunnerTree(AnnotationBasedExecution)}. Instances of this class
 * specify where to search for classes, which filter and grouping to use, and which class loader (if any) to use. <br>
 * You can use the static tool method {@link AnnotationBasedExecution#parseFilterString(String)} to build a test class filter from
 * a text-based filter definition.
 * 
 * @author falbrech */
public final class AnnotationBasedExecution {

    private File jarOrClassRoot;

    private TestClassFilter filter;

    private List<String> groupingAttributes;

    private ClassLoader classLoader;

    /** Creates a new annotation based execution configuration object.
     * 
     * @param jarOrClassRoot Location to search for Java class or source files. MUST be a valid root, i.e. no subdirectory like
     *            <code>org/aludratest</code>, because this would cause the class name resolution to fail. <br>
     *            If this specifies a file, it is treated as JAR archive, and its entries are searched to determine contained
     *            class names. Otherwise, the specified folder is recursively searched for .class and .java files to determine
     *            class names. The classes are not directly loaded, but the specified or the default class loader is asked for a
     *            class with the determined name.
     * @param filter Filter to use to determine which classes to use.
     * @param groupingAttributes List of attributes to use for grouping the tests. If empty, the package names are used instead.
     * @param classLoader Class loader to use to load the classes, or <code>null</code>. When <code>null</code>, the classes are
     *            loaded with {@link Class#forName(String)}. */
    public AnnotationBasedExecution(File jarOrClassRoot, TestClassFilter filter, List<String> groupingAttributes,
            ClassLoader classLoader) {
        this.jarOrClassRoot = jarOrClassRoot;
        this.filter = filter;
        this.groupingAttributes = Collections.unmodifiableList(new ArrayList<String>(groupingAttributes));
        this.classLoader = classLoader;
    }

    /** Returns the class file location specified by this configuration object.
     * 
     * @return The class file location specified by this configuration object. */
    public File getJarOrClassRoot() {
        return jarOrClassRoot;
    }

    /** Returns the test class filter specified by this configuration object.
     * 
     * @return The test class filter specified by this configuration object. */
    public TestClassFilter getFilter() {
        return filter;
    }

    /** Returns the attribute names which shall be used for grouping, or an empty list to indicate to use the package names
     * instead.
     * 
     * @return The attribute names which shall be used for grouping, or an empty list. */
    public List<String> getGroupingAttributes() {
        return groupingAttributes;
    }

    /** Returns the class loader to use to load classes from found class names, or <code>null</code> to use
     * {@link Class#forName(String)} instead.
     * 
     * @return The class loader to use to load classes from found class names, or <code>null</code>. */
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    /** Builds a new test class filter from the given filter string. The filter string must be in the filter string syntax
     * specified in AludraTest documentation. Two examples follow:
     * 
     * <pre>
     * author=(jdoe,mmiller);status!=(InWork;Draft);application=Core
     * author!=jdoe|status=Approved
     * </pre>
     * 
     * Note that the attribute names and values are completely test-application specific and must be provided via
     * {@link org.aludratest.testcase.TestAttribute} or {@link org.aludratest.testcase.TestAttributes} annotations on the test
     * classes.
     * 
     * @param filterString String based definition of the test class filter.
     * 
     * @return A filter built from the given string based definition, which can be used to filter test classes.
     * 
     * @throws ParseException If the filter string has an invalid syntax. */
    public static TestClassFilter parseFilterString(String filterString) throws ParseException {
        return new FilterParser().parse(filterString);
    }

}
