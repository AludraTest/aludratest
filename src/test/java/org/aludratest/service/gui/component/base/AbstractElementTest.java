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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.aludratest.service.gui.web.AludraWebGUI;
import org.aludratest.service.locator.element.GUIElementLocator;
import org.aludratest.service.locator.element.IdLocator;
import org.aludratest.testcase.TestStatus;
import org.junit.Test;

/**
 * Unit test for class {@link org.aludratest.service.gui.component.Element}
 * @author ywang
 */
@SuppressWarnings("javadoc")
public abstract class AbstractElementTest extends GUITest {

    @Test
    public void isEnabledOnEnabledElement() {
        guiTestUIMap.dropDownBox().assertEnabled();
        checkLastStepStatus(TestStatus.PASSED);
    }

    @Test
    public void isEnabledOnDisabledElement() {
        guiTestUIMap.disabledTextField().assertEnabled();
        checkLastStepStatus(TestStatus.FAILED);
        checkLastStepErrorMessage("Element not enabled");
    }

    @Test
    public void isNotEnabledOnDisabledElement() {
        guiTestUIMap.disabledTextField().assertNotEnabled();
        checkLastStepStatus(TestStatus.PASSED);
    }

    @Test
    public void isNotEnabledOnEnabledElement() {
        guiTestUIMap.dropDownBox().assertNotEnabled();
        checkLastStepStatus(TestStatus.FAILED);
        checkLastStepErrorMessage("Element not expected to be enabled");
    }

    /**
     * positive case to test method {@link org.aludratest.service.gui.component.Element#assertPresent()}
     * <br/> dropdown box is present
     */
    @Test
    public void assertPresentOnPresentElement() {
        guiTestUIMap.dropDownBox().assertPresent();
        checkLastStepStatus(TestStatus.PASSED);
    }

    /**
     * negative case to test method {@link org.aludratest.service.gui.component.Element#assertPresent()}
     * <br/> a not existing element is not present
     */
    @Test
    public void assertPresentOnNotExistingElement() {
        guiTestUIMap.notExistingButton().assertPresent();
        checkLastStepStatus(TestStatus.FAILED);
        checkLastStepErrorMessageMatches("Element not found.*");
    }

    /**
     * positive case to test method {@link org.aludratest.service.gui.component.Element#assertNotPresent()}
     * <br/> a not existing element is not present
     */
    @Test
    public void isNotPresentOnNotExistingElement() {
        guiTestUIMap.notExistingButton().assertNotPresent();
        checkLastStepStatus(TestStatus.PASSED);
    }

    /**
     * negative case to test method {@link org.aludratest.service.gui.component.Element#assertNotPresent()}
     * <br/> dropdown box could be found
     */
    @Test
    public void isNotPresentOnPresentElement() {
        guiTestUIMap.dropDownBox().assertNotPresent();
        checkLastStepStatus(TestStatus.FAILED);
        checkLastStepErrorMessage("An element was unexpectedly found");
    }

    /**
     * positive case to test method {@link org.aludratest.service.gui.component.Element#assertVisible()}
     * <br/> find button is visible
     */
    @Test
    public void isVisibleOnVisibleElement() {
        guiTestUIMap.findButton().assertVisible();
        checkLastStepStatus(TestStatus.PASSED);
    }

    /**
     * negative case to test method {@link org.aludratest.service.gui.component.Element#assertVisible()}
     * <br/> hidden button is not visible
     */
    @Test
    public void isVisibleOnHiddenElement() {
        guiTestUIMap.hiddenButton().assertVisible();
        checkLastStepStatus(TestStatus.FAILED);
        checkLastStepErrorMessage("The element is not visible.");
    }

    /**
     * negative case to test method {@link org.aludratest.service.gui.component.Element#assertVisible()}
     * <br/> hidden button (in DIV) is not visible
     */
    @Test
    public void isVisibleOnHiddenElementInDiv() {
        guiTestUIMap.hiddenDivButton().assertVisible();
        checkLastStepStatus(TestStatus.FAILED);
        checkLastStepErrorMessage("The element is not visible.");
    }

    @Test
    public void assertNotVisibleOnHiddenElement() {
        guiTestUIMap.hiddenButton().assertNotVisible();
        checkLastStepStatus(TestStatus.PASSED);
    }

    @Test
    public void assertNotVisibleOnVisibleElement() {
        guiTestUIMap.findButton().assertNotVisible();
        checkLastStepStatus(TestStatus.FAILED);
        checkLastStepErrorMessage("The element is unexpectedly visible.");
    }

    /**
     * positive case to test method {@link org.aludratest.service.gui.component.Element#assertFocus()}
     * <br/> test focus text field,
     * only click on it is not enough to get the focus
     */
    @Test
    public void hasFocusOnFocusedElement() {
        guiTestUIMap.textField().focus();
        guiTestUIMap.textField().assertFocus();
        checkLastStepStatus(TestStatus.PASSED);
    }

    /**
     * negative case to test method {@link org.aludratest.service.gui.component.Element#assertFocus()}
     * <br/> text field does not have focus by default
     */
    @Test
    public void hasFocusOnNotFocusedElement() {
        guiTestUIMap.textField().assertFocus();
        checkLastStepStatus(TestStatus.FAILED);
        checkLastStepErrorMessage("The element does not have the focus.");
    }

    /**
     * positive case to test method {@link org.aludratest.service.gui.component.Element#focus()}
     * <br/> test focus on first check box
     */
    @Test
    public void focusOnEnabledElement() {
        guiTestUIMap.firstCheckBox().focus();
        guiTestUIMap.firstCheckBox().assertFocus();
        checkLastStepStatus(TestStatus.PASSED);
    }

    /**
     * negative case to test method {@link org.aludratest.service.gui.component.Element#focus()}
     * <br/> disabled text field can not get focus by calling the method focus()
     */
    @Test
    public void focusOnDisabledElement() {
        guiTestUIMap.disabledTextField().focus();
        checkLastStepStatus(TestStatus.FAILEDAUTOMATION);
        checkLastStepErrorMessage("Element not enabled");
    }

    /**
     * positive case to test method {@link org.aludratest.service.gui.component.Element#doubleClick()}
     * <br/> double click on dropdown box
     */
    @Test
    public void doubleClickOnEditableElementDropDownBox() {
        guiTestUIMap.dropDownBox().doubleClick();
        checkLastStepStatus(TestStatus.PASSED);
    }

    /**
     * positive case to test method {@link org.aludratest.service.gui.component.Element#doubleClick()}
     * <br/> double click on image button
     */
    @Test
    public void doubleClickOnEditableElementImageButton() {
        guiTestUIMap.imageButton().doubleClick();
        checkLastStepStatus(TestStatus.PASSED);
    }

    @Test
    public void doubleClickOnNotEditableElementButton() {
        guiTestUIMap.disabledButton().doubleClick();
        checkLastStepStatus(TestStatus.PASSED);
    }

    @Test
    public void doubleClickOnNotEditableElementImage() {
        guiTestUIMap.image().doubleClick();
        checkLastStepStatus(TestStatus.PASSED);
    }

    @Test
    public void doubleClickOnNotEditableElementLabel() {
        guiTestUIMap.label().doubleClick();
        checkLastStepStatus(TestStatus.PASSED);
    }

    /**
     * positive case to test method {@link org.aludratest.service.gui.component.Element#click()}
     * <br/> click on or radio button
     */
    @Test
    public void clickOnEditableElementRadioButton() {
        guiTestUIMap.orRadioButton().click();
        checkLastStepStatus(TestStatus.PASSED);
        guiTestUIMap.orRadioButton().assertChecked();
        checkLastStepStatus(TestStatus.PASSED);
    }

    /**
     * positive case to test method {@link org.aludratest.service.gui.component.Element#click()}
     * <br/> click on image button
     */
    @Test
    public void clickOnNotEditableElementImageButton() {
        guiTestUIMap.imageButton().click();
        checkLastStepStatus(TestStatus.PASSED);
    }

    /** positive case to test method {@link org.aludratest.service.gui.component.Element#click()} <br>
     * click on disabled button */
    @Test
    public void clickOnNotEditableElementButton() {
        guiTestUIMap.disabledButton().click();
        checkLastStepStatus(TestStatus.PASSED);
    }

    /** positive case to test method {@link org.aludratest.service.gui.component.Element#click()} <br>
     * click on image */
    @Test
    public void clickOnNotEditableElementImage() {
        guiTestUIMap.image().click();
        checkLastStepStatus(TestStatus.PASSED);
    }

    /** positive case to test method {@link org.aludratest.service.gui.component.Element#click()} <br/>
     * click on image */
    @Test
    public void clickOnElementLabel() {
        guiTestUIMap.label().click();
        checkLastStepStatus(TestStatus.PASSED);
    }

    /**
     * positive case to test method {@link org.aludratest.service.gui.component.Element#clickNotEditable()}
     * <br/> not-editable-click on dropdown box
     */
    @Test
    public void clickOnEditableElement() {
        guiTestUIMap.dropDownBox().click();
        checkLastStepStatus(TestStatus.PASSED);
    }

    @Test
    public void hoverElement() {
        assertFalse(guiTestUIMap.hoverValue().isVisible());
        guiTestUIMap.hoverText().hover();
        checkLastStepStatus(TestStatus.PASSED);
        assertTrue(guiTestUIMap.hoverValue().isVisible());
    }

    /**
     * positive case to test method {@link org.aludratest.service.gui.component.Element#isPresent()}
     * <br/> dropdown box is present
     */
    @Test
    public void isPresentOnExistingElement() {
        assertTrue(guiTestUIMap.dropDownBox().isPresent());
    }

    /**
     * positive case to test method {@link org.aludratest.service.gui.component.Element#isPresent()}
     * <br/> hidden button is present
     */
    @Test
    public void isPresentOnHiddenElement() {
        assertTrue(guiTestUIMap.hiddenButton().isPresent());
    }

    /**
     * positive case to test method {@link org.aludratest.service.gui.component.Element#isPresent()}
     * <br/> hidden button in div is present
     */
    @Test
    public void isPresentOnHiddenElementInDiv() {
        assertTrue(guiTestUIMap.hiddenButton().isPresent());
    }

    /**
     * negative case to test method {@link org.aludratest.service.gui.component.Element#isPresent()}
     * <br/> a not-existing element is not present
     */
    @Test
    public void isPresentOnNotExistingElement() {
        assertFalse(guiTestUIMap.notExistingButton().isPresent());
    }

    /**
     * positive case to test method {@link org.aludratest.service.gui.component.Element#isPresent(long)}
     * <br/> dropdown box is present (with default timeout)
     */
    @Test
    public void isPresentOnExistingElementWithTimeout() {
        assertTrue(guiTestUIMap.dropDownBox().isPresent(DEFAULT_TIMEOUT));
    }

    /**
     * positive case to test method {@link org.aludratest.service.gui.component.Element#isPresent(long)}
     * <br/> hidden button is present (with default timeout)
     */
    @Test
    public void isPresentOnHiddenElementWithTimeout() {
        assertTrue(guiTestUIMap.hiddenButton().isPresent(DEFAULT_TIMEOUT));
    }

    /**
     * positive case to test method {@link org.aludratest.service.gui.component.Element#isPresent(long)}
     * <br/> hidden button in div is present (with default timeout)
     */
    @Test
    public void isPresentOnHiddenElementInDivWithTimeout() {
        assertTrue(guiTestUIMap.hiddenButton().isPresent(DEFAULT_TIMEOUT));
    }

    /**
     * negative case to test method {@link org.aludratest.service.gui.component.Element#isPresent(long)}
     * <br/> a not-existing element is not present (with default timeout)
     */
    @Test
    public void isPresentOnNotExistingElementWithTimeout() {
        assertFalse(guiTestUIMap.notExistingButton().isPresent(DEFAULT_TIMEOUT));
    }

    /**
     * positive case to test method {@link org.aludratest.service.gui.component.Element#isNotPresent()}
     * <br/> a not-existing element is not present
     */
    @Test
    public void notPresentOnNotExistingElement() {
        assertTrue(guiTestUIMap.notExistingButton().isNotPresent());
    }

    /**
     * negative case to test method {@link org.aludratest.service.gui.component.Element#isNotPresent()}
     * <br/> hidden button is present
     */
    @Test
    public void notPresentOnHiddenElement() {
        assertFalse(guiTestUIMap.hiddenButton().isNotPresent());
    }

    /**
     * negative case to test method {@link org.aludratest.service.gui.component.Element#isNotPresent()}
     * <br/> hidden button in div is present
     */
    @Test
    public void notPresentOnHiddenElementInDiv() {
        assertFalse(guiTestUIMap.hiddenButton().isNotPresent());
    }

    /**
     * negative case to test method {@link org.aludratest.service.gui.component.Element#isNotPresent()}
     * <br/> dropdown box is present
     */
    @Test
    public void notPresentOnExistingElement() {
        assertFalse(guiTestUIMap.dropDownBox().isNotPresent());
    }

    /**
     * positive case to test method {@link org.aludratest.service.gui.component.Element#isNotPresent(long)}
     * <br/> a not-existing element is not present (with default timeout)
     */
    @Test
    public void notPresentOnNotExistingElementWithTimeout() {
        assertTrue(guiTestUIMap.notExistingButton().isNotPresent(DEFAULT_TIMEOUT));
    }

    /**
     * negative case to test method {@link org.aludratest.service.gui.component.Element#isNotPresent(long)}
     * <br/> hidden button is present (with default timeout)
     */
    @Test
    public void notPresentOnHiddenElementWithTimeout() {
        assertFalse(guiTestUIMap.hiddenButton().isNotPresent(DEFAULT_TIMEOUT));
    }

    /**
     * negative case to test method {@link org.aludratest.service.gui.component.Element#isNotPresent(long)}
     * <br/> hidden button in div is present (with default timeout)
     */
    @Test
    public void notPresentOnHiddenElementInDivWithTimeout() {
        assertFalse(guiTestUIMap.hiddenButton().isNotPresent(DEFAULT_TIMEOUT));
    }

    /**
     * negative case to test method {@link org.aludratest.service.gui.component.Element#isNotPresent(long)}
     * <br/> dropdown box is present (with default timeout)
     */
    @Test
    public void notPresentOnExistingElementWithTimeout() {
        assertFalse(guiTestUIMap.dropDownBox().isNotPresent(DEFAULT_TIMEOUT));
    }

    /**
     * positive case to test method {@link org.aludratest.service.gui.component.Element#isVisible()}
     * <br/> dropdown box is visible
     */
    @Test
    public void visibleOnExistingElement() {
        assertTrue(guiTestUIMap.dropDownBox().isVisible());
    }

    /**
     * negative case to test method {@link org.aludratest.service.gui.component.Element#isVisible()}
     * <br/> a not-existing element is not visible
     */
    @Test
    public void visibleOnNotExistingElement() {
        assertFalse(guiTestUIMap.notExistingButton().isVisible());
    }

    /**
     * negative case to test method {@link org.aludratest.service.gui.component.Element#isVisible()}
     * <br/> hidden button is not visible
     */
    @Test
    public void visibleOnHiddenElement() {
        assertFalse(guiTestUIMap.hiddenButton().isVisible());
    }

    /**
     * negative case to test method {@link org.aludratest.service.gui.component.Element#isVisible()}
     * <br/> hidden button in div is not visible
     */
    @Test
    public void visibleOnHiddenElementInDiv() {
        assertFalse(guiTestUIMap.hiddenButton().isVisible());
    }

    /**
     * positive case to test method {@link org.aludratest.service.gui.component.Element#isVisible(long)}
     * <br/> dropdown box is visible (with default timeout)
     */
    @Test
    public void visibleOnExistingElementWithTimeout() {
        assertTrue(guiTestUIMap.dropDownBox().isVisible(DEFAULT_TIMEOUT));
    }

    /**
     * negative case to test method {@link org.aludratest.service.gui.component.Element#isVisible(long)}
     * <br/> a not-existing element is not visible (with default timeout)
     */
    @Test
    public void visibleOnNotExistingElementWithTimeout() {
        assertFalse(guiTestUIMap.notExistingButton().isVisible(DEFAULT_TIMEOUT));
    }

    /**
     * negative case to test method {@link org.aludratest.service.gui.component.Element#isVisible(long)}
     * <br/> hidden button is not visible (with default timeout)
     */
    @Test
    public void visibleOnHiddenElementWithTimeoutt() {
        assertFalse(guiTestUIMap.hiddenButton().isVisible(DEFAULT_TIMEOUT));
    }

    /**
     * negative case to test method {@link org.aludratest.service.gui.component.Element#isVisible(long)}
     * <br/> hidden button in div is not visible (with default timeout)
     */
    @Test
    public void visibleOnHiddenElementInDivWithTimeout() {
        assertFalse(guiTestUIMap.hiddenButton().isVisible(DEFAULT_TIMEOUT));
    }

    @Test
    public void presentAndInForeground() {
        checkPresentAndInForeground(GUITestUIMap.DROPDOWNBOX_ID, true, aludraWebGUI);
        checkPresentAndInForeground(GUITestUIMap.DISABLED_TEXT_FIELD_ID, true, aludraWebGUI);
        checkPresentAndInForeground(new IdLocator("lksndofhweoidf"), false, aludraWebGUI);
    }

    private void checkPresentAndInForeground(GUIElementLocator locator, boolean expectedValue, AludraWebGUI aludraWebGUI) {
        assertEquals("Element " + locator + " is " + (expectedValue ? "not " : "") + " present and in foreground, ", expectedValue,
                aludraWebGUI.check().isElementPresentandInForeground("el", "op", locator));
    }

}