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

import static org.junit.Assert.*;

import org.aludratest.service.gui.web.AludraWebGUI;
import org.aludratest.service.locator.Locator;
import org.aludratest.service.locator.window.TitleLocator;
import org.aludratest.testcase.TestStatus;
import org.junit.Test;

/**
 * Tests label features of {@link AludraWebGUI} services.
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public abstract class AbstractLinkAndWindowTest extends GUITest {

    @Test
    public void clickLinkAndSelectWindow_positive() {
        // click on the link to open a second page in a second window
        guiTestUIMap.testLink().click();
        checkLastStepStatus(TestStatus.PASSED);
        // select the second window
        aludraWebGUI.perform().selectWindow(GUITestUIMap.LINKED_PAGE_TITLE);
        checkLastStepStatus(TestStatus.PASSED);
        // select the main window again
        aludraWebGUI.perform().selectWindow(GUITestUIMap.TEST_PAGE_TITLE);
        checkLastStepStatus(TestStatus.PASSED);
    }

    @Test
    public void selectWindow_negative() {
        // try to select a window that does not exist
        aludraWebGUI.perform().selectWindow(new TitleLocator("lsjdvsndm"));
        checkLastStepStatus(TestStatus.FAILEDAUTOMATION);
    }

    @Test
    public void closeOtherWindows() {
        // open a second page
        guiTestUIMap.testLink().click();
        checkLastStepStatus(TestStatus.PASSED);
        // close the second page
        aludraWebGUI.perform().closeOtherWindows("el", "op", GUITestUIMap.TEST_PAGE_TITLE);
        checkLastStepStatus(TestStatus.PASSED);
        // try to select the second page
        aludraWebGUI.perform().selectWindow(GUITestUIMap.LINKED_PAGE_TITLE);
        checkLastStepStatus(TestStatus.FAILEDAUTOMATION);
    }

    @Test
    public void closeWindow_selected() {
        // open a second page
        guiTestUIMap.testLink().click();
        checkLastStepStatus(TestStatus.PASSED);
        // close the main page
        aludraWebGUI.perform().closeWindows("el", "op", GUITestUIMap.TEST_PAGE_TITLE);
        checkLastStepStatus(TestStatus.PASSED);
        // try to select the main page
        aludraWebGUI.perform().selectWindow(GUITestUIMap.TEST_PAGE_TITLE);
        checkLastStepStatus(TestStatus.FAILEDAUTOMATION);
    }

    @Test
    public void closeWindow_unselected() {
        // open a second page
        guiTestUIMap.testLink().click();
        checkLastStepStatus(TestStatus.PASSED);
        // close the second page
        aludraWebGUI.perform().closeWindows("el", "op", GUITestUIMap.LINKED_PAGE_TITLE);
        checkLastStepStatus(TestStatus.PASSED);
        // try to select the second page
        aludraWebGUI.perform().selectWindow(GUITestUIMap.LINKED_PAGE_TITLE);
        checkLastStepStatus(TestStatus.FAILEDAUTOMATION);
    }

    @Test
    public void isWindowOpen() {
        checkOpen(GUITestUIMap.TEST_PAGE_TITLE, true);
        checkOpen(new TitleLocator("lwdncnclknwd"), false);
    }

    private void checkOpen(Locator title, boolean expectedValue) {
        boolean open = aludraWebGUI.check().isWindowOpen("el", "op", title);
        assertEquals("Window " + title + " is " + (expectedValue ? "" : "not ") + " expected to be open", expectedValue, open);
    }

}
