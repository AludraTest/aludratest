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

import org.aludratest.codecheck.rule.pmd.AbstractPmdTestCase;
import org.aludratest.codecheck.rule.pmd.data.DataClassImplementToString;
import org.junit.Test;
import org.test.testclasses.data.NoToStringData;
import org.test.testclasses.data.ToStringInParentData;

public class DataClassImplementToStringTest extends AbstractPmdTestCase {

    @Test
    public void testNoToString() {
        Report report = runPmdTest(NoToStringData.class, new DataClassImplementToString());
        assertReportViolations(report, 1);
    }

    @Test
    public void testToStringInParentClass() {
        // toString() is implemented in parent class (in same file!); yet should
        // issue violation
        // This test also tests that valid classes do not issue violations, as
        // parent class
        // (ToStringInParentData.class) does NOT cause an additional violation
        // (count == 1)
        Report report = runPmdTest(ToStringInParentData.class, new DataClassImplementToString());
        assertReportViolations(report, 1);
        assertReportViolationLine(report, 0, 35);
    }

}
