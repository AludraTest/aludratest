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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.aludratest.exception.AutomationException;
import org.aludratest.exception.PerformanceFailure;
import org.aludratest.exception.TechnicalException;
import org.aludratest.service.SystemConnector;
import org.aludratest.service.gui.web.selenium.ProxyPool;
import org.aludratest.service.gui.web.selenium.SeleniumResourceService;
import org.aludratest.service.gui.web.selenium.SeleniumWrapperConfiguration;
import org.aludratest.service.gui.web.selenium.httpproxy.AuthenticatingHttpProxy;
import org.aludratest.service.gui.web.selenium.selenium2.condition.DropDownEntryPresence;
import org.aludratest.service.gui.web.selenium.selenium2.condition.ElementAbsence;
import org.aludratest.service.gui.web.selenium.selenium2.condition.ElementClickable;
import org.aludratest.service.gui.web.selenium.selenium2.condition.ElementCondition;
import org.aludratest.service.gui.web.selenium.selenium2.condition.ElementValuePresence;
import org.aludratest.service.gui.web.selenium.selenium2.condition.OptionSelected;
import org.aludratest.service.gui.web.selenium.selenium2.condition.WindowPresence;
import org.aludratest.service.locator.element.GUIElementLocator;
import org.aludratest.service.locator.element.IdLocator;
import org.aludratest.service.locator.option.IndexLocator;
import org.aludratest.service.locator.option.OptionLocator;
import org.aludratest.service.locator.window.TitleLocator;
import org.aludratest.service.locator.window.WindowLocator;
import org.aludratest.service.util.ServiceUtil;
import org.aludratest.service.util.TaskCompletionUtil;
import org.aludratest.testcase.event.attachment.Attachment;
import org.aludratest.testcase.event.attachment.BinaryAttachment;
import org.aludratest.testcase.event.attachment.StringAttachment;
import org.aludratest.util.data.helper.DataMarkerCheck;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
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
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.SeleniumException;

/**
 * Wraps an instance of the {@link Selenium2Facade} and provides methods
 * for accessing UI elements and timing.
 * @author Marcel Malitz
 * @author Joerg Langnickel
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public class Selenium2Wrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(Selenium2Wrapper.class);

    // constants ---------------------------------------------------------------

    private static final String DROPDOWN_OPTION_VALUE_PROPERTY = "value";
    private static final String DROPDOWN_OPTION_LABEL_PROPERTY = "text";

    // scripts -----------------------------------------------------------------

    private static final String WINDOW_FOCUS_SCRIPT = "window.focus()";

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

    // static attributes -------------------------------------------------------

    private static ProxyPool proxyPool = null;

    // attributes --------------------------------------------------------------

    private final SeleniumWrapperConfiguration configuration;

    private SeleniumResourceService resourceService;

    private String usedSeleniumHost = null;

    private AuthenticatingHttpProxy proxy;

    SystemConnector systemConnector;

    private WebDriver driver;

    private LocatorSupport locatorSupport;

    private URL seleniumUrl;

    private WebElement highlightedElement;

    public Selenium2Wrapper(SeleniumWrapperConfiguration configuration, SeleniumResourceService resourceService) {
        try {
            this.configuration = configuration;
            this.resourceService = resourceService;
            if (configuration.isUrlOfAutHttp() && configuration.isUsingLocalProxy()) {
                this.proxy = getProxyPool().acquire();
                this.proxy.start();
            }
            this.usedSeleniumHost = resourceService.acquire();
            this.seleniumUrl = new URL(usedSeleniumHost + "/wd/hub");
            this.driver = newDriver();
            this.locatorSupport = new LocatorSupport(this.driver, configuration);
        } catch (Exception e) {
            LOGGER.error("Error initializing Selenium 2", e);
            String host = usedSeleniumHost;
            forceCloseApplicationUnderTest();
            throw new TechnicalException(e.getMessage() + ". Used Host = " + host, e);
        }
    }

    private synchronized ProxyPool getProxyPool() {
        if (proxyPool == null && configuration.isUrlOfAutHttp() && configuration.isUsingLocalProxy()) {
            URL url = configuration.getUrlOfAutAsUrl();
            proxyPool = new ProxyPool(url.getHost(), url.getPort(), configuration.getMinProxyPort(),
                    resourceService.getHostCount());
        }
        return proxyPool;
    }

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

    /**
     * Closes the application under test respectively the main browser window
     * and all child windows.
     */
    private void closeApplicationUnderTest() {
        if (configuration.getCloseTestappAfterExecution()) {
            forceCloseApplicationUnderTest();
        }
    }

    private void forceCloseApplicationUnderTest() {
        if (this.proxy != null) {
            this.proxy.stop();
            getProxyPool().release(this.proxy);
            this.proxy = null;
        }
        if (this.driver != null) {
            close();
            quit();
            this.driver = null;
        }
        if (this.usedSeleniumHost != null) {
            resourceService.release(this.usedSeleniumHost);
            this.usedSeleniumHost = null;
        }
    }

    /**
     * Closes the application under test.
     */
    public void tearDown() {
        closeApplicationUnderTest();
    }

    public SeleniumWrapperConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * 
     * @return currently used SeleniumHost
     */
    public String getUsedSeleniumHost() {
        return usedSeleniumHost;
    }

    public void refresh() {
        driver.navigate().refresh();
    }

    @SuppressWarnings("unchecked")
    private WebElement doBeforeDelegate(GUIElementLocator locator, boolean visible, boolean enabled, boolean actionPending) {
        if (actionPending) {
            waitUntilNotBusy();
        }
        ElementCondition condition = new ElementCondition(locator, locatorSupport, visible, enabled);
        try {
            WebElement element = waitFor(condition, configuration.getTimeout());
            highlight(locator);
            return element;
        }
        catch (TimeoutException e) {
            throw new AutomationException(condition.getMessage());
        }
    }

    private void doAfterDelegate(int taskCompletionTimeout, String failureMessage) {
        if (taskCompletionTimeout >= 0) {
            int timeout = (taskCompletionTimeout == 0 ? configuration.getTaskCompletionTimeout() : taskCompletionTimeout);
            TaskCompletionUtil.waitForActivityAndCompletion(systemConnector, failureMessage, configuration.getTaskStartTimeout(),
                    timeout, configuration.getTaskPollingInterval());
        }
    }

    private void waitUntilNotBusy() {
        if (this.systemConnector != null) {
            TaskCompletionUtil.waitUntilNotBusy(this.systemConnector, configuration.getTaskCompletionTimeout(),
                    configuration.getTaskPollingInterval(), "System not available");
        }
    }

    public void click(GUIElementLocator locator, String operation, int taskCompletionTimeout) {
        WebElement element = doBeforeDelegate(locator, true, true, true);
        click(element);
        doAfterDelegate(taskCompletionTimeout, operation);
    }

    public void clickNotEditable(GUIElementLocator locator, String operation, int taskCompletionTimeout) {
        WebElement element = doBeforeDelegate(locator, true, false, true);
        click(element);
        doAfterDelegate(taskCompletionTimeout, operation);
    }

    private void click(WebElement element) {
        try {
            element.click();
        }
        catch (Exception e) {
            handleSeleniumException(e);
        }
    }

    public void handleSeleniumException(Throwable e) {
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

    public void doubleClick(GUIElementLocator locator, String operation, int taskCompletionTimeout) {
        WebElement element = doBeforeDelegate(locator, true, true, true);
        doubleClick(element);
        doAfterDelegate(taskCompletionTimeout, operation);
    }

    public void doubleClickNotEditable(GUIElementLocator locator, String operation, int taskCompletionTimeout) {
        WebElement element = doBeforeDelegate(locator, true, false, true);
        doubleClick(element);
        doAfterDelegate(taskCompletionTimeout, operation);
    }

    private void doubleClick(WebElement element) {
        element = LocatorSupport.unwrap(element);
        new Actions(driver).doubleClick(element).build().perform();
    }

    public void select(GUIElementLocator locator, final OptionLocator optionLocator, int taskCompletionTimeout) {
        WebElement element = doBeforeDelegate(locator, true, true, true);
        Select select = new Select(element);
        if (optionLocator instanceof org.aludratest.service.locator.option.LabelLocator) {
            select.selectByVisibleText(optionLocator.toString());
        }
        else if (optionLocator instanceof IndexLocator) {
            select.selectByIndex(((IndexLocator) optionLocator).getIndex());
        }
        else {
            throw ServiceUtil.newUnsupportedLocatorException(optionLocator);
        }
        doAfterDelegate(taskCompletionTimeout, "select");
    }

    public void type(GUIElementLocator locator, final String value, int taskCompletionTimeout) {
        doBeforeDelegate(locator, true, true, true);
        // setValue instead of sendKeys to ensure field is reset, and to work with file component
        setValue(locator, value);
        doAfterDelegate(taskCompletionTimeout, "type");
    }

    private void setValue(GUIElementLocator locator, String value) {
        WebElement element = findElementImmediately(locator);
        String id = element.getAttribute("id");
        String fieldType = element.getAttribute("type");
        boolean fallback = true;
        if (!DataMarkerCheck.isNull(id) || "file".equals(fieldType)) {
            executeScript("document.getElementById('" + id + "').setAttribute('value', '" + value.replace("'", "\\'")
                    + "')");
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

    public void sendKeys(GUIElementLocator locator, String keys, int taskCompletionTimeout) {
        WebElement element = doBeforeDelegate(locator, true, true, true);
        element.sendKeys(keys);
        doAfterDelegate(taskCompletionTimeout, "sendKeys");
    }

    public String getText(GUIElementLocator locator, Boolean visible) {
        WebElement element = doBeforeDelegate(locator, visible, false, false);
        String text = element.getText();
        doAfterDelegate(-1, "getText");
        return text;
    }

    public boolean isChecked(GUIElementLocator locator) {
        WebElement element = doBeforeDelegate(locator, true, false, false);
        Boolean returnValue = element.isSelected();
        doAfterDelegate(-1, "isChecked");
        return returnValue.booleanValue();
    }

    public String[] getSelectOptions(GUIElementLocator locator) {
        WebElement element = doBeforeDelegate(locator, true, false, false);
        Select select = new Select(element);
        List<WebElement> options = select.getOptions();
        String[] labels = new String[options.size()];
        for (int i = 0; i < options.size(); i++) {
            labels[i] = options.get(i).getText();
        }
        doAfterDelegate(-1, "getSelectOptions");
        return labels;
    }

    public String getSelectedValue(GUIElementLocator locator) {
        WebElement element = doBeforeDelegate(locator, true, false, false);
        Select select = new Select(element);
        String selectedValue = select.getFirstSelectedOption().getText();
        doAfterDelegate(-1, "getSelectedValue");
        return selectedValue;
    }

    public String getSelectedLabel(GUIElementLocator locator) {
        WebElement element = doBeforeDelegate(locator, true, false, false);
        Select select = new Select(element);
        String selectedLabel = select.getFirstSelectedOption().getText();
        doAfterDelegate(-1, "getSelectedLabel");
        return selectedLabel;
    }

    public String getValue(GUIElementLocator locator) {
        return findElementImmediately(locator).getAttribute("value");
    }

    // window operations -------------------------------------------------------

    @SuppressWarnings("unchecked")
    private void waitForWindow(WindowLocator locator) {
        try {
            waitFor(new WindowPresence(locator, this), configuration.getTimeout());
        }
        catch (TimeoutException e) {
            throw new AutomationException("Window not found");
        }
    }

    public void selectWindow(WindowLocator locator) {
        waitForWindow(locator);
        selectWindowImmediately(locator);
    }

    public void selectWindowImmediately(WindowLocator locator) {
        if (locator instanceof TitleLocator) {
            String requestedTitle = locator.toString();

            // performance tweak: if the current window is the requested one, return immediately
            try {
                if (requestedTitle.equals(driver.getTitle())) {
                    return;
                }
            }
            catch (NoSuchWindowException e) {
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
        }
        else {
            throw ServiceUtil.newUnsupportedLocatorException(locator);
        }
    }

    public void selectWindowByTechnicalName(String windowId) {
        removeHighlight();
        driver.switchTo().window(windowId);
    }

    public Map<String, String> getAllWindowHandlesAndTitles() {
        Map<String, String> handlesAndTitles = new HashMap<String, String>();
        // store handle and title of current window
        String initialWindowHandle;
        try {
            initialWindowHandle = driver.getWindowHandle();
        }
        catch (NoSuchWindowException e) {
            // fallback to next best window - current window has just been closed!
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

    /** @see Selenium#getAllWindowTitles() */
    public String[] getAllWindowTitles() {
        Map<String, String> handlesAndTitles = getAllWindowHandlesAndTitles();
        Collection<String> titles = handlesAndTitles.values();
        LOGGER.debug("getAllWindowTitles() -> {}", titles);
        return titles.toArray(new String[handlesAndTitles.size()]);
    }

    /** @see Selenium#getAllWindowIds() */
    public String[] getAllWindowIDs() {
        Set<String> handles = getWindowHandles();
        return handles.toArray(new String[handles.size()]);
    }

    /** @see Selenium#getAllWindowNames()  */
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

    /** @see Selenium#getTitle() */
    public String getTitle() {
        return driver.getTitle();
    }

    public String getWindowHandle() {
        return driver.getWindowHandle();
    }

    /** @see Selenium#windowMaximize() */
    public void windowMaximize() {
        driver.manage().window().maximize();
    }

    /** @see Selenium#windowFocus() */
    public void windowFocus() {
        executeScript(WINDOW_FOCUS_SCRIPT);
    }

    private Set<String> getWindowHandles() {
        Set<String> handles = driver.getWindowHandles();
        LOGGER.debug("getWindowHandles() -> {}", handles);
        return handles;
    }

    // iframe operations -------------------------------------------------------

    public void switchToIFrame(GUIElementLocator iframeLocator) {
        if (iframeLocator != null) {
            WebElement element = waitUntilPresent(iframeLocator, configuration.getTimeout());
            element = LocatorSupport.unwrap(element);
            driver.switchTo().frame(element);
        }
        else {
            driver.switchTo().defaultContent();
        }
    }

    public boolean hasFocus(final GUIElementLocator locator) {
        WebElement element = doBeforeDelegate(locator, true, true, false);
        boolean returnValue = (Boolean) executeScript(HAS_FOCUS_SCRIPT, element);
        doAfterDelegate(-1, "hasFocus");
        return returnValue;
    }

    public String[] getLabels(GUIElementLocator locator) {
        return getPropertyValues(locator, DROPDOWN_OPTION_LABEL_PROPERTY);
    }

    public String[] getValues(final GUIElementLocator locator) {
        doBeforeDelegate(locator, true, false, false);
        String[] returnValue = getPropertyValues(locator, DROPDOWN_OPTION_VALUE_PROPERTY);
        doAfterDelegate(-1, "getValues");
        return returnValue;
    }

    private String[] getPropertyValues(GUIElementLocator locator, String propertyName) {
        WebElement element = findElementImmediately(locator);
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

    public void focus(GUIElementLocator locator) {
        WebElement element = findElementImmediately(locator);
        if (!ElementClickable.isClickable(element)) {
            throw new AutomationException("Element not editable");
        }
        executeScript("arguments[0].focus()", element);
    }

    public String getAttributeValue(final GUIElementLocator locator, final String attributeName) {
        WebElement element = doBeforeDelegate(locator, true, false, false);
        String returnValue = element.getAttribute(attributeName);
        doAfterDelegate(-1, "getAttributeValue");
        return returnValue;
    }

    public void keyPress(int keycode) {
        driver.switchTo().activeElement().sendKeys(String.valueOf((char) keycode));
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

    public String getTable(GUIElementLocator locator, int row, int col) {
        WebElement table = findElementImmediately(locator);
        List<WebElement> tbodies = table.findElements(By.tagName("tbody"));
        WebElement rowHolder = (tbodies.size() > 0 ? tbodies.get(0) : table);
        List<WebElement> trs = rowHolder.findElements(By.tagName("tr"));
        if (row >= trs.size()) {
            throw new AutomationException("Table cell not found. " + "Requested row index " + row + " of " + trs.size()
                    + " rows ");
        }
        List<WebElement> tds = trs.get(row).findElements(By.tagName("td"));
        if (col >= tds.size()) {
            throw new AutomationException("Table cell not found. " + "Requested column index " + col + " of " + tds.size()
                    + " cells ");
        }
        return tds.get(col).getText();
    }

    // features that require intervention for authentication -------------------

    public void open(String url) {
        try {
            driver.get(mapUrl(url));
        }
        catch (SeleniumException e) {
            String message = e.getMessage();
            if (message != null && message.contains("Timed out")) {
                throw new PerformanceFailure("Timed out opening '" + url + "': " + e);
            }
        }
    }

    private String mapUrl(String url) {
        return (proxy != null ? proxy.mapTargetToProxyUrl(url) : url);
    }

    public void addCustomRequestHeader(String key, String value) {
        if (proxy != null) {
            proxy.setCustomRequestHeader(key, value);
        }
    }

    // wait features -----------------------------------------------------------

    public WebElement waitUntilPresent(GUIElementLocator locator, long timeOutInMillis) {
        return locatorSupport.waitUntilPresent(locator, timeOutInMillis);
    }

    @SuppressWarnings("unchecked")
    public void waitUntilClickable(GUIElementLocator locator, long timeOutInMillis) {
        try {
            waitFor(ExpectedConditions.elementToBeClickable(LocatorSupport.by(locator)), timeOutInMillis,
                    NoSuchElementException.class);
        } catch (TimeoutException e) {
            throw new AutomationException("Element not editable"); // NOSONAR
        }
    }

    @SuppressWarnings("unchecked")
    public void waitUntilVisible(GUIElementLocator locator, long timeOutInMillis) {
        try {
            waitFor(new ElementCondition(locator, locatorSupport, true, false), timeOutInMillis, NoSuchElementException.class);
        } catch (TimeoutException e) {
            throw new AutomationException("The element is not visible."); // NOSONAR
        }
    }

    @SuppressWarnings("unchecked")
    public void waitUntilElementNotPresent(final GUIElementLocator locator, long timeOutInMillis) {
        try {
            waitFor(new ElementAbsence(locator, locatorSupport), timeOutInMillis, NoSuchElementException.class);
        }
        catch (TimeoutException e) {
            throw new AutomationException("An element was unexpectedly found"); // NOSONAR
        }
    }

    @SuppressWarnings("unchecked")
    public void waitUntilInForeground(final GUIElementLocator locator, long timeOutInMillis) {
        ElementCondition condition = new ElementCondition(locator, locatorSupport, false, false);
        try {
            waitFor(condition, timeOutInMillis, NoSuchElementException.class);
        }
        catch (TimeoutException e) {
            throw new AutomationException(condition.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void waitForDropDownEntry(final OptionLocator entryLocator, final GUIElementLocator dropDownLocator) {
        try {
            waitFor(new DropDownEntryPresence(dropDownLocator, entryLocator, this), configuration.getTimeout(),
                    NoSuchElementException.class, StaleElementReferenceException.class);
        }
        catch (TimeoutException e) {
            throw new AutomationException("Element not found");
        }
    }

    @SuppressWarnings("unchecked")
    public String waitForValue(GUIElementLocator locator) {
        ElementValuePresence condition = new ElementValuePresence(locator, locatorSupport);
        try {
            return waitFor(condition, configuration.getTimeout(), NoSuchElementException.class);
        }
        catch (TimeoutException e) {
            throw new AutomationException(condition.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public String waitForSelection(GUIElementLocator locator) {
        OptionSelected condition = new OptionSelected(locator, locatorSupport);
        try {
            return waitFor(condition, configuration.getTimeout(), NoSuchElementException.class);
        }
        catch (TimeoutException e) {
            throw new AutomationException(condition.getMessage());
        }
    }

    private <T> T waitFor(ExpectedCondition<T> condition, long timeOutInMillis, Class<? extends Exception>... exceptionsToIgnore) {
        return locatorSupport.waitFor(condition, timeOutInMillis, exceptionsToIgnore);
    }

    // HTML source and screenshot provision ------------------------------------

    public Attachment getPageSource() {
        String pageSource = driver.getPageSource();
        return new StringAttachment("Source", pageSource, configuration.getPageSourceAttachmentExtension());
    }

    public Attachment getScreenshotOfThePage() {
        final String base64Data = captureScreenshotToString();
        final Attachment attachment = getScreenshotAttachment(base64Data);
        return attachment;
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
        }
        else {
            screenshotDriver = driver;
        }
        if (screenshotDriver instanceof TakesScreenshot) {
            TakesScreenshot tsDriver = (TakesScreenshot) screenshotDriver;
            return tsDriver.getScreenshotAs(OutputType.BASE64);
        }
        else {
            throw new UnsupportedOperationException(driver.getClass() + " does not implement TakeScreenshot");
        }
    }

    private Attachment getScreenshotAttachment(String base64Data) {
        final String title = "Screenshot";
        final Base64 base64 = new Base64();
        final byte[] decodedData = base64.decode(base64Data);
        return new BinaryAttachment(title, decodedData, configuration.getScreenshotAttachmentExtension());
    }

    // element highlighting ----------------------------------------------------

    void removeHighlight() {
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
            findElementImmediately(new IdLocator("__aludra_selenium_hidden"));
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

                WebElement elementToHighlight = findElementImmediately(locator);
                executeScript("arguments[0].className +=' selenium-highlight'", elementToHighlight);
                this.highlightedElement = elementToHighlight;
            }
            catch (WebDriverException e) {
                // It does not matter if highlighting works or not, why a
                // possibly thrown exception must be caught to avoid test
                // execution termination.
                LOGGER.trace("Highlighting failed. ", e);
            }
        }
    }

    // script support ----------------------------------------------------------

    private Object executeScript(String script, Object... arguments) {
        // first unwrap all possibly wrapped arguments of type WebElement, or JavaScript/JSON will fail
        for (int i = 0; i < arguments.length; i++) {
            if (arguments[i] instanceof WebElement) {
                arguments[i] = LocatorSupport.unwrap((WebElement) arguments[i]);
            }
        }
        // then execute the script
        return ((JavascriptExecutor) driver).executeScript(script, arguments);
    }


    // element lookup ----------------------------------------------------------

    private WebElement findElementImmediately(GUIElementLocator locator) {
        return locatorSupport.findElementImmediately(locator);
    }

}