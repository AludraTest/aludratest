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
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.aludratest.exception.AludraTestException;
import org.aludratest.exception.AutomationException;
import org.aludratest.exception.TechnicalException;
import org.aludratest.service.gui.web.selenium.SeleniumWrapperConfiguration;
import org.aludratest.service.locator.Locator;
import org.aludratest.service.locator.element.CSSLocator;
import org.aludratest.service.locator.element.ElementLocators.ElementLocatorsGUI;
import org.aludratest.service.locator.element.GUIElementLocator;
import org.aludratest.service.locator.element.IdLocator;
import org.aludratest.service.locator.element.LabelLocator;
import org.aludratest.service.locator.element.XPathLocator;
import org.aludratest.util.AludraTestUtil;
import org.aludratest.util.retry.RetryService;
import org.aludratest.util.timeout.TimeoutService;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Maps AludraTest {@link Locator}s to Selenium 2 {@link By} objects.
 * @author Volker Bergmann
 */
public class LocatorSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocatorSupport.class);

    private static final int MAX_RETRIES_ON_STALE_ELEMENT = 3;
    private static final int SECOND_MILLIS = 1000;
    private static final int DEFAULT_IMPLICIT_WAIT_MILLIS = 100;

    private final WebDriver driver;
    private final SeleniumWrapperConfiguration config;

    /** Private constructor of utility class preventing instantiation by other classes
     * @param driver
     * @param config */
    public LocatorSupport(WebDriver driver, SeleniumWrapperConfiguration config) {
        this.driver = driver;
        this.config = config;
    }

    /** @return the {@link #driver} */
    public WebDriver getDriver() {
        return driver;
    }

    /** Performs an immediate element lookup (meaning no implicit Selenium wait time is imposed). It is expected to return
     * immediately. Unfortunately there exists ChromeDriver issue #402 which sometimes makes the ChromeDriver hang on lookups and
     * timeout after 600 seconds. See <a href="https://code.google.com/p/chromedriver/issues/detail?id=402">code.google.com</a>.
     * In order to overcome this issue, an external timeout is applied to interrupting the lookup when the call can be assumed to
     * be hanging.
     * @param locator
     * @return the element if it was found
     * @throws {@link NoSuchElementException} if the element was not found
     * @throws AutomationException if the timeout applied and the driver is assumed to be hanging */
    public WebElement findElementImmediatelyWithResponseTimeout(final GUIElementLocator locator) {
        Callable<WebElement> finder = new Callable<WebElement>() {
            @Override
            public WebElement call() throws Exception {
                return findElementImmediately(locator);
            }
        };
        Callable<WebElement> finderWithTimeout = TimeoutService.createCallableWithTimeout(finder, config.getTimeout());
        try {
            return RetryService.call(finderWithTimeout, java.util.concurrent.TimeoutException.class, 1);
        }
        catch (java.util.concurrent.TimeoutException e) {
            throw new AutomationException("Interrupted hanging driver", e);
        }
        catch (NoSuchElementException e) {
            throw e;
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

    /** Looks up an element immediately without implicit or explicit wait.
     * @param locator
     * @return the element if it is found
     * @throws NoSuchElementException if no matching element is found */
    public WebElement findElementImmediately(GUIElementLocator locator) {
        LOGGER.debug("findElementImmediately({})", locator);
        try {
            driver.manage().timeouts().implicitlyWait(0, TimeUnit.MILLISECONDS);
            return findElement(locator, config.getTimeout());
        }
        finally {
            driver.manage().timeouts().implicitlyWait(DEFAULT_IMPLICIT_WAIT_MILLIS, TimeUnit.MILLISECONDS);
        }
    }

    /** Finds an element using Selenium's internal timeout mechanism.
     * @param locator the locator of the element to find
     * @param timeOutInMillis the maximum time to wait
     * @return the element if it is found
     * @throws NoSuchElementException if no matching element is found */
    public WebElement findElementWithImplicitWait(GUIElementLocator locator, long timeOutInMillis) {
        LOGGER.debug("findElementWithImplicitWait({}, {})", locator, timeOutInMillis);
        try {
            driver.manage().timeouts().implicitlyWait(timeOutInMillis, TimeUnit.MILLISECONDS);
            return findElement(locator, timeOutInMillis);
        }
        finally {
            driver.manage().timeouts().implicitlyWait(DEFAULT_IMPLICIT_WAIT_MILLIS, TimeUnit.MILLISECONDS);
        }
    }

    /** Provides the parent of a given {@link WebElement}.
     * @param child
     * @return */
    public WebElement getParent(final WebElement child) {
        WebElement parent = child.findElement(By.xpath(".."));
        return wrapElement(parent, new ElementLookup() {
            @Override
            public WebElement perform() {
                return child.findElement(By.xpath(".."));
            }
        });

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
        while (element instanceof ElementWrapper) {
            element = ((ElementWrapper) element).getWrappedElement();
        }
        return element;
    }

    // wait features -----------------------------------------------------------

    /** @param locator
     * @param timeOutInMillis
     * @return */
    @SuppressWarnings("unchecked")
    public WebElement waitUntilPresent(final GUIElementLocator locator, final long timeOutInMillis) {
        ExpectedCondition<WebElement> condition = new ExpectedCondition<WebElement>() {
            @Override
            public WebElement apply(WebDriver driver) {
                return findElementImmediatelyWithResponseTimeout(locator);
            }
        };
        try {
            return waitFor(condition, timeOutInMillis, NoSuchElementException.class);
        }
        catch (TimeoutException e) {
            throw new AutomationException("Element not found"); // NOSONAR
        }
    }

    /** Waits until a condition is met.
     * @param condition
     * @param driver
     * @param timeOutInMillis
     * @param millisBetweenRetries
     * @param exceptionsToIgnore
     * @return */
    public <T> T waitFor(ExpectedCondition<T> condition, long timeOutInMillis, Class<? extends Exception>... exceptionsToIgnore) {
        long timeoutInSeconds = (timeOutInMillis + SECOND_MILLIS - 1) / SECOND_MILLIS;
        WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds, config.getPauseBetweenRetries());
        for (Class<? extends Exception> exceptionToIgnore : exceptionsToIgnore) {
            wait.ignoring(exceptionToIgnore);
        }
        return wait.until(condition);
    }

    // non-public helpers ------------------------------------------------------

    /** Finds an element using a preset timeout using Selenium's internal timeout mechanism.
     * @param locator locator of the element to find
     * @param relocationTimeout the timeout to apply when a call on the found element causes a StaleElementReferenceException and
     *            the element lookup is repeated
     * @return the element if it is found
     * @throws NoSuchElementException if no matching element is found */
    private WebElement findElement(final GUIElementLocator locator, final long relocationTimeout) {
        WebElement element = driver.findElement(by(locator));
        return wrapElement(element, new ElementLookup() {
            @Override
            public WebElement perform() {
                return waitUntilPresent(locator, relocationTimeout);
            }
        });
    }

    private WebElement wrapElement(WebElement element, ElementLookup lookup) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InvocationHandler handler = new WebElementProxyHandler(element, lookup);
        return (WebElement) Proxy.newProxyInstance(classLoader, new Class[] { ElementWrapper.class }, handler);
    }

    interface ElementLookup {
        WebElement perform();
    }

    class WebElementProxyHandler implements InvocationHandler {

        private WebElement realElement;
        private ElementLookup lookup;

        public WebElementProxyHandler(WebElement realElement, ElementLookup lookup) {
            this.realElement = realElement;
            this.lookup = lookup;
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

        private Object invokeRealElement(Method method, Object[] args) throws Throwable {
            StaleElementReferenceException staleEx = null;
            // try to get z order up to 3 times
            for (int invocationCount = 0; invocationCount < MAX_RETRIES_ON_STALE_ELEMENT; invocationCount++) {
                try {
                    return method.invoke(realElement, args);
                }
                catch (InvocationTargetException invocationEx) {
                    Throwable cause = AludraTestUtil.unwrapInvocationTargetException(invocationEx);
                    if (cause instanceof StaleElementReferenceException) {
                        // ... on stale refs, catch and store the exception...
                        staleEx = (StaleElementReferenceException) cause;
                        // relocate the element
                        this.realElement = lookup.perform();
                        // and repeat (or finish) the loop
                    }
                    else {
                        // in case of another failure, provide it to the caller immediately
                        throw cause;
                    }
                }
            }
            // code has repeatedly failed, so forward the last exception
            throw staleEx;
        }

    }

}
