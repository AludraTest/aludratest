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
import org.test.testclasses.testcase.InvalidMethodNameTestCaseClass;
import org.test.testclasses.testcase.ValidTestCaseClass;

@SuppressWarnings("javadoc")
public class TestCaseMethodNamingRuleTest extends AbstractPmdTestCase {

    @Test
    public void testInvalidClass() {
        TestCaseMethodNamingRule rule = new TestCaseMethodNamingRule();
        rule.setProperty(rule.messageProperty, "Method name not OK");
        Report report = runPmdTest(InvalidMethodNameTestCaseClass.class, rule);
        assertReportViolations(report, 1);
        assertReportViolationMethod(report, 0, "test_illegal_name");
        assertReportViolationMessageMatches(report, 0, "Method name not OK");
    }

    @Test
    public void testValidClass() {
        TestCaseMethodNamingRule rule = new TestCaseMethodNamingRule();
        rule.setProperty(rule.regexProperty, "test");
        Report report = runPmdTest(ValidTestCaseClass.class, rule);
        assertReportViolations(report, 0);
    }

}
