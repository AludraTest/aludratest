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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.aludratest.service.gui.component.base.AbstractLocatorTest;
import org.aludratest.service.locator.element.ElementLocators;
import org.aludratest.service.locator.element.ElementLocators.ElementLocatorsGUI;
import org.aludratest.service.locator.element.IdLocator;
import org.aludratest.service.locator.element.XPathLocator;
import org.aludratest.testcase.TestStatus;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests locators with Selenium 1.
 * @author Volker Bergmann
 */
public class Selenium1LocatorTest extends AbstractLocatorTest {

    /** Activates Selenium 1 before the tests
     *  (inherited from the parent class) are executed. */
    @BeforeClass
    public static void setUpSelenium1() {
        activateSelenium1();
    }

    @Test
    public void elementLocators() {
        IdLocator opt1 = new IdLocator("xlkhbsdcbli");
        XPathLocator opt2 = new XPathLocator("//a[@id='before:LinktoTThis:after']");
        ElementLocatorsGUI elementLocators = (ElementLocatorsGUI) new ElementLocators(opt1, opt2).newMutableInstance();
        assertNull(elementLocators.getUsedOption());
        aludraWebGUI.verify().assertElementPresent("el", "op", elementLocators);
        assertNotNull(elementLocators.getUsedOption());
        assertEquals(opt2, elementLocators.getUsedOption());
        checkLastStepStatus(TestStatus.PASSED);
    }

}
