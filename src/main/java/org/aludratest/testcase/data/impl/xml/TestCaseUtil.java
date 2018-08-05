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
package org.aludratest.testcase.data.impl.xml;

import java.util.List;

import org.aludratest.testcase.data.TestCaseData;

/** Provides utility methods for test case handling.
 * @author Volker Bergmann */
public class TestCaseUtil {

    private TestCaseUtil() {
    }

    /** Creates a unique number for a test data set. */
    public static String getNextAutoId(List<TestCaseData> dataSets, boolean error) {
        String prefix = (error ? "error-" : "");
        int nextAutoId = dataSets.size();
        boolean found;
        do {
            found = false;
            for (TestCaseData tcd : dataSets) {
                if (tcd.getId().equals(prefix + nextAutoId)) {
                    found = true;
                    break;
                }
            }
            if (found) {
                nextAutoId++;
            }
        }
        while (found);
        return prefix + nextAutoId;
    }

}
