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
import java.util.concurrent.atomic.AtomicInteger;

import org.aludratest.impl.log4testing.configuration.Log4TestingConfiguration;
import org.aludratest.impl.log4testing.data.TestCaseLog;
import org.aludratest.impl.log4testing.data.TestStepGroup;
import org.aludratest.impl.log4testing.data.TestStepLog;
import org.aludratest.impl.log4testing.data.TestSuiteLog;
import org.aludratest.impl.log4testing.data.TestSuiteLogComponent;
import org.aludratest.impl.log4testing.data.attachment.Attachment;
import org.aludratest.impl.log4testing.data.attachment.StringAttachment;
import org.aludratest.impl.log4testing.output.util.OutputUtil;
import org.aludratest.impl.log4testing.output.writer.VelocityTestCaseWriter;
import org.aludratest.impl.log4testing.output.writer.VelocityTestSuiteWriter;
import org.databene.commons.IOUtil;
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

    private AtomicInteger attachmentCount;

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
        return component.getTag("HTMLPath");
    }

    @Override
    protected void writeCase(TestCaseLog testCase) {
        waitUntilInitialized();
        TestSuiteLog testSuite = testCase.getParent();
        synchronized (testSuite) {
            File targetFolder = new File(filePathOf(testCase)).getParentFile();
            writeAttachments(testCase, targetFolder);
            super.writeCase(testCase);
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
        component.setTag("HTMLPath", outputFile.getAbsolutePath());
        component.setTag("HTMLPathFromBaseDir", OutputUtil.pathFromBaseDir(outputFile, new File(outputDir)));
        component.setTag("HTMLPathToBaseDir", OutputUtil.pathToBaseDir(outputFile, new File(outputDir)));
    }

    private void openBrowserWithRootSuiteReport(TestSuiteLog rootSuite) {
        String url = "file://" + System.getProperty("user.dir") + '/' + outputDir + '/' + rootSuite.getTag("HTMLPath");
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

}
