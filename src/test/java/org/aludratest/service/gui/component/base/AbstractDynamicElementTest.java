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
package org.aludratest.service.gui.component.base;

import org.aludratest.service.gui.web.AludraWebGUI;
import org.aludratest.testcase.TestStatus;
import org.aludratest.util.validator.ContainsIgnoreCaseValidator;
import org.junit.Test;

/**
 * Tests dynamic and polling features of {@link AludraWebGUI} services.
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public abstract class AbstractDynamicElementTest extends GUITest {

    @Test
    public void buttonTextMatches() {
        guiTestUIMap.findButton().assertTextMatches(new ContainsIgnoreCaseValidator("found"));
        checkLastStepStatus(TestStatus.PASSED);
    }

    @Test
    public void dropDownBoxSelectionMatches() {
        guiTestUIMap.dropDownBox().assertSelectionMatches(new ContainsIgnoreCaseValidator("City"));
        checkLastStepStatus(TestStatus.PASSED);
    }

    @Test
    public void labelTextMatches() {
        // positive test
        guiTestUIMap.label().assertTextMatches(new ContainsIgnoreCaseValidator("done"));
        checkLastStepStatus(TestStatus.PASSED);
    }

    @Override
    protected String getTestPageUrl() {
        return "http://localhost:8080/dynamic.html";
    }

}
