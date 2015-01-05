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
package org.aludratest.service.gui.web.selenium.selenium1;

import org.aludratest.exception.AutomationException;
import org.aludratest.exception.FunctionalFailure;
import org.aludratest.exception.TechnicalException;
import org.aludratest.service.SystemConnector;
import org.aludratest.service.gui.component.Link;
import org.aludratest.service.gui.web.selenium.ConditionCheck;
import org.aludratest.service.gui.web.selenium.ElementCommand;
import org.aludratest.service.gui.web.selenium.SeleniumResourceService;
import org.aludratest.service.gui.web.selenium.SeleniumWrapperConfiguration;
import org.aludratest.service.gui.web.selenium.WindowCommand;
import org.aludratest.service.locator.element.ElementLocators.ElementLocatorsGUI;
import org.aludratest.service.locator.element.GUIElementLocator;
import org.aludratest.service.locator.option.OptionLocator;
import org.aludratest.service.locator.window.TitleLocator;
import org.aludratest.service.locator.window.WindowLocator;
import org.aludratest.service.util.ServiceUtil;
import org.aludratest.service.util.TaskCompletionUtil;
import org.aludratest.testcase.event.attachment.Attachment;
import org.aludratest.testcase.event.attachment.BinaryAttachment;
import org.aludratest.testcase.event.attachment.StringAttachment;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.SeleniumException;

/**
 * Wraps an instance of the {@link SeleniumFacade} and provides methods
 * for accessing UI elements and timing.
 * @author Marcel Malitz
 * @author Joerg Langnickel
 * @author Volker Bergmann
 */
public class SeleniumWrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeleniumWrapper.class);

    private SeleniumFacade selenium = null;

    private final SeleniumWrapperConfiguration configuration;

    private SeleniumResourceService seleniumResourceService;

    private String usedSeleniumHost = null;

    private SystemConnector systemConnector;

    // initialization ----------------------------------------------------------

    /**
     * Constructor.
     * 
     * @param moduleName
     *            the name of the module for which to create a wrapper instance.
     * @param seleniumResourceService
     *            the {@link SeleniumResourceService} to use
     * @param configuration
     *            the configuration to use.
     * */
    public SeleniumWrapper(String moduleName, SeleniumResourceService seleniumResourceService,
            SeleniumWrapperConfiguration configuration) {
        this.configuration = configuration;
        this.seleniumResourceService = seleniumResourceService;
        init();
    }

    private void init() {
        try {
            usedSeleniumHost = seleniumResourceService.acquire();
            // int seleniumPort = configuration.getDefaultSeleniumPort();
            String host = usedSeleniumHost;

            selenium = new SeleniumFacade(configuration, host);
            selenium.start();
        } catch (Exception e) {
            if (selenium != null) {
                selenium.stop();
            }
            seleniumResourceService.release(usedSeleniumHost);
            throw new TechnicalException(e.getMessage() + ". Used Host = " + usedSeleniumHost, e);
        }
    }


    // interface ---------------------------------------------------------------

    /** @return the {@link #configuration} */
    public SeleniumWrapperConfiguration getConfiguration() {
        return configuration;
    }

    public int getHostCount() {
        return seleniumResourceService.getHostCount();
    }

    /** Sets the {@link #systemConnector}.
     *  @param systemConnector the {@link SystemConnector} to set */
    public void setSystemConnector(SystemConnector systemConnector) {
        this.systemConnector = systemConnector;
    }

    /** Closes the application under test. */
    public void tearDown() {
        closeApplicationUnderTest();
        // give host URL back to available hosts in blocking queue
        seleniumResourceService.release(usedSeleniumHost);
    }

    /** Opens a web page.
     *  @param url the URL of the web page to open */
    public void open(String url) {
        selenium.open(url);
        waitForPageToLoad();
    }

    /** Waits for a web page to load. */
    public void waitForPageToLoad() {
        selenium.waitForPageToLoad(getTimeout());
    }

    /** Refreshes the currently active web page. */
    public void refresh() {
        selenium.refresh();
        waitForPageToLoad();
    }

    /** @return the currently used SeleniumHost */
    public String getUsedSeleniumHost() {
        return usedSeleniumHost;
    }

    /** Waits until an element is present.
     *  @param locator a {@link GUIElementLocator} of the desired element */
    public void waitForElement(GUIElementLocator locator) {
        waitForElement(locator, getTimeout());
    }

    /** Waits until an element is present.
     *  @param locator a {@link GUIElementLocator} of the desired element
     *  @param timeout the maximum time to wait */
    public void waitForElement(final GUIElementLocator locator, long timeout) {
        ConditionCheck elementPresenceCheck = new ConditionCheck() {
            @Override
            public boolean eval() {
                return selenium.isElementPresent(locator);
            }
        };
        if (!retryUntilTrueOrTimeout(elementPresenceCheck, timeout)) {
            throw new AutomationException("Element not found: " + locator);
        }
    }

    /** Waits until an element is absent.
     *  @param locator a {@link GUIElementLocator} of the target element */
    public void waitForElementNotPresent(GUIElementLocator locator) {
        waitForElementNotPresent(locator, getTimeout());
    }

    /** Waits until an element is absent.
     *  @param locator a {@link GUIElementLocator} of the target element
     *  @param timeout the maximum time to wait */
    public void waitForElementNotPresent(final GUIElementLocator locator, long timeout) {
        boolean elementIsNotFound = retryUntilTrueOrTimeout(new ConditionCheck() {
            @Override
            public boolean eval() {
                return !selenium.isElementPresent(locator);
            }
        }, timeout);
        if (!elementIsNotFound) {
            throw new AutomationException("An element was unexpectedly found");
        }
    }

    /** Waits until an element is visible.
     *  @param locator a {@link GUIElementLocator} of the target element */
    public void waitForVisible(GUIElementLocator locator) {
        waitForVisible(locator, getTimeout());
    }

    /** Waits until an element is visible.
     *  @param locator a {@link GUIElementLocator} of the target element
     *  @param timeout the maximum time to wait */
    public void waitForVisible(final GUIElementLocator locator, long timeout) {
        if (!isCalledByLink()) {
            if ((locator instanceof ElementLocatorsGUI) && ((ElementLocatorsGUI) locator).getUsedOption() == null) {
                throw new AutomationException("ElementLocatorsGUI must have usedOption set before waiting for visible");
            }

            ConditionCheck visibilityCheck = new ConditionCheck() {
                @Override
                public boolean eval() {
                    return selenium.isVisible(locator);
                }
            };
            if (!retryUntilTrueOrTimeout(visibilityCheck, timeout)) {
                throw new AutomationException("The element is not visible.");
            }
        }
    }

    /** Waits until an element is enabled.
     *  @param locator a {@link GUIElementLocator} of the target element */
    public void waitForEnabled(GUIElementLocator locator) {
        waitForEnabled(locator, getTimeout());
    }

    /** Waits until an element is enabled.
     *  @param locator a {@link GUIElementLocator} of the target element
     *  @param timeout the maximum time to wait */
    public void waitForEnabled(final GUIElementLocator locator, long timeout) {
        if (!isCalledByLink()) {
            boolean elementIsEnabled = retryUntilTrueOrTimeout(new ConditionCheck() {
                @Override
                public boolean eval() {
                    return selenium.isEditable(locator);
                }
            }, timeout);
            if (!elementIsEnabled) {
                throw new AutomationException("Element not editable.");
            }
        }
    }

    /** Waits until an element is in the foreground.
     *  @param locator a {@link GUIElementLocator} of the target element */
    public void waitForInForeground(final GUIElementLocator locator) {
        waitForInForeground(locator, getTimeout());
    }

    /** Waits until an element is in the foreground.
     *  @param locator a {@link GUIElementLocator} of the target element
     *  @param timeout the maximum time to wait */
    public void waitForInForeground(final GUIElementLocator locator, long timeout) {
        ConditionCheck foregroundCheck = new ConditionCheck() {
            @Override
            public boolean eval() {
                return selenium.isInForeground(locator);
            }
        };
        if (!retryUntilTrueOrTimeout(foregroundCheck, timeout)) {
            throw new AutomationException("Element not in foreground.");
        }
    }

    /** Clicks a web GUI element.
     *  @param locator
     *  @param taskCompletionTimeout */
    public void click(GUIElementLocator locator, int taskCompletionTimeout) {
        ElementCommand<Void> clickCommand = new ElementCommand<Void>("click", true) {
            @Override
            public Void call(GUIElementLocator locator) {
                selenium.click(locator);
                return null;
            }
        };
        callElementCommand(locator, taskCompletionTimeout, clickCommand);
    }

    /** Clicks a web GUI element requiring it to be not editable.
     *  @param locator
     *  @param taskCompletionTimeout */
    public void clickNotEditable(GUIElementLocator locator, int taskCompletionTimeout) {
        ElementCommand<Void> clickNotEditableCommand = new ElementCommand<Void>("clickNotEditable", true) {
            @Override
            public Void call(GUIElementLocator locator) {
                selenium.click(locator);
                return null;
            }
        };
        callElementCommand(locator, taskCompletionTimeout, true, false, clickNotEditableCommand);
    }

    /**
     * Double clicks a web GUI element requiring it to be not editable.
     * 
     * @param locator
     * @param taskCompletionTimeout
     */
    public void doubleClickNotEditable(GUIElementLocator locator, int taskCompletionTimeout) {
        ElementCommand<Void> doubleClickNotEditableCommand = new ElementCommand<Void>("doubleClickNotEditable", true) {
            @Override
            public Void call(GUIElementLocator locator) {
                selenium.doubleClick(locator);
                return null;
            }
        };
        callElementCommand(locator, taskCompletionTimeout, true, false, doubleClickNotEditableCommand);
    }

    /** Tells if an element is present.
     *  @param locator
     *  @return true if the element is present, otherwise false */
    public boolean isElementPresent(GUIElementLocator locator) {
        ElementCommand<Boolean> presenceCommand = new ElementCommand<Boolean>("isElementPresent", false) {
            @Override
            public Boolean call(GUIElementLocator locator) {
                // this method will only be called if the element is
                // present why this method can directly return true
                return true;
            }
        };
        return callElementCommand(locator, -1, true, false, presenceCommand);
    }

    /** Tells if an element is editable.
     * @param locator
     * @return true if the element is editable, otherwise false */
    public boolean isEditable(GUIElementLocator locator) {
        ElementCommand<Boolean> editableCheckCommand = new ElementCommand<Boolean>("isEditable", false) {
            @Override
            public Boolean call(GUIElementLocator locator) {
                return selenium.isEditable(locator);
            }
        };
        return callElementCommand(locator, -1, true, false, editableCheckCommand);
    }

    /** Selects an element.
     *  @param locator
     *  @param optionLocator
     *  @param taskCompletionTimeout  */
    public void select(final GUIElementLocator locator, final OptionLocator optionLocator, int taskCompletionTimeout) {
        ElementCommand<Void> selectCommand = new ElementCommand<Void>("select", true) {
            @Override
            public Void call(GUIElementLocator locator) {
                selenium.select(locator, optionLocator);
                return null;
            }
        };
        callElementCommand(locator, taskCompletionTimeout, selectCommand);
    }

    /** Sends characters to a web GUI element.
     *  @param locator
     *  @param value
     *  @param taskCompletionTimeout  */
    public void type(GUIElementLocator locator, final String value, int taskCompletionTimeout) {
        ElementCommand<Void> typeCommand = new ElementCommand<Void>("type", true) {
            @Override
            public Void call(GUIElementLocator locator) {
                selenium.type(locator, value);
                return null;
            }
        };
        callElementCommand(locator, taskCompletionTimeout, typeCommand);
    }

    /** @param locator
     *  @return the text of a web GUI element */
    public String getText(GUIElementLocator locator) {
        return getText(locator, true);
    }

    /**
     * Returns the text of the GUI element identified by the locator
     * @param locator the locator of the element to examine
     * @param visible flag that tells whether to wait until the element is visible
     * @return the text of the GUI element identified by the locator
     */
    public String getText(GUIElementLocator locator, boolean visible) {
        ElementCommand<String> command = new ElementCommand<String>("getText", false) {
            @Override
            public String call(GUIElementLocator locator) {
                return selenium.getText(locator);
            }
        };
        return callElementCommand(locator, -1, visible, false, command);
    }

    /** Tells if a web GUI element is checked.
     *  @param locator
     *  @return true if the element is checked, otherwise false */
    public boolean isChecked(GUIElementLocator locator) {
        ElementCommand<Boolean> isCheckedCommand = new ElementCommand<Boolean>("isChecked", false) {
            @Override
            public Boolean call(GUIElementLocator locator) {
                return selenium.isChecked(locator);
            }
        };
        return callElementCommand(locator, -1, true, false, isCheckedCommand);
    }

    /** Returns the options of a web GUI &lt;select&gt; element.
     *  @param locator
     *  @return an array of the options of a web GUI &lt;select&gt; element */
    public String[] getSelectOptions(GUIElementLocator locator) {
        ElementCommand<String[]> selectOptionsCommand = new ElementCommand<String[]>("getSelectOptions", false) {
            @Override
            public String[] call(GUIElementLocator locator) {
                return selenium.getSelectOptions(locator);
            }
        };
        return callElementCommand(locator, -1, true, false, selectOptionsCommand);
    }

    /** Returns the selected value of a GUI element.
     *  @param locator
     *  @return the selected value of the GUI element */
    public String getSelectedValue(GUIElementLocator locator) {
        ElementCommand<String> selectedValueCommand = new ElementCommand<String>("getSelectedValue", false) {
            @Override
            public String call(GUIElementLocator locator) {
                return selenium.getSelectedValue(locator);
            }
        };
        return callElementCommand(locator, -1, true, false, selectedValueCommand);
    }

    /** Returns the selected label of a web GUI component.
     *  @param locator
     *  @return the selected label of a web GUI component. */
    public String getSelectedLabel(GUIElementLocator locator) {
        ElementCommand<String> selectedLabelCommand = new ElementCommand<String>("getSelectedLabel", false) {
            @Override
            public String call(GUIElementLocator locator) {
                return selenium.getSelectedLabel(locator);
            }
        };
        return callElementCommand(locator, -1, true, false, selectedLabelCommand);
    }

    /** Returns the value of a web GUI component.
     *  @param locator
     *  @return  the value of the requested web GUI component */
    public String getValue(GUIElementLocator locator) {
        ElementCommand<String> getValueCommand = new ElementCommand<String>("getValue", false) {
            @Override
            public String call(GUIElementLocator locator) {
                return selenium.getValue(locator);
            }
        };
        return callElementCommand(locator, -1, true, false, getValueCommand);
    }

    /** Selects a window.
     *  @param locator */
    public void selectWindow(WindowLocator locator) {
        callWindowCommand(locator, new WindowCommand() {
            @Override
            public void call(WindowLocator locator) {
                selenium.selectWindow(locator);
            }
        });
    }

    /** Selects a window by its technical name.
     *  @param locator */
    public void selectWindowByTechnicalName(String locator) {
        selenium.selectWindow(locator);
    }

    /** @see Selenium#getAllWindowTitles()
     *  @return a string array of all window titles  */
    public String[] getAllWindowTitles() {
        return selenium.getAllWindowTitles();
    }

    /** @see Selenium#getAllWindowIds()
     *  @return a string array of all window IDs */
    public String[] getAllWindowIDs() {
        return selenium.getAllWindowIDs();
    }

    /** @see Selenium#getAllWindowNames()
     *  @return a string array of all window names */
    public String[] getAllWindowNames() {
        return selenium.getAllWindowNames();
    }

    /** @see Selenium#getTitle()
     *  @return the title of the current web page */
    public String getTitle() {
        return selenium.getTitle();
    }

    /** @see Selenium#windowMaximize() */
    public void windowMaximize() {
        selenium.windowMaximize();
    }

    /** @see Selenium#windowFocus() */
    public void windowFocus() {
        selenium.windowFocus();
    }

    public void switchToIFrame(GUIElementLocator locator) {
        selenium.switchToIFrame(locator);
    }

    /** @return the source of the current web page */
    public Attachment getPageSource() {
        String pageSource = selenium.getHtmlSource();
        return new StringAttachment("Source", pageSource, configuration.getPageSourceAttachmentExtension());
    }

    /** @return a screenshot of the whole screen */
    public Attachment getScreenshotOfTheWholeScreen() {
        String base64Data = selenium.captureEntirePageScreenshotToString();
        Attachment attachment = getScreenshotAttachment(base64Data);
        return attachment;
    }

    /** @return a screenshot of the page */
    public Attachment getScreenshotOfThePage() {
        String base64Data = selenium.captureScreenshotToString();
        Attachment attachment = getScreenshotAttachment(base64Data);
        return attachment;
    }

    /** @param locator
     *  @return true if the element is focused, otherwise false */
    public boolean hasFocus(final GUIElementLocator locator) {
        ElementCommand<Boolean> hasFocusCommand = new ElementCommand<Boolean>("hasFocus", false) {
            @Override
            public Boolean call(GUIElementLocator locators) {
                return selenium.hasFocus(locator);
            }
        };
        return callElementCommand(locator, -1, hasFocusCommand);
    }

    /** @param locator
     *  @return a string array of all labels of a drop down list */
    public String[] getLabels(GUIElementLocator locator) {
        return selenium.getDropDownLabels(locator);
    }

    /** @param locator
     *  @return a string array of the values of a web GUI element */
    public String[] getValues(final GUIElementLocator locator) {
        ElementCommand<String[]> getValuesCommand = new ElementCommand<String[]>("getValues", false) {
            @Override
            public String[] call(GUIElementLocator locators) {
                return selenium.getDropDownValues(locator);
            }
        };
        return callElementCommand(locator, -1, true, false, getValuesCommand);
    }

    /** Focuses a web GUI element.
     *  @param locator */
    public void focus(final GUIElementLocator locator) {
        ElementCommand<Void> focusCommand = new ElementCommand<Void>("focus", false) {
            @Override
            public Void call(GUIElementLocator locators) {
                selenium.focus(locator);
                return null;
            }
        };
        callElementCommand(locator, -1, focusCommand);
    }

    /** @param elementLocator
     *  @param attributeName
     *  @return the value of the requested attribute of the requested web GUI element
     */
    public String getAttributeValue(final GUIElementLocator elementLocator, final String attributeName) {
        ElementCommand<String> getAttributeValueCommand = new ElementCommand<String>("getAttributeValue", false) {
            @Override
            public String call(GUIElementLocator locators) {
                return selenium.getAttributeValue(elementLocator, attributeName);
            }
        };
        return callElementCommand(elementLocator, -1, true, false, getAttributeValueCommand);
    }

    /** Sends a key-press event to the web GUI.
     *  @param keycode the code of the key to send */
    public void keyPress(int keycode) {
        selenium.keyPress(keycode);
    }

    /** Double-clicks a web GUI element.
     *  @param locator
     *  @param taskCompletionTimeout */
    public void doubleClick(final GUIElementLocator locator, int taskCompletionTimeout) {
        ElementCommand<Void> doubleClickCommand = new ElementCommand<Void>("doubleClick", true) {
            @Override
            public Void call(GUIElementLocator locators) {
                selenium.doubleClick(locator);
                return null;
            }
        };
        callElementCommand(locator, -1, doubleClickCommand);
    }

    /** Closes the Selenium client. */
    public void close() {
        selenium.close();
    }

    /** Returns the text content of a table cell.
     *  @param locator a locator for the table
     *  @param row the table row index
     *  @param col the table column index
     *  @return the text content of a table cell */
    public String getTableCellText(final GUIElementLocator locator, final int row, final int col) {
        ElementCommand<String> getTableCellTextCommand = new ElementCommand<String>("getTableCellText", false) {
            @Override
            public String call(GUIElementLocator locator) {
                return selenium.getTableCellText(locator, row, col);
            }
        };
        return callElementCommand(locator, -1, getTableCellTextCommand);
    }

    /** Adds a custom request header.
     *  @param key the key of the custom request header
     *  @param value the value of the custom request header */
    public void addCustomRequestHeader(String key, String value) {
        selenium.addCustomRequestHeader(key, value);
    }

    /** @return the configured pause between retries */
    public int getPauseBetweenRetries() {
        return configuration.getPauseBetweenRetries();
    }

    /** @return the configured timeout */
    public int getTimeout() {
        return configuration.getTimeout();
    }


    /**
     * Retries to evaluate the condition. If evaluation is successful, the
     * method will return regularly. If not, this method will retry it with a
     * pause of {@link SeleniumWrapperConfiguration#getPauseBetweenRetries()} ms
     * between retries until a timeout of {@link SeleniumWrapperConfiguration#getTimeout()}
     * ms is exceeded.
     * @param condition which will be evaluated on each retry
     * @return true, if the condition could be executed successfully; otherwise
     *         false will be returned.
     */
    public boolean retryUntilTimeout(ConditionCheck condition) {
        return retryUntilTrueOrTimeout(condition, getTimeout());
    }


    // private helpers ---------------------------------------------------------

    /**
     * Retries to evaluate the condition. If evaluation is successful, the
     * method will return regularly. If not, this method will retry it with a
     * pause of {@link SeleniumWrapperConfiguration#getPauseBetweenRetries()} ms
     * between retries until a timeout of {@link SeleniumWrapperConfiguration#getTimeout()}
     * ms is exceeded.
     * @param condition which will be evaluated on each retry
     * @param timeout how long will be waited
     * @return true if the condition could be executed successfully, otherwise false
     */
    private boolean retryUntilTrueOrTimeout(ConditionCheck condition, long timeout) {
        long time = System.currentTimeMillis() + timeout;
        while (System.currentTimeMillis() < time) {
            if (condition.eval()) {
                return true;
            } else {
                sleepBetweenRetries();
            }
        }
        return false;
    }

    /** If highlighting is activated all HTML elements which are a target of a
     *  action get highlighted (marked yellow).
     *  @param locator of the element to highlight */
    private void highlight(GUIElementLocator locator) {
        if (configuration.getHighlightCommands()) {
            try {
                selenium.highlight(locator);
            } catch (SeleniumException e) {
                // It does not matter if highlighting works or not, why a
                // possibly thrown exception must be caught to avoid test
                // execution termination.
                LOGGER.trace("Highlighting does not work.", e);
            }
        }
    }

    private <T> T callElementCommand(GUIElementLocator locator, int taskCompletionTimeout,
            ElementCommand<T> command) {
        return callElementCommand(locator, taskCompletionTimeout, true, true, command);
    }

    private <T> T callElementCommand(GUIElementLocator locator, int taskCompletionTimeout,
            boolean visible, boolean enabled, ElementCommand<T> command) {
        doBeforeDelegate(locator, visible, enabled, command.isInteraction());
        try {
            T returnValue = command.call(locator);
            doAfterDelegate(taskCompletionTimeout, command.toString());
            return returnValue;
        }
        catch (SeleniumException e) {
            String msg = e.getMessage();
            if (msg != null && msg.matches("ERROR: Element .* not found")) {
                throw new FunctionalFailure(msg);
            }
            throw e;
        }
    }

    private void doBeforeDelegate(GUIElementLocator locator,
            boolean visible, boolean enabled, boolean actionPending) {
        if (actionPending) {
            waitUntilNotBusy();
        }
        waitForElement(locator);
        waitForInForeground(locator);
        if (visible) {
            waitForVisible(locator);
        }
        if (enabled) {
            waitForEnabled(locator);
        }
        highlight(locator);
    }

    private void doAfterDelegate(int taskCompletionTimeout, String failureMessage) {
        if (taskCompletionTimeout >= 0) {
            int timeout = (taskCompletionTimeout == 0 ? configuration.getTaskCompletionTimeout() : taskCompletionTimeout);
            TaskCompletionUtil.waitForActivityAndCompletion(systemConnector, failureMessage,
                    configuration.getTaskStartTimeout(), timeout, configuration.getTaskPollingInterval());
        }
    }

    private void waitUntilNotBusy() {
        if (this.systemConnector != null) {
            TaskCompletionUtil.waitUntilNotBusy(this.systemConnector, configuration.getTaskCompletionTimeout(),
                    configuration.getTaskPollingInterval(), "System not available");
        }
    }

    private void sleepBetweenRetries() {
        try {
            Thread.sleep(configuration.getPauseBetweenRetries());
        } catch (InterruptedException e) {
            throw new TechnicalException("Interrupted while waiting", e);
        }
    }

    private boolean isCalledBy(Class<?> klass) {
        boolean calledBy = false;
        String fullLinkClassName = klass.getName();
        StackTraceElement[] traceElements = Thread.currentThread().getStackTrace();
        for (int i = 0; i < traceElements.length && !calledBy; i++) {
            String fullClassName = traceElements[i].getClassName();
            if (fullLinkClassName.equals(fullClassName)) {
                calledBy = true;
            }
        }
        return calledBy;
    }

    private boolean isCalledByLink() {
        return isCalledBy(Link.class);
    }

    private void waitForWindow(final WindowLocator windowLocator) {
        boolean windowIsFound = retryUntilTimeout(new ConditionCheck() {
            @Override
            public boolean eval() {
                if (windowLocator instanceof TitleLocator) {
                    String requestedTitle = ((TitleLocator) windowLocator).getTitle();
                    String[] titles = getAllWindowTitles();
                    for (String title : titles) {
                        if (title.equalsIgnoreCase(requestedTitle)) {
                            return true;
                        }
                    }
                    return false;
                } else {
                    throw ServiceUtil.newUnsupportedLocatorException(windowLocator);
                }
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

    private Attachment getScreenshotAttachment(String base64Data) {
        Base64 base64 = new Base64();
        byte[] decodedData = base64.decode(base64Data);
        String suffix = configuration.getScreenshotAttachmentExtension();
        return new BinaryAttachment("Screenshot", decodedData, suffix);
    }

    /** Closes the application under test respectively the main browser window
     *  and all child windows. */
    private void closeApplicationUnderTest() {
        if (configuration.getCloseTestappAfterExecution()) {
            selenium.stop();
        }
    }

}