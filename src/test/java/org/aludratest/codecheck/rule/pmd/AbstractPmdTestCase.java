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
package org.aludratest.codecheck.rule.pmd;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.util.datasource.DataSource;

public abstract class AbstractPmdTestCase extends TestCase {

    /**
     * Runs PMD on the given single class with the given single rule.
     * 
     * @param singleClassUnderTest
     *            Single class to run PMD on.
     * @param ruleUnderTest
     *            Single PMD Rule to apply.
     * @return The PMD Report, containing the results of the PMD execution.
     */
    protected final Report runPmdTest(Class<?> singleClassUnderTest, AbstractJavaRule ruleUnderTest) {
        return runPmdTest(new Class<?>[] { singleClassUnderTest }, ruleUnderTest);
    }

    /**
     * Runs PMD on the given classes with the given single rule.
     * 
     * @param classesUnderTest
     *            Classes to run PMD on.
     * @param ruleUnderTest
     *            Single PMD Rule to apply.
     * @return The PMD Report, containing the results of the PMD execution.
     */
    protected final Report runPmdTest(Class<?>[] classesUnderTest, AbstractJavaRule ruleUnderTest) {
        try {
            return PMDTestHelper.doPMD(classesUnderTest, ruleUnderTest);
        }
        catch (IOException e) {
            throw new AssertionFailedError("Unexpected I/O exception when running PMD: " + e.getMessage());
        }
    }

    /**
     * This method can be used if the Java file to parse contains potentially
     * compiler breaking code and should not be on the default compile paths of
     * AludraTest.
     * 
     * @param javaFilesUnderTest
     *            Resource URLs of the files to test, e.g. retrieved from
     *            Classloader.
     * @param ruleUnderTest
     *            Single PMD Rule to apply.
     * 
     * @return The PMD Report, containing the results of the PMD execution.
     */
    protected final Report runPmdTest(URL[] javaFilesUnderTest, AbstractJavaRule ruleUnderTest) {
        // convert URLs into DataSources
        List<DataSource> pmdFiles = new ArrayList<DataSource>();
        for (URL url : javaFilesUnderTest) {
            pmdFiles.add(new URLDataSource(url));
        }

        return PMDTestHelper.doPMD(pmdFiles, ruleUnderTest);
    }



    protected final void assertReportViolations(Report report, int violationCount) {
        int cnt = 0;
        Iterator<RuleViolation> iter = report.iterator();
        while (iter.hasNext()) {
            iter.next();
            cnt++;
        }

        // assert that no other errors
        if (report.errors().hasNext()) {
            throw new AssertionFailedError("Report should not contain processing errors, but does: "
                    + report.errors().next().getMsg());
        }

        assertEquals(violationCount, cnt);
    }

    private RuleViolation getViolation(Report report, int index) {
        int cnt = 0;
        Iterator<RuleViolation> iter = report.iterator();
        while (iter.hasNext()) {
            RuleViolation violation = iter.next();
            if (cnt == index)
                return violation;
            cnt++;
        }

        throw new IndexOutOfBoundsException();
    }

    protected final void assertReportViolationClass(Report report, int violationIndex, String simpleClassName) {
        RuleViolation violation = getViolation(report, violationIndex);
        assertEquals(simpleClassName, violation.getClassName());
    }

    protected final void assertReportViolationLine(Report report, int violationIndex, int lineNumber) {
        RuleViolation violation = getViolation(report, violationIndex);
        assertEquals(lineNumber, violation.getBeginLine());
    }

    protected final void assertReportViolationMessageMatches(Report report, int violationIndex, String regexpPattern) {
        RuleViolation violation = getViolation(report, violationIndex);
        assertTrue(violation.getDescription().matches(regexpPattern));
    }

    protected final void assertReportViolationPriority(Report report, int violationIndex, RulePriority priority) {
        RuleViolation violation = getViolation(report, violationIndex);
        assertEquals(priority, violation.getRule().getPriority());
    }

    protected final void assertReportViolationMethod(Report report, int violationIndex, String methodName) {
        RuleViolation violation = getViolation(report, violationIndex);
        assertEquals(methodName, violation.getMethodName());
    }

    protected final void assertReportViolationField(Report report, int violationIndex, String fieldName) {
        RuleViolation violation = getViolation(report, violationIndex);
        assertEquals(fieldName, violation.getVariableName());
    }

    /**
     * Private helper class which implements the PMD DataSource interface and
     * wraps a URL.
     * 
     */
    private static class URLDataSource implements DataSource {

        private URL url;

        public URLDataSource(URL url) {
            this.url = url;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return url.openStream();
        }

        @Override
        public String getNiceFileName(boolean shortNames, String inputFileName) {
            return shortNames ? url.getFile() : url.toExternalForm();
        }

    }

}
