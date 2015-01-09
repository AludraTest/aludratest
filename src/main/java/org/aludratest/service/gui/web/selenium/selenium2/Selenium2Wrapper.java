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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.aludratest.exception.AutomationException;
import org.aludratest.exception.TechnicalException;
import org.aludratest.service.gui.web.selenium.ConditionCheck;
import org.aludratest.service.gui.web.selenium.ElementCommand;
import org.aludratest.service.gui.web.selenium.ProxyPool;
import org.aludratest.service.gui.web.selenium.SeleniumResourceService;
import org.aludratest.service.gui.web.selenium.SeleniumWrapperConfiguration;
import org.aludratest.service.gui.web.selenium.WindowCommand;
import org.aludratest.service.gui.web.selenium.httpproxy.AuthenticatingHttpProxy;
import org.aludratest.service.locator.Locator;
import org.aludratest.service.locator.element.GUIElementLocator;
import org.aludratest.service.locator.option.OptionLocator;
import org.aludratest.service.locator.window.WindowLocator;
import org.aludratest.testcase.event.attachment.Attachment;
import org.aludratest.testcase.event.attachment.BinaryAttachment;
import org.aludratest.testcase.event.attachment.StringAttachment;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.selenium.Selenium;

/**
 * Wraps an instance of the {@link Selenium2Facade} and provides methods
 * for accessing UI elements and timing.
 * @author Marcel Malitz
 * @author Joerg Langnickel
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public class Selenium2Wrapper {

    // FIXME Currently, Selenium2 does not work with Selenium RC on configured hosts/ports, as far as I can see.

    private static final Logger LOGGER = LoggerFactory.getLogger(Selenium2Wrapper.class);

    private static ProxyPool proxyPool = null;

    private Selenium2Facade selenium = null;

    private final SeleniumWrapperConfiguration configuration;

    private SeleniumResourceService resourceService;

    private String usedSeleniumHost = null;

    private int seleniumPort;

    private AuthenticatingHttpProxy proxy;

    public Selenium2Wrapper(SeleniumWrapperConfiguration configuration, SeleniumResourceService resourceService) {
        try {
            this.configuration = configuration;
            this.resourceService = resourceService;
            if (configuration.isUrlOfAutHttp()) {
                this.proxy = getProxyPool().acquire();
                this.proxy.start();
            }
            this.usedSeleniumHost = resourceService.acquire();
            this.seleniumPort = configuration.getDefaultSeleniumPort();
            if (usedSeleniumHost.contains(":")) {
                try {
                    seleniumPort = Integer.parseInt(usedSeleniumHost.substring(usedSeleniumHost.indexOf(':') + 1).trim());
                    usedSeleniumHost = usedSeleniumHost.substring(0, usedSeleniumHost.indexOf(':'));
                }
                catch (NumberFormatException e) {
                    LOGGER.error("Invalid host:port syntax for selenium host: " + usedSeleniumHost);
                }
            }

            this.selenium = new Selenium2Facade(configuration, usedSeleniumHost, seleniumPort);
        } catch (Exception e) {
            LOGGER.error("Error initializing Selenium 2", e);
            String host = usedSeleniumHost;
            forceCloseApplicationUnderTest();
            throw new TechnicalException(e.getMessage() + ". Used Host = " + host, e);
        }
    }

    private synchronized ProxyPool getProxyPool() {
        if (proxyPool == null && configuration.isUrlOfAutHttp()) {
            URL url = configuration.getUrlOfAutAsUrl();
            proxyPool = new ProxyPool(url.getHost(), url.getPort(), configuration.getMinProxyPort(),
                    resourceService.getHostCount());
        }
        return proxyPool;
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
        if (this.selenium != null) {
            this.selenium.close();
            this.selenium.quit();
            this.selenium = null;
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

    public void refresh() {
        selenium.refresh();
    }

    /**
     * 
     * @return currently used SeleniumHost
     */
    public String getUsedSeleniumHost() {
        return usedSeleniumHost;
    }

    /**
     * If highlighting is activated all HTML elements which are a target of a
     * action get highlighted (marked yellow).
     * @param locator of the element to highlight
     */
    private void highlight(GUIElementLocator locator) {
        selenium.highlight(locator);
    }

    /**
     * Retries to evaluate the condition. If evaluation is successful, the
     * method will return regularly. If not, this method will retry it with
     * pauses until a timeout is exceeded.
     * 
     * @param condition
     *            which will be evaluated on each retry
     * @return true, if the condition could be executed successfully; otherwise
     *         false will be returned.
     */
    public boolean retryUntilTimeout(ConditionCheck condition) {
        return retryUntilTimeout(condition, configuration.getTimeout());
    }

    /** Retries to evaluate the condition. If evaluation is successful, the method will return regularly. If not, this method will
     * retry it with a configured pause between retries until a given timeout is exceeded.
     * 
     * @param condition which will be evaluated on each retry
     * @param timeout how long will be waited
     * @return <code>true</code> if the condition could be executed successfully; otherwise, <code>false</code> will be returned. */
    public boolean retryUntilTimeout(ConditionCheck condition, long timeout) {
        final long time = System.currentTimeMillis() + timeout;
        boolean successful = false;
        while (System.currentTimeMillis() < time && !successful) {
            if (condition.eval()) {
                successful = true;
            } else {
                try {
                    Thread.sleep(configuration.getPauseBetweenRetries());
                } catch (InterruptedException e) {
                    throw new TechnicalException("Interrupted while waiting", e);
                }
            }
        }
        return successful;
    }

    private void doBeforeDelegate(GUIElementLocator locator, boolean visible, boolean enabled) {
        waitForInForeground(locator);

        if (visible) {
            selenium.waitUntilVisible(locator);
        }
        if (enabled) {
            selenium.waitUntilClickable(locator); // this fails for non-editable images and labels, ...
            if (!selenium.isEditable(locator)) { // ...so recheck it explicitly
                throw new AutomationException("Element not editable.");
            }
        }
        highlight(locator);
    }

    private Object callElementCommand(GUIElementLocator locator, ElementCommand<Object> command) {
        return callElementCommand(locator, command, true, true);
    }

    private Object callElementCommand(GUIElementLocator locator, ElementCommand<Object> command, boolean visible, boolean enabled) {
        doBeforeDelegate(locator, visible, enabled);
        final List<Object> returnValues = new ArrayList<Object>();
        Object returnValue = command.call(locator);
        if (returnValue != null) {
            returnValues.add(returnValue);
        }
        if (returnValues.isEmpty()) {
            return null;
        } else {
            return returnValues.get(0);
        }
    }

    public void click(GUIElementLocator locator) {
        doBeforeDelegate(locator, true, true);
        selenium.click(locator);
    }

    public void clickNotEditable(GUIElementLocator locator) {
        doBeforeDelegate(locator, true, false);
        selenium.click(locator);
    }

    public void doubleClickNotEditable(GUIElementLocator locator) {
        doBeforeDelegate(locator, true, false);
        selenium.doubleClick(locator);
    }

    public boolean isElementPresent(GUIElementLocator locator) {
        final Boolean returnValue = (Boolean) callElementCommand(locator,
                new ElementCommand<Object>("isElementPresent", false) {

            @Override
            public Object call(GUIElementLocator locator) {
                // this method will only be called if the element is
                // present why this method can directly return true
                return true;
            }

        }, true, false);
        return returnValue.booleanValue();
    }

    public boolean isEditable(GUIElementLocator locator) {
        final Boolean returnValue = (Boolean) callElementCommand(locator,
                new ElementCommand<Object>("isEditable", false) {

            @Override
            public Object call(GUIElementLocator locator) {
                return selenium.isEditable(locator);
            }

        }, true, false);
        return returnValue.booleanValue();
    }

    public void select(GUIElementLocator locator, final OptionLocator optionLocator) {
        callElementCommand(locator, new ElementCommand<Object>("select", true) {

            @Override
            public Object call(GUIElementLocator locator) {
                selenium.select(locator, optionLocator);
                return null;
            }

        });
    }

    public void type(GUIElementLocator locator, final String value) {
        callElementCommand(locator, new ElementCommand<Object>("type", true) {

            @Override
            public Object call(GUIElementLocator locator) {
                // setValue instead of sendKeys to ensure field is reset, and to work with file component
                selenium.setValue(locator, value);
                return null;
            }

        });
    }

    public void sendKeys(GUIElementLocator locator, final String keys) {
        callElementCommand(locator, new ElementCommand<Object>("sendKeys", true) {

            @Override
            public Object call(GUIElementLocator locator) {
                selenium.sendKeys(locator, keys);
                return null;
            }

        });
    }

    public String getText(GUIElementLocator locator, Boolean visible) {
        final String text = (String) callElementCommand(locator,
                new ElementCommand<Object>("getText", false) {

            @Override
            public Object call(GUIElementLocator locator) {
                return selenium.getText(locator);
            }
        }, visible, false);
        return text;
    }

    public boolean isChecked(GUIElementLocator locator) {
        final Boolean returnValue = (Boolean) callElementCommand(locator,
                new ElementCommand<Object>("isChecked", false) {

            @Override
            public Object call(GUIElementLocator locator) {
                return selenium.isChecked(locator);
            }
        }, true, false);
        return returnValue.booleanValue();
    }

    public String[] getSelectOptions(GUIElementLocator locator) {
        final String[] selectedOptions = (String[]) callElementCommand(locator, new ElementCommand<Object>("getSelectOptions",
                false) {

            @Override
            public Object call(GUIElementLocator locator) {
                return selenium.getSelectOptions(locator);
            }
        }, true, false);
        return selectedOptions;
    }

    public String getSelectedValue(GUIElementLocator locator) {
        final String selectedValue = (String) callElementCommand(locator,
                new ElementCommand<Object>("getSelectedValue", false) {

            @Override
            public Object call(GUIElementLocator locator) {
                return selenium.getSelectedValue(locator);
            }
        }, true, false);
        return selectedValue;
    }

    public String getSelectedLabel(GUIElementLocator locator) {
        final String selectedLabel = (String) callElementCommand(locator,
                new ElementCommand<Object>("getSelectedLabel", false) {

            @Override
            public Object call(GUIElementLocator locator) {
                return selenium.getSelectedLabel(locator);
            }
        }, true, false);
        return selectedLabel;
    }

    public String getValue(GUIElementLocator locator) {
        return selenium.getValue(locator);
    }

    private void waitForWindow(final WindowLocator windowLocator) {
        boolean windowIsFound = retryUntilTimeout(new ConditionCheck() {
            @Override
            public boolean eval() {
                final String windowTitle = windowLocator.toString();
                final String[] titles = getAllWindowTitles();
                for (String title : titles) {
                    if (title.equalsIgnoreCase(windowTitle)) {
                        return true;
                    }
                }
                return false;
            }
        });
        if (!windowIsFound) {
            throw new AutomationException("Window not found");
        }
    }

    private void callWindowCommand(WindowLocator locator, WindowCommand command) {
        waitForWindow(locator);
        command.call(locator);
    }

    public void selectWindow(WindowLocator locator) {
        callWindowCommand(locator, new WindowCommand() {

            @Override
            public void call(WindowLocator locator) {
                selectWindowDirectly(locator);
            }

        });
    }

    public void selectWindowDirectly(WindowLocator locator) {
        selenium.selectWindow(locator);
    }

    public void selectWindowByTechnicalName(String locator) {
        selenium.selectWindow(locator);
    }

    public Map<String, String> getAllWindowHandlesAndTitles() {
        return selenium.getAllWindowHandlesAndTitles();
    }

    /**
     * @see Selenium#getAllWindowTitles()
     */
    public String[] getAllWindowTitles() {
        return selenium.getAllWindowTitles();
    }

    /**
     * @see Selenium#getAllWindowIds()
     */
    public String[] getAllWindowIDs() {
        return selenium.getAllWindowIDs();
    }

    /** @see Selenium#getAllWindowNames()  */
    public String[] getAllWindowNames() {
        return selenium.getAllWindowNames();
    }

    /** @see Selenium#getTitle() */
    public String getTitle() {
        return selenium.getTitle();
    }

    public String getWindowHandle() {
        return selenium.getWindowHandle();
    }

    /** @see Selenium#windowMaximize() */
    public void windowMaximize() {
        selenium.windowMaximize();
    }

    /**
     * @see Selenium#windowFocus()
     */
    public void windowFocus() {
        selenium.windowFocus();
    }

    public void switchToIFrame(Locator iframeLocator) {
        selenium.switchToIFrame(iframeLocator);
    }

    public Attachment getPageSource() {
        final String pageSource = selenium.getHtmlSource();
        final Attachment attachment = new StringAttachment("Source", pageSource, configuration.getPageSourceAttachmentExtension());
        return attachment;
    }

    private Attachment getScreenshotAttachment(String base64Data) {
        final String title = getTitle();
        final Base64 base64 = new Base64();
        final byte[] decodedData = base64.decode(base64Data);
        return new BinaryAttachment(title, decodedData, configuration.getScreenshotAttachmentExtension());
    }

    public Attachment getScreenshotOfThePage() {
        final String base64Data = selenium.captureScreenshotToString();
        final Attachment attachment = getScreenshotAttachment(base64Data);
        return attachment;
    }

    public boolean hasFocus(final GUIElementLocator locator) {
        boolean returnValue = (Boolean) callElementCommand(locator,
                new ElementCommand<Object>("hasFocus", false) {

            @Override
            public Object call(GUIElementLocator locators) {
                return selenium.hasFocus(locator);
            }

        });
        return returnValue;
    }

    public String[] getLabels(GUIElementLocator locator) {
        return selenium.getDropDownLabels(locator);
    }

    public String[] getValues(final GUIElementLocator locator) {
        String[] returnValue = (String[]) callElementCommand(locator,
                new ElementCommand<Object>("getValues", false) {

            @Override
            public Object call(GUIElementLocator locators) {
                return selenium.getDropDownValues(locator);
            }
        }, true, false);
        return returnValue;
    }

    public void focus(GUIElementLocator locator) {
        selenium.focus(locator);
    }

    public String getAttributeValue(final GUIElementLocator elementLocator, final String attributeName) {
        String returnValue = (String) callElementCommand(elementLocator,
                new ElementCommand<Object>("getAttributeValue", false) {

            @Override
            public Object call(GUIElementLocator locators) {
                return selenium.getAttributeValue(elementLocator, attributeName);
            }
        }, true, false);
        return returnValue;
    }

    public void keyPress(int keycode) {
        selenium.keyPress(keycode);
    }

    public void doubleClick(GUIElementLocator locator) {
        doBeforeDelegate(locator, true, true);
        selenium.doubleClick(locator);
    }

    public void close() {
        selenium.close();
    }

    public void quit() {
        selenium.quit();
    }

    public String getTable(GUIElementLocator locator, int row, int col) {
        return selenium.getTable(locator, row, col);
    }

    // features that require intervention for authentication -------------------

    public void open(String url) {
        selenium.open(mapUrl(url));
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

    public void waitUntilPresent(GUIElementLocator locator) {
        waitUntilPresent(locator, configuration.getTimeout());
    }

    public void waitUntilPresent(GUIElementLocator locator, long timeOutInMillis) {
        selenium.waitUntilPresent(locator, timeOutInMillis);
    }

    public void waitUntilClickable(GUIElementLocator locator) {
        waitUntilClickable(locator, configuration.getTimeout());
    }

    public void waitUntilClickable(GUIElementLocator locator, long timeOutInMillis) {
        selenium.waitUntilClickable(locator, timeOutInMillis);
    }

    public void waitUntilVisible(GUIElementLocator locator) {
        waitUntilVisible(locator, configuration.getTimeout());
    }

    public void waitUntilVisible(GUIElementLocator locator, long timeOutInMillis) {
        selenium.waitUntilVisible(locator, timeOutInMillis);
    }

    public void waitUntilInvisible(GUIElementLocator locator) {
        selenium.waitUntilInvisible(locator);
    }

    public void waitForElementNotPresent(GUIElementLocator locator) { // ENHANCE migrate to Selenium 2
        waitForElementNotPresent(locator, getTimeout());
    }

    public void waitForElementNotPresent(final GUIElementLocator locator, long timeout) { // ENHANCE migrate to Selenium 2
        boolean elementIsNotFound = retryUntilTimeout(new ConditionCheck() {
            @Override
            public boolean eval() {
                return !selenium.isElementPresent(locator);
            }
        }, timeout);
        if (!elementIsNotFound) {
            throw new AutomationException("An element was unexpectedly found");
        }
    }

    public void waitForInForeground(GUIElementLocator locator) { // ENHANCE migrate to Selenium 2
        waitForInForeground(locator, getTimeout());
    }

    public void waitForInForeground(final GUIElementLocator locator, long timeout) {
        // ENHANCE migrate to Selenium 2
        boolean elementIsInForeground = retryUntilTimeout(new ConditionCheck() {
            @Override
            public boolean eval() {
                return selenium.isInForeground(locator);
            }
        }, timeout);
        if (!elementIsInForeground) {
            throw new AutomationException("Element not in foreground.");
        }
    }

    public int getTimeout() {
        return configuration.getTimeout();
    }

    public int getPauseBetweenRetries() {
        return configuration.getPauseBetweenRetries();
    }

    public SeleniumWrapperConfiguration getConfiguration() {
        return configuration;
    }

}