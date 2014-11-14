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
import org.aludratest.codecheck.rule.pmd.data.DataClassSimpleNameMustBeUnique;
import org.junit.Test;
import org.test.testclasses.data.CompliantNoData;
import org.test.testclasses.data.NonCompliantData;

public class DataClassSimpleNameMustBeUniqueTest extends AbstractPmdTestCase {

    @Test
    public void testDoubleClassName() {
        Report report = runPmdTest(new Class<?>[] { NonCompliantData.class,
                org.test.testclasses.data.duplicate.NonCompliantData.class }, new DataClassSimpleNameMustBeUnique());

        // only second class will cause violation by now
        assertReportViolations(report, 1);
    }

    @Test
    public void testNoDataClassDoubleClassName() {
        Report report = runPmdTest(new Class<?>[] { CompliantNoData.class,
                org.test.testclasses.data.duplicate.CompliantNoData.class
        }, new DataClassSimpleNameMustBeUnique());

        assertReportViolations(report, 0);
    }


}
