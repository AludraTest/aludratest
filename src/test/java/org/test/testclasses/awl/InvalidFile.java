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
package org.test.testclasses.awl;

import org.aludratest.service.file.File;
import org.aludratest.testcase.AludraTestCase;
import org.test.testclasses.data.NonCompliantData;
import org.test.testclasses.data.ToStringInParentData;
import org.test.testclasses.page.InvalidPage;
import org.test.testclasses.page.MyPageHelper;

public class InvalidFile extends File {

    private static int illegalField;

    private static final int legalField = 1;

    private AludraTestCase someTest;

    private MyPageHelper invalidUsage;

    private InvalidPage invalidPageUsage;

    public InvalidFile() {
        super(null, null);
    }

    public String invalidMethod() {
        return null;
    }

    public File validMethod() {
        return this;
    }

    public void invalidVoidMethod() {
    }

    public File invalidMethodBecauseOfParam(String invalidParam) {
        return this;
    }

    public File invalidMethodBecauseTooMuchParams(ToStringInParentData data1, NonCompliantData data2) {
        return this;
    }

}
