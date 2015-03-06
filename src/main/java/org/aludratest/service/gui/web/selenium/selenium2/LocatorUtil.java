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

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.aludratest.exception.AludraTestException;
import org.aludratest.exception.AutomationException;
import org.aludratest.exception.TechnicalException;
import org.aludratest.service.locator.Locator;
import org.aludratest.service.locator.element.CSSLocator;
import org.aludratest.service.locator.element.ElementLocators.ElementLocatorsGUI;
import org.aludratest.service.locator.element.IdLocator;
import org.aludratest.service.locator.element.LabelLocator;
import org.aludratest.service.locator.element.XPathLocator;
import org.aludratest.util.retry.RetryService;
import org.aludratest.util.timeout.TimeoutService;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Maps AludraTest {@link Locator}s to Selenium 2 {@link By} objects.
 * @author Volker Bergmann
 */
public class LocatorUtil {

    /** Private constructor of utility class preventing instantiation by other classes */
    private LocatorUtil() {
    }

    /** Finds an element using Selenium's internal timeout mechanism.
     * @param locator the locator of the element to find
     * @param timeout the maximum time to wait
     * @param driver the WebDriver to use
     * @return */
    public static WebElement findElementWithInternalTimeout(Locator locator, long timeout, WebDriver driver) {
        try {
            driver.manage().timeouts().implicitlyWait(timeout, TimeUnit.MILLISECONDS);
            return findElement(locator, driver);
        }
        catch (NoSuchElementException e) {
            throw new AutomationException("Element could not be found.", e);
        }
        finally {
            driver.manage().timeouts().implicitlyWait(100, TimeUnit.MILLISECONDS);
        }
    }

    /** Finds an element imposing an external timeout and interrupting the lookup when the timeout is exceeded. This is provided in
     * order to overcome Selenium issue 402 which makes the ChromeDriver hang on lookups and timeout after 600 seconds. See <a
     * href="https://code.google.com/p/chromedriver/issues/detail?id=402">code.google.com</a>
     * @param locator
     * @param driver
     * @param timeout
     * @return */
    public static WebElement findElementWithExternalTimeout(final Locator locator, final WebDriver driver, long timeout) {
        try {
            Callable<WebElement> finder = new Callable<WebElement>() {
                @Override
                public WebElement call() throws Exception {
                    return findElement(locator, driver);
                }
            };
            Callable<WebElement> finderWithTimeout = TimeoutService.createCallableWithTimeout(finder, timeout);
            return RetryService.call(finderWithTimeout, TimeoutException.class, 1);
        }
        catch (NoSuchElementException e) {
            throw new AutomationException("Element could not be found.", e);
        }
        catch (Throwable e) {
            if (e instanceof AludraTestException) {
                throw (AludraTestException) e;
            }
            else {
                throw new TechnicalException("Error resolving locator", e);
            }
        }
    }

    /** Finds an element using a preset timeout using Selenium's internal timeout mechanism.
     * @param locator locator of the element to find
     * @param driver the WebDriver to use
     * @return */
    public static WebElement findElement(Locator locator, WebDriver driver) {
        return driver.findElement(by(locator));
    }

    /** Implements Selenium 2's {@link By} interface in a way that supports AludraTest's {@link Locator}s
     * @param locator the AludraTest locator of the element(s) to look up.
     * @return */
    public static By by(Locator locator) {
        if (locator == null) {
            throw new IllegalArgumentException("Locator is null");
        }
        else if (locator instanceof ElementLocatorsGUI) {
            return new ByElementLocators((ElementLocatorsGUI) locator);
        } else if (locator instanceof IdLocator) {
            return By.cssSelector("[id$='" + locator.toString() + "']");
        } else if (locator instanceof CSSLocator) {
            return By.cssSelector(locator.toString());
        } else if (locator instanceof LabelLocator) {
            return By.linkText(locator.toString());
        } else if (locator instanceof XPathLocator) {
            return By.xpath(locator.toString());
        } else {
            throw new UnsupportedOperationException("Unsupported locator type: " + locator.getClass().getName());
        }
    }

}
