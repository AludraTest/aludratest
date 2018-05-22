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
package org.aludratest.config;

/** Provides the central configuration of AludraTest. <br>
 * If you need access to an instance of this interface, the preferred way is to ask your context object for a
 * <code>newComponentInstance()</code> using this Interface class as the key.
 *
 * @author Volker Bergmann
 * @author falbrech */
@InternalComponent(singleton = true)
@ConfigProperties({
    @ConfigProperty(name = AludraTestConfig.NUMBER_OF_THREADS_PROP, type = int.class, description = "The number of threads to use for parallel test case execution", defaultValue = "1"),
    @ConfigProperty(name = AludraTestConfig.STOP_ON_VERIFICATION_PROP, type = boolean.class, description = "If set to true, test execution stops if an exception is thrown on verfication actions.", defaultValue = "true"),
    @ConfigProperty(name = AludraTestConfig.STOP_ON_INTERACTION_PROP, type = boolean.class, description = "If set to true, test execution stops if an exception is thrown on interaction actions.", defaultValue = "true"),
    @ConfigProperty(name = AludraTestConfig.LOGGING_DISABLED_PROP, type = boolean.class, description = "If set to true, test logging is enabled.", defaultValue = "false"),
    @ConfigProperty(name = AludraTestConfig.XLS_ROOT_PROP, type = String.class, description = "The root directory containing Excel files with test data", defaultValue = "./src/main/resources/testdata"),
    @ConfigProperty(name = AludraTestConfig.DEFAULT_LOCALE_PROP, type = String.class, description = "The locale to use as default locale. If not specified, the system default locale is used."),
    @ConfigProperty(name = AludraTestConfig.CONFIG_TAB_REQUIRED_PROP, type = boolean.class, description = "If set to true, a configuration tab is required in Excel files, and its absence will cause an exception.", defaultValue = "false"),
    @ConfigProperty(name = AludraTestConfig.IGNORE_ENABLED_PROP, type = boolean.class, description = "If set to true, test case classes can be marked as ignored. Otherwise, the @Ignored annotation will itself be ignored, causing the marked tests to be executed.", defaultValue = "false"),
    @ConfigProperty(name = AludraTestConfig.NUMERIC_TOLERANCE_PROP, type = double.class, description = "The maximum allowed difference of a current and an expected value. This is for handling rounding issues when dealing with double precision values.", defaultValue = " 0.00000001"),
    @ConfigProperty(name = AludraTestConfig.DEBUG_ON_FRAMEWORK_EXCEPTION_PROP, type = boolean.class, description = "If set to true, debug attachments (e.g. screenshots) will be created also on framework (blue) errors.", defaultValue = "false"),
    @ConfigProperty(name = AludraTestConfig.RUNNER_TREE_SORTER_PROP, type = String.class, description = "A simple or fully qualified name of a Runner Tree Sorter class to use. Default sorter is the Alphabetic sorter. This sorting only applies for filter / grouping execution mode (not for suite-based execution mode).", defaultValue = "Alphabetic"),
    @ConfigProperty(name = AludraTestConfig.ATTACHMENTS_AS_FILE_PROP, type = boolean.class, description = "If set to true, test step attachments are buffered on the file system as temporary files (using File.createTempFile()). This helps reducing memory usage when running many test cases. Default is false.", defaultValue = "false"),
    @ConfigProperty(name = AludraTestConfig.SECONDS_OFFSET_PROP, type = int.class, description = "Amount of seconds to add to script calculations when evaluating test data. Use negative amount to subtract. Can be used for 'time travel' features of application under test."),
    @ConfigProperty(name = AludraTestConfig.DEFERRED_EVALUATION_PROP, type = boolean.class, description = "If set to true, script formulas are evaluated when test case starts, otherwise, they are evaluated when test execution tree is built (default). Currently only applies to XML test data sources.", defaultValue = "false", required = false) })
public interface AludraTestConfig extends Configurable {

    /** Configuration property name. */
    public static final String NUMBER_OF_THREADS_PROP = "number.of.threads";

    /** Configuration property name. */
    public static final String STOP_ON_VERIFICATION_PROP = "stop.testcase.on.verification.exception";

    /** Configuration property name. */
    public static final String STOP_ON_INTERACTION_PROP = "stop.testcase.on.interaction.exception";

    /** Configuration property name. */
    public static final String LOGGING_DISABLED_PROP = "aludratest.logging.disabled";

    /** Configuration property name. */
    public static final String XLS_ROOT_PROP = "javatest.xls.root";

    /** Configuration property name. */
    public static final String DEFAULT_LOCALE_PROP = "default.locale";

    /** Configuration property name. */
    public static final String CONFIG_TAB_REQUIRED_PROP = "config.tab.required";

    /** Configuration property name. */
    public static final String IGNORE_ENABLED_PROP = "ignore.enabled";

    /** Configuration property name. */
    public static final String NUMERIC_TOLERANCE_PROP = "verification.numeric.difference";

    /** Configuration property name. */
    public static final String DEBUG_ON_FRAMEWORK_EXCEPTION_PROP = "debug.attachments.on.framework.exception";

    /** Configuration property name. */
    public static final String RUNNER_TREE_SORTER_PROP = "runner.tree.sorter";

    /** Configuration property name. */
    public static final String ATTACHMENTS_AS_FILE_PROP = "attachments.filebuffer";

    /** Configuration property name. */
    public static final String SECONDS_OFFSET_PROP = "script.seconds.offset";

    /** Configuration property name. */
    public static final String DEFERRED_EVALUATION_PROP = "deferred.script.evaluation";

    // interface ---------------------------------------------------------------

    /** @return The version of AludraTest, e.g. <code>2.7.0-17</code>. */
    public String getVersion();

    /** @return whether to stop test step execution on exceptions in a {@link org.aludratest.service.Interaction} */
    public boolean isStopTestCaseOnInteractionException();

    /** @return whether to stop test step execution on exceptions in a {@link org.aludratest.service.Verification} */
    public boolean isStopTestCaseOnVerificationException();

    /** @return whether to stop test logging */
    public boolean isAludratestLoggingDisabled();

    /** @return whether to stop test step execution on exceptions in a location other than {@link org.aludratest.service.Interaction}
     *         or {@link org.aludratest.service.Verification} */
    public boolean isStopTestCaseOnOtherException();

    /** @return the root path to check for Excel files which are consumed to fill test case parameters. */
    public String getXlsRootPath();

    /** @return the number of Threads to use by the AludraTest test runner. Default value is 1. */
    public int getNumberOfThreads();

    /** @return if each Excel sheet must have a 'config' tab */
    public boolean isConfigTabRequired();

    /** @return Indicates if test case classes can be marked as ignored. If <code>false</code>, the Ignored annotation will itself
     *         be ignored, causing the marked tests to be executed. */
    public boolean isIgnoreEnabled();

    /** Returns the numeric tolerance to use for double precision based operations.
     *
     * @return The numeric tolerance to use for double precision based operations. */
    public double getNumericTolerance();

    /** Returns if debug attachments shall be created when a Framework Exception occurs.
     *
     * @return <code>true</code> if to include debug attachments on Framework Exceptions. */
    public boolean isDebugAttachmentsOnFrameworkException();

    /** Returns the simple or fully qualified name of a Runner Tree Sorter to use. Simple names should be looked up in package
     * <code>org.aludratest.scheduler.sort</code>.
     *
     * @return The simple or fully qualified name of a Runner Tree Sorter to use. */
    public String getRunnerTreeSorterName();

    /** Returns <code>true</code> if test step attachments shall be buffered on the file system as temporary files, to reduce
     * memory usage when running many test cases.
     *
     * @return <code>true</code> to buffer test step attachments on file system, <code>false</code> otherwise. */
    public boolean isAttachmentsFileBuffer();

    /** Returns the amount of seconds to add to test data script results (when they evaluate to a <code>Date</code> value). This
     * can be used for "time travel" features of the application under test.
     *
     * @return The amount of seconds to add to test data script results. Can be zero or negative. */
    public int getScriptSecondsOffset();

    /** Returns <code>true</code> if script expressions in XML data shall be evaluated only when the according test case execution
     * starts. Otherwise, they are evaluated when the test execution tree is built (default).
     *
     * @return <code>true</code> if script expressions in XML data shall be evaluated only when the according test case execution
     *         starts, <code>false</code> otherwise. */
    public boolean isDeferredScriptEvaluation();

}
