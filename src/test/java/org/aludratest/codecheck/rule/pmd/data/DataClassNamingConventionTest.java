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
package org.aludratest.codecheck.rule.pmd.data;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RulePriority;

import org.aludratest.codecheck.rule.pmd.AbstractPmdTestCase;
import org.aludratest.codecheck.rule.pmd.data.DataClassNamingConvention;
import org.junit.Test;
import org.test.testclasses.data.InvalidDataName;
import org.test.testclasses.data.NoToStringData;

public class DataClassNamingConventionTest extends AbstractPmdTestCase {

    @Test
    public void testInvalidName() {
        Report report = runPmdTest(InvalidDataName.class, new DataClassNamingConvention());
        assertReportViolations(report, 1);
        assertReportViolationPriority(report, 0, RulePriority.MEDIUM_HIGH);
    }

    @Test
    public void testValidName() {
        Report report = runPmdTest(NoToStringData.class, new DataClassNamingConvention());
        assertReportViolations(report, 0);
    }

}
