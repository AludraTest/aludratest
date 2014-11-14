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

import org.aludratest.service.AludraService;
import org.aludratest.service.ComponentId;
import org.aludratest.service.file.FileService;
import org.aludratest.service.gui.web.page.Page;
import org.aludratest.testcase.AludraTestCase;
import org.test.testclasses.page.InvalidPage;

public class ValidTestCaseClass extends AludraTestCase {

    @SuppressWarnings("unused")
    public void test() {
        Page pg = new InvalidPage();
        pg.verifyState();

        AludraService svc = getService(ComponentId.create(FileService.class, "aludra"));
    }

}
