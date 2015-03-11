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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.TimeUnit;

import org.aludratest.exception.AutomationException;
import org.aludratest.service.locator.Locator;
import org.aludratest.service.locator.element.CSSLocator;
import org.aludratest.service.locator.element.ElementLocators.ElementLocatorsGUI;
import org.aludratest.service.locator.element.GUIElementLocator;
import org.aludratest.service.locator.element.IdLocator;
import org.aludratest.service.locator.element.LabelLocator;
import org.aludratest.service.locator.element.XPathLocator;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Maps AludraTest {@link Locator}s to Selenium 2 {@link By} objects.
 * @author Volker Bergmann
 */
public class LocatorUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocatorUtil.class);

    /** Private constructor of utility class preventing instantiation by other classes */
    private LocatorUtil() {
    }

    /** Looks up an element immediately without implicit or explicit wait.
     * @param locator
     * @param driver
     * @return */
    public static WebElement findElementImmediately(GUIElementLocator locator, WebDriver driver) {
        return findElementWithImplicitWait(locator, 0, driver);
    }

    /** Finds an element using Selenium's internal timeout mechanism.
     * @param locator the locator of the element to find
     * @param timeout the maximum time to wait
     * @param driver the WebDriver to use
     * @return */
    public static WebElement findElementWithImplicitWait(GUIElementLocator locator, long timeout, WebDriver driver) {
        LOGGER.debug("findElementWithImplicitWait({})", locator);
        try {
            driver.manage().timeouts().implicitlyWait(timeout, TimeUnit.MILLISECONDS);
            return findElement(locator, driver);
        }
        catch (NoSuchElementException e) {
            return null;
        }
        finally {
            driver.manage().timeouts().implicitlyWait(100, TimeUnit.MILLISECONDS);
        }
    }

    /** Finds an element using a preset timeout using Selenium's internal timeout mechanism.
     * @param locator locator of the element to find
     * @param driver the WebDriver to use
     * @return */
    public static WebElement findElement(GUIElementLocator locator, WebDriver driver) {
        return wrapElement(locator, driver, driver.findElement(by(locator)));
    }

    /** Implements Selenium 2's {@link By} interface in a way that supports AludraTest's {@link GUIElementLocator}s
     * @param locator the AludraTest locator of the element(s) to look up.
     * @return */
    public static By by(GUIElementLocator locator) {
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

    /** Unwraps {@link ElementWrapper}s.
     * @param element
     * @return */
    public static WebElement unwrap(WebElement element) {
        if (element instanceof ElementWrapper) {
            return ((ElementWrapper) element).getWrappedElement();
        }
        else {
            return element;
        }
    }

    // non-public helpers ------------------------------------------------------

    private static WebElement wrapElement(GUIElementLocator locator, WebDriver driver, WebElement element) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InvocationHandler handler = new WebElementProxyHandler(locator, driver, element);
        return (WebElement) Proxy.newProxyInstance(classLoader, new Class[] { ElementWrapper.class }, handler);
    }

    static class WebElementProxyHandler implements InvocationHandler {

        private static final int MAX_RETRIES_ON_STALE_ELEMENT = 3;

        private GUIElementLocator locator;
        private WebDriver driver;
        private WebElement realElement;

        public WebElementProxyHandler(GUIElementLocator locator, WebDriver driver, WebElement realElement) {
            this.locator = locator;
            this.driver = driver;
            this.realElement = realElement;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ("getWrappedElement".equals(method.getName())) {
                return realElement;
            }
            else {
                return invokeRealElement(method, args);
            }
        }

        private Object invokeRealElement(Method method, Object[] args) throws IllegalAccessException, InvocationTargetException {
            StaleElementReferenceException exception = null;
            // try to get z order up to 3 times
            for (int invocationCount = 0; invocationCount < MAX_RETRIES_ON_STALE_ELEMENT; invocationCount++) {
                try {
                    return method.invoke(realElement, args);
                }
                catch (StaleElementReferenceException e) {
                    // ... on failure, catch and store the exception...
                    exception = e;
                    // relocate the element
                    this.realElement = findElementImmediately(locator, driver);
                    if (this.realElement == null) {
                        throw new AutomationException("Element disappeared");
                    }
                    // and repeat (or finish) the loop
                }
            }
            // code has repeatedly failed, so forward the last exception
            throw exception;
        }

    }

}
