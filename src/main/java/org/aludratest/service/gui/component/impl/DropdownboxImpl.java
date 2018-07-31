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
package org.aludratest.service.gui.component.impl;

import org.aludratest.service.gui.component.Dropdownbox;
import org.aludratest.service.locator.option.LabelLocator;
import org.aludratest.service.locator.option.OptionLocator;
import org.aludratest.util.data.helper.DataMarkerCheck;
import org.aludratest.util.validator.ContainsValidator;
import org.aludratest.util.validator.EqualsIgnoreCaseTrimmedValidator;
import org.aludratest.util.validator.EqualsValidator;
import org.aludratest.util.validator.NotEqualsValidator;
import org.databene.commons.Validator;

/** Default implementation of the Dropdownbox interface. */
public class DropdownboxImpl extends AbstractElement<Dropdownbox> implements Dropdownbox {

    @Override
    public void selectEntry(String label) {
        if (!DataMarkerCheck.isNull(label)) {
            selectEntry(new LabelLocator(DataMarkerCheck.convertIfEmpty(label)));
        }
    }

    @Override
    public void selectEntry(OptionLocator optionLocator) {
        if (optionLocator != null) {
            perform().selectDropDownEntry(elementType, elementName, getLocator(), optionLocator, taskCompletionTimeout);
        }
    }

    @Override
    public String getSelectedEntry() {
        return perform().getInputFieldValue(elementType, elementName, getLocator());
    }

    @Override
    public String getSelectedLabel() {
        return perform().getInputFieldSelectedLabel(elementType, elementName, getLocator());
    }

    @Override
    public void assertIsSelected(String label) {
        if (!DataMarkerCheck.isNull(label)) {
            verify().assertDropDownEntrySelectionMatches(elementType, elementName, getLocator(),
                    new EqualsValidator(DataMarkerCheck.convertIfEmpty(label)));
        }
    }

    @Override
    public void assertTextNotEquals(String label) {
        if (!DataMarkerCheck.isNull(label)) {
            verify().assertDropDownEntrySelectionMatches(elementType, elementName, getLocator(),
                    new NotEqualsValidator(DataMarkerCheck.convertIfEmpty(label)));
        }
    }

    @Override
    public void assertSelectedContains(String label) {
        if (!DataMarkerCheck.isNull(label)) {
            verify().assertDropDownEntrySelectionMatches(elementType, elementName, getLocator(), new ContainsValidator(label));
        }
    }

    @Override
    public void assertSelectedIgnoreCaseTrimmed(String label) {
        if (!DataMarkerCheck.isNull(label)) {
            verify().assertDropDownEntrySelectionMatches(elementType, elementName, getLocator(),
                    new EqualsIgnoreCaseTrimmedValidator(DataMarkerCheck.convertIfEmpty(label)));
        }
    }

    @Override
    public void assertSelectionMatches(Validator<String> validator) {
        verify().assertDropDownEntrySelectionMatches(elementType, elementName, getLocator(), validator);
    }

    /** @deprecated Use {@link #assertHasValues(boolean, String...)} */
    @Override
    @Deprecated
    public void assertHasValues(String... values) {
        assertHasValues(true, values);
    }

    /** @deprecated Use {@link #assertHasLabels(boolean, String...)} */
    @Override
    @Deprecated
    public void assertHasLabels(String... labels) {
        assertHasLabels(true, labels);
    }

    @Override
    public void assertContainsLabels(String... labels) {
        if (labels.length == 1) {
            String value1 = labels[0];
            if (!DataMarkerCheck.isNull(value1)) {
                verify().assertContainsLabels(elementType, elementName, getLocator(),
                        new String[] { DataMarkerCheck.convertIfEmpty(value1) });
            }
        }
        else {
            verify().assertContainsLabels(elementType, elementName, getLocator(), DataMarkerCheck.convertIfEmpty(labels));
        }
    }

    @Override
    public void assertContainsValues(String... values) {
        if (values.length == 1) {
            String value1 = values[0];
            if (!DataMarkerCheck.isNull(value1)) {
                verify().assertContainsValues(elementType, elementName, getLocator(),
                        new String[] { DataMarkerCheck.convertIfEmpty(value1) });
            }
        }
        else {
            verify().assertContainsValues(elementType, elementName, getLocator(), DataMarkerCheck.convertIfEmpty(values));
        }
    }

    @Override
    public boolean checkContainsLabels(String... labels) {
        return check().containsLabels(elementType, elementName, getLocator(), DataMarkerCheck.convertIfEmpty(labels));
    }

    /** @deprecated Use {@link #checkHasLabels(boolean, String...)} */
    @Override
    @Deprecated
    public boolean checkEqualsLabels(String... labels) {
        return checkHasLabels(true, labels);
    }

    @Override
    public void assertHasLabels(boolean checkOrder, String... labels) {
        if (labels.length == 1) {
            String value1 = labels[0];
            if (!DataMarkerCheck.isNull(value1)) {
                verify().assertHasLabels(elementType, elementName, getLocator(),
                        new String[] { DataMarkerCheck.convertIfEmpty(value1) }, checkOrder);
            }
        }
        else {
            verify().assertHasLabels(elementType, elementName, getLocator(), DataMarkerCheck.convertIfEmpty(labels), checkOrder);
        }
    }

    @Override
    public void assertHasValues(boolean checkOrder, String... values) {
        if (values.length == 1) {
            String value1 = values[0];
            if (!DataMarkerCheck.isNull(value1)) {
                verify().assertHasValues(elementType, elementName, getLocator(),
                        new String[] { DataMarkerCheck.convertIfEmpty(value1) }, checkOrder);
            }
        }
        else {
            verify().assertHasValues(elementType, elementName, getLocator(), DataMarkerCheck.convertIfEmpty(values), checkOrder);
        }
    }

    @Override
    public boolean checkHasLabels(boolean checkOrder, String... labels) {
        return check().hasLabels(elementType, elementName, getLocator(), checkOrder, DataMarkerCheck.convertIfEmpty(labels));
    }

    @Override
    public boolean checkHasValues(boolean checkOrder, String... values) {
        return check().hasValues(elementType, elementName, getLocator(), checkOrder, DataMarkerCheck.convertIfEmpty(values));
    }

    @Override
    public boolean checkContainsValues(String... values) {
        return check().containsValues(elementType, elementName, getLocator(), values);
    }

}
