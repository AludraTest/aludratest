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

import org.aludratest.service.gui.component.Button;
import org.aludratest.service.gui.web.AludraWebGUI;
import org.aludratest.service.gui.web.uimap.UIMap;
import org.aludratest.service.locator.element.XPathLocator;
import org.test.testclasses.uimap.helper.ValidUIMapHelper;
import org.test.testclasses.uimap.helper.ValidUIMapUtility;

public class ConstructingUIMap extends UIMap {

    public ConstructingUIMap(AludraWebGUI aludraTest) {
        super(aludraTest);
    }

    public Button giveMeAButton() {
        ValidUIMapHelper.class.getName();
        ValidUIMapUtility.class.getName();
        return new Button(aludraGUI, new XPathLocator("/"));
    }

}
