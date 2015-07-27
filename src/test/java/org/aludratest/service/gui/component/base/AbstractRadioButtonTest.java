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

import org.aludratest.service.gui.web.AludraWebGUI;
import org.aludratest.testcase.TestStatus;
import org.junit.Test;

/**
 * Tests radio-button features of {@link AludraWebGUI} services.
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public abstract class AbstractRadioButtonTest extends GUITest {

    @Test
    public void selectOnEnabledRadioButton() {
        guiTestUIMap.andRadioButton().assertChecked();
        guiTestUIMap.orRadioButton().assertChecked("false");
        guiTestUIMap.orRadioButton().select();
        checkLastStepStatus(TestStatus.PASSED);
        guiTestUIMap.orRadioButton().assertChecked();
        checkLastStepStatus(TestStatus.PASSED);
    }

    @Test
    public void isCheckedOnCheckedRadioButton() {
        assertTrue(guiTestUIMap.andRadioButton().isChecked());
    }

    @Test
    public void isCheckedOnUncheckedRadioButton() {
        assertFalse(guiTestUIMap.orRadioButton().isChecked());
    }

}
