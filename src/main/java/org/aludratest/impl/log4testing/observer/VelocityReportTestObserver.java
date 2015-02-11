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

import java.io.File;

import org.aludratest.impl.log4testing.data.TestCaseLog;
import org.aludratest.impl.log4testing.data.TestSuiteLog;
import org.aludratest.impl.log4testing.data.TestSuiteLogComponent;
import org.aludratest.impl.log4testing.output.writer.VelocityTestCaseWriter;
import org.aludratest.impl.log4testing.output.writer.VelocityTestSuiteWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parent class for test observers that use Velocity for creating report files.
 * @author Volker Bergmann
 */
public abstract class VelocityReportTestObserver extends AbstractTestObserver {

    private static final Logger LOGGER = LoggerFactory.getLogger(VelocityReportTestObserver.class);

    protected String outputDir;

    protected String testCaseTemplate;
    protected String testSuiteTemplate;

    protected VelocityTestCaseWriter testCaseWriter;
    protected VelocityTestSuiteWriter testSuiteWriter;

    protected volatile boolean initialized;


    /** Sets the {@link #outputDir}.
     * @param outputDir the outputDir to set */
    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    /** Sets the {@link #testCaseTemplate}.
     *  @param testCaseTemplate the testCaseTemplate to set */
    public void setTestCaseTemplate(String testCaseTemplate) {
        this.testCaseTemplate = testCaseTemplate;
    }

    /** Sets the {@link #testSuiteTemplate}.
     *  @param testSuiteTemplate the testSuiteTemplate to set */
    public void setTestSuiteTemplate(String testSuiteTemplate) {
        this.testSuiteTemplate = testSuiteTemplate;
    }

    @Override
    public void startingTestProcess(TestSuiteLog rootSuite) {
        try {
            prepareOutputDir();
            createTestCaseWriter();
            createTestSuiteWriter();
            configurePaths(rootSuite);
        }
        finally {
            // always switch to this state to avoid endless waits
            this.initialized = true;
        }
    }

    protected void prepareOutputDir() {
        new File(outputDir).mkdirs();
    }

    protected void createTestCaseWriter() {
        // empty implementation
    }

    protected void createTestSuiteWriter() {
        // empty implementation
    }

    protected void writeSuite(TestSuiteLog testSuite) {
        if (testSuite != null && testSuiteWriter != null) {
            waitUntilInitialized();
            synchronized (testSuite) {
                testSuiteWriter.write(testSuite, filePathOf(testSuite));
            }
        }
    }

    protected void writeCase(TestCaseLog testCase) {
        if (testCaseWriter != null) {
            waitUntilInitialized();
            synchronized (testCase) {
                testCaseWriter.write(testCase, filePathOf(testCase));
            }
        }
    }

    protected void configurePaths(TestSuiteLog testSuite) {
    }

    protected abstract String filePathOf(TestSuiteLogComponent component);

    protected void waitUntilInitialized() {
        try {
            while (!initialized) {
                Thread.sleep(10);
            }
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted waitUntilInitialized()", e);
        }
    }


    // java.lang.Object overrides ----------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                " [outputDir=" + outputDir + ", " +
                "testCaseTemplate=" + testCaseTemplate + ", " +
                "testSuiteTemplate=" + testSuiteTemplate + "]";
    }

}
