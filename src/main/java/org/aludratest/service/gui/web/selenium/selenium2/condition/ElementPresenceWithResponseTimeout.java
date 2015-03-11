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

import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

import org.aludratest.exception.AludraTestException;
import org.aludratest.exception.AutomationException;
import org.aludratest.exception.TechnicalException;
import org.aludratest.service.gui.web.selenium.selenium2.LocatorUtil;
import org.aludratest.service.locator.element.GUIElementLocator;
import org.aludratest.util.retry.RetryService;
import org.aludratest.util.timeout.TimeoutService;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

/** Performs an element lookup imposing an external timeout and interrupting the lookup when the timeout is exceeded. This is
 * provided in order to overcome Selenium issue 402 which makes the ChromeDriver hang on lookups and timeout after 600 seconds.
 * See <a href="https://code.google.com/p/chromedriver/issues/detail?id=402">code.google.com</a>
 * @author Volker Bergmann */
public class ElementPresenceWithResponseTimeout implements ExpectedCondition<WebElement> {

    final GUIElementLocator locator;
    final long timeout;

    /** Full constructor.
     * @param locator
     * @param timeout */
    public ElementPresenceWithResponseTimeout(GUIElementLocator locator, long timeout) {
        this.locator = locator;
        this.timeout = timeout;
    }

    @Override
    public WebElement apply(WebDriver driver) {
        return findElementWithResponseTimeout(locator, driver, timeout);
    }

    /** Performs the element lookup imposing an explicit external timeout.
     * @param locator
     * @param driver
     * @param timeout
     * @return the element if it was found within the timeout, otherwise null */
    public static WebElement findElementWithResponseTimeout(final GUIElementLocator locator, final WebDriver driver,
            final long timeout) {
        try {
            Callable<WebElement> finder = new Callable<WebElement>() {
                @Override
                public WebElement call() throws Exception {
                    return LocatorUtil.findElementImmediately(locator, driver);
                }
            };
            Callable<WebElement> finderWithTimeout = TimeoutService.createCallableWithTimeout(finder, timeout);
            return RetryService.call(finderWithTimeout, TimeoutException.class, 1);
        }
        catch (NoSuchElementException e) {
            return null;
        }
        catch (TimeoutException e) {
            throw new AutomationException("Element not found. ", e);
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

    @Override
    public String toString() {
        return "presence check with response timeout for element located by: " + locator;
    }

}
