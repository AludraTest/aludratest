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
package org.aludratest.service.gui.web.selenium.selenium2;

import org.aludratest.exception.AutomationException;
import org.aludratest.exception.FunctionalFailure;
import org.aludratest.service.gui.web.WebGUIVerification;
import org.aludratest.service.locator.Locator;
import org.aludratest.service.locator.element.GUIElementLocator;
import org.aludratest.util.DataUtil;
import org.aludratest.util.PolledValidationTask;
import org.aludratest.util.Provider;
import org.aludratest.util.poll.PollService;
import org.databene.commons.StringUtil;
import org.databene.commons.Validator;

/**
 * This class is providing all operations for verify elements on a WebGUI with Selenium2
 * @author Marcel Malitz
 * @author Joerg Langnickel
 * @author Volker Bergmann
 */
public class Selenium2Verification extends AbstractSelenium2Action implements WebGUIVerification {

    Selenium2Verification(Selenium2Wrapper seleniumWrapper) {
        super(seleniumWrapper);
    }

    @Override
    public void assertTextMatches(String elementType, String operation, Locator locator, Validator<String> validator) {
        final GUIElementLocator elementLocator = getDefaultElementLocator(locator);
        Provider<String> provider = new TextProvider(elementLocator);
        waitForMatch(provider, validator);
    }

    @Override
    public void assertVisible(String elementType, String operation, Locator locator) {
        final GUIElementLocator elementLocator = getDefaultElementLocator(locator);
        try {
            wrapper.waitUntilVisible(elementLocator);
        }
        catch (AutomationException e) {
            throw new FunctionalFailure(e.getMessage());
        }
    }

    @Override
    public void assertEditable(String elementType, String operation, Locator locator) {
        final GUIElementLocator elementLocator = getDefaultElementLocator(locator);
        if (!wrapper.isEditable(elementLocator)) {
            throw new FunctionalFailure("Element not editable.");
        }
    }

    @Override
    public void assertNotEditable(String elementType, String operation, Locator locator) {
        final GUIElementLocator elementLocator = getDefaultElementLocator(locator);
        if (wrapper.isEditable(elementLocator)) {
            throw new FunctionalFailure("Element not expected to be editable");
        }
    }

    @Override
    public void assertElementPresent(String elementType, String operation, Locator locator) {
        final GUIElementLocator elementLocator = getDefaultElementLocator(locator);
        try {
            wrapper.isElementPresent(elementLocator);
        }
        catch (AutomationException e) {
            // because of ASSERTION, it is a functional failure (expected failure from SUT)
            throw new FunctionalFailure(e.getMessage());
        }
    }

    @Override
    public void assertChecked(String elementType, String operation, Locator locator) {
        assertChecked(elementType, operation, true, locator);
    }

    @Override
    public void assertChecked(String elementType, String operation, boolean expected, Locator locator) {
        final GUIElementLocator elementLocator = getDefaultElementLocator(locator);
        if (wrapper.isChecked(elementLocator) != expected) {
            String message = "Checkbox or Radiobutton is " + (expected ? "unchecked" : "checked");
            throw new FunctionalFailure(message);
        }
    }

    @Override
    public void assertHasFocus(String elementType, String operation, Locator locator) {
        final GUIElementLocator elementLocator = getDefaultElementLocator(locator);
        final boolean hasFocus = wrapper.hasFocus(elementLocator);
        if (!hasFocus) {
            throw new FunctionalFailure("The element does not have the focus.");
        }
    }

    @Override
    public void assertHasValues(String elementType, String operation, Locator locator, String[] expectedValues) {
        final GUIElementLocator elementLocator = getDefaultElementLocator(locator);
        final String[] actualValues = wrapper.getSelectOptions(elementLocator);
        final String mismatches = DataUtil.expectEqualArrays(expectedValues, actualValues);
        if (!StringUtil.isEmpty(mismatches)) {
            throw new FunctionalFailure("The actual values are not as expected. " +
                    "As follows the unequal pairs (expected!=actual): " + mismatches);
        }
    }

    @Override
    public void assertHasLabels(String elementType, String operation, Locator locator, String[] expectedLabels) {
        final GUIElementLocator elementLocator = getDefaultElementLocator(locator);
        checkLabels(expectedLabels, elementLocator, false);
    }

    @Override
    public void assertValueMatches(String elementType, String operation, Locator locator, Validator<String> validator) {
        final GUIElementLocator elementLocator = getDefaultElementLocator(locator);
        Provider<String> provider = new Provider<String>() {
            @Override
            public String getName() {
                return "Value";
            }
            @Override
            public String getValue() {
                return wrapper.getValue(elementLocator);
            }
        };
        waitForMatch(provider, validator);
    }

    @Override
    public void assertContainsLabels(String elementType, String operation, Locator locator, String[] labels) {
        final GUIElementLocator elementLocator = getDefaultElementLocator(locator);
        checkLabels(labels, elementLocator, true);

    }

    @Override
    public void assertElementNotPresent(String elementType, String operation, Locator locator) {
        final GUIElementLocator elementLocator = getDefaultElementLocator(locator);
        try {
            wrapper.waitForElementNotPresent(elementLocator);
        }
        catch (AutomationException e) {
            // because of ASSERTION, it is a functional failure (expected failure from SUT)
            throw new FunctionalFailure(e.getMessage());
        }
    }

    @Override
    public void assertDropDownEntrySelectionMatches(String elementType, String operation, Locator locator, Validator<String> validator) {
        final GUIElementLocator elementLocator = getDefaultElementLocator(locator);
        Provider<String> provider = new Provider<String>() {
            @Override
            public String getName() {
                return "Label";
            }
            @Override
            public String getValue() {
                return wrapper.getSelectedLabel(elementLocator);
            }
        };
        waitForMatch(provider, validator);
    }

    private void checkLabels(String[] labels, GUIElementLocator elementLocator, boolean contains) {
        final String[] actualLabels = wrapper.getLabels(elementLocator);
        final String mismatches;
        if (contains) {
            mismatches = DataUtil.containsStrings(labels, actualLabels);
            if (mismatches.length() > 0) {
                throw new FunctionalFailure("The expected labels are not contained " +
                        "in the actual labels. Following Label(s) is/are missing: " +
                        mismatches);
            }
        } else {
            mismatches = DataUtil.expectEqualArrays(labels, actualLabels);
            if (!StringUtil.isEmpty(mismatches)) {
                throw new FunctionalFailure("The actual labels are not equal " +
                        "to the expected ones. As follows the unequal pairs " +
                        "(expected!=actual): " + mismatches);
            }
        }
    }

    private void waitForMatch(Provider<String> provider, Validator<String> validator) {
        PollService pollService = new PollService(wrapper.getTimeout(), wrapper.getPauseBetweenRetries());
        pollService.poll(new PolledValidationTask(provider, validator));
    }

    class TextProvider implements Provider<String> {

        private GUIElementLocator elementLocator;

        public TextProvider(GUIElementLocator elementLocator) {
            this.elementLocator = elementLocator;
        }

        @Override
        public String getName() {
            return "Text";
        }

        @Override
        public String getValue() {
            return wrapper.getText(elementLocator, true);
        }

    }

    class ValueProvider implements Provider<String> {

        private GUIElementLocator elementLocator;

        public ValueProvider(GUIElementLocator elementLocator) {
            this.elementLocator = elementLocator;
        }

        @Override
        public String getName() {
            return "Value";
        }

        @Override
        public String getValue() {
            return wrapper.getValue(elementLocator);
        }

    }

    class SelectedLabelProvider implements Provider<String> {

        private GUIElementLocator elementLocator;

        public SelectedLabelProvider(GUIElementLocator elementLocator) {
            this.elementLocator = elementLocator;
        }

        @Override
        public String getName() {
            return "Label";
        }

        @Override
        public String getValue() {
            return wrapper.getSelectedLabel(elementLocator);
        }

    }

}
