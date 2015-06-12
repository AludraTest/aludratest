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
import static org.junit.Assert.assertNull;

import org.aludratest.service.locator.Locator;
import org.aludratest.service.locator.element.CSSLocator;
import org.aludratest.service.locator.element.ElementLocators;
import org.aludratest.service.locator.element.ElementLocators.ElementLocatorsGUI;
import org.aludratest.service.locator.element.IdLocator;
import org.aludratest.service.locator.element.LabelLocator;
import org.aludratest.service.locator.element.XPathLocator;
import org.aludratest.testcase.TestStatus;
import org.junit.Test;

/**
 * Parent class for {@link Locator} tests.
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public abstract class AbstractLocatorTest extends GUITest {

    @Test
    public void idLocator() {
        aludraWebGUI.verify().assertElementPresent("el", "op", new IdLocator("LinktoTThis:after"));
        checkLastStepStatus(TestStatus.PASSED);
    }

    @Test
    public void idLocator_notfound() {
        aludraWebGUI.verify().assertElementPresent("el", "op", new IdLocator("LinktoTThis:after_notfound"));
        checkLastStepStatus(TestStatus.FAILED);
    }

    @Test
    public void cssLocator() {
        aludraWebGUI.verify().assertElementPresent("el", "op", new CSSLocator(".iceCmdLnk"));
        checkLastStepStatus(TestStatus.PASSED);
    }

    @Test
    public void cssLocator_notfound() {
        aludraWebGUI.verify().assertElementPresent("el", "op", new CSSLocator(".iceCmdLnk_notfound"));
        checkLastStepStatus(TestStatus.FAILED);
    }

    @Test
    public void labelLocator() {
        aludraWebGUI.verify().assertElementPresent("el", "op", new LabelLocator("Link"));
        checkLastStepStatus(TestStatus.PASSED);
    }

    @Test
    public void labelLocator_notfound() {
        aludraWebGUI.verify().assertElementPresent("el", "op", new LabelLocator("Link_notfound"));
        checkLastStepStatus(TestStatus.FAILED);
    }

    @Test
    public void xpathLocator() {
        aludraWebGUI.verify().assertElementPresent("el", "op", new XPathLocator("//a[@id='before:LinktoTThis:after']"));
        checkLastStepStatus(TestStatus.PASSED);
    }

    @Test
    public void xpathLocator_notfound() {
        aludraWebGUI.verify().assertElementPresent("el", "op", new XPathLocator("//a[@id='before:LinktoTThis:after_notfound']"));
        checkLastStepStatus(TestStatus.FAILED);
    }

    @Test
    public void elementLocators_notfound() {
        ElementLocatorsGUI elementLocators = (ElementLocatorsGUI) new ElementLocators(new IdLocator("xlkhbsdcbli"),
                new IdLocator("klsdbckjsbdc"), new IdLocator("sdknclksndc")).newMutableInstance();
        aludraWebGUI.verify().assertElementPresent("el", "op", elementLocators);
        assertNull(elementLocators.getUsedOption());
        checkLastStepStatus(TestStatus.FAILED);
    }

    @Test
    public void elementLocators_mutable() {
        IdLocator opt1 = new IdLocator("xlkhbsdcbli");
        XPathLocator opt2 = new XPathLocator("//a[@id='before:LinktoTThis:after']");

        ElementLocators locators = new ElementLocators(opt1, opt2);
        ElementLocatorsGUI mutableLocators1 = (ElementLocatorsGUI) locators.newMutableInstance();
        ElementLocatorsGUI mutableLocators2 = (ElementLocatorsGUI) locators.newMutableInstance();
        mutableLocators1.setUsedOption(opt1);
        assertEquals(opt1, mutableLocators1.getUsedOption());
        assertNull(mutableLocators2.getUsedOption());
        mutableLocators2.setUsedOption(opt2);
        assertEquals(opt2, mutableLocators2.getUsedOption());
        assertEquals(opt1, mutableLocators1.getUsedOption());
    }

}