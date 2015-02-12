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
package org.test.testclasses.page;

import java.sql.Connection;

import org.aludratest.service.gui.web.page.Page;

public class InvalidPage extends Page {

    public String someExposedField;

    public InvalidPage() {
        super(null);
    }

    @SuppressWarnings("unused")
    public Page somePageMethod() {
        Connection conn = null;
        return this;
    }

    public void someIllegalPageMethod() {
    }

    public String someAlsoIllegalPageMethod() {
        return null;
    }

    @Override
    protected void checkCorrectPage() {

    }

}
