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

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;

public enum Drivers {

    FIREFOX(BrowserType.FIREFOX, FirefoxDriver.class), INTERNET_EXPLORER(BrowserType.IE, InternetExplorerDriver.class), HTML_UNIT(
            BrowserType.HTMLUNIT, HtmlUnitDriver.class), CHROME(BrowserType.GOOGLECHROME, ChromeDriver.class), SAFARI(
                    BrowserType.SAFARI, SafariDriver.class), PHANTOMJS(BrowserType.PHANTOMJS, RemoteWebDriver.class);

    private String browserName;
    private Class<? extends WebDriver> driverClass;

    private Drivers(String browserName, Class<? extends WebDriver> driverClass) {
        this.browserName = browserName;
        this.driverClass = driverClass;
    }

    public Class<? extends WebDriver> getDriverClass() {
        return driverClass;
    }

    public String getBrowserName() {
        return browserName;
    }

}
