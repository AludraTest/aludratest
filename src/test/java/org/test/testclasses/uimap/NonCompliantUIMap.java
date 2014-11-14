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
package org.test.testclasses.uimap;

import org.aludratest.dict.Data;
import org.aludratest.service.gui.web.AludraWebGUI;
import org.aludratest.service.gui.web.uimap.UIMap;
import org.test.testclasses.page.ValidPageUtility;
import org.test.testclasses.uimap.helper.ValidUIMapHelper;

/**
 * Non-compliant UIMap implementation.
 * @author Volker Bergmann
 */
public class NonCompliantUIMap extends UIMap {

    public int someState;

    public Data data;

    public NonCompliantUIMap(AludraWebGUI aludraTest) {
        super(aludraTest);
    }

    public void someMethod() {
        ValidUIMapHelper.class.getName();
        ValidPageUtility.someMethod();
    }

    public String someOtherMethod() {
        return "";
    }


}
