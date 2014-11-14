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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Report.ProcessingError;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.RuleSetReferenceId;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.renderers.AbstractRenderer;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.util.datasource.DataSource;
import net.sourceforge.pmd.util.datasource.FileDataSource;

public class PMDTestHelper {

    private final static String TEST_JAVA_PREFIX = "src/test/java";

    public static Report doPMD(Class<?>[] classesUnderTest, AbstractJavaRule ruleUnderTest) throws IOException {
        // calculate Java file location, from current location
        List<DataSource> fileList = new ArrayList<DataSource>();
        for (Class<?> clazz : classesUnderTest) {
            File file = new File(new File("").getAbsoluteFile(), TEST_JAVA_PREFIX + "/" + clazz.getName().replace('.', '/')
                    + ".java");
            if (!file.isFile())
                throw new IOException("Test Java file " + file + " does not exist");
            fileList.add(new FileDataSource(file));
        }

        return doPMD(fileList, ruleUnderTest);
    }

    public static Report doPMD(List<DataSource> javaFilesUnderTest, AbstractJavaRule ruleUnderTest) {

        RuleSetFactory factory = new SimpleRuleSetFactory(ruleUnderTest);
        RuleContext context = new RuleContext();

        PMDConfiguration config = new PMDConfiguration();
        config.setInputPaths("."); // overridden by file list
        config.setClassLoader(PMDTestHelper.class.getClassLoader());
        config.setThreads(1);
        config.setRuleSets("aludratest.xml");

        List<Report> fileReports = new ArrayList<Report>();

        PMD.processFiles(config, factory, javaFilesUnderTest, context,
                Collections.<Renderer> singletonList(new GetReportRenderer(fileReports)));

        // combine multiple reports to one
        Report aggregate = new Report();
        for (Report r : fileReports) {
            Iterator<RuleViolation> iter = r.iterator();
            while (iter.hasNext()) {
                aggregate.addRuleViolation(iter.next());
            }
            Iterator<ProcessingError> iter2 = r.errors();
            while (iter2.hasNext()) {
                aggregate.addError(iter2.next());
            }
        }

        return aggregate;
    }

    private static class SimpleRuleSetFactory extends RuleSetFactory {

        private RuleSet ruleSet;

        private RuleSets ruleSets;

        private SimpleRuleSetFactory(AbstractJavaRule ruleUnderTest) {
            this.ruleSet = new RuleSet();
            ruleSet.setName("AludraTestUnitTest");
            ruleSet.addRule(ruleUnderTest);

            this.ruleSets = new RuleSets(ruleSet);
        }

        @Override
        public synchronized RuleSet createRuleSet(RuleSetReferenceId ruleSetReferenceId) throws RuleSetNotFoundException {
            return ruleSet;
        }

        @Override
        public synchronized RuleSets createRuleSets(List<RuleSetReferenceId> ruleSetReferenceIds) throws RuleSetNotFoundException {
            return ruleSets;
        }

        @Override
        public synchronized RuleSet createRuleSet(String referenceString) throws RuleSetNotFoundException {
            return ruleSet;
        }

        @Override
        public synchronized RuleSets createRuleSets(String referenceString) throws RuleSetNotFoundException {
            return ruleSets;
        }
    }

    private static class GetReportRenderer extends AbstractRenderer {

        private List<Report> reports = new ArrayList<Report>();

        public GetReportRenderer(List<Report> reports) {
            super("GetReportRenderer", "Extracts report objects from multi-threaded PMD execution");
            this.reports = reports;
        }

        @Override
        public String defaultFileExtension() {
            return null;
        }

        @Override
        public void start() throws IOException {
        }

        @Override
        public void startFileAnalysis(DataSource dataSource) {
        }

        @Override
        public synchronized void renderFileReport(Report report) throws IOException {
            this.reports.add(report);
        }

        @Override
        public void end() throws IOException {
        }

    }

}
