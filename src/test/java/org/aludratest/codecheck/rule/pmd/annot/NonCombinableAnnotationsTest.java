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
package org.aludratest.codecheck.rule.pmd.annot;

import net.sourceforge.pmd.Report;

import org.aludratest.codecheck.rule.pmd.AbstractPmdTestCase;
import org.aludratest.codecheck.rule.pmd.annot.NonCombinableAnnotations;
import org.junit.Test;
import org.test.testclasses.annot.InvalidAnnotated;
import org.test.testclasses.annot.ValidAnnotated;
import org.test.testclasses.testcase.InvalidNoTestCaseClass;
import org.test.testclasses.testcase.InvalidTestCaseClass;

public class NonCombinableAnnotationsTest extends AbstractPmdTestCase {

    @Test
    public void testIllegalClass() {
        Report report = runPmdTest(InvalidTestCaseClass.class, new NonCombinableAnnotations());
        assertReportViolations(report, 2);
    }

    @Test
    public void testValidClass() {
        Report report = runPmdTest(InvalidNoTestCaseClass.class, new NonCombinableAnnotations());
        assertReportViolations(report, 0);
    }

    @Test
    public void testConfiguration() {
        NonCombinableAnnotations rule = new NonCombinableAnnotations();
        // added deprecated just to test 3 instead of 2
        rule.setProperty(NonCombinableAnnotations.ANNOTATION_NAMES_DESCRIPTOR, new String[] { "Parallel", "Sequential",
                "Deprecated" });
        Report report = runPmdTest(ValidAnnotated.class, rule);
        assertReportViolations(report, 0);
        report = runPmdTest(InvalidAnnotated.class, rule);
        assertReportViolations(report, 5);
    }

}
