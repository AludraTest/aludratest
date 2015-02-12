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

import static org.junit.Assert.assertEquals;

import org.aludratest.service.gui.web.AludraWebGUI;
import org.aludratest.testcase.TestStatus;
import org.aludratest.util.validator.ContainsIgnoreCaseValidator;
import org.junit.Test;

/**
 * Tests label features of {@link AludraWebGUI} services.
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public abstract class AbstractLabelTest extends GUITest {

    @Test
    public void getText() {
        assertEquals("READY", guiTestUIMap.label().getText());
    }

    @Test
    public void equalsIgnoreCaseTrimmed() {
        // positive test
        guiTestUIMap.label().assertTextEqualsIgnoreCaseTrimmed(" \trEaDy\r\n ");
        checkLastStepStatus(TestStatus.PASSED);
        // negative test
        guiTestUIMap.label().assertTextEqualsIgnoreCaseTrimmed("WAITING");
        checkLastStepStatus(TestStatus.FAILED);
    }

    @Test
    public void textMatches() {
        // positive test
        guiTestUIMap.label().assertTextMatches(new ContainsIgnoreCaseValidator("rEaDy"));
        checkLastStepStatus(TestStatus.PASSED);
        // negative test
        guiTestUIMap.label().assertTextMatches(new ContainsIgnoreCaseValidator("WAITING"));
        checkLastStepStatus(TestStatus.FAILED);
    }

    @Test
    public void equals() {
        // positive test
        guiTestUIMap.label().assertTextEquals("READY");
        checkLastStepStatus(TestStatus.PASSED);
        // negative test
        guiTestUIMap.label().assertTextEquals("ready");
        checkLastStepStatus(TestStatus.FAILED);
    }

    @Test
    public void notEquals() {
        // positive test
        guiTestUIMap.label().assertTextNotEquals("ready");
        checkLastStepStatus(TestStatus.PASSED);
        // negative test
        guiTestUIMap.label().assertTextNotEquals("READY");
        checkLastStepStatus(TestStatus.FAILED);
    }

    @Test
    public void contains() {
        // positive test - identical
        guiTestUIMap.label().assertTextContains("READY");
        checkLastStepStatus(TestStatus.PASSED);
        // positive test - part
        guiTestUIMap.label().assertTextContains("EAD");
        checkLastStepStatus(TestStatus.PASSED);
        // negative test - capitalization
        guiTestUIMap.label().assertTextContains("ready");
        checkLastStepStatus(TestStatus.FAILED);
    }

}
