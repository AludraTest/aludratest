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

import org.aludratest.service.gui.component.InputField;
import org.aludratest.service.gui.web.AludraWebGUI;
import org.aludratest.testcase.TestStatus;
import org.junit.Test;

/**
 * Tests {@link InputField}-related features of {@link AludraWebGUI} services.
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public abstract class AbstractInputFieldTest extends GUITest {

    @Test
    public void enter_textEquals() {
        guiTestUIMap.textField().assertTextEquals("");
        checkLastStepStatus(TestStatus.PASSED);
        guiTestUIMap.textField().enter("MyTest");
        guiTestUIMap.textField().assertTextEquals("MyTest");
        checkLastStepStatus(TestStatus.PASSED);
        guiTestUIMap.textField().assertTextEquals("mytest");
        checkLastStepStatus(TestStatus.FAILED);
    }

    @Test
    public void getText() {
        assertEquals("", guiTestUIMap.textField().getText());
        checkLastStepStatus(TestStatus.PASSED);
        guiTestUIMap.textField().enter("MyTest");
        assertEquals("MyTest", guiTestUIMap.textField().getText());
        checkLastStepStatus(TestStatus.PASSED);
    }

    @Test
    public void textContainsIgnoreCaseTrimmed() {
        guiTestUIMap.textField().assertTextEquals("");
        checkLastStepStatus(TestStatus.PASSED);
        guiTestUIMap.textField().enter("sdfmytestoi");
        // positive test
        guiTestUIMap.textField().assertTextContainsIgnoreCaseTrimmed("  \tMyTest\r\n ");
        checkLastStepStatus(TestStatus.PASSED);
        // negative test
        guiTestUIMap.textField().assertTextContainsIgnoreCaseTrimmed("  MyTestxxx ");
        checkLastStepStatus(TestStatus.FAILED);
    }

    @Test
    public void textEquals() {
        guiTestUIMap.textField().assertTextEquals("");
        checkLastStepStatus(TestStatus.PASSED);
        guiTestUIMap.textField().enter("MyTest");
        // psoitive test
        guiTestUIMap.textField().assertTextEquals("MyTest");
        checkLastStepStatus(TestStatus.PASSED);
        // negative test
        guiTestUIMap.textField().assertTextEquals("mytest");
        checkLastStepStatus(TestStatus.FAILED);
    }

    @Test
    public void textContains() {
        guiTestUIMap.textField().assertTextEquals("");
        checkLastStepStatus(TestStatus.PASSED);
        guiTestUIMap.textField().enter("MyTest");
        // psoitive test
        guiTestUIMap.textField().assertTextContains("MyTest");
        checkLastStepStatus(TestStatus.PASSED);
        guiTestUIMap.textField().assertTextContains("yTes");
        checkLastStepStatus(TestStatus.PASSED);
        // negative test
        guiTestUIMap.textField().assertTextContains("mytest");
        checkLastStepStatus(TestStatus.FAILED);
    }

    @Test
    public void notEquals() {
        guiTestUIMap.textField().assertTextEquals("");
        checkLastStepStatus(TestStatus.PASSED);
        guiTestUIMap.textField().enter("MyTest");
        // positive test
        guiTestUIMap.textField().assertTextNotEquals("mytest");
        checkLastStepStatus(TestStatus.PASSED);
        // negative test
        guiTestUIMap.textField().assertTextNotEquals("MyTest");
        checkLastStepStatus(TestStatus.FAILED);
    }

    @Test
    public void valueGreaterThan() {
        guiTestUIMap.textField().assertTextEquals("");
        checkLastStepStatus(TestStatus.PASSED);
        guiTestUIMap.textField().enter("4711");
        // positive test
        guiTestUIMap.textField().assertValueGreaterThan("0");
        checkLastStepStatus(TestStatus.PASSED);
        // negative test
        guiTestUIMap.textField().assertValueGreaterThan("4712");
        checkLastStepStatus(TestStatus.FAILED);
    }

    @Test
    public void valueLessThan() {
        guiTestUIMap.textField().assertTextEquals("");
        checkLastStepStatus(TestStatus.PASSED);
        guiTestUIMap.textField().enter("4711");
        // positive test
        guiTestUIMap.textField().assertValueLessThan("4712");
        checkLastStepStatus(TestStatus.PASSED);
        // negative test
        guiTestUIMap.textField().assertValueLessThan("4710");
        checkLastStepStatus(TestStatus.FAILED);
    }

    @Test
    public void enterWholeField() {
        guiTestUIMap.textField().assertTextEquals("");
        checkLastStepStatus(TestStatus.PASSED);
        guiTestUIMap.textField().enter("4711");
        // positive test
        guiTestUIMap.textField().assertTextEquals("4711");
        checkLastStepStatus(TestStatus.PASSED);
        // next enter should enter WHOLE field
        guiTestUIMap.textField().enter("4712");
        guiTestUIMap.textField().assertTextEquals("4712");
        checkLastStepStatus(TestStatus.PASSED);
    }

    @Test
    public void enterWholeField_noId() {
        guiTestUIMap.noidTextField().assertTextEquals("");
        checkLastStepStatus(TestStatus.PASSED);
        guiTestUIMap.noidTextField().enter("4711");
        // positive test
        guiTestUIMap.noidTextField().assertTextEquals("4711");
        checkLastStepStatus(TestStatus.PASSED);
        // next enter should enter WHOLE field
        guiTestUIMap.noidTextField().enter("4712");
        guiTestUIMap.noidTextField().assertTextEquals("4712");
        checkLastStepStatus(TestStatus.PASSED);
    }

    @Test
    public void testAssertNotEditable() {
        guiTestUIMap.readonlyTextField().assertNotEditable();
        checkLastStepStatus(TestStatus.PASSED);
        guiTestUIMap.disabledTextField().assertNotEditable();
        checkLastStepStatus(TestStatus.PASSED);
        guiTestUIMap.readonlyTextField().assertEditable();
        checkLastStepStatus(TestStatus.FAILED);
    }

    @Test
    public void testAssertEditable() {
        guiTestUIMap.textField().assertEditable();
        checkLastStepStatus(TestStatus.PASSED);
        guiTestUIMap.readonlyTextField().assertEditable();
        checkLastStepStatus(TestStatus.FAILED);
    }

}
