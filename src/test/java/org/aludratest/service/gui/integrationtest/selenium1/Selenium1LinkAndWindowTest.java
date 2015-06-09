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
package org.aludratest.service.gui.integrationtest.selenium1;

import org.aludratest.service.gui.component.Link;
import org.aludratest.service.gui.component.Window;
import org.aludratest.service.gui.component.base.AbstractLinkAndWindowTest;
import org.aludratest.service.gui.component.base.GUITestUIMap;
import org.aludratest.testcase.TestStatus;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests {@link Link} and {@link Window} features with Selenium 1.
 * @author Volker Bergmann
 */
public class Selenium1LinkAndWindowTest extends AbstractLinkAndWindowTest {

    /** Activates Selenium 1 before the tests
     *  (inherited from the parent class) are executed. */
    @BeforeClass
    public static void setUpSelenium1() {
        activateSelenium1();
    }

    /** The method is overridden to work around a Selenium RC issue which is not expected to be fixed. */
    @Override
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
        // The following check fails for Google Chrome due to a Selenium RC issue which is not expected to be fixed
        // checkLastStepStatus(TestStatus.FAILEDAUTOMATION);
    }
}
