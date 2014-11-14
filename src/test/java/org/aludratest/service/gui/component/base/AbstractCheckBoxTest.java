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

import org.aludratest.testcase.TestStatus;
import org.junit.Test;

/**
 * Unit test for class {@link org.aludratest.service.gui.component.Checkbox}
 * @author ywang
 */
@SuppressWarnings("javadoc")
public abstract class AbstractCheckBoxTest extends GUITest {
    /**
     *  positive case to test method {@link org.aludratest.service.gui.component.Checkbox#select()}
     *  <br/> select first check box
     */
    @Test
    public void selectOnEnabledCheckBox() {
        guiTestUIMap.firstCheckBox().select();
        checkLastStepStatus(TestStatus.PASSED);
        guiTestUIMap.firstCheckBox().assertChecked();
        checkLastStepStatus(TestStatus.PASSED);
    }

    /**
     *  positive case to test method {@link org.aludratest.service.gui.component.Checkbox#deselect()}
     *  <br/> deselect first check box
     */
    @Test
    public void deselectOnEnabledCheckBox() {
        guiTestUIMap.firstCheckBox().select();
        // deselect
        guiTestUIMap.firstCheckBox().deselect();
        checkLastStepStatus(TestStatus.PASSED);
        guiTestUIMap.firstCheckBox().assertChecked("false");
        checkLastStepStatus(TestStatus.PASSED);
    }

    /**
     *  positive case to test method {@link org.aludratest.service.gui.component.Checkbox#select(boolean)}
     *  <br/> select and deselct first check box (with value)
     */
    @Test
    public void selectWithValueOnEnabledCheckBox() {
        // select
        guiTestUIMap.firstCheckBox().select("true");
        checkLastStepStatus(TestStatus.PASSED);
        guiTestUIMap.firstCheckBox().assertChecked("true");
        checkLastStepStatus(TestStatus.PASSED);
        // deselect
        guiTestUIMap.firstCheckBox().select("false");
        checkLastStepStatus(TestStatus.PASSED);
        guiTestUIMap.firstCheckBox().assertChecked("false");
        checkLastStepStatus(TestStatus.PASSED);
    }

    /**
     *  positive case to test method {@link org.aludratest.service.gui.component.Checkbox#assertChecked()}
     *  <br/> check the checked status of the second checkbox
     */
    @Test
    public void isCheckedOnCheckedCheckBox() {
        guiTestUIMap.secondCheckBox().assertChecked();
        checkLastStepStatus(TestStatus.PASSED);
    }

    /**
     *  false case to test method {@link org.aludratest.service.gui.component.Checkbox#assertChecked()}
     *  <br/> check the unchecked status of the first checkbox
     */
    @Test
    public void isCheckedOnUncheckedCheckBox() {
        guiTestUIMap.firstCheckBox().assertChecked();
        checkLastStepStatus(TestStatus.FAILED);
        checkLastStepErrorMessage("Checkbox or Radiobutton is unchecked");
    }

    /**
     *  positive case to test method {@link org.aludratest.service.gui.component.Checkbox#assertChecked(boolean)}
     *  <br/> check the checked status of the second checkbox
     */
    @Test
    public void isCheckedWithValueTrueOnCheckedCheckBox() {
        guiTestUIMap.secondCheckBox().assertChecked("true");
        checkLastStepStatus(TestStatus.PASSED);
    }

    /**
     *  positive case to test method {@link org.aludratest.service.gui.component.Checkbox#assertChecked(boolean)}
     *  <br/> check the unchecked status of the first checkbox
     */
    @Test
    public void isCheckedWithValueFalseOnUncheckedCheckBox() {
        guiTestUIMap.firstCheckBox().assertChecked("false");
        checkLastStepStatus(TestStatus.PASSED);
    }

    /**
     *  false case to test method {@link org.aludratest.service.gui.component.Checkbox#assertChecked(boolean)}
     *  <br/> check the unchecked status of the first checkbox
     */
    @Test
    public void isCheckedWithValueTrueOnUnCheckedCheckBox() {
        guiTestUIMap.firstCheckBox().assertChecked("true");
        checkLastStepStatus(TestStatus.FAILED);
        checkLastStepErrorMessage("Checkbox or Radiobutton is unchecked");
    }

    /**
     *  false case to test method {@link org.aludratest.service.gui.component.Checkbox#assertChecked(boolean)}
     *  <br/> check the checked status of the second checkbox
     */
    @Test
    public void isCheckedWithValueFalseOnCheckedCheckBox() {
        guiTestUIMap.secondCheckBox().assertChecked("false");
        checkLastStepStatus(TestStatus.FAILED);
        checkLastStepErrorMessage("Checkbox or Radiobutton is checked");
    }

    /**
     *  positive case to test method {@link org.aludratest.service.gui.component.Checkbox#isEnabled()}
     *  <br/> check the enabled status of the second checkbox
     */
    @Test
    public void enabledOnEnabledCheckBox() {
        guiTestUIMap.secondCheckBox().isEnabled();
        checkLastStepStatus(TestStatus.PASSED);
    }

    /**
     *  negative case to test method {@link org.aludratest.service.gui.component.Checkbox#isEnabled()}
     *  <br/> check the enabled status of the disabled checkbox
     */
    @Test
    public void enabledOnDisabledCheckBox() {
        assertEquals(false, guiTestUIMap.disabledCheckBox().isEnabled());
    }

    /**
     *  positive case to test method {@link org.aludratest.service.gui.component.Checkbox#isEnabled(long)}
     *  <br/> check the enabled status of the second checkbox with timeout
     */
    @Test
    public void enabledOnEnabledCheckBoxWithTimeout() {
        guiTestUIMap.secondCheckBox().isEnabled(DEFAULT_TIMEOUT);
        checkLastStepStatus(TestStatus.PASSED);
    }

    /**
     *  negative case to test method {@link org.aludratest.service.gui.component.Checkbox#isEnabled(long)}
     *  <br/> check the enabled status of the disabled checkbox with timeout
     */
    @Test
    public void enabledOnDisabledCheckBoxWithTimeout() {
        assertEquals(false, guiTestUIMap.disabledCheckBox().isEnabled(DEFAULT_TIMEOUT));
    }

}
