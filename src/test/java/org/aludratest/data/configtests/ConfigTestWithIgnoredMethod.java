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
package org.aludratest.data.configtests;

import org.aludratest.data.PersonBean;
import org.aludratest.testcase.Ignored;
import org.aludratest.testcase.Test;
import org.aludratest.testcase.data.Source;

/** Tests the default Excel test data parser with a test case that uses an Excel sheet with a properly formatted 'config' tab and
 * 'testConfiguration' column and a method that has been annotated as {@link org.aludratest.testcase.Ignored}.
 * @author Volker Bergmann */
@SuppressWarnings("javadoc")
public class ConfigTestWithIgnoredMethod extends AbstractConfigTest {

    @Test
    @Ignored
    public void test(@Source(uri = "ignored_method.xls", segment = "persons") PersonBean person) {
        verifyPerson(person);
    }

}
