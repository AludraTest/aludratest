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
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.aludratest.exception.AutomationException;
import org.aludratest.exception.FunctionalFailure;
import org.aludratest.exception.PerformanceFailure;
import org.aludratest.exception.TechnicalException;
import org.aludratest.service.SystemConnector;
import org.aludratest.service.gui.web.selenium.ProxyPool;
import org.aludratest.service.gui.web.selenium.SeleniumResourceService;
import org.aludratest.service.gui.web.selenium.SeleniumWrapperConfiguration;
import org.aludratest.service.gui.web.selenium.SystemDownloadProvider;
import org.aludratest.service.gui.web.selenium.httpproxy.AuthenticatingHttpProxy;
import org.aludratest.service.gui.web.selenium.selenium2.condition.AbstractAjaxIdleCondition;
import org.aludratest.service.gui.web.selenium.selenium2.condition.AnyDropDownOptions;
import org.aludratest.service.gui.web.selenium.selenium2.condition.DropDownBoxOptionLabelsPresence;
import org.aludratest.service.gui.web.selenium.selenium2.condition.DropDownOptionLocatable;
import org.aludratest.service.gui.web.selenium.selenium2.condition.ElementAbsence;
import org.aludratest.service.gui.web.selenium.selenium2.condition.ElementEditableCondition;
import org.aludratest.service.gui.web.selenium.selenium2.condition.ElementEnabledCondition;
import org.aludratest.service.gui.web.selenium.selenium2.condition.ElementNotVisibleCondition;
import org.aludratest.service.gui.web.selenium.selenium2.condition.ElementValuePresence;
import org.aludratest.service.gui.web.selenium.selenium2.condition.IceFacesAjaxIdleCondition;
import org.aludratest.service.gui.web.selenium.selenium2.condition.JQueryAjaxIdleCondition;
import org.aludratest.service.gui.web.selenium.selenium2.condition.MixedElementCondition;
import org.aludratest.service.gui.web.selenium.selenium2.condition.NotCondition;
import org.aludratest.service.gui.web.selenium.selenium2.condition.OptionSelected;
import org.aludratest.service.gui.web.selenium.selenium2.condition.PrimeFacesAjaxIdleCondition;
import org.aludratest.service.gui.web.selenium.selenium2.condition.ValidatingCondition;
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
import org.aludratest.util.retry.RetryService;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.databene.commons.Validator;
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
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.selenium.Selenium;

/** Wraps an instance of the {@link Selenium2Facade} and provides methods for accessing UI elements and timing.
 * @author Volker Bergmann
 * @author Marcel Malitz
 * @author Joerg Langnickel */
@SuppressWarnings("javadoc")
public class Selenium2Wrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(Selenium2Wrapper.class);

    // scripts -----------------------------------------------------------------

    private static final String WINDOW_FOCUS_SCRIPT = "window.focus()";

    private static final String HAS_FOCUS_SCRIPT = "return arguments[0] == window.document.activeElement";

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
            // TODO support HTTPS
            if ("http".equals(configuration.getUrlOfAutAsUrl().getProtocol()) && configuration.isUsingLocalProxy()) {
                this.proxy = getProxyPool().acquire();
                this.proxy.start();
            }
            this.usedSeleniumHost = resourceService.acquire();
            this.seleniumUrl = new URL(usedSeleniumHost + "/wd/hub");
            this.driver = newDriver();
            this.driver.manage().timeouts().pageLoadTimeout(configuration.getTimeout(), TimeUnit.MILLISECONDS);
            this.locatorSupport = new LocatorSupport(this.driver, configuration);
        } catch (Exception e) {
            LOGGER.error("Error initializing Selenium 2", e);
            String host = usedSeleniumHost;
            forceCloseApplicationUnderTest();
            throw new TechnicalException(e.getMessage() + ". Used Host = " + host, e);
        }
    }

    private synchronized ProxyPool getProxyPool() {
        if (proxyPool == null && "http".equals(configuration.getUrlOfAutAsUrl().getProtocol())
                && configuration.isUsingLocalProxy()) { // TODO support HTTPS
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
                return driverEnum.newRemoteDriver(seleniumUrl, configuration.getBrowserArguments());
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
            for (String id : getAllWindowIDs()) {
                try {
                    selectWindowByTechnicalName(id);
                    close();
                }
                catch (Exception e) {
                    // ignore during close
                }
            }
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
    private WebElement doBeforeDelegate(GUIElementLocator locator, boolean visible, boolean actionPending, boolean enabled) {
        if (actionPending) {
            waitUntilNotBusy();
        }
        MixedElementCondition condition = new MixedElementCondition(locator, locatorSupport, visible, enabled);
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
        WebElement element = doBeforeDelegate(locator, true, true, false);
        click(element);
        doAfterDelegate(taskCompletionTimeout, operation);
    }

    public void doubleClick(GUIElementLocator locator, String operation, int taskCompletionTimeout) {
        WebElement element = doBeforeDelegate(locator, true, true, false);
        doubleClick(element);
        doAfterDelegate(taskCompletionTimeout, operation);
    }

    public void hover(GUIElementLocator locator, String operation, int taskCompletionTimeout) {
        WebElement element = doBeforeDelegate(locator, true, true, false);
        hover(element);
        doAfterDelegate(taskCompletionTimeout, operation);
    }

    public String clickForDownload(GUIElementLocator locator, int taskCompletionTimeout) {
        // FIXME temporarily set "enabled" to false due to img used in GLOBE for tests. Change after tests.
        WebElement element = doBeforeDelegate(locator, true, false, true);
        // get System Connector for download
        SystemDownloadProvider downloader = systemConnector.getConnector(SystemDownloadProvider.class);
        if (downloader != null) {
            Attachment att = downloader.downloadFile(configuration.getUrlOfAutAsUrl(), element, driver.manage().getCookies());
            if (att != null) {
                return att.getFileName() + ":" + att.getFileDataAsBase64String();
            }
            else {
                throw new FunctionalFailure("Expected file download, but no download available");
            }
        }
        else {
            throw new AutomationException("A SystemDownloadProvider system connector must be available for downloads");
        }
    }

    public void select(GUIElementLocator locator, final OptionLocator optionLocator, int taskCompletionTimeout) {
        WebElement element = doBeforeDelegate(locator, true, true, true);
        select(optionLocator, element);
        doAfterDelegate(taskCompletionTimeout, "select");
    }

    public void type(GUIElementLocator locator, final String value, int taskCompletionTimeout) {
        WebElement element = doBeforeDelegate(locator, true, true, true);
        // setValue instead of sendKeys to ensure field is reset, and to work with file component
        setValue(element, value);
        doAfterDelegate(taskCompletionTimeout, "type");
    }

    public void sendKeys(GUIElementLocator locator, String keys, int taskCompletionTimeout) {
        WebElement element = doBeforeDelegate(locator, true, true, false);
        sendKeys(element, keys);
        doAfterDelegate(taskCompletionTimeout, "sendKeys");
    }

    public String getText(GUIElementLocator locator, Boolean visible) {
        WebElement element = doBeforeDelegate(locator, visible, false, false);
        String text = getText(element);
        doAfterDelegate(-1, "getText");
        return text;
    }

    public boolean isChecked(GUIElementLocator locator) {
        WebElement element = doBeforeDelegate(locator, true, false, false);
        LOGGER.debug("isChecked({})", locator);
        Boolean returnValue = element.isSelected();
        doAfterDelegate(-1, "isChecked");
        return returnValue.booleanValue();
    }

    public String[] getSelectOptions(GUIElementLocator locator) {
        WebElement element = doBeforeDelegate(locator, true, false, false);
        LOGGER.debug("getSelectOptions({})", locator);
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
        String selectedValue = getSelectedLabel(element);
        doAfterDelegate(-1, "getSelectedValue");
        return selectedValue;
    }

    public String getSelectedLabel(GUIElementLocator locator) {
        WebElement element = doBeforeDelegate(locator, true, false, false);
        String selectedLabel = getSelectedLabel(element);
        doAfterDelegate(-1, "getSelectedLabel");
        return selectedLabel;
    }

    public String getValue(GUIElementLocator locator) {
        LOGGER.debug("getValue({})", locator);
        return getValue(findElementImmediately(locator));
    }

    // window operations -------------------------------------------------------

    @SuppressWarnings("unchecked")
    private void waitForWindow(WindowLocator locator) {
        LOGGER.debug("waitForWindow({})", locator);
        try {
            waitFor(new WindowPresence(locator, this), configuration.getTimeout());
        }
        catch (TimeoutException e) {
            throw new AutomationException("Window not found");
        }
    }

    public void selectWindow(WindowLocator locator) {
        LOGGER.debug("selectWindow({})", locator);
        waitForWindow(locator);
        selectWindowImmediately(locator);
    }

    public void selectWindowImmediately(WindowLocator locator) {
        LOGGER.debug("selectWindowImmediately({})", locator);
        if (locator instanceof TitleLocator) {
            String requestedTitle = locator.toString();

            // performance tweak: if the current window is the requested one, return immediately
            try {
                LOGGER.debug("driver.getTitle()");
                if (requestedTitle.equals(driver.getTitle())) {
                    return;
                }
            }
            catch (WebDriverException e) {
                // this happens, when trying to call getTitle()
                // on a driver which has just close()d the recent window.
                // In this case, I fall back to iterating all windows below
            }

            removeHighlight();

            // iterate all windows and return the one with the desired title
            StringBuilder sb = new StringBuilder();
            for (String handle : getWindowHandles()) {
                try {
                    LOGGER.debug("driver.switchTo().window()", locator);
                    String title = driver.switchTo().window(handle).getTitle();
                    if (sb.length() > 0) {
                        sb.append(", ");
                    }
                    sb.append("\"").append(title).append("\"");
                    if (requestedTitle.equals(title)) {
                        return;
                    }
                }
                catch (WebDriverException e) {
                    // ignore; window has been removed in the meantime
                }
            }

            // if the window has not been found, throw an ElementNotFoundException
            throw new AutomationException("Element not found. Available window titles are: " + sb.toString());
        }
        else {
            throw ServiceUtil.newUnsupportedLocatorException(locator);
        }
    }

    public void selectWindowByTechnicalName(String windowId) {
        LOGGER.debug("selectWindowByTechnicalName({})", windowId);
        removeHighlight();
        LOGGER.debug("driver.switchTo().window()", windowId);
        driver.switchTo().window(windowId);
    }

    public Map<String, String> getAllWindowHandlesAndTitles() {
        LOGGER.debug("getAllWindowHandlesAndTitles()");
        Map<String, String> handlesAndTitles = new HashMap<String, String>();
        // store handle and title of current window
        String initialWindowHandle;
        try {
            LOGGER.debug("driver.getWindowHandle()");
            initialWindowHandle = driver.getWindowHandle();
        }
        catch (NoSuchWindowException e) {
            // fallback to next best window - current window has just been closed!
            LOGGER.debug("driver.getWindowHandles()");
            Set<String> handles = driver.getWindowHandles();
            if (handles.isEmpty()) {
                return Collections.emptyMap();
            }

            initialWindowHandle = handles.iterator().next();
            LOGGER.debug("driver.switchTo().window(initialWindowHandle)");
            driver.switchTo().window(initialWindowHandle);
        }

        try {
            String title = getTitle();
            handlesAndTitles.put(initialWindowHandle, title);
        }
        catch (WebDriverException e) {
            // ignore current window
            LOGGER.warn("Could not determine title of current window. Assuming window has just been closed.");
        }

        // iterate all other windows by handle and get their titles
        String currentHandle = initialWindowHandle;
        Set<String> handles = getWindowHandles();
        for (String handle : handles) {
            if (!handle.equals(initialWindowHandle)) {
                LOGGER.debug("Switching to window with handle {}", handle);
                try {
                    LOGGER.debug("driver.switchTo().window(handle)");
                    driver.switchTo().window(handle);
                    currentHandle = handle;
                    String title;
                    handlesAndTitles.put(handle, title = driver.getTitle());
                    LOGGER.debug("Window with handle {} has title '{}'", handle, title);
                }
                catch (NoSuchWindowException e) {
                    // ignore this window
                }
                catch (WebDriverException e) {
                    // ignore this window
                    LOGGER.warn("WebDriverException when querying window with handle " + handle
                            + ". Assuming window has just been closed.");
                }
            }
        }
        // switch back to the original window
        if (!currentHandle.equals(initialWindowHandle)) {
            try {
                LOGGER.debug("driver.switchTo().window(initialWindowHandle)");
                driver.switchTo().window(initialWindowHandle);
            }
            catch (WebDriverException e) {
                // selenium could now be on an unexpected window
                LOGGER.warn("Could not switch back to initial window after window iteration. Active window is now unspecified.");
            }
        }
        return handlesAndTitles;
    }

    /** @see Selenium#getAllWindowTitles() */
    public String[] getAllWindowTitles() {
        LOGGER.debug("getAllWindowTitles()");
        Map<String, String> handlesAndTitles = getAllWindowHandlesAndTitles();
        Collection<String> titles = handlesAndTitles.values();
        LOGGER.debug("getAllWindowTitles() -> {}", titles);
        return titles.toArray(new String[handlesAndTitles.size()]);
    }

    /** @see Selenium#getAllWindowIds() */
    public String[] getAllWindowIDs() {
        LOGGER.debug("getWindowHandles()");
        Set<String> handles = getWindowHandles();
        return handles.toArray(new String[handles.size()]);
    }

    /** @see Selenium#getAllWindowNames()  */
    public String[] getAllWindowNames() {
        LOGGER.debug("getAllWindowNames()");
        String current = driver.getWindowHandle();
        List<String> names = new ArrayList<String>();
        for (String handle : getWindowHandles()) {
            LOGGER.debug("driver.switchTo().window(handle)");
            driver.switchTo().window(handle);
            names.add(executeScript("return window.name").toString());
        }
        LOGGER.debug("driver.switchTo().window(current)");
        driver.switchTo().window(current);
        return names.toArray(new String[names.size()]);
    }

    /** @see Selenium#getTitle() */
    public String getTitle() {
        LOGGER.debug("getTitle()");
        if (driver instanceof RemoteWebDriver) {
            // also reduce timeout for TCP connection, in case remote hangs
            ((AludraSeleniumHttpCommandExecutor) ((RemoteWebDriver) driver).getCommandExecutor()).setRequestTimeout(5000);
        }
        try {
            return driver.getTitle();
        }
        finally {
            if (driver instanceof RemoteWebDriver) {
                ((AludraSeleniumHttpCommandExecutor) ((RemoteWebDriver) driver).getCommandExecutor()).setRequestTimeout(0);
            }
        }
    }

    public String getWindowHandle() {
        LOGGER.debug("getWindowHandle()");
        return driver.getWindowHandle();
    }

    /** @see Selenium#windowMaximize() */
    public void windowMaximize() {
        LOGGER.debug("windowMaximize()");
        driver.manage().window().maximize();
    }

    /** @see Selenium#windowFocus() */
    public void windowFocus() {
        LOGGER.debug("windowFocus()");
        executeScript(WINDOW_FOCUS_SCRIPT);
    }

    private Set<String> getWindowHandles() {
        LOGGER.debug("getWindowHandles()");
        // try up to three times when getting strange WebDriverExceptions
        Callable<Set<String>> callable = new Callable<Set<String>>() {
            @Override
            public Set<String> call() throws Exception {
                try {
                    Set<String> handles = driver.getWindowHandles();
                    LOGGER.debug("getWindowHandles() -> {}", handles);
                    return handles;
                }
                catch (Exception e) {
                    // wait a little bit in case of exception, e.g. for browser window to close
                    try {
                        Thread.sleep(100);
                    }
                    catch (InterruptedException ie) {
                    }
                    throw e;
                }
            }
        };
        try {
            if (driver instanceof RemoteWebDriver) {
                // also reduce timeout for TCP connection, in case remote hangs
                ((AludraSeleniumHttpCommandExecutor) ((RemoteWebDriver) driver).getCommandExecutor()).setRequestTimeout(5000);
            }
            return RetryService.call(callable, WebDriverException.class, 2);
        }
        catch (Throwable t) {
            LOGGER.error("Could not retrieve window handles", t);
            return Collections.emptySet();
        }
        finally {
            if (driver instanceof RemoteWebDriver) {
                // also reduce timeout for TCP connection, in case remote hangs
                ((AludraSeleniumHttpCommandExecutor) ((RemoteWebDriver) driver).getCommandExecutor()).setRequestTimeout(0);
            }
        }
    }

    // iframe operations -------------------------------------------------------

    public void switchToIFrame(GUIElementLocator iframeLocator) {
        LOGGER.debug("switchToIFrame({})", iframeLocator);
        if (iframeLocator != null) {
            WebElement element = waitUntilPresent(iframeLocator, configuration.getTimeout());
            element = LocatorSupport.unwrap(element);
            driver.switchTo().frame(element);
        }
        else {
            driver.switchTo().defaultContent();
        }
    }

    public boolean hasFocus(GUIElementLocator locator) {
        WebElement element = doBeforeDelegate(locator, true, false, true);
        LOGGER.debug("hasFocus({})", locator);
        boolean returnValue = (Boolean) executeScript(HAS_FOCUS_SCRIPT, element);
        doAfterDelegate(-1, "hasFocus");
        return returnValue;
    }

    public void focus(GUIElementLocator locator) {
        LOGGER.debug("focus({})", locator);
        WebElement element = waitUntilEnabled(locator, configuration.getTimeout());
        executeScript("arguments[0].focus()", element);
    }

    public String getAttributeValue(final GUIElementLocator locator, String attributeName) {
        WebElement element = doBeforeDelegate(locator, true, false, false);
        LOGGER.debug("WebElement.getAttributeValue({})", attributeName);
        String returnValue = element.getAttribute(attributeName);
        doAfterDelegate(-1, "getAttributeValue");
        return returnValue;
    }

    public void keyPress(int keycode) {
        LOGGER.debug("keyPress({})", keycode);
        sendKeys(driver.switchTo().activeElement(), String.valueOf((char) keycode));
    }

    public void close() {
        LOGGER.debug("close()");
        try {
            driver.close();
        }
        catch (WebDriverException e) {
            // ignore this
        }
    }

    public void quit() {
        LOGGER.debug("quit()");
        driver.quit();
    }

    public String getTable(GUIElementLocator locator, int row, int col) {
        LOGGER.debug("getTable({})", locator);
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
        LOGGER.debug("open({})", url);
        try {
            driver.get(mapUrl(url));
        }
        catch (TimeoutException e) {
            throw new PerformanceFailure("Timed out opening '" + url + "': " + e.getMessage(), e);
        }
        catch (WebDriverException e) {
            String message = e.getMessage();
            if (message != null && message.contains("Timed out")) {
                throw new PerformanceFailure("Timed out opening '" + url + "': " + e.getMessage(), e);
            }
        }
    }

    private String mapUrl(String url) {
        String mappedUrl = proxy != null ? proxy.mapTargetToProxyUrl(url) : url;
        LOGGER.debug("mapUrl({}) -> {}", url, mappedUrl);
        return mappedUrl;
    }

    public void addCustomRequestHeader(String key, String value) {
        LOGGER.debug("addCustomRequestHeader({})", key, value);
        if (proxy != null) {
            proxy.setCustomRequestHeader(key, value);
        }
    }

    // wait features -----------------------------------------------------------

    public WebElement waitUntilPresent(GUIElementLocator locator, long timeOutInMillis) {
        return locatorSupport.waitUntilPresent(locator, timeOutInMillis);
    }

    @SuppressWarnings("unchecked")
    public WebElement waitUntilEnabled(GUIElementLocator locator, long timeOutInMillis) {
        ElementEnabledCondition condition = new ElementEnabledCondition(locator, locatorSupport);
        try {
            return waitFor(condition, timeOutInMillis, NoSuchElementException.class);
        } catch (TimeoutException e) {
            throw new AutomationException(condition.getMessage()); // NOSONAR
        }
    }

    @SuppressWarnings("unchecked")
    public WebElement waitUntilNotEnabled(GUIElementLocator locator, long timeOutInMillis) {
        ElementEnabledCondition condition = new ElementEnabledCondition(locator, locatorSupport, false);
        try {
            return waitFor(condition, timeOutInMillis, NoSuchElementException.class);
        }
        catch (TimeoutException e) {
            throw new AutomationException(condition.getMessage()); // NOSONAR
        }
    }

    @SuppressWarnings("unchecked")
    public WebElement waitUntilEditable(GUIElementLocator locator, long timeOutInMillis) {
        ElementEditableCondition condition = new ElementEditableCondition(locator, locatorSupport);
        try {
            return waitFor(condition, timeOutInMillis, NoSuchElementException.class);
        }
        catch (TimeoutException e) {
            throw new AutomationException(condition.getMessage()); // NOSONAR
        }
    }

    @SuppressWarnings("unchecked")
    public void waitUntilVisible(GUIElementLocator locator, long timeOutInMillis) {
        try {
            waitFor(new MixedElementCondition(locator, locatorSupport, true, false), timeOutInMillis,
                    NoSuchElementException.class);
        } catch (TimeoutException e) {
            throw new AutomationException("The element is not visible."); // NOSONAR
        }
    }

    @SuppressWarnings("unchecked")
    public void waitUntilNotVisible(GUIElementLocator locator, long timeOutInMillis) {
        try {
            waitFor(new ElementNotVisibleCondition(locator, locatorSupport), timeOutInMillis, NoSuchElementException.class);
        }
        catch (TimeoutException e) {
            throw new AutomationException("The element is unexpectedly visible."); // NOSONAR
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
        MixedElementCondition condition = new MixedElementCondition(locator, locatorSupport, false, false);
        try {
            waitFor(condition, timeOutInMillis, NoSuchElementException.class);
        }
        catch (TimeoutException e) {
            throw new AutomationException(condition.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public String[] waitForAnyDropDownOptionLabels(final GUIElementLocator dropDownLocator) {
        AnyDropDownOptions condition = new AnyDropDownOptions(dropDownLocator, AnyDropDownOptions.DROPDOWN_OPTION_LABEL_PROPERTY,
                locatorSupport);
        try {
            return waitFor(condition, configuration.getTimeout(), NoSuchElementException.class,
                    StaleElementReferenceException.class);
        }
        catch (TimeoutException e) {
            throw new AutomationException(condition.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public String[] waitForAnyDropDownOptionValues(final GUIElementLocator dropDownLocator) {
        AnyDropDownOptions condition = new AnyDropDownOptions(dropDownLocator, AnyDropDownOptions.DROPDOWN_OPTION_VALUE_PROPERTY,
                locatorSupport);
        try {
            return waitFor(condition, configuration.getTimeout(), NoSuchElementException.class,
                    StaleElementReferenceException.class);
        }
        catch (TimeoutException e) {
            throw new AutomationException(condition.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void waitForDropDownEntryLocatablity(final OptionLocator entryLocator, final GUIElementLocator dropDownLocator) {
        DropDownOptionLocatable condition = new DropDownOptionLocatable(dropDownLocator, entryLocator, locatorSupport);
        try {
            waitFor(condition, configuration.getTimeout(), NoSuchElementException.class, StaleElementReferenceException.class);
        }
        catch (TimeoutException e) {
            throw new AutomationException(condition.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void waitForDropDownEntries(GUIElementLocator dropDownLocator, String[] labels, boolean contains) {
        DropDownBoxOptionLabelsPresence condition = new DropDownBoxOptionLabelsPresence(dropDownLocator, labels, contains,
                locatorSupport);
        try {
            waitFor(condition, configuration.getTimeout(), NoSuchElementException.class, StaleElementReferenceException.class);
        }
        catch (TimeoutException e) {
            throw new AutomationException(condition.getMessage());
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

    public void waitForTextValidity(GUIElementLocator locator, Validator<String> validator) {
        waitForValidity(new ValidatingCondition(locator, locatorSupport, validator, "Text") {
            @Override
            protected String getTextToValidate(WebElement element) {
                return getText(element);
            }
        });
    }

    public void waitForValueValidity(GUIElementLocator locator, Validator<String> validator) {
        waitForValidity(new ValidatingCondition(locator, locatorSupport, validator, "Value") {
            @Override
            protected String getTextToValidate(WebElement element) {
                return getValue(element);
            }
        });
    }

    public void waitForSelectedLabelValidity(GUIElementLocator locator, Validator<String> validator) {
        waitForValidity(new ValidatingCondition(locator, locatorSupport, validator, "Label") {
            @Override
            protected String getTextToValidate(WebElement element) {
                return getSelectedLabel(element);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public void waitForValidity(ValidatingCondition condition) {
        try {
            waitFor(condition, configuration.getTimeout());
        }
        catch (TimeoutException e) {
            throw new FunctionalFailure(condition.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void waitForWindowToBeClosed(TitleLocator locator, int taskCompletionTimeout) {
        try {
            waitFor(new NotCondition(new WindowPresence(locator, this)), taskCompletionTimeout == -1 ? configuration.getTimeout()
                    : taskCompletionTimeout);
        }
        catch (TimeoutException e) {
            throw new PerformanceFailure("Window was not closed within timeout");
        }
    }

    @SuppressWarnings("unchecked")
    public void waitForAjaxOperationEnd(String frameworkName, int maxWaitTime) {
        frameworkName = frameworkName.toLowerCase(Locale.US);
        AbstractAjaxIdleCondition condition = null;

        if ("jquery".equals(frameworkName)) {
            condition = new JQueryAjaxIdleCondition();
        }
        else if ("primefaces".equals(frameworkName)) {
            condition = new PrimeFacesAjaxIdleCondition();
        }
        else if ("icefaces".equals(frameworkName)) {
            condition = new IceFacesAjaxIdleCondition();
        }

        if (condition != null) {
            try {
                waitFor(condition, maxWaitTime);
            }
            catch (TimeoutException e) {
                throw new PerformanceFailure("AJAX operation was not finished within timeout");
            }
        }
        else {
            throw new AutomationException("Unsupported JavaScript framework for AJAX check: " + frameworkName);
        }
    }

    private <T> T waitFor(ExpectedCondition<T> condition, long timeOutInMillis, Class<? extends Exception>... exceptionsToIgnore) {
        return locatorSupport.waitFor(condition, timeOutInMillis, exceptionsToIgnore);
    }

    // HTML source and screenshot provision ------------------------------------

    public Attachment getPageSource() {
        LOGGER.debug("getPageSource()");
        String pageSource = driver.getPageSource();
        return new StringAttachment("Source", pageSource, configuration.getPageSourceAttachmentExtension());
    }

    public Attachment getScreenshotOfThePage() {
        LOGGER.debug("getScreenshotOfThePage()");
        final String base64Data = captureScreenshotToString();
        final Attachment attachment = getScreenshotAttachment(base64Data);
        return attachment;
    }

    public List<Attachment> getWindowsScreenshots() {
        LOGGER.debug("getWindowsScreenshots()");
        Base64 base64 = new Base64();

        Set<String> windowHandles = getWindowHandles();
        String activeHandle = getWindowHandle();

        List<Attachment> result = new ArrayList<Attachment>();

        int index = 0;
        for (String handle : windowHandles) {
            driver.switchTo().window(handle);
            String data = captureActiveWindowScreenshotToString();
            byte[] decodedData = base64.decode(data);
            String title = driver.getTitle();

            result.add(new BinaryAttachment("Screenshot-" + (title == null ? "" + (++index) : title), decodedData, configuration
                    .getScreenshotAttachmentExtension()));
        }

        driver.switchTo().window(activeHandle);

        return result;
    }

    private String captureScreenshotToString() {
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

    private String captureActiveWindowScreenshotToString() {
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

    private void removeHighlight() {
        if (configuration.getHighlightCommands() && this.highlightedElement != null) {
            // unwrap element if wrapped, for direct access
            while (highlightedElement instanceof ElementWrapper) {
                highlightedElement = ((ElementWrapper) highlightedElement).getWrappedElement();
            }
            try {
                // first try via ID
                String id = highlightedElement.getAttribute("id");
                if (id != null) {
                    executeScript("document.getElementById('" + id + "').className = document.getElementById('" + id
                            + "').className.replace( /(?:^|\\s)selenium-highlight(?!\\S)/g , '' );");
                }
                else {
                    // fallback via Selenium arguments infrastructure
                    executeScript(
                            "arguments[0].className = arguments[0].className.replace( /(?:^|\\s)selenium-highlight(?!\\S)/g , '' );",
                            highlightedElement);
                }
            }
            catch (WebDriverException e) {
                LOGGER.trace("Highlight remove failed. ", e);
            }
        }
        highlightedElement = null;
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
            LOGGER.debug("highlight()");
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
        LOGGER.debug("executeScript({})", script);
        // first unwrap all possibly wrapped arguments of type WebElement, or JavaScript/JSON will fail
        for (int i = 0; i < arguments.length; i++) {
            if (arguments[i] instanceof WebElement) {
                arguments[i] = LocatorSupport.unwrap((WebElement) arguments[i]);
            }
        }
        // then execute the script
        return ((JavascriptExecutor) driver).executeScript(script, arguments);
    }


    // element lookup and access -----------------------------------------------

    private WebElement findElementImmediately(GUIElementLocator locator) {
        return locatorSupport.findElementImmediately(locator);
    }

    private String getText(WebElement element) {
        LOGGER.debug("getText(WebElement)");
        return element.getText();
    }

    private void sendKeys(WebElement element, CharSequence... keys) {
        LOGGER.debug("sendKeys({}, WebElement)", keys);
        element.sendKeys(keys);
    }

    private String getValue(WebElement element) {
        LOGGER.debug("getValue(WebElement)");
        return element.getAttribute("value");
    }

    private void setValue(WebElement element, String value) {
        LOGGER.debug("setValue(WebElement, {})", value);
        sendKeys(element, Keys.END);
        String text;
        int tryCounter = 3;
        while (tryCounter > 0 && !DataMarkerCheck.isNull(text = getValue(element))) {
            int length = text.length();
            String[] arr = new String[length];
            for (int i = 0; i < length; i++) {
                arr[i] = "\b";
            }
            sendKeys(element, arr);
            tryCounter--;
        }
        if (tryCounter == 0) {
            throw new AutomationException("Could not clear input field. Maybe covered by other component?");
        }
        sendKeys(element, value);

        try {
            sendKeys(element, Keys.TAB);
        }
        catch (StaleElementReferenceException e) {
            // ignore; key could have caused page change
            LOGGER.debug("Could not fire change event for element because element is now stale.");
        }
    }

    private String getSelectedLabel(WebElement element) {
        LOGGER.debug("getSelectedLabel(WebElement)");
        Select select = new Select(element);
        return select.getFirstSelectedOption().getText();
    }

    private void select(final OptionLocator optionLocator, WebElement element) {
        LOGGER.debug("select({}, WebElement)", optionLocator);
        Select select = new Select(element);
        try {
            if (optionLocator instanceof org.aludratest.service.locator.option.LabelLocator) {
                select.selectByVisibleText(((org.aludratest.service.locator.option.LabelLocator) optionLocator).getLabel());
            }
            else if (optionLocator instanceof IndexLocator) {
                select.selectByIndex(((IndexLocator) optionLocator).getIndex());
            }
            else {
                throw ServiceUtil.newUnsupportedLocatorException(optionLocator);
            }
        }
        catch (NoSuchElementException e) {
            throw new AutomationException("Selection Item not found");
        }
    }

    private void click(WebElement element) {
        LOGGER.debug("click(WebElement)");
        try {
            element.click();
        }
        catch (Exception e) {
            handleSeleniumException(e);
        }
    }

    private void doubleClick(WebElement element) {
        LOGGER.debug("doubleClick(WebElement)");
        element = LocatorSupport.unwrap(element);
        new Actions(driver).doubleClick(element).build().perform();
    }

    private void hover(WebElement element) {
        LOGGER.debug("hover(WebElement)");
        try {
            element = LocatorSupport.unwrap(element);
            Actions actions = new Actions(driver);
            actions.moveToElement(element).build().perform();
        }
        catch (Exception e) {
            handleSeleniumException(e);
        }
    }

    // special handling of some Selenium exceptions ----------------------------

    private void handleSeleniumException(Throwable e) {
        Throwable origException = e;
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
            if (message == null) {
                message = wde.getMessage();
            }
            // "not clickable" exception
            Pattern p = Pattern.compile("(unknown error: )?(.* not clickable .*)");
            Matcher m;
            if (message != null && (m = p.matcher(message)).find() && m.start() == 0) {
                throw new AutomationException(m.group(2), wde);
            }

            // NoSuchElementException
            if (wde instanceof NoSuchElementException) {
                throw new AutomationException("Element not found", wde);
            }

            // check for a time out
            if (wde instanceof TimeoutException) {
                throw new PerformanceFailure(wde.getMessage(), wde);
            }

            throw wde;
        }


        // otherwise, throw a technical exception
        throw new TechnicalException("Unknown exception when clicking element", origException);
    }

}