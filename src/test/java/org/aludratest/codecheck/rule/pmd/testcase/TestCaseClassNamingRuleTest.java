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
package org.aludratest.codecheck.rule.pmd.testcase;

import net.sourceforge.pmd.Report;

import org.aludratest.codecheck.rule.pmd.AbstractPmdTestCase;
import org.junit.Test;
import org.test.testclasses.testcase.InvalidTestCaseClass;
import org.test.testclasses.testcase.ValidTestCaseClass;

@SuppressWarnings("javadoc")
public class TestCaseClassNamingRuleTest extends AbstractPmdTestCase {

    @Test
    public void testInvalidClass() {
        TestCaseClassNamingRule rule = new TestCaseClassNamingRule();
        rule.setProperty(rule.regexProperty, "ValidTestCaseClass");
        rule.setProperty(rule.messageProperty, "Class name not OK");
        Report report = runPmdTest(InvalidTestCaseClass.class, rule);
        assertReportViolations(report, 1);
        assertReportViolationMessageMatches(report, 0, "Class name not OK");
    }

    @Test
    public void testValidClass() {
        TestCaseClassNamingRule rule = new TestCaseClassNamingRule();
        rule.setProperty(rule.regexProperty, "ValidTestCaseClass");
        Report report = runPmdTest(ValidTestCaseClass.class, rule);
        assertReportViolations(report, 0);
    }

}
