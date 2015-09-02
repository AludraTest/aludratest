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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.slf4j.LoggerFactory;

/** Enumerates the available web drivers for Selenium 2.
 * @author Volker Bergmann */
public class Selenium2Driver {

    // Note: Needed to avoid 'enum' approach since this would make plexus' generate-metadata goal fail which considers a method
    // call in an enum constructor call to be a syntax error

    private static final Map<String, Selenium2Driver> INSTANCES = new HashMap<String, Selenium2Driver>();

    // Constants for the instances ---------------------------------------------

    /** Firefox driver */
    public static Selenium2Driver FIREFOX = new Selenium2Driver("FIREFOX", BrowserType.FIREFOX, FirefoxDriver.class,
            DesiredCapabilities.firefox());

    /** Internet Explorer driver */
    public static Selenium2Driver INTERNET_EXPLORER = new Selenium2Driver("INTERNET_EXPLORER", BrowserType.IE, InternetExplorerDriver.class,
            DesiredCapabilities.internetExplorer());

    /** HTMLUnit driver */
    public static Selenium2Driver HTML_UNIT = new Selenium2Driver("HTML_UNIT", BrowserType.HTMLUNIT, HtmlUnitDriver.class,
            createHtmlUnitCaps());

    /** Google Chrome driver */
    public static Selenium2Driver CHROME = new Selenium2Driver("CHROME", BrowserType.GOOGLECHROME, ChromeDriver.class, createChromeCaps());

    /** Safari driver */
    public static Selenium2Driver SAFARI = new Selenium2Driver("SAFARI", BrowserType.SAFARI, SafariDriver.class, DesiredCapabilities.safari());

    /** PhantomJS driver */
    public static Selenium2Driver PHANTOMJS = new Selenium2Driver("PHANTOMJS", BrowserType.PHANTOMJS, PhantomJSDriver.class,
            createPhantomJsCaps());

    // attributes --------------------------------------------------------------

    private final String browserName;
    private final Class<? extends WebDriver> driverClass;
    private final DesiredCapabilities capabilities;

    // constructor -------------------------------------------------------------

    private Selenium2Driver(String driverName, String browserName, Class<? extends WebDriver> driverClass,
            DesiredCapabilities capabilities) {
        this.browserName = browserName;
        this.driverClass = driverClass;
        this.capabilities = capabilities;
        INSTANCES.put(driverName, this);
    }

    // public interface --------------------------------------------------------

    /** @return the name of the related browser */
    public String getBrowserName() {
        return browserName;
    }

    /** @return a freshly created instance of the related WebDriver class */
    public WebDriver newLocalDriver() {
        Constructor<?> cstr = null;
        try {
            cstr = driverClass.getConstructor(DesiredCapabilities.class);
        }
        catch (SecurityException e) {
            throw new RuntimeException(e);
        }
        catch (NoSuchMethodException e) {
            try {
                cstr = driverClass.getConstructor(Capabilities.class);
            }
            catch (SecurityException e1) {
                throw new WebDriverException(e1);
            }
            catch (NoSuchMethodException e1) {
                throw new WebDriverException(e1);
            }
        }

        try {
            return (WebDriver) cstr.newInstance(capabilities);
        }
        catch (IllegalArgumentException e) {
            throw new WebDriverException(e);
        }
        catch (InstantiationException e) {
            throw new WebDriverException(e);
        }
        catch (IllegalAccessException e) {
            throw new WebDriverException(e);
        }
        catch (InvocationTargetException e) {
            throw new WebDriverException(e);
        }
    }

    /** @param url the URL for which to create a WebDriver instance.
     * @param arguments Additional arguments to include for the WebDriver instance, or an empty array.
     * @return a freshly created instance of the related WebDriver class */
    public WebDriver newRemoteDriver(URL url, String[] arguments) {
        AludraSeleniumHttpCommandExecutor executor = new AludraSeleniumHttpCommandExecutor(url);

        DesiredCapabilities caps = capabilities;
        if (arguments != null && arguments.length > 0) {
            caps = new DesiredCapabilities(capabilities);
            // this looks strange, but is the only way to avoid having all Threads sharing the same ChromeOptions object
            ChromeOptions opts = (ChromeOptions) createChromeCaps().getCapability(ChromeOptions.CAPABILITY);
            if (opts != null) {
                opts.addArguments(arguments);
                caps.setCapability(ChromeOptions.CAPABILITY, opts);
            }
        }

        try {
            RemoteWebDriver driver = new RemoteWebDriver(executor, caps);
            driver.setFileDetector(new LocalFileDetector());
            return driver;
        }
        catch (WebDriverException e) {
            LoggerFactory.getLogger(Selenium2Driver.class).error(
                    "Could not create remote web driver. Last remote HTTP response: " + executor.getLastResponse());
            throw e;
        }
    }

    // public static interface -------------------------------------------------

    /** @param driverName the name of the driver
     * @return the driver instance associated with the driverName
     * @throws IllegalArgumentException if no driver is configured with this name */
    public static Selenium2Driver valueOf(String driverName) {
        Selenium2Driver driver = INSTANCES.get(driverName);
        if (driver == null) {
            throw new IllegalArgumentException("Unknown driver: " + driverName);
        }
        return driver;
    }

    // private helper methods --------------------------------------------------

    private static DesiredCapabilities createChromeCaps() {
        DesiredCapabilities caps = DesiredCapabilities.chrome();
        ChromeOptions opts = new ChromeOptions();
        opts.addArguments("--disable-extensions");
        caps.setCapability(ChromeOptions.CAPABILITY, opts);
        return caps;
    }

    private static DesiredCapabilities createPhantomJsCaps() {
        DesiredCapabilities caps = DesiredCapabilities.phantomjs();
        caps.setJavascriptEnabled(true);
        caps.setCapability("takesScreenshot", true);
        caps.setCapability("phantomjs.cli.args", new String[] { "--web-security=no", "--ssl-protocol=any",
        "--ignore-ssl-errors=yes" });
        return caps;
    }

    private static DesiredCapabilities createHtmlUnitCaps() {
        DesiredCapabilities caps = DesiredCapabilities.htmlUnit();
        caps.setJavascriptEnabled(true);
        caps.setBrowserName(BrowserType.CHROME);
        return caps;
    }

}
