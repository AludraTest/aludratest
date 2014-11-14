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
import org.aludratest.service.locator.Locator;
import org.aludratest.service.locator.element.IdLocator;
import org.aludratest.testcase.TestStatus;
import org.junit.Test;

/**
 * Unit test for class {@link org.aludratest.service.gui.component.Element}
 * @author ywang
 */
@SuppressWarnings("javadoc")
public abstract class AbstractElementTest extends GUITest {

    /**
     * positive case to test method {@link org.aludratest.service.gui.component.Element#assertEditable()}
     * <br/> dropdownbox is editable
     */
    @Test
    public void isEditableOnEditableElement() {
        guiTestUIMap.dropDownBox().assertEditable();
        checkLastStepStatus(TestStatus.PASSED);
    }

    /**
     * negative case to test method {@link org.aludratest.service.gui.component.Element#assertEditable()}
     * <br/> disabledTextField is not editable
     */
    @Test
    public void isEditableOnDisableElement() {
        guiTestUIMap.disabledTextField().assertEditable();
        checkLastStepStatus(TestStatus.FAILED);
        checkLastStepErrorMessage("Element not editable.");
    }

    /**
     * positive case to test method {@link org.aludratest.service.gui.component.Element#assertNotEditable()}
     * <br/> disabledTextField is not editable
     */
    @Test
    public void isNotEditableOnDisabledElement() {
        guiTestUIMap.disabledTextField().assertNotEditable();
        checkLastStepStatus(TestStatus.PASSED);
    }

    /**
     * negative case to test method {@link org.aludratest.service.gui.component.Element#assertNotEditable()}
     * <br/> dropdownbox is editable
     */
    @Test
    public void isNotEditableOnEditableElement() {
        guiTestUIMap.dropDownBox().assertNotEditable();
        checkLastStepStatus(TestStatus.FAILED);
        checkLastStepErrorMessage("Element not expected to be editable");
    }

    /**
     * positive case to test method {@link org.aludratest.service.gui.component.Element#assertPresent()}
     * <br/> dropdown box is present
     */
    @Test
    public void isPresentOnPresentElement() {
        guiTestUIMap.dropDownBox().assertPresent();
        checkLastStepStatus(TestStatus.PASSED);
    }

    /**
     * negative case to test method {@link org.aludratest.service.gui.component.Element#assertPresent()}
     * <br/> a not existing element is not present
     */
    @Test
    public void isPresentOnNotExistingElement() {
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
        checkLastStepErrorMessage("Element not editable.");
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

    /**
     * negative case to test method {@link org.aludratest.service.gui.component.Element#doubleClick()}
     * <br/> double click on diabled button
     */
    @Test
    public void doubleClickOnNotEditableElementButton() {
        guiTestUIMap.disabledButton().doubleClick();
        checkLastStepStatus(TestStatus.FAILEDAUTOMATION);
        checkLastStepErrorMessage("Element not editable.");
    }

    /**
     * negative case to test method {@link org.aludratest.service.gui.component.Element#doubleClick()}
     * <br/> double click on image
     */
    @Test
    public void doubleClickOnNotEditableElementImage() {
        guiTestUIMap.image().doubleClick();
        checkLastStepStatus(TestStatus.FAILEDAUTOMATION);
        checkLastStepErrorMessage("Element not editable.");
    }

    /**
     * negative case to test method {@link org.aludratest.service.gui.component.Element#doubleClick()}
     * <br/> double click on label
     */
    @Test
    public void doubleClickOnNotEditableElementLabel() {
        guiTestUIMap.label().doubleClick();
        checkLastStepStatus(TestStatus.FAILEDAUTOMATION);
        checkLastStepErrorMessage("Element not editable.");
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

    /**
     * negative case to test method {@link org.aludratest.service.gui.component.Element#click()}
     * <br/> click on disabled button
     */
    @Test
    public void clickOnNotEditableElementButton() {
        guiTestUIMap.disabledButton().click();
        checkLastStepStatus(TestStatus.FAILEDAUTOMATION);
        checkLastStepErrorMessage("Element not editable.");
    }

    /**
     * negative case to test method {@link org.aludratest.service.gui.component.Element#click()}
     * <br/> click on image
     */
    @Test
    public void clickOnNotEditableElementImage() {
        guiTestUIMap.image().click();
        checkLastStepStatus(TestStatus.FAILEDAUTOMATION);
        checkLastStepErrorMessage("Element not editable.");
    }

    /**
     * negative case to test method {@link org.aludratest.service.gui.component.Element#click()}
     * <br/> click on image
     */
    @Test
    public void clickOnNotEditableElementLabel() {
        guiTestUIMap.label().click();
        checkLastStepStatus(TestStatus.FAILEDAUTOMATION);
        checkLastStepErrorMessage("Element not editable.");
    }

    /**
     * positive case to test method {@link org.aludratest.service.gui.component.Element#clickNotEditable()}
     * <br/> not-editable-click on disabled button
     */
    @Test
    public void clickNotEditableOnNotEditableElementButton() {
        guiTestUIMap.disabledButton().clickNotEditable();
        checkLastStepStatus(TestStatus.PASSED);
    }

    /**
     * positive case to test method {@link org.aludratest.service.gui.component.Element#clickNotEditable()}
     * <br/> not-editable-click on image
     */
    @Test
    public void clickNotEditableOnNotEditableElementImage() {
        guiTestUIMap.image().clickNotEditable();
        checkLastStepStatus(TestStatus.PASSED);
    }

    /**
     * positive case to test method {@link org.aludratest.service.gui.component.Element#clickNotEditable()}
     * <br/> not-editable-click on label button
     */
    @Test
    public void clickNotEditableOnNotEditableElementLabel() {
        guiTestUIMap.label().clickNotEditable();
        checkLastStepStatus(TestStatus.PASSED);
    }

    /**
     * positive case to test method {@link org.aludratest.service.gui.component.Element#clickNotEditable()}
     * <br/> not-editable-click on dropdown box
     */
    @Test
    public void clickNotEditableOnEditableElement() {
        guiTestUIMap.dropDownBox().clickNotEditable();
        checkLastStepStatus(TestStatus.PASSED);
    }

    /**
     * positive case to test method {@link org.aludratest.service.gui.component.Element#isPresent()}
     * <br/> dropdown box is present
     */
    @Test
    public void presentOnExistingElement() {
        assertEquals(true, guiTestUIMap.dropDownBox().isPresent());
    }

    /**
     * positive case to test method {@link org.aludratest.service.gui.component.Element#isPresent()}
     * <br/> hidden button is present
     */
    @Test
    public void presentOnHiddenElement() {
        assertEquals(true, guiTestUIMap.hiddenButton().isPresent());
    }

    /**
     * positive case to test method {@link org.aludratest.service.gui.component.Element#isPresent()}
     * <br/> hidden button in div is present
     */
    @Test
    public void presentOnHiddenElementInDiv() {
        assertEquals(true, guiTestUIMap.hiddenButton().isPresent());
    }

    /**
     * negative case to test method {@link org.aludratest.service.gui.component.Element#isPresent()}
     * <br/> a not-existing element is not present
     */
    @Test
    public void presentOnNotExistingElement() {
        assertEquals(false, guiTestUIMap.notExistingButton().isPresent());
    }

    /**
     * positive case to test method {@link org.aludratest.service.gui.component.Element#isPresent(long)}
     * <br/> dropdown box is present (with default timeout)
     */
    @Test
    public void presentOnExistingElementWithTimeout() {
        assertEquals(true, guiTestUIMap.dropDownBox().isPresent(DEFAULT_TIMEOUT));
    }

    /**
     * positive case to test method {@link org.aludratest.service.gui.component.Element#isPresent(long)}
     * <br/> hidden button is present (with default timeout)
     */
    @Test
    public void presentOnHiddenElementWithTimeout() {
        assertEquals(true, guiTestUIMap.hiddenButton().isPresent(DEFAULT_TIMEOUT));
    }

    /**
     * positive case to test method {@link org.aludratest.service.gui.component.Element#isPresent(long)}
     * <br/> hidden button in div is present (with default timeout)
     */
    @Test
    public void presentOnHiddenElementInDivWithTimeout() {
        assertEquals(true, guiTestUIMap.hiddenButton().isPresent(DEFAULT_TIMEOUT));
    }

    /**
     * negative case to test method {@link org.aludratest.service.gui.component.Element#isPresent(long)}
     * <br/> a not-existing element is not present (with default timeout)
     */
    @Test
    public void presentOnNotExistingElementWithTimeout() {
        assertEquals(false, guiTestUIMap.notExistingButton().isPresent(DEFAULT_TIMEOUT));
    }

    /**
     * positive case to test method {@link org.aludratest.service.gui.component.Element#isNotPresent()}
     * <br/> a not-existing element is not present
     */
    @Test
    public void notPresentOnNotExistingElement() {
        assertEquals(true, guiTestUIMap.notExistingButton().isNotPresent());
    }

    /**
     * negative case to test method {@link org.aludratest.service.gui.component.Element#isNotPresent()}
     * <br/> hidden button is present
     */
    @Test
    public void notPresentOnHiddenElement() {
        assertEquals(false, guiTestUIMap.hiddenButton().isNotPresent());
    }

    /**
     * negative case to test method {@link org.aludratest.service.gui.component.Element#isNotPresent()}
     * <br/> hidden button in div is present
     */
    @Test
    public void notPresentOnHiddenElementInDiv() {
        assertEquals(false, guiTestUIMap.hiddenButton().isNotPresent());
    }

    /**
     * negative case to test method {@link org.aludratest.service.gui.component.Element#isNotPresent()}
     * <br/> dropdown box is present
     */
    @Test
    public void notPresentOnExistingElement() {
        assertEquals(false, guiTestUIMap.dropDownBox().isNotPresent());
    }

    /**
     * positive case to test method {@link org.aludratest.service.gui.component.Element#isNotPresent(long)}
     * <br/> a not-existing element is not present (with default timeout)
     */
    @Test
    public void notPresentOnNotExistingElementWithTimeout() {
        assertEquals(true, guiTestUIMap.notExistingButton().isNotPresent(DEFAULT_TIMEOUT));
    }

    /**
     * negative case to test method {@link org.aludratest.service.gui.component.Element#isNotPresent(long)}
     * <br/> hidden button is present (with default timeout)
     */
    @Test
    public void notPresentOnHiddenElementWithTimeout() {
        assertEquals(false, guiTestUIMap.hiddenButton().isNotPresent(DEFAULT_TIMEOUT));
    }

    /**
     * negative case to test method {@link org.aludratest.service.gui.component.Element#isNotPresent(long)}
     * <br/> hidden button in div is present (with default timeout)
     */
    @Test
    public void notPresentOnHiddenElementInDivWithTimeout() {
        assertEquals(false, guiTestUIMap.hiddenButton().isNotPresent(DEFAULT_TIMEOUT));
    }

    /**
     * negative case to test method {@link org.aludratest.service.gui.component.Element#isNotPresent(long)}
     * <br/> dropdown box is present (with default timeout)
     */
    @Test
    public void notPresentOnExistingElementWithTimeout() {
        assertEquals(false, guiTestUIMap.dropDownBox().isNotPresent(DEFAULT_TIMEOUT));
    }

    /**
     * positive case to test method {@link org.aludratest.service.gui.component.Element#isVisible()}
     * <br/> dropdown box is visible
     */
    @Test
    public void visibleOnExistingElement() {
        assertEquals(true, guiTestUIMap.dropDownBox().isVisible());
    }

    /**
     * negative case to test method {@link org.aludratest.service.gui.component.Element#isVisible()}
     * <br/> a not-existing element is not visible
     */
    @Test
    public void visibleOnNotExistingElement() {
        assertEquals(false, guiTestUIMap.notExistingButton().isVisible());
    }

    /**
     * negative case to test method {@link org.aludratest.service.gui.component.Element#isVisible()}
     * <br/> hidden button is not visible
     */
    @Test
    public void visibleOnHiddenElement() {
        assertEquals(false, guiTestUIMap.hiddenButton().isVisible());
    }

    /**
     * negative case to test method {@link org.aludratest.service.gui.component.Element#isVisible()}
     * <br/> hidden button in div is not visible
     */
    @Test
    public void visibleOnHiddenElementInDiv() {
        assertEquals(false, guiTestUIMap.hiddenButton().isVisible());
    }

    /**
     * positive case to test method {@link org.aludratest.service.gui.component.Element#isVisible(long)}
     * <br/> dropdown box is visible (with default timeout)
     */
    @Test
    public void visibleOnExistingElementWithTimeout() {
        assertEquals(true, guiTestUIMap.dropDownBox().isVisible(DEFAULT_TIMEOUT));
    }

    /**
     * negative case to test method {@link org.aludratest.service.gui.component.Element#isVisible(long)}
     * <br/> a not-existing element is not visible (with default timeout)
     */
    @Test
    public void visibleOnNotExistingElementWithTimeout() {
        assertEquals(false, guiTestUIMap.notExistingButton().isVisible(DEFAULT_TIMEOUT));
    }

    /**
     * negative case to test method {@link org.aludratest.service.gui.component.Element#isVisible(long)}
     * <br/> hidden button is not visible (with default timeout)
     */
    @Test
    public void visibleOnHiddenElementWithTimeoutt() {
        assertEquals(false, guiTestUIMap.hiddenButton().isVisible(DEFAULT_TIMEOUT));
    }

    /**
     * negative case to test method {@link org.aludratest.service.gui.component.Element#isVisible(long)}
     * <br/> hidden button in div is not visible (with default timeout)
     */
    @Test
    public void visibleOnHiddenElementInDivWithTimeout() {
        assertEquals(false, guiTestUIMap.hiddenButton().isVisible(DEFAULT_TIMEOUT));
    }

    @Test
    public void presentAndInForeground() {
        checkPresentAndInForeground(GUITestUIMap.DROPDOWNBOX_ID, true, aludraWebGUI);
        checkPresentAndInForeground(GUITestUIMap.DISABLED_TEXT_FIELD_ID, true, aludraWebGUI);
        checkPresentAndInForeground(new IdLocator("lksndofhweoidf"), false, aludraWebGUI);
    }

    private void checkPresentAndInForeground(Locator locator, boolean expectedValue, AludraWebGUI aludraWebGUI) {
        assertEquals("Element " + locator + " is " + (expectedValue ? "not " : "") + " present and in foreground, ", expectedValue,
                aludraWebGUI.check().isElementPresentandInForeground("el", "op", locator));
    }

}