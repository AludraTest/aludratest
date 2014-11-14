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
package org.test.testclasses.data;

import java.util.Date;

import org.aludratest.dict.Data;
import org.aludratest.service.gui.web.page.Page;

/**
 * Non-compliant DataClass implementation.
 * @author Volker Bergmann
 */
public class NonCompliantData extends Data {

    /** Illegal reference to a Page object. */
    public Page page;

    /** Another illegal field */
    private Date anyDate;

    private NoToStringData data3;

    private static String ILLEGAL_STATIC_FIELD;

    public NoToStringData getData3() {
        return data3;
    }

    public void setData3(NoToStringData data3) {
        this.data3 = data3;
    }
}
