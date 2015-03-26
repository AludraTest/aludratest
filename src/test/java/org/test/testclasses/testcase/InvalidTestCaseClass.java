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
package org.test.testclasses.testcase;

import java.sql.Date;

import org.aludratest.service.gui.component.Button;
import org.aludratest.service.gui.component.GUIComponent;
import org.aludratest.service.gui.web.page.Page;
import org.aludratest.service.gui.web.uimap.UIMap;
import org.aludratest.testcase.After;
import org.aludratest.testcase.AludraTestCase;
import org.aludratest.testcase.Before;
import org.test.testclasses.page.InvalidPage;
import org.test.testclasses.uimap.ConstructingUIMap;

public class InvalidTestCaseClass extends AludraTestCase {

    @SuppressWarnings("unused")
    public void test() {
        // cause some bad imports
        Date dt = new Date(1);
        UIMap uiMap = new ConstructingUIMap(null);
        GUIComponent component = null;
        Button button = null;

        Page pg = new InvalidPage();
    }

    @Before
    @After
    public void doThisAlways() {
    }

}
