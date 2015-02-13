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

import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.invisibilityOfElementLocated;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.aludratest.exception.AutomationException;
import org.aludratest.exception.PerformanceFailure;
import org.aludratest.exception.TechnicalException;
import org.aludratest.service.gui.web.selenium.SeleniumWrapperConfiguration;
import org.aludratest.service.locator.Locator;
import org.aludratest.service.locator.element.GUIElementLocator;
import org.aludratest.service.locator.element.IdLocator;
import org.aludratest.service.locator.option.IndexLocator;
import org.aludratest.service.locator.option.OptionLocator;
import org.aludratest.service.locator.window.TitleLocator;
import org.aludratest.service.locator.window.WindowLocator;
import org.aludratest.service.util.ServiceUtil;
import org.aludratest.util.data.helper.DataMarkerCheck;
import org.apache.commons.io.IOUtils;
import org.databene.commons.CollectionUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.ErrorHandler.UnknownServerException;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.selenium.SeleniumException;

/**
 * Wraps an instance of Selenium's {@link WebDriver} class (Selenium 2.x)
 * and provides delegation methods.
 * @author Volker Bergmann
 */
public class Selenium2Facade {

    private static final Logger LOGGER = LoggerFactory.getLogger(Selenium2Facade.class);

    // constants ---------------------------------------------------------------

    private static final int MAX_RETRIES_ON_STALE_ELEMENT = 3;

    private static final String HAS_FOCUS_SCRIPT = "return arguments[0] == window.document.activeElement";

    // @formatter:off
    private static final String FIRE_ONCHANGE_SCRIPT = "var element = arguments[0]; var event; "
            + "if (document.createEvent) {"
            + "    event = document.createEvent(\"HTMLEvents\");"
            + "    event.initEvent(\"change\", true, false);"
            + "  } else {"
            + "    event = document.createEventObject();"
            + "    event.eventType = \"change\";"
            + "  }"
            + "  event.eventName = \"change\";"
            + "  if (document.createEvent) {"
            + "    element.dispatchEvent(event);"
            + "  } else {"
            + "    element.fireEvent(\"on\" + event.eventType, event);"
            + "  }";
    // @formatter:on

    private static final String DROPDOWN_OPTION_VALUE_PROPERTY = "value";
    private static final String DROPDOWN_OPTION_LABEL_PROPERTY = "text";

    private static final Set<String> EDITABLE_ELEMENTS = CollectionUtil.toSet("input", "textarea", "select", "a");

    // attributes --------------------------------------------------------------

    private SeleniumWrapperConfiguration configuration;

    private WebDriver driver;

    private ZIndexSupport zIndexSupport;

    private WebElement highlightedElement;

    private URL seleniumUrl;

    // constructor -------------------------------------------------------------

    public Selenium2Facade(SeleniumWrapperConfiguration configuration, String seleniumUrl)
            throws MalformedURLException {
        this.configuration = configuration;
        this.seleniumUrl = new URL(seleniumUrl + "/wd/hub");
        this.driver = newDriver();
        this.zIndexSupport = new ZIndexSupport(driver);

        // configure Selenium class to not log annoying (expected) exceptions
        java.util.logging.Logger.getLogger(ExpectedConditions.class.getName()).setLevel(java.util.logging.Level.OFF);
    }

    /** Creates an instance of the web driver configured in the configuration file. */
    private WebDriver newDriver() {
        try {
            String driverName = configuration.getDriverName();
            Selenium2Driver driverEnum = Selenium2Driver.valueOf(driverName);

            if (configuration.isUsingRemoteDriver()) {
                return driverEnum.newRemoteDriver(seleniumUrl);
            }
            else {
                return driverEnum.newLocalDriver();
            }
        }
        catch (Exception e) {
            throw new TechnicalException("WebDriver creation failed: ", e);
        }
    }

    // interface ---------------------------------------------------------------

    public void selectWindow(String windowId) {
        removeHighlight();
        driver.switchTo().window(windowId);
    }

    public void close() {
        try {
            driver.close();
        }
        catch (WebDriverException e) {
            // ignore this
        }
    }

    public void quit() {
        driver.quit();
    }

    public void open(String url) {
        try {
            driver.get(url);
        } catch (SeleniumException e) {
            String message = e.getMessage();
            if (message != null && message.contains("Timed out")) {
                throw new PerformanceFailure("Timed out opening '" + url + "': " + e);
            }
        }
    }

    public void refresh() {
        driver.navigate().refresh();
    }

    private void removeHighlight() {
        if (configuration.getHighlightCommands() && this.highlightedElement != null) {
            try {
                executeScript(
                        "arguments[0].className = arguments[0].className.replace( /(?:^|\\s)selenium-highlight(?!\\S)/g , '' ); return arguments[0].className;",
                        highlightedElement);
            }
            catch (WebDriverException e) {
                LOGGER.trace("Highlight remove failed. ", e);
            }
        }
    }

    private void checkHighlightCss() {
        // check if our hidden element is present
        try {
            findElement(new IdLocator("__aludra_selenium_hidden"), 1000);
        }
        catch (Exception e) {
            // add CSS and element
            executeScript("var css = document.createElement('style'); css.setAttribute('id', '__aludra_selenium_css'); css.setAttribute('type', 'text/css'); css.innerHTML = '.selenium-highlight { border: 3px solid red !important; }'; document.getElementsByTagName('head')[0].appendChild(css);");
            // add hidden element
            executeScript("var hidden = document.createElement('div'); hidden.setAttribute('id', '__aludra_selenium_hidden'); hidden.setAttribute('style', 'display:none;'); document.getElementsByTagName('body')[0].appendChild(hidden);");
        }
    }

    public void highlight(GUIElementLocator locator) {
        if (configuration.getHighlightCommands()) {
            try {
                removeHighlight();

                // ensure that current document has highlight CSS class
                checkHighlightCss();

                WebElement elementToHighlight = findElement(locator);
                executeScript("arguments[0].className +=' selenium-highlight'", elementToHighlight);
                this.highlightedElement = elementToHighlight;
            } catch (WebDriverException e) {
                // It does not matter if highlighting works or not, why a
                // possibly thrown exception must be caught to avoid test
                // execution termination.
                LOGGER.trace("Highlighting failed. ", e);
            }
        }
    }

    public void click(GUIElementLocator locator) {
        WebElement element = findElement(locator);
        try {
            element.click();
        }
        catch (Exception e) {
            handleSeleniumException(e);
        }
    }

    public boolean isEditable(GUIElementLocator locator) {
        return isEditable(findElement(locator));
    }

    public boolean isElementPresent(GUIElementLocator elementLocator) {
        try {
            findElement(elementLocator);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public boolean isInForeground(GUIElementLocator locator) {
        StaleElementReferenceException exception = null;
        // try to get z order up to 3 times
        for (int i = 0; i < MAX_RETRIES_ON_STALE_ELEMENT; i++) {
            try {
                waitUntilPresent(locator);

                WebElement element = findElement(locator);
                // on success, return the value...
                return zIndexSupport.isInForeground(element);
            } catch (StaleElementReferenceException e) {
                // ... on failure, catch and store the exception...
                exception = e;
                // and repeat (or finish) the loop
            }
        }
        // code has repeatedly failed, so forward the last exception
        throw exception;
    }

    public void setValue(GUIElementLocator locator, String value) {
        WebElement element = findElement(locator);
        String id = element.getAttribute("id");
        String fieldType = element.getAttribute("type");
        boolean fallback = true;
        if (!DataMarkerCheck.isNull(id) || "file".equals(fieldType)) {
            executeScript("document.getElementById('" + id + "').setAttribute('value', '" + value.replace("'", "\\'") + "')");
            // validate success
            if (value.equals(element.getAttribute("value"))) {
                fallback = false;
                executeScript(FIRE_ONCHANGE_SCRIPT, element);
            }
        }

        // fallback code
        if (fallback) {
            element.sendKeys(Keys.END);
            String text;
            while (!DataMarkerCheck.isNull(text = element.getAttribute("value"))) {
                int length = text.length();
                String[] arr = new String[length];
                for (int i = 0; i < length; i++) {
                    arr[i] = "\b";
                }
                element.sendKeys(arr);
            }
            element.sendKeys(value);
        }
    }

    public void sendKeys(GUIElementLocator locator, String keys) {
        findElement(locator).sendKeys(keys);
    }

    public void select(GUIElementLocator locator, OptionLocator optionLocator) {
        WebElement element = findElement(locator);
        Select select = new Select(element);
        if (optionLocator instanceof org.aludratest.service.locator.option.LabelLocator) {
            select.selectByVisibleText(optionLocator.toString());
        } else if (optionLocator instanceof IndexLocator) {
            select.selectByIndex(((IndexLocator) optionLocator).getIndex());
        } else {
            throw ServiceUtil.newUnsupportedLocatorException(optionLocator);
        }
    }

    public Object getText(GUIElementLocator locator) {
        return findElement(locator).getText();
    }

    public boolean isChecked(GUIElementLocator locator) {
        return findElement(locator).isSelected();
    }

    public String[] getSelectOptions(GUIElementLocator locator) {
        WebElement element = findElement(locator);
        Select select = new Select(element);
        List<WebElement> options = select.getOptions();
        String[] labels = new String[options.size()];
        for (int i = 0; i < options.size(); i++) {
            labels[i] = options.get(i).getText();
        }
        return labels;
    }

    public Object getSelectedValue(GUIElementLocator locator) {
        Select select = new Select(findElement(locator));
        return select.getFirstSelectedOption().getText();
    }

    public Object isVisible(GUIElementLocator locator) {
        return findElement(locator).isDisplayed();
    }

    public String getSelectedLabel(GUIElementLocator locator) {
        WebElement element = findElement(locator);
        Select select = new Select(element);
        return select.getFirstSelectedOption().getText();
    }

    public String getValue(GUIElementLocator locator) {
        return findElement(locator).getAttribute("value");
    }

    public void selectWindow(WindowLocator locator) {
        if (locator instanceof TitleLocator) {
            String requestedTitle = locator.toString();

            // performance tweak: if the current window is the requested one, return immediately
            try {
                if (requestedTitle.equals(driver.getTitle())) {
                    return;
                }
            } catch (NoSuchWindowException e) {
                // this happens, when trying to call getTitle()
                // on a driver which has just close()d the recent window.
                // In this case, I fall back to iterating all windows below
            }

            removeHighlight();

            // iterate all windows and return the one with the desired title
            for (String handle : getWindowHandles()) {
                try {
                    String title = driver.switchTo().window(handle).getTitle();
                    if (requestedTitle.equals(title)) {
                        return;
                    }
                }
                catch (NoSuchWindowException e) {
                    // ignore; window has been removed in the meantime
                }
            }

            // if the window has not been found, throw an ElementNotFoundException
            throw new AutomationException("Element not found");
        } else {
            throw ServiceUtil.newUnsupportedLocatorException(locator);
        }
    }

    public String[] getAllWindowIDs() {
        Set<String> handles = getWindowHandles();
        return handles.toArray(new String[handles.size()]);
    }

    public String[] getAllWindowTitles() {
        Map<String, String> handlesAndTitles = getAllWindowHandlesAndTitles();
        Collection<String> titles = handlesAndTitles.values();
        LOGGER.debug("getAllWindowTitles() -> {}", titles);
        return titles.toArray(new String[handlesAndTitles.size()]);
    }

    public Map<String, String> getAllWindowHandlesAndTitles() {
        Map<String, String> handlesAndTitles = new HashMap<String, String>();
        // store handle and title of current window, if possible
        String initialWindowHandle;
        try {
            initialWindowHandle = driver.getWindowHandle();
        }
        catch (NoSuchWindowException e) {
            // fallback to next best window
            Set<String> handles = driver.getWindowHandles();
            if (handles.isEmpty()) {
                return Collections.emptyMap();
            }
            initialWindowHandle = handles.iterator().next();
            driver.switchTo().window(initialWindowHandle);
        }
        String title = driver.getTitle();
        handlesAndTitles.put(initialWindowHandle, title);
        // iterate all other windows by handle and get their titles
        String currentHandle = initialWindowHandle;
        Set<String> handles = getWindowHandles();
        for (String handle : handles) {
            if (!handle.equals(initialWindowHandle)) {
                LOGGER.debug("Switching to window with handle {}", handle);
                try {
                    driver.switchTo().window(handle);
                    currentHandle = handle;
                    handlesAndTitles.put(handle, driver.getTitle());
                }
                catch (NoSuchWindowException e) {
                    // ignore this window
                }
                LOGGER.debug("Window with handle {} has title '{}'", handle, title);
            }
        }
        // switch back to the original window
        if (!currentHandle.equals(initialWindowHandle)) {
            driver.switchTo().window(initialWindowHandle);
        }
        return handlesAndTitles;
    }

    public String[] getAllWindowNames() {
        String current = driver.getWindowHandle();
        List<String> names = new ArrayList<String>();
        for (String handle : getWindowHandles()) {
            driver.switchTo().window(handle);
            names.add(executeScript("return window.name").toString());
        }
        driver.switchTo().window(current);
        return names.toArray(new String[names.size()]);
    }

    public String getTitle() {
        return driver.getTitle();
    }

    public String getWindowHandle() {
        return driver.getWindowHandle();
    }

    public void windowMaximize() {
        driver.manage().window().maximize();
    }

    public void windowFocus() {
        executeScript("window.focus()");
    }

    public void switchToIFrame(Locator iframeLocator) {
        if (iframeLocator == null) {
            driver.switchTo().defaultContent();
        }
        else {
            driver.switchTo().frame(findElement(iframeLocator));
        }
    }

    public String getHtmlSource() {
        return driver.getPageSource();
    }

    public String captureScreenshotToString() {
        // use Selenium1 interface to capture full screen
        String url = seleniumUrl.toString();
        Pattern p = Pattern.compile("(http(s)?://.+)/wd/hub(/?)");
        Matcher matcher = p.matcher(url);
        if (matcher.matches()) {
            String screenshotUrl = matcher.group(1);
            screenshotUrl += (screenshotUrl.endsWith("/") ? "" : "/") + "selenium-server/driver/?cmd=captureScreenshotToString";
            InputStream in = null;
            try {
                in = new URL(screenshotUrl).openStream();
                in.read(new byte[3]); // read away "OK,"
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                IOUtils.copy(in, baos);
                return new String(baos.toByteArray(), "UTF-8");
            }
            catch (IOException e) {
                // OK, fallthrough to Selenium 2 method
            }
            finally {
                IOUtils.closeQuietly(in);
            }
        }

        WebDriver screenshotDriver;
        if (RemoteWebDriver.class.isAssignableFrom(driver.getClass())) {
            screenshotDriver = new Augmenter().augment(driver);
        } else {
            screenshotDriver = driver;
        }
        if (screenshotDriver instanceof TakesScreenshot) {
            TakesScreenshot tsDriver = (TakesScreenshot) screenshotDriver;
            return tsDriver.getScreenshotAs(OutputType.BASE64);
        } else {
            throw new UnsupportedOperationException(driver.getClass() + " does not implement TakeScreenshot");
        }
    }

    public boolean hasFocus(Locator locator) {
        WebElement element = findElement(locator);
        return (Boolean) executeScript(HAS_FOCUS_SCRIPT, element);
    }

    public void focus(GUIElementLocator locator) {
        WebElement element = findElement(locator);
        if (!isEditable(element)) {
            throw new AutomationException("Element not editable.");
        }
        executeScript("arguments[0].focus()", element);
    }

    public String[] getDropDownValues(GUIElementLocator locator) {
        return getPropertyValues(locator, DROPDOWN_OPTION_VALUE_PROPERTY);
    }

    public String[] getDropDownLabels(GUIElementLocator locator) {
        return getPropertyValues(locator, DROPDOWN_OPTION_LABEL_PROPERTY);
    }

    public String getAttributeValue(GUIElementLocator locator, String attributeName) {
        return findElement(locator).getAttribute(attributeName);
    }

    public void keyPress(int keycode) {
        driver.switchTo().activeElement().sendKeys(String.valueOf((char) keycode));
    }

    public void doubleClick(GUIElementLocator locator) {
        WebElement element = findElement(locator);
        new Actions(driver).doubleClick(element).build().perform();
    }

    public String getTable(GUIElementLocator locator, int row, int col) {
        WebElement table = findElement(locator);
        List<WebElement> tbodies = table.findElements(By.tagName("tbody"));
        WebElement rowHolder = (tbodies.size() > 0 ? tbodies.get(0) : table);
        List<WebElement> trs = rowHolder.findElements(By.tagName("tr"));
        if (row >= trs.size()) {
            throw new AutomationException("Table cell not found. " +
                    "Requested row index " + row + " of " + trs.size() + " rows ");
        }
        List<WebElement> tds = trs.get(row).findElements(By.tagName("td"));
        if (col >= tds.size()) {
            throw new AutomationException("Table cell not found. " +
                    "Requested column index " + col + " of " + tds.size() + " cells ");
        }
        return tds.get(col).getText();
    }

    // private helpers -------------------------------------------------------------------------------------------------

    private void handleSeleniumException(Throwable e) {
        String message = e.getMessage();

        // check if there is a WebDriverException
        WebDriverException wde = null;
        while (e != null) {
            if (e instanceof WebDriverException) {
                wde = (WebDriverException) e;
                break;
            }
            e = e.getCause();
        }

        if (wde != null) {
            // "not clickable" exception
            Pattern p = Pattern.compile("(unknown error: )?(.* not clickable .*)");
            Matcher m;
            if (message != null && (m = p.matcher(message)).find() && m.start() == 0) {
                throw new AutomationException(m.group(2), wde);
            }

            throw wde;
        }
    }

    private Object executeScript(String script, Object... arguments) {
        return ((JavascriptExecutor) driver).executeScript(script, arguments);
    }

    private boolean isEditable(WebElement element) {
        for (int i = 0; i < MAX_RETRIES_ON_STALE_ELEMENT; i++) {
            try {
                if (!element.isEnabled()) {
                    return false;
                }
                String tagName = element.getTagName().toLowerCase();
                if (!EDITABLE_ELEMENTS.contains(tagName)) {
                    return false;
                }
                if ("input".equals(tagName)) {
                    String readonly = element.getAttribute("readonly");
                    return (readonly == null || "false".equals(readonly));
                } else {
                    return true;
                }
            } catch (StaleElementReferenceException e) {
                // ignore this and retry in the next loop iteration
            }
        }

        // assert that it is gone forever. This means "not editable".
        return false;
    }

    private String[] getPropertyValues(GUIElementLocator locator, String propertyName) {
        WebElement element = findElement(locator);
        Select select = new Select(element);
        List<WebElement> options = select.getOptions();
        ArrayList<String> values = new ArrayList<String>();
        for (WebElement option : options) {
            String value = option.getAttribute(propertyName);
            if (value != null) {
                values.add(value);
            }
        }
        return values.toArray(new String[values.size()]);
    }

    public void waitUntilClickable(GUIElementLocator locator) {
        waitUntilClickable(locator, configuration.getTimeout());
    }

    public void waitUntilClickable(GUIElementLocator locator, long timeOutInMillis) {
        try {
            waitFor(elementToBeClickable(LocatorUtil.by(locator)), timeOutInMillis);
        } catch (TimeoutException e) {
            throw new AutomationException("Element not editable."); // NOSONAR
        }
    }

    public void waitUntilPresent(GUIElementLocator locator) {
        waitUntilPresent(locator, configuration.getTimeout());
    }

    public void waitUntilPresent(GUIElementLocator locator, long timeout) {
        // FIXME debug only
        System.out.println(Arrays.asList(getAllWindowTitles()));

        // optimization: if very small timeout, use findElement
        if (timeout < 1000) {
            try {
                findElement(locator, timeout);
            }
            catch (Exception e) {
                throw new AutomationException("Element not found");
            }
        }
        else {
            try {
                waitFor(presenceOfElementLocated(LocatorUtil.by(locator)), timeout);
            }
            catch (TimeoutException e) {
                throw new AutomationException("Element not found"); // NOSONAR
            }
        }
    }

    public void waitUntilNotPresent(GUIElementLocator locator, long timeout) {
        try {
            waitFor(AludraTestExpectedConditions.noPresenceOfElementLocated(LocatorUtil.by(locator)), timeout);
        }
        catch (TimeoutException e) {
            throw new AutomationException("An element was unexpectedly found"); // NOSONAR
        }
    }

    public void waitUntilVisible(GUIElementLocator locator) {
        waitUntilVisible(locator, configuration.getTimeout());
    }

    public void waitUntilVisible(GUIElementLocator locator, long timeOutInMillis) {
        try {
            waitFor(visibilityOfElementLocated(LocatorUtil.by(locator)), timeOutInMillis);
        } catch (TimeoutException e) {
            throw new AutomationException("The element is not visible."); // NOSONAR
        }
    }

    public void waitUntilInvisible(GUIElementLocator locator) {
        try {
            waitFor(invisibilityOfElementLocated(LocatorUtil.by(locator)), configuration.getTimeout());
        }
        catch (TimeoutException e) {
            throw new AutomationException("The element is still visible."); // NOSONAR
        }
    }

    public void waitUntilInForeground(final GUIElementLocator locator, long timeout) {
        try {
            waitFor(new ExpectedCondition<WebElement>() {
                @Override
                public WebElement apply(WebDriver input) {
                    try {
                        return isInForeground(locator) ? findElement(locator) : null;
                    }
                    catch (WebDriverException e) {
                        return null;
                    }
                }
            }, timeout);
        }
        catch (TimeoutException e) {
            throw new AutomationException("The element is not in foreground.");
        }
    }

    private void waitFor(ExpectedCondition<?> condition, long timeOutInMillis) {
        int sleepInMillis = configuration.getPauseBetweenRetries();
        WebDriverWait wait = new WebDriverWait(driver, 1, sleepInMillis);
        wait.withTimeout(timeOutInMillis, TimeUnit.MILLISECONDS);
        wait.ignoring(UnknownServerException.class);
        wait.until(condition);
    }

    private WebElement findElement(Locator locator) {
        return findElement(locator, configuration.getTimeout());
    }

    private WebElement findElement(Locator locator, long timeout) {
        try {
            this.driver.manage().timeouts().implicitlyWait(timeout, TimeUnit.MILLISECONDS);
            return LocatorUtil.findElement(locator, driver);
        }
        catch (NoSuchElementException e) {
            throw new AutomationException("Element could not be found.", e);
        }
        finally {
            this.driver.manage().timeouts().implicitlyWait(100, TimeUnit.MILLISECONDS);
        }
    }

    private Set<String> getWindowHandles() {
        Set<String> handles = driver.getWindowHandles();
        LOGGER.debug("getWindowHandles() -> {}", handles);
        return handles;
    }

    private static class AludraTestExpectedConditions {

        private AludraTestExpectedConditions() {
        }

        private static ExpectedCondition<Boolean> noPresenceOfElementLocated(final By locator) {
            return new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(WebDriver input) {
                    try {
                        WebElement elem = input.findElement(locator);
                        return Boolean.valueOf(elem == null);
                    }
                    catch (NoSuchElementException e) {
                        return Boolean.TRUE;
                    }
                }
            };
        }

    }

}
