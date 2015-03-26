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
package org.aludratest.service.gui.web.selenium.selenium2.condition;

import org.aludratest.service.gui.web.selenium.selenium2.Selenium2Wrapper;
import org.aludratest.service.locator.window.TitleLocator;
import org.aludratest.service.locator.window.WindowLocator;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

/** Checks the presence of a window.
 * @author Volker Bergmann */
public class WindowPresence implements ExpectedCondition<Boolean> {

    private TitleLocator locator;
    private Selenium2Wrapper wrapper;

    /** Constructor.
     * @param locator
     * @param wrapper */
    public WindowPresence(WindowLocator locator, Selenium2Wrapper wrapper) {
        if (!(locator instanceof TitleLocator)) {
            throw new UnsupportedOperationException("Locator type not supported in waitForWindow(): " + locator.getClass());
        }
        this.locator = (TitleLocator) locator;
        this.wrapper = wrapper;
    }

    @Override
    public Boolean apply(WebDriver input) {
        final String searchedTitle = locator.getTitle();
        final String[] titles;
        try {
            titles = wrapper.getAllWindowTitles();
        }
        catch (NoSuchWindowException e) {
            // ignore (just closed); try in next scan
            return false;
        }
        for (String title : titles) {
            if (title.equalsIgnoreCase(searchedTitle)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "presence of the window located by " + locator;
    }

}
