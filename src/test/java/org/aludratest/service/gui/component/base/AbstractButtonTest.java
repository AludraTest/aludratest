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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.aludratest.testcase.TestStatus;
import org.aludratest.util.validator.ContainsIgnoreCaseValidator;
import org.junit.Test;

/**
 * Unit test for class {@link org.aludratest.service.gui.component.Button}
 * @author ywang
 */
@SuppressWarnings("javadoc")
public abstract class AbstractButtonTest extends GUITest {

    private final String TEXT_ON_BUTTON = "Find";

    /**
     *  positive case to test method {@link org.aludratest.service.gui.component.Button#equals(String)}
     *  <br/> for button with value "Find"
     *  <br/> to check IgnoreCaseTrimmed text
     */
    @Test
    public void assertTextEqualsIgnoreCaseTrimmedWithCorrectText() {
        guiTestUIMap.findButton().assertTextEqualsIgnoreCaseTrimmed("Find");
        checkLastStepStatus(TestStatus.PASSED);
        guiTestUIMap.findButton().assertTextEqualsIgnoreCaseTrimmed("find");
        checkLastStepStatus(TestStatus.PASSED);
        guiTestUIMap.findButton().assertTextEqualsIgnoreCaseTrimmed(" find");
        checkLastStepStatus(TestStatus.PASSED);
        guiTestUIMap.findButton().assertTextEqualsIgnoreCaseTrimmed("find ");
        checkLastStepStatus(TestStatus.PASSED);
        guiTestUIMap.findButton().assertTextEqualsIgnoreCaseTrimmed(" find ");
        checkLastStepStatus(TestStatus.PASSED);
    }

    /**
     *  negative case to test method {@link org.aludratest.service.gui.component.Button#equals(String)}
     *  <br/> for button with value "Find"
     *  <br/> to check text with different length
     */
    @Test
    public void equalsWithDifferentLength() {
        guiTestUIMap.findButton().assertTextEqualsIgnoreCaseTrimmed(TEXT_ON_BUTTON + "1");
        checkLastStepStatus(TestStatus.FAILED);
        checkLastStepErrorMessage("Value 'Find' does not match the validator EqualsIgnoreCaseTrimmedValidator "
                + "(expecting 'Find1' after trimming, ignoring case)");
    }

    /**
     *  negative case to test method {@link org.aludratest.service.gui.component.Button#equals(String)}
     *  <br/> for button with value "Find"
     *  <br/> to check text with a blank in it
     */
    @Test
    public void equalsWithSpaceInMiddle() {
        guiTestUIMap.findButton().assertTextEqualsIgnoreCaseTrimmed("Fi nd");
        checkLastStepStatus(TestStatus.FAILED);
        checkLastStepErrorMessage("Value 'Find' does not match the validator "
                + "EqualsIgnoreCaseTrimmedValidator (expecting 'Fi nd' after trimming, ignoring case)");
    }

    /**
     *  positive case to test method {@link org.aludratest.service.gui.component.Button#isEnabled()}
     *  find button is enabled
     */
    @Test
    public void enabledOnEnabledButton() {
        assertTrue(guiTestUIMap.findButton().isEnabled());
        checkLastStepStatus(TestStatus.PASSED);
    }

    /**
     *  negative case to test method {@link org.aludratest.service.gui.component.Button#isEnabled()}
     *  disabled button is not enabled
     */
    @Test
    public void enabledOnDisabledButton() {
        assertFalse(guiTestUIMap.disabledButton().isEnabled());
        checkLastStepStatus(TestStatus.PASSED);
    }

    /**
     *  positive case to test method {@link org.aludratest.service.gui.component.Button#isEnabled(long)}
     *  find button is enabled (with default timeout)
     */
    @Test
    public void enabledOnEnabledButtonWithTimeout() {
        assertTrue(guiTestUIMap.findButton().isEnabled(DEFAULT_TIMEOUT));
        checkLastStepStatus(TestStatus.PASSED);
    }

    /**
     *  negative case to test method {@link org.aludratest.service.gui.component.Button#isEnabled(long)}
     *  disabled button is not enabled (with default timeout)
     */
    @Test
    public void enabledOnDisabledButtonWithTimeout() {
        assertFalse(guiTestUIMap.disabledButton().isEnabled(DEFAULT_TIMEOUT));
        checkLastStepStatus(TestStatus.PASSED);
    }

    @Test
    public void textMatches() {
        guiTestUIMap.findButton().assertTextMatches(new ContainsIgnoreCaseValidator("Find"));
        checkLastStepStatus(TestStatus.PASSED);
        guiTestUIMap.findButton().assertTextMatches(new ContainsIgnoreCaseValidator("Fi nd"));
        checkLastStepStatus(TestStatus.FAILED);
    }

}
