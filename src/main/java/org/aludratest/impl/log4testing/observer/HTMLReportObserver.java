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
package org.aludratest.impl.log4testing.observer;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import org.aludratest.impl.log4testing.configuration.Log4TestingConfiguration;
import org.aludratest.impl.log4testing.data.TestCaseLog;
import org.aludratest.impl.log4testing.data.TestStepGroup;
import org.aludratest.impl.log4testing.data.TestStepLog;
import org.aludratest.impl.log4testing.data.TestSuiteLog;
import org.aludratest.impl.log4testing.data.TestSuiteLogComponent;
import org.aludratest.impl.log4testing.output.util.OutputUtil;
import org.aludratest.impl.log4testing.output.writer.VelocityTestCaseWriter;
import org.aludratest.impl.log4testing.output.writer.VelocityTestSuiteWriter;
import org.aludratest.testcase.TestStatus;
import org.aludratest.testcase.event.attachment.Attachment;
import org.aludratest.testcase.event.attachment.StringAttachment;
import org.databene.commons.IOUtil;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exports test suite and test case results in HTML format.
 * @author Volker Bergmann
 */
public class HTMLReportObserver extends VelocityReportTestObserver {

    private static final Logger LOGGER = LoggerFactory.getLogger(HTMLReportObserver.class);

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private static final String XML_SUFFIX = ".xml";

    private String ignoreableRoot;
    private boolean abbreviating;
    private boolean shortTimeFormat;
    private boolean openBrowser;
    private String commandRegexp;

    private AtomicInteger attachmentCount;

    private Map<TestSuiteLogComponent, LogPathInfo> logFiles = new HashMap<TestSuiteLogComponent, LogPathInfo>();

    /** Public default constructor. */
    public HTMLReportObserver() {
        this.openBrowser = false;
        this.attachmentCount = new AtomicInteger();
    }

    // properties --------------------------------------------------------------

    /** Sets the {@link #ignoreableRoot}.
     *  @param ignoreableRoot the ignoreableRoot to set */
    public void setIgnoreableRoot(String ignoreableRoot) {
        this.ignoreableRoot = ignoreableRoot;
    }

    /** Sets the {@link #abbreviating} flag
     *  @param abbreviating the abbreviating flag to set */
    public void setAbbreviating(boolean abbreviating) {
        this.abbreviating = abbreviating;
    }

    /** Sets the {@link #shortTimeFormat} flag.
     * @param shortTimeFormat the shortTimeFormat to set */
    public void setShortTimeFormat(boolean shortTimeFormat) {
        this.shortTimeFormat = shortTimeFormat;
    }

    /** Sets the {@link #openBrowser} flag.
     *  @param openBrowser the value to set */
    public void setOpenBrowser(String openBrowser) {
        this.openBrowser = Boolean.parseBoolean(openBrowser);
    }

    /** Sets the Regular Expression for commands to log. If not set or <code>null</code>, all commands are logged.
     * 
     * @param commandRegexp Regular Expression for commands to log. */
    public void setCommandRegexp(String commandRegexp) {
        this.commandRegexp = commandRegexp;
    }

    /** Returns the Regular Expression for commands to log.
     * 
     * @return The Regular Expression for commands to log. */
    public String getCommandRegexp() {
        return commandRegexp;
    }

    // TestObserver interface implementation -----------------------------------

    @Override
    public void startingTestSuite(TestSuiteLog testSuite) {
        writeSuite(testSuite);
    }

    @Override
    public void startingTestCase(TestCaseLog testCase) {
        writeParentSuites(testCase);
        writeCase(testCase);
    }

    @Override
    public void finishedTestCase(TestCaseLog testCase) {
        writeParentSuites(testCase);
        writeCase(testCase);
    }

    @Override
    public void finishedTestSuite(TestSuiteLog testSuite) {
        writeParentSuites(testSuite);
        writeSuite(testSuite);
    }

    @Override
    public void finishedTestProcess(TestSuiteLog rootSuite) {
        if (openBrowser && Desktop.isDesktopSupported()) {
            openBrowserWithRootSuiteReport(rootSuite);
        }
    }


    // overrides of parent class -----------------------------------------------

    @Override
    protected void prepareOutputDir() {
        super.prepareOutputDir();
        try {
            String outputDirUri = outputDir.replace('\\', '/');
            IOUtil.copyFile("org/aludratest/log4testing/output/html/log4testing.css", outputDirUri + "/log4testing.css");
            IOUtil.copyFile("org/aludratest/log4testing/output/html/jquery.js", outputDirUri + "/jquery.js");
            IOUtil.copyFile("org/aludratest/log4testing/output/html/testcase.js", outputDirUri + "/testcase.js");
        } catch (IOException e) {
            LOGGER.error("Error copying resource files", e);
        }
    }

    @Override
    protected void createTestCaseWriter() {
        this.testCaseWriter = new VelocityTestCaseWriter();
        this.testCaseWriter.setAbbreviating(abbreviating);
        this.testCaseWriter.setExtension("html");
        this.testCaseWriter.setIgnoreableRoot(ignoreableRoot);
        this.testCaseWriter.setOutputDir(outputDir);
        this.testCaseWriter.setShortTimeFormat(shortTimeFormat);
        this.testCaseWriter.setTemplate(testCaseTemplate);
        this.testCaseWriter.setVariable("testCase");
    }

    @Override
    protected void createTestSuiteWriter() {
        this.testSuiteWriter = new VelocityTestSuiteWriter();
        this.testSuiteWriter.setAbbreviating(abbreviating);
        this.testSuiteWriter.setExtension("html");
        this.testSuiteWriter.setIgnoreableRoot(ignoreableRoot);
        this.testSuiteWriter.setOutputDir(outputDir);
        this.testSuiteWriter.setShortTimeFormat(shortTimeFormat);
        this.testSuiteWriter.setTemplate(testSuiteTemplate);
        this.testSuiteWriter.setVariable("testSuite");
    }

    @Override
    protected void configurePaths(TestSuiteLog testSuite) {
        super.configurePaths(testSuite);
        configureHtmlPaths(testSuite);
        for (TestSuiteLog child : testSuite.getTestSuites()) {
            configurePaths(child);
        }
        for (TestCaseLog child : testSuite.getTestCases()) {
            configureHtmlPaths(child);
        }
    }

    @Override
    protected String filePathOf(TestSuiteLogComponent component) {
        LogPathInfo f = logFiles.get(component);
        if (f == null) {
            configureHtmlPaths(component);
        }
        return logFiles.get(component).getLogFile().getAbsolutePath();
    }

    @Override
    protected void writeSuite(TestSuiteLog testSuite) {
        if (testSuite != null && testSuiteWriter != null) {
            waitUntilInitialized();
            synchronized (testSuite) {
                testSuiteWriter.write(testSuite, filePathOf(testSuite),
                        Collections.<String, Object> singletonMap("pathHelper", new LogPathHelper()));
            }
        }
    }

    @Override
    protected void writeCase(TestCaseLog testCase) {
        waitUntilInitialized();
        TestSuiteLog testSuite = testCase.getParent();
        synchronized (testSuite) {
            File targetFolder = new File(filePathOf(testCase)).getParentFile();
            writeAttachments(testCase, targetFolder);
            if (commandRegexp != null && commandRegexp.length() > 0) {
                testCase = filter(testCase);
            }
        }
        // inject path helper into VM context
        synchronized (testCase) {
            testCaseWriter.write(testCase, filePathOf(testCase),
                    Collections.<String, Object> singletonMap("pathHelper", new LogPathHelper()));
        }
    }


    // private helpers ---------------------------------------------------------

    private void writeParentSuites(TestSuiteLogComponent component) {
        TestSuiteLog parentSuite = component.getParent();
        while (parentSuite != null) {
            writeSuite(parentSuite);
            parentSuite = parentSuite.getParent();
        }
    }

    private void configureHtmlPaths(TestSuiteLogComponent component) {
        File outputFile = OutputUtil.outputFile(component.getName(), "html",
                ignoreableRoot, abbreviating, outputDir);
        logFiles.put(component, new LogPathInfo(outputFile,  OutputUtil.pathFromBaseDir(outputFile, new File(outputDir)), OutputUtil.pathToBaseDir(outputFile, new File(outputDir))));
    }

    private void openBrowserWithRootSuiteReport(TestSuiteLog rootSuite) {
        String url = "file://" + System.getProperty("user.dir") + '/' + outputDir + '/'
                + new LogPathHelper().getPathFromBaseDir(rootSuite);
        url = url.replace('\\', '/');
        if (url.endsWith(XML_SUFFIX)) {
            url = url.substring(0, url.length() - XML_SUFFIX.length()) + ".html";
        }
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            LOGGER.error("Error opening browser with uri " + url);
        }
    }

    private void writeAttachments(TestCaseLog testCase, File targetDir) {
        for (TestStepGroup testStepGroup : testCase.getTestStepGroups()) {
            for (TestStepLog step : testStepGroup.getTestSteps()) {
                writeAttachments(step, targetDir);
            }
        }
    }

    private void writeAttachments(TestStepLog testStep, File targetDir) {
        Iterable<Attachment> attachments = testStep.getAttachments();
        for (Attachment attachment : attachments) {
            String fileName = "attachment_" + attachmentCount.incrementAndGet() +
                    "." + attachment.getFileExtension();
            try {
                File outputFile = new File(targetDir, fileName);
                attachment.setFileName(fileName);
                byte[] data = replace(attachment);
                IOUtil.writeBytes(data, outputFile);
            } catch (IOException e) {
                LOGGER.error("A problem has occurred while writing the attachment " + attachment.getLabel() + " to the destination " + fileName, e);
            }
        }
    }

    private static byte[] replace(Attachment attachment) {
        byte[] data = attachment.getFileData();
        if (attachment instanceof StringAttachment) {
            String output = new String(data, UTF_8);
            output = Log4TestingConfiguration.getInstance().replace(output);
            data = output.getBytes(UTF_8);
        }
        return data;
    }

    private TestCaseLog filter(TestCaseLog log) {
        return new FilteredTestCaseLog(log, Pattern.compile(commandRegexp));
    }

    private static class LogPathInfo {

        private File logFile;

        private String pathFromBaseDir;

        private String pathToBaseDir;

        public LogPathInfo(File logFile, String pathFromBaseDir, String pathToBaseDir) {
            this.logFile = logFile;
            this.pathFromBaseDir = pathFromBaseDir;
            this.pathToBaseDir = pathToBaseDir;
        }

        public File getLogFile() {
            return logFile;
        }

        public String getPathFromBaseDir() {
            return pathFromBaseDir;
        }

        public String getPathToBaseDir() {
            return pathToBaseDir;
        }

    }

    private static class FilteredTestCaseLog extends TestCaseLog {

        private TestCaseLog delegate;

        private List<TestStepGroup> filteredGroups = new ArrayList<TestStepGroup>();

        protected FilteredTestCaseLog(TestCaseLog delegate, Pattern commandPattern) {
            super(delegate.getName());
            this.delegate = delegate;
            for (TestStepGroup group : delegate.getTestStepGroups()) {
                filteredGroups.add(new FilteredTestStepGroup(group, commandPattern));
            }
        }

        @Override
        public TestSuiteLog getParent() {
            return delegate.getParent();
        }

        @Override
        public String getName() {
            return delegate.getName();
        }

        @Override
        public List<TestStepGroup> getTestStepGroups() {
            return Collections.unmodifiableList(filteredGroups);
        }

        @Override
        public boolean isFailed() {
            return delegate.isFailed();
        }

        @Override
        public String getId() {
            return delegate.getId();
        }

        @Override
        public Duration getDuration() {
            return delegate.getDuration();
        }

        @Override
        public String getComment() {
            return delegate.getComment();
        }

        @Override
        public boolean isIgnored() {
            return delegate.isIgnored();
        }

        @Override
        public String getIgnoredReason() {
            return delegate.getIgnoredReason();
        }

        @Override
        public boolean isIgnoredAndPassed() {
            return delegate.isIgnoredAndPassed();
        }

        @Override
        public boolean isIgnoredAndFailed() {
            return delegate.isIgnoredAndFailed();
        }

        @Override
        public boolean isFinished() {
            return delegate.isFinished();
        }

        @Override
        public DateTime getStartingTime() {
            return delegate.getStartingTime();
        }

        @Override
        public DateTime getFinishingTime() {
            return delegate.getFinishingTime();
        }

        @Override
        public TestStatus getStatus() {
            return delegate.getStatus();
        }
    }

    private static class FilteredTestStepGroup extends TestStepGroup {

        private TestStepGroup delegate;

        private List<TestStepLog> filteredSteps = new ArrayList<TestStepLog>();

        protected FilteredTestStepGroup(TestStepGroup delegate, Pattern commandFilter) {
            super(delegate.getName(), delegate.getParent());
            this.delegate = delegate;
            for (TestStepLog step : delegate.getTestSteps()) {
                if (step.getCommand() == null || commandFilter.matcher(step.getCommand()).matches() || step.isFailed()) {
                    filteredSteps.add(step);
                }
            }
        }

        @Override
        public String getName() {
            return delegate.getName();
        }

        @Override
        public TestCaseLog getParent() {
            return delegate.getParent();
        }

        @Override
        public boolean isFailed() {
            return delegate.isFailed();
        }

        @Override
        public String getId() {
            return delegate.getId();
        }

        @Override
        public List<TestStepLog> getTestSteps() {
            return filteredSteps;
        }

        @Override
        public Duration getDuration() {
            return delegate.getDuration();
        }

        @Override
        public DateTime getStartingTime() {
            return delegate.getStartingTime();
        }

        @Override
        public DateTime getFinishingTime() {
            return delegate.getFinishingTime();
        }

        @Override
        public TestStatus getStatus() {
            return delegate.getStatus();
        }

        @Override
        public String getComment() {
            return delegate.getComment();
        }

    }

    /** Helper class of which an instance is passed to Velocitymacro context.
     * 
     * @author falbrech */
    public class LogPathHelper {

        /** Returns the path from output base directory to given component's log.
         * @param component
         * @return */
        public String getPathFromBaseDir(TestSuiteLogComponent component) {
            LogPathInfo info = logFiles.get(component);
            if (info == null) {
                configureHtmlPaths(component);
                info = logFiles.get(component);
            }
            return info.getPathFromBaseDir();
        }

        /** Returns the path from given component's log to output base directory.
         * @param component
         * @return */
        public String getPathToBaseDir(TestSuiteLogComponent component) {
            LogPathInfo info = logFiles.get(component);
            if (info == null) {
                configureHtmlPaths(component);
                info = logFiles.get(component);
            }
            return info.getPathToBaseDir();
        }

        /** Returns absolute path of component's log.
         * @param component
         * @return */
        public String getPath(TestSuiteLogComponent component) {
            LogPathInfo info = logFiles.get(component);
            if (info == null) {
                configureHtmlPaths(component);
                info = logFiles.get(component);
            }
            return info.getLogFile().getAbsolutePath();
        }

    }

}
