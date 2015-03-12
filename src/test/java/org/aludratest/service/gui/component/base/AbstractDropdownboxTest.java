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

import org.aludratest.service.gui.component.Dropdownbox;
import org.aludratest.service.locator.option.IndexLocator;
import org.aludratest.testcase.TestStatus;
import org.aludratest.util.validator.ContainsIgnoreCaseValidator;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Unit test for class {@link org.aludratest.service.gui.component.Checkbox}
 * @author ywang
 */
@SuppressWarnings("javadoc")
public abstract class AbstractDropdownboxTest extends GUITest {
    /**
     *  positive case to test method {@link org.aludratest.service.gui.component.Dropdownbox#selectEntry(Object)}
     *  <br/> select an entry in dropdownbox
     * @throws Exception
     */
    @Test
    public void selectEntry_DropdownBox_enabled_byLabel() throws Throwable {
        Dropdownbox box = guiTestUIMap.dropDownBox();
        box.selectEntry("Partner Short Name");
        checkLastStepStatus(TestStatus.PASSED);
        assertEquals("Partner Short Name_value", box.getSelectedEntry());
    }

    @Test
    public void selectEntry_DropdownBox_enabled_byIndex() throws Throwable {
        Dropdownbox box = guiTestUIMap.dropDownBox();
        box.selectEntry(new IndexLocator(2));
        checkLastStepStatus(TestStatus.PASSED);
        assertEquals("City_value", box.getSelectedEntry());
    }

    /**
     *  negative case to test method {@link org.aludratest.service.gui.component.Dropdownbox#selectEntry(Object)}
     *  <br/> select a not-existing entry in dropdownbox
     */
    @Test
    public void selectEntryNotExistingOnEnabledDropdownBox() {
        String notExistingEntry = "Partner Short Name1";
        guiTestUIMap.dropDownBox().selectEntry(notExistingEntry);
        checkLastStepStatus(TestStatus.FAILEDAUTOMATION);
        checkLastStepErrorMessage("The expected labels are not contained in the actual labels. Following Label(s) is/are missing: Partner Short Name1");
    }

    /**
     *  negative case to test method {@link org.aludratest.service.gui.component.Dropdownbox#selectEntry(Object)}
     *  <br/> select a disabled entry in dropdownbox
     */
    @Ignore("Does not work with selenium1")
    @Test
    public void selectEntryDisabledOnEnabledDropdownBox() {
        guiTestUIMap.dropDownBox().selectEntry("Disabled entry");
        checkLastStepStatus(TestStatus.FAILEDAUTOMATION);
        checkLastStepErrorMessage("Following option is disabled. " + "Disabled entry");
    }

    /**
     *  negative case to test method {@link org.aludratest.service.gui.component.Dropdownbox#selectEntry(Object)}
     *  <br/> select an entry in disabled dropdownbox
     */
    @Test
    public void selectEntryOnDisabledDropdownBox() {
        guiTestUIMap.disabledDropDownBox().selectEntry("City");
        checkLastStepStatus(TestStatus.FAILEDAUTOMATION);
        checkLastStepErrorMessage("Element not editable");
    }

    /** Positive case to select an entry in a dropdown box using the EMPTY marker. This should select the "" entry in the
     * Dropdownbox. */
    @Test
    public void selectEmptyEntryOnDropdownBox() {
        guiTestUIMap.dropDownBox().selectEntry("<EMPTY>");
        assertEquals("empty_value", guiTestUIMap.dropDownBox().getSelectedEntry());
    }

    /**
     *  positive case to test method {@link org.aludratest.service.gui.component.Dropdownbox#getSelectedEntry()}
     *  <br/> get selected entry in dropdownbox
     */
    @Test
    public void getSelectedEntryOnEnabledDropdownBox() {
        assertEquals("Partner_Name_value", guiTestUIMap.dropDownBox().getSelectedEntry());
        checkLastStepStatus(TestStatus.PASSED);
    }

    /**
     *  positive case to test method {@link org.aludratest.service.gui.component.Dropdownbox#assertIsSelected(String)}
     *  <br/> check if an option has been selected
     */
    @Test
    public void hasSelectedOnDropdownBox() {
        guiTestUIMap.dropDownBox().assertIsSelected("Partner Name");
        checkLastStepStatus(TestStatus.PASSED);
    }

    /**
     *  negative case to test method {@link org.aludratest.service.gui.component.Dropdownbox#assertIsSelected(String)}
     *  <br/> check if an option has been selected
     */
    @Test
    public void hasSelectedOnDropdownBoxNot() {
        guiTestUIMap.dropDownBox().assertIsSelected("City");
        checkLastStepStatus(TestStatus.FAILED);
        checkLastStepErrorMessage("Label 'Partner Name' does not match the validator EqualsValidator "
                + "(expecting string to be 'City')");
    }

    /**
     *  positive case to test method {@link org.aludratest.service.gui.component.Dropdownbox#assertTextNotEquals(String)}
     *  <br/> check if an option has not been selected
     */
    @Test
    public void notEqualsOnDropdownBox() {
        guiTestUIMap.dropDownBox().assertTextNotEquals("City");
        checkLastStepStatus(TestStatus.PASSED);
    }

    /**
     *  negative case to test method {@link org.aludratest.service.gui.component.Dropdownbox#assertTextNotEquals(String)}
     *  <br/> check if an option has not been selected
     */
    @Test
    public void notEqualsOnDropdownBoxNot() {
        guiTestUIMap.dropDownBox().assertTextNotEquals("Partner Name");
        checkLastStepStatus(TestStatus.FAILED);
        checkLastStepErrorMessage("Label 'Partner Name' does not match the validator NotEqualsValidator "
                + "(expects string to be different from 'Partner Name')");
    }

    /**
     *  positive case to test method {@link org.aludratest.service.gui.component.Dropdownbox#assertSelectedContains(String)}
     *  <br/> check that the label is contained by the selected item in the dropdown box
     */
    @Test
    public void hasSelectedContainsOnDropdownBox() {
        guiTestUIMap.dropDownBox().assertSelectedContains("Partner");
        checkLastStepStatus(TestStatus.PASSED);
        guiTestUIMap.dropDownBox().assertSelectedContains("Name");
        checkLastStepStatus(TestStatus.PASSED);
    }

    /**
     *  negative case to test method {@link org.aludratest.service.gui.component.Dropdownbox#assertSelectedContains(String)}
     *  <br/> check that the label is contained by the selected item in the dropdown box
     */
    @Test
    public void hasSelectedContainsOnDropdownBoxNot() {
        guiTestUIMap.dropDownBox().assertSelectedContains("Partner1");
        checkLastStepStatus(TestStatus.FAILED);
        checkLastStepErrorMessage("Label 'Partner Name' does not match the validator ContainsValidator "
                + "(expecting sub string 'Partner1')");
    }

    @Test
    public void hasValues_multiarg() {
        // FIXME shouldn't these be the VALUES of the option tags? Please clarify.
        // positive test
        guiTestUIMap.dropDownBox().assertHasValues("Partner Name", "Partner Short Name", "City", "State", "Country", "Sales Rep",
                "Partner Number", "District", "Customer Facility Code", "Equipment Customer", "Non Globe BP", "Creation SOU",
                "<EMPTY>", "Disabled entry");
        checkLastStepStatus(TestStatus.PASSED);
        // negative test with multiple values
        guiTestUIMap.dropDownBox().assertHasValues("Partner Name", "Partner Short Name");
        checkLastStepStatus(TestStatus.FAILED);
    }

    @Test
    public void hasValues_singlearg() {
        guiTestUIMap.dropDownBox().assertHasValues("Partner Name");
        checkLastStepStatus(TestStatus.FAILED);
    }

    @Test
    public void hasLabels_multiarg() {
        // positive test
        guiTestUIMap.dropDownBox().assertHasLabels("Partner Name", "Partner Short Name", "City", "State", "Country", "Sales Rep",
                "Partner Number", "District", "Customer Facility Code", "Equipment Customer", "Non Globe BP", "Creation SOU",
                "<EMPTY>", "Disabled entry");
        checkLastStepStatus(TestStatus.PASSED);
        // negative test with multiple values
        guiTestUIMap.dropDownBox().assertHasLabels("Partner Name", "Partner Short Name");
        checkLastStepStatus(TestStatus.FAILED);
    }

    @Test
    public void equalsLabels_multiarg() {
        // positive test
        boolean checkValue = guiTestUIMap.dropDownBox().checkEqualsLabels("Partner Name", "Partner Short Name", "City", "State",
                "Country", "Sales Rep", "Partner Number", "District", "Customer Facility Code", "Equipment Customer",
                "Non Globe BP", "Creation SOU", "<EMPTY>", "Disabled entry");
        Assert.assertTrue(checkValue);
        // negative test with multiple values
        checkValue = guiTestUIMap.dropDownBox().checkEqualsLabels("Partner Name", "Partner Short Name");
        Assert.assertFalse(checkValue);
    }

    @Test
    public void hasLabels_singlearg() {
        guiTestUIMap.dropDownBox().assertHasLabels("Partner Name");
        checkLastStepStatus(TestStatus.FAILED);
    }

    @Test
    public void equalsLabels_singlearg() {
        boolean checkValue = guiTestUIMap.dropDownBox().checkEqualsLabels("Partner Name");
        Assert.assertFalse(checkValue);
    }

    @Test
    public void containsLabels() {
        // positive test with complete label list
        guiTestUIMap.dropDownBox().assertContainsLabels("Partner Name", "Partner Short Name", "City", "State", "Country", "Sales Rep", "Partner Number", "District", "Customer Facility Code",
                "Equipment Customer", "Non Globe BP", "Creation SOU", "Disabled entry");
        checkLastStepStatus(TestStatus.PASSED);
        // positive test with partial label list
        guiTestUIMap.dropDownBox().assertContainsLabels("Partner Short Name", "Disabled entry");
        checkLastStepStatus(TestStatus.PASSED);
        // negative test with multiple values
        guiTestUIMap.dropDownBox().assertContainsLabels("Partner Name", "xxx");
        checkLastStepStatus(TestStatus.FAILED);
    }

    @Test
    public void containsLabelsCheck() {
        // positive test with complete label list
        boolean checkValue = guiTestUIMap.dropDownBox().checkContainsLabels("Partner Name", "Partner Short Name", "City",
                "State", "Country", "Sales Rep", "Partner Number", "District", "Customer Facility Code", "Equipment Customer",
                "Non Globe BP", "Creation SOU", "Disabled entry");
        Assert.assertTrue(checkValue);
        // positive test with partial label list
        checkValue = guiTestUIMap.dropDownBox().checkContainsLabels("Partner Short Name", "Disabled entry");
        Assert.assertTrue(checkValue);
        // negative test with multiple values
        checkValue = guiTestUIMap.dropDownBox().checkContainsLabels("Partner Name", "xxx");
        Assert.assertFalse(checkValue);
    }

    @Test
    public void hasSelectedIgnoreCaseTrimmed_existingLabels() {
        // positive test
        guiTestUIMap.dropDownBox().assertSelectedIgnoreCaseTrimmed(" PartneR NamE\r\n ");
        checkLastStepStatus(TestStatus.PASSED);
        // negative test with unselected label
        guiTestUIMap.dropDownBox().assertSelectedIgnoreCaseTrimmed(" City ");
        checkLastStepStatus(TestStatus.FAILED);
    }

    @Test
    public void hasSelectedIgnoreCaseTrimmed_nonexistingLabels() {
        // negative test with non-existing label
        guiTestUIMap.dropDownBox().assertSelectedIgnoreCaseTrimmed(" xxx ");
        checkLastStepStatus(TestStatus.FAILED);
    }

    @Test
    public void selectionMatches() {
        // positive test
        guiTestUIMap.dropDownBox().assertSelectionMatches(new ContainsIgnoreCaseValidator("PartneR Nam"));
        checkLastStepStatus(TestStatus.PASSED);
        // negative test with unselected label
        guiTestUIMap.dropDownBox().assertSelectionMatches(new ContainsIgnoreCaseValidator("City"));
        checkLastStepStatus(TestStatus.FAILED);
    }

}
