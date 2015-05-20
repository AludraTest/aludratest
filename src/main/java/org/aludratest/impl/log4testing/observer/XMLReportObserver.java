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
import org.aludratest.impl.log4testing.output.util.OutputUtil;
import org.aludratest.impl.log4testing.output.writer.VelocityTestSuiteWriter;

/** 
 * Exports the complete test suite structure to a single XML file.
 * @author Volker Bergmann
 * @author Joerg Langnickel
 */
public class XMLReportObserver extends VelocityReportTestObserver {

    /** Default constructor. */
    public XMLReportObserver() {
    }
    
    @Override
    public void finishedTestProcess(TestSuiteLog rootSuite) {
        writeSuite(rootSuite);
    }


    // private helpers ---------------------------------------------------------

    @Override
    protected void createTestSuiteWriter() {
        this.testSuiteWriter = new VelocityTestSuiteWriter();
        this.testSuiteWriter.setExtension("xml");
        this.testSuiteWriter.setOutputDir(outputDir);
        this.testSuiteWriter.setTemplate(testSuiteTemplate);
        this.testSuiteWriter.setVariable("testSuite");
    }
    
    @Override
    protected void configurePaths(TestSuiteLog testSuite) {
        super.configurePaths(testSuite);
        configureXmlPaths(testSuite);
        for (TestSuiteLog child : testSuite.getTestSuites()) {
            configurePaths(child);
        }
        for (TestCaseLog child : testSuite.getTestCases()) {
            configureXmlPaths(child);
        }
    }

    private void configureXmlPaths(TestSuiteLogComponent component) {
        File outputFile = OutputUtil.outputFile(component.getName(), "xml", null, false, outputDir);
        component.setTag("XMLPath", outputFile.getAbsolutePath());
    }

    @Override
    protected String filePathOf(TestSuiteLogComponent component) {
        return component.getTag("XMLPath");
    }

}
