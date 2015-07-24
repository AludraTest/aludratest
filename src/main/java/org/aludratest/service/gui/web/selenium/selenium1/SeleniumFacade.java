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

import java.util.ArrayList;

import org.aludratest.exception.AutomationException;
import org.aludratest.exception.PerformanceFailure;
import org.aludratest.exception.TechnicalException;
import org.aludratest.service.gui.web.selenium.SeleniumWrapperConfiguration;
import org.aludratest.service.locator.Locator;
import org.aludratest.service.locator.element.CSSLocator;
import org.aludratest.service.locator.element.ElementLocators.ElementLocatorsGUI;
import org.aludratest.service.locator.element.GUIElementLocator;
import org.aludratest.service.locator.element.IdLocator;
import org.aludratest.service.locator.element.LabelLocator;
import org.aludratest.service.locator.element.XPathLocator;
import org.aludratest.service.locator.option.OptionLocator;
import org.aludratest.service.locator.window.TitleLocator;
import org.aludratest.service.locator.window.WindowLocator;
import org.aludratest.service.util.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.selenium.CommandProcessor;
import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.HttpCommandProcessor;
import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.SeleniumException;

/**
 * Wraps an instance of the {@link Selenium} class (v.1.x) and provides
 * delegation methods.
 * 
 * @author Marcel Malitz
 * @author Joerg Langnickel
 * @author Volker Bergmann
 */
public class SeleniumFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeleniumFacade.class);

    // constants ---------------------------------------------------------------

    static final String HAS_FOCUS_SCRIPT = "this.browserbot.findElement('%s') == window.document.activeElement";
    private static final String DELIMITER = ";;;";
    private static final String EMPTY_MARKER = "|||";
    private static final String DROPDOWN_SCRIPT = "var dropdownbox = this.browserbot.findElement('%s');" + "var values = '';"
            + "for (var i = 0; i < dropdownbox.length; i++) { var t=dropdownbox.options[i].%s; " + "values = values + '"
            + DELIMITER + "' + (t == '' ? '" + EMPTY_MARKER + "' : t);" + "}" + "values;";
    private static final int DEFAULT_Z_INDEX = 0;

    private static final String ZINDEX_SCRIPT = "var testObj = this.browserbot.findElement('%s');"
            // +
            // "var iframe = this.browserbot.findElement('css=[id^=\"history-frame\"]');"
            // + "var value = iframe.style.zIndex;"
            + "var value;" + "var found = false;" + "while (found == false && testObj.nodeType == 1) {"
            + "    if (testObj.style.zIndex != '') {" + "        found = true;" + "        value = testObj.style.zIndex;"
            + "    } else {" + "        testObj = testObj.parentNode;" + "    }" + "}" + "value = 'z-index: ' + value;";

    private final String zIndexSearch = "this.browserbot.findElement('xpath=(//" + "iframe[contains(@style, \"z-index\")])[%d]')"
            + ".getAttribute('style')";

    /*
     * The event AsynchronousReceive will be checked and the variable
     * 'ajaxGuiReady' will be set to true. But because of an error of request
     * handling in Globe application, the event AsynchronousReceive can not be
     * triggered in some special situation. So the variable 'ajaxGuiReady' can
     * not be checked as a condition. The function onAsynchronousReceive() will
     * be called anyway to balance the waiting time of a request and the waiting
     * time of GUI update.
     */
    private static final String REQUEST_COUNTER_SCRIPT = "var requestCounter = 0;" + "var ajaxGuiReady = true;"
            + "var intervalId = setInterval('inject()', 0);" + "function onIceSend() {" + "    requestCounter++;" + "}"
            + "function onIceReceive() {" + "	if (requestCounter>=1) {" + "    requestCounter--;" + " }" + "}"
            + "function onGuiReady() {" + "    ajaxGuiReady = true;" + "}" + "function inject() {"
            + "    if (selenium.browserbot.getUserWindow().IceLoaded) {"
            + "        selenium.browserbot.getUserWindow().Ice.onSendReceive('document:body', onIceSend, onIceReceive);"
            + "    	   if (typeof(selenium.browserbot.getUserWindow().jQuery) == 'function') {"
            + "             ajaxGuiReady = false;"
            + "        		selenium.browserbot.getUserWindow().Ice.onAsynchronousReceive('document:body', onGuiReady);"
            + "        }" + "    }" + "	   clearInterval(intervalId);" + "}" + "";

    private static final String DROPDOWN_OPTION_VALUE_PROPERTY = "value";
    private static final String DROPDOWN_OPTION_LABEL_PROPERTY = "text";
    private static final String ATTRIBUTE = "@";

    // attributes --------------------------------------------------------------

    private SeleniumWrapperConfiguration configuration;
    private Selenium selenium = null;

    // constructor -------------------------------------------------------------

    /** Constructor.
     * 
     * @param configuration the relevant {@link SeleniumWrapperConfiguration} instance
     * @param usedSeleniumHost the used Selenium host
     * @param seleniumUrl The full Selenium URL, e.g. http://127.0.0.1:4444/. */
    public SeleniumFacade(SeleniumWrapperConfiguration configuration, String seleniumUrl) {
        this.configuration = configuration;
        String url = seleniumUrl + (seleniumUrl.endsWith("/") ? "" : "/") + "selenium-server/driver/";
        CommandProcessor processor = new HttpCommandProcessor(url,
                configuration.getBrowser(), configuration.getUrlOfAut());
        selenium = new DefaultSelenium(processor);
    }

    // interface ---------------------------------------------------------------

    /** Starts the Selenium client. */
    public void start() {
        selenium.start("addCustomRequestHeaders=true");
        selenium.setTimeout(String.valueOf(configuration.getTimeout()));
        selenium.setSpeed(configuration.getSpeed());
        selenium.setBrowserLogLevel(configuration.getBrowserLogLevel());
    }

    /**
     * Selects a browser window.
     * 
     * @param windowId
     *            the id of the window
     */
    public void selectWindow(String windowId) {
        selenium.selectWindow(windowId);
        addAjaxScript();
    }

    /** Closes the Selenium client */
    public void close() {
        selenium.close();
    }

    /** Stops the Selenium client */
    public void stop() {
        selenium.stop();
    }

    /**
     * Waits until the currently loading web page is completely loaded.
     * 
     * @param timeout
     *            the maximum time to wait
     */
    public void waitForPageToLoad(int timeout) {
        selenium.waitForPageToLoad(String.valueOf(timeout));
        addAjaxScript();
    }

    /** Waits until the currently active requests have ended. */
    public void waitForRequestsToEnd() {
        selenium.getEval("requestCounter;");
        selenium.waitForCondition("requestCounter == 0", String.valueOf(configuration.getTimeout()));
    }

    /**
     * Opens the web page with the provided url.
     * 
     * @param url
     *            the URL of the page to open
     */
    public void open(String url) {
        LOGGER.debug("Opening {}", url);
        try {
            selenium.open(url);
        }
        catch (SeleniumException e) {
            String message = e.getMessage();
            if (message != null && message.contains("Timed out")) {
                throw new PerformanceFailure("Error opening '" + url + "' : " + e);
            }
        }
    }

    /** refreshes the currently active web page. */
    public void refresh() {
        selenium.refresh();
    }

    /**
     * Highlights a web GUI element.
     * 
     * @param locator
     *            a Locator of the element to highlight
     */
    public void highlight(GUIElementLocator locator) {
        selenium.highlight(getLocatorOfAllPossible(locator));
    }

    /**
     * Tells if an element is present.
     * 
     * @param locator
     *            a {@link Locator} of the element to find
     * @return true if an element has been found for the locator, otherwise
     *         false
     */
    public boolean isElementPresent(GUIElementLocator locator) {
        if (locator instanceof ElementLocatorsGUI) {
            return determineLocatorsOption((ElementLocatorsGUI) locator);
        }
        else {
            try {
                return selenium.isElementPresent(getLocatorOfAllPossible(locator));
            }
            catch (SeleniumException se) {
                if (isFirefoxError(se)) {
                    return false;
                }
                if (isWindowClosedError(se)) {
                    LOGGER.debug("Current window was closed while checking element present: " + se.getMessage());
                    return false;
                }
                throw se;
            }
        }
    }

    /** Clicks a web GUI element.
     * 
     * @param locator a {@link Locator} of the element to click */
    public void click(GUIElementLocator locator) {
        selenium.click(getSeleniumLocatorForClick(locator));
    }

    /** Hovers a web GUI element.
     * 
     * @param locator a {@link Locator} of the element to hover */
    public void hover(GUIElementLocator locator) {
        selenium.mouseOver(getSeleniumLocatorForClick(locator));
    }

    /** Tells if a web GUI element is editable.
     * 
     * @param locator a {@link Locator} of the element to examine
     * @return <code>true</code> if the element is editable, otherwise <code>false</code>. */
    public boolean isEditable(GUIElementLocator locator) {
        try {
            if (!isEnabled(locator)) {
                return false;
            }

            String attr = selenium.getAttribute(getSeleniumLocator(locator) + "@readonly");
            return (attr == null || !("readonly".equals(attr) || "true".equals(attr)));
        }
        catch (Exception e) { // NOSONAR
            // assert "no readonly attribute". This could return false positives for non-input fields.
            return true;
        }
    }

    /** Tells if a web GUI element is enabled.
     * 
     * @param locator a {@link Locator} of the element to examine
     * @return <code>true</code> if the element is enabled, otherwise <code>false</code>. */
    public boolean isEnabled(GUIElementLocator locator) {
        try {
            return selenium.isEditable(getSeleniumLocator(locator));
        }
        catch (Exception e) { // NOSONAR
            return false;
        }
    }

    /**
     * To get the z-index (defined in the attribute "style") for the operated
     * element. There are 3 possibilities for retrieving z-index: <br/>
     * 1) If a z-index is defined for this element or its ancestor, then return
     * this value <br/>
     * 2) If no z-index is defined for this element and its ancestor, then use
     * the base z-index for this page <br/>
     * 3) For an element of the type "LabelLocator", the base z-index will be
     * returned
     * 
     * @param locator
     *            locator for element
     * @return current z-Index
     */
    public int getCurrentZIndex(GUIElementLocator locator) {
        if (!(locator instanceof LabelLocator)) {
            String seleniumLocator = getSeleniumLocator(locator).replace('\'', '"');
            selenium.isElementPresent(seleniumLocator);
            String script = String.format(ZINDEX_SCRIPT, seleniumLocator);
            try {
                String zIndex = selenium.getEval(script);
                return getzIndexFromStyle(zIndex);
            }
            catch (Exception e) { // NOSONAR
                return getBaseZIndex();
            }
        }
        else {
            return getBaseZIndex();
        }
    }

    /**
     * Checks if an element is blocked by a modal dialog
     * 
     * @param locator
     *            of the element to check
     * @return the element is blocked or not
     */
    public boolean isInForeground(GUIElementLocator locator) {
        if (getCurrentZIndex(locator) < getMaxZIndex()) {
            return false;
        }
        return true;
    }

    /**
     * Sends some key events to the specified web GUI element.
     * 
     * @param locator
     *            {@link Locator} of the element to receive the characters
     * @param value
     *            a {@link String} containing the characters to type
     */
    public void type(GUIElementLocator locator, String value) {
        selenium.type(getSeleniumLocator(locator), value);
    }

    /**
     * Selects a drop-down option in a web GUI.
     * 
     * @param locator
     *            locator of the drop-down element
     * @param optionLocator
     *            locator of the option within the drop-down element
     */
    public void select(GUIElementLocator locator, OptionLocator optionLocator) {
        selenium.select(getSeleniumLocator(locator), getSelectOptionLocator(optionLocator));
    }

    /**
     * @param locator
     *            the {@link Locator} of the element to examine
     * @return the text of a web GUI element
     */
    public String getText(GUIElementLocator locator) {
        return selenium.getText(getSeleniumLocator(locator));
    }

    /**
     * Tells if a Checkbox is checked.
     * 
     * @param locator
     *            {@link Locator} of the GUI element to examine
     * @return true if the element is checked, otherwise false
     */
    public boolean isChecked(GUIElementLocator locator) {
        return selenium.isChecked(getSeleniumLocator(locator));
    }

    /**
     * @param locator
     *            {@link Locator} of the GUI element to examine
     * @return the options provided by a drop-down list
     */
    public String[] getSelectOptions(GUIElementLocator locator) {
        return selenium.getSelectOptions(getSeleniumLocator(locator));
    }

    /**
     * @param locator
     *            {@link Locator} of the GUI element to examine
     * @return the selected value of a drop-down list
     */
    public String getSelectedValue(GUIElementLocator locator) {
        return selenium.getSelectedValue(getSeleniumLocator(locator));
    }

    /**
     * @param locator
     *            {@link Locator} of the GUI element to examine
     * @return true if the element is visible, otherwise false
     */
    public boolean isVisible(GUIElementLocator locator) {
        try {
            return selenium.isVisible(getSeleniumLocator(locator));
        }
        catch (SeleniumException e) {
            String msg = e.getMessage();
            if (msg != null && msg.endsWith(" not found")) {
                return false;
            }
            throw e;
        }
    }

    /**
     * @param locator
     *            {@link Locator} of the GUI element to examine
     * @return the content of the selected label
     */
    public String getSelectedLabel(GUIElementLocator locator) {
        return selenium.getSelectedLabel(getSeleniumLocator(locator));
    }

    /**
     * @param locator
     *            {@link Locator} of the GUI element to examine
     * @return the value of the denoted GUI element
     */
    public String getValue(GUIElementLocator locator) {
        return selenium.getValue(getSeleniumLocator(locator));
    }

    /**
     * Selects a GUI window.
     * 
     * @param locator
     *            {@link Locator} of the window to examine
     */
    public void selectWindow(WindowLocator locator) {
        selenium.selectWindow(getWindowLocator(locator));
        addAjaxScript();
    }

    /** @return the titles of all GUI windows of the associated Selenium client */
    public String[] getAllWindowTitles() {
        return selenium.getAllWindowTitles();
    }

    /** @return the IDs of all GUI windows of the associated Selenium client */
    public String[] getAllWindowIDs() {
        return selenium.getAllWindowIds();
    }

    /** @return the titles of all GUI windows of the associated Selenium client */
    public String[] getAllWindowNames() {
        return selenium.getAllWindowNames();
    }

    /** @return the title of the currently active. */
    public String getTitle() {
        return selenium.getTitle();
    }

    /** Maximizes the currently active window. */
    public void windowMaximize() {
        selenium.windowMaximize();
    }

    /** Focuses the GUI window */
    public void windowFocus() {
        selenium.windowFocus();
    }

    /** Switches to the requested IFrame.
     * @param iframeLocator the locator of the IFrame to switch to, or null for the top-level frame */
    public void switchToIFrame(GUIElementLocator iframeLocator) {
        if (iframeLocator == null) {
            selenium.selectFrame("relative=top");
        }
        else {
            selenium.selectFrame(getSeleniumLocator(iframeLocator));
        }
    }

    /** @return the HTML source code of the active web page */
    public String getHtmlSource() {
        return selenium.getHtmlSource();
    }

    /**
     * Captures the entire web page as String.
     * 
     * @return the screenshot as base-64 encoded PNG file content
     */
    public String captureEntirePageScreenshotToString() {
        return selenium.captureEntirePageScreenshotToString("");
    }

    /**
     * Captures the entire screen content as screen shot.
     * 
     * @return the screenshot as base-64 encoded PNG file content
     */
    public String captureScreenshotToString() {
        return selenium.captureScreenshotToString();
    }

    /**
     * @param locator
     *            a {@link Locator} of the element to examine
     * @return true if the denoted element has the focus, otherwise false
     */
    public boolean hasFocus(GUIElementLocator locator) {
        String seleniumLocator = getSeleniumLocator(locator);
        String script = String.format(HAS_FOCUS_SCRIPT, seleniumLocator);
        String result = selenium.getEval(script);
        return Boolean.parseBoolean(result);
    }

    /**
     * Focuses a web GUI element.
     * 
     * @param locator
     *            a {@link Locator} of the element to focus
     */
    public void focus(GUIElementLocator locator) {
        String seleniumLocator = getSeleniumLocator(locator);
        if (!isEnabled(locator)) {
            throw new AutomationException("Element not enabled");
        }
        selenium.focus(seleniumLocator);
    }

    /**
     * @param locator
     *            a {@link Locator} of the web GUI element to examine
     * @return the values in a drop-down list
     */
    public String[] getDropDownValues(GUIElementLocator locator) {
        return getDropDownPropertyValues(locator, DROPDOWN_OPTION_VALUE_PROPERTY);
    }

    /**
     * @param locator
     *            a {@link Locator} of the web GUI element to examine
     * @return the labels in a drop-down list
     */
    public String[] getDropDownLabels(GUIElementLocator locator) {
        return getDropDownPropertyValues(locator, DROPDOWN_OPTION_LABEL_PROPERTY);
    }

    /**
     * @param locator
     *            a {@link Locator} of the web GUI element to examine
     * @param attributeName
     *            the name of the requested attribute
     * @return the value of the requested attribute
     */
    public String getAttributeValue(GUIElementLocator locator, String attributeName) {
        String seleniumLocator = getSeleniumLocator(locator);
        return selenium.getAttribute(seleniumLocator + ATTRIBUTE + attributeName);
    }

    /**
     * Sends a key-press to the web GUI.
     * 
     * @param keycode
     *            the code of the key to send
     */
    public void keyPress(int keycode) {
        selenium.keyPressNative(String.valueOf(keycode));
    }

    /**
     * Performs a double click on a web GUI element.
     * 
     * @param locator
     *            a {@link Locator} of the web GUI element to use
     */
    public void doubleClick(GUIElementLocator locator) {
        String seleniumLocator = getSeleniumLocator(locator);
        selenium.doubleClick(seleniumLocator);
    }

    /**
     * Fetches a table cell.
     * 
     * @param locator
     *            a {@link Locator} of the web GUI element to examine
     * @param row
     *            the row index of the requested table cell
     * @param col
     *            the column index of the requested table cell
     * @return the content of the table cell
     */
    public String getTableCellText(GUIElementLocator locator, int row, int col) {
        String seleniumLocator = getSeleniumLocator(locator) + "." + row + "." + col;
        return selenium.getTable(seleniumLocator);
    }

    /**
     * Counts the number of matches of an XPath expression within the currently
     * active web page.
     * 
     * @param xpath
     *            the XPath expression to evaluate
     * @return the number of matches
     */
    public int getXPathCount(String xpath) {
        try {
            Number xpathCount = selenium.getXpathCount(xpath);
            return (xpathCount != null ? xpathCount.intValue() : 0);
        }
        catch (SeleniumException se) {
            if (isFirefoxError(se)) {
                return 0;
            }
            // OK, something else, fire it
            throw se;
        }
    }

    /**
     * Adds a custom request header to the Selenium calls.
     * 
     * @param key
     *            the request header key
     * @param value
     *            the value for the request header
     */
    public void addCustomRequestHeader(String key, String value) {
        selenium.addCustomRequestHeader(key, value);
    }

    // private helpers ---------------------------------------------------------

    private static String[] getValues(String csv) {
        String[] values = csv.split(DELIMITER);
        ArrayList<String> nonEmptyValues = new ArrayList<String>();
        for (String value : values) {
            if (value.length() > 0) {
                nonEmptyValues.add(EMPTY_MARKER.equals(value) ? "" : value);
            }
        }
        return nonEmptyValues.toArray(new String[0]);
    }

    private String[] getDropDownPropertyValues(GUIElementLocator locator, String propertyName) {
        String seleniumLocator = getSeleniumLocator(locator);
        String script = String.format(DROPDOWN_SCRIPT, seleniumLocator, propertyName);
        String csv = selenium.getEval(script);
        return getValues(csv);
    }

    private void addAjaxScript() {
        selenium.removeScript("requestCounterScript");
        selenium.addScript(REQUEST_COUNTER_SCRIPT, "requestCounterScript");
    }

    // private helpers for locator handling ------------------------------------

    private String getWindowLocator(WindowLocator windowLocator) {
        if (windowLocator instanceof TitleLocator) {
            return getLocator("title=", windowLocator);
        }
        else {
            throw ServiceUtil.newUnsupportedLocatorException(windowLocator);
        }
    }

    /** Calculate for a typed locator corresponding selenium syntax
     * 
     * @param locator
     * @return typed locator in selenium syntax */
    private String getSeleniumLocator(GUIElementLocator locator) {
        GUIElementLocator simpleLocator = unwrap(locator);
        if (simpleLocator instanceof XPathLocator) {
            return getXPathLocator((XPathLocator) simpleLocator);
        }
        else if (simpleLocator instanceof IdLocator) {
            return getIdLocator((IdLocator) simpleLocator);
        }
        else if (simpleLocator instanceof CSSLocator) {
            return getCSSLocator((CSSLocator) simpleLocator);
        }
        else if (simpleLocator instanceof LabelLocator) {
            return getLinkLocator(simpleLocator);
        }
        else {
            throw ServiceUtil.newUnsupportedLocatorException(simpleLocator);
        }
    }

    /** @param locator
     * @return the used option for an @link(ElementLocatorsGUI) */
    private static GUIElementLocator unwrap(GUIElementLocator locator) {
        if (locator instanceof ElementLocatorsGUI) {
            return ((ElementLocatorsGUI) locator).getUsedOption();
        }
        else {
            return locator;
        }
    }

    private boolean determineLocatorsOption(ElementLocatorsGUI locators) {
        locators.setUsedOption(null);
        for (GUIElementLocator option : locators) {
            if (selenium.isElementPresent(getLocatorOfAllPossible(option))) {
                locators.setUsedOption(option);
                break;
            }
        }

        return locators.getUsedOption() != null;
    }

    private String getSelectOptionLocator(OptionLocator optionLocator) {
        String locator = "";
        if (optionLocator instanceof org.aludratest.service.locator.option.LabelLocator) {
            locator = getLabelOptionLocator(optionLocator);
        }
        else if (optionLocator instanceof org.aludratest.service.locator.option.IndexLocator) {
            locator = getIndexOptionLocator(optionLocator);
        }
        else {
            throw ServiceUtil.newUnsupportedLocatorException(optionLocator);
        }
        return locator;
    }

    private String getLabelOptionLocator(OptionLocator optionLocator) {
        return getLocator("label=", optionLocator);
    }

    private String getIndexOptionLocator(OptionLocator optionLocator) {
        return getLocator("index=", optionLocator);
    }

    private String getLinkLocator(GUIElementLocator locator) {
        return getLocator("link=", locator);
    }

    private String getLocatorOfAllPossible(GUIElementLocator locator) {
        return getSeleniumLocatorForClick(locator);
    }

    private String getSeleniumLocatorForClick(GUIElementLocator locator) {
        if (locator instanceof LabelLocator) {
            return getLinkLocator(locator);
        }
        else {
            return getSeleniumLocator(locator);
        }
    }

    private String getLocator(String prefix, Locator locator) {
        return getLocator(prefix, locator, "");
    }

    private String getLocator(String prefix, Locator locator, String suffix) {
        return prefix + locator + suffix;
    }

    private String getXPathLocator(XPathLocator locator) {
        return getLocator("xpath=", locator);
    }

    private String getCSSLocator(CSSLocator locator) {
        return getLocator("css=", locator);
    }

    /**
     * Concatenates a prefix given by the configuration parameter
     * Interactions.IDENTIFICATION_PREFIX and the parameter id. Afterwards it
     * returns the concatenated value.
     * 
     * @param id
     *            of the element for which the selenium locator shall be
     *            generated
     * @return a concatenation of a search prefix and the id
     */
    private String getIdLocator(IdLocator locator) {
        // For some informations about partial id matching see following links:
        // http://clearspace.openqa.org/thread/6014 (problem description)
        // http://www.w3.org/TR/2001/CR-css3-selectors-20011113/#id-selectors
        // http://www.w3.org/TR/2001/CR-css3-selectors-20011113/#attribute-substrings
        // As follows an example css selector which matches all elements which
        // have a id with a ending "idvalueending"
        // css=[id$="idvalueending"]
        return getLocator(configuration.getIdentificationPrefix(), locator, configuration.getIdentificationSuffix());
    }

    // private helpers for Z-index handling ------------------------------------

    private int getBaseZIndex() {
        if (getHistoryFrameCount() > 0) {
            // If it has a default z-Index defined in code, then get its value
            return getzIndex(0);
        }
        else {
            // If it has not defined a default z-Index in code, then set it to a
            // default value
            return DEFAULT_Z_INDEX;
        }
    }

    private int getZIndexCount() {
        return getXPathCount("//iframe[contains(@style, \"z-index\")]");
    }

    private int getHistoryFrameCount() {
        int historyFrameCount = 0;
        historyFrameCount = getXPathCount("//iframe[starts-with(@id, \"history-frame\")]");
        return historyFrameCount;
    }

    /**
     * To get the biggest value of z-index for all of the elements on current
     * page. The element with the biggest value of z-index will be shown in
     * foreground. The elements with the lower value of z-index will be shown in
     * background.
     * 
     * @return the biggest value of z-index on current page
     */
    private int getMaxZIndex() {

        int zIndex = getBaseZIndex();
        int zIndexCount = 0;
        zIndexCount = getZIndexCount();
        for (int i = 0; i < zIndexCount; i++) {
            int tmpzIndex = getzIndex(i + 1);
            zIndex = (tmpzIndex > zIndex) ? tmpzIndex : zIndex;
        }
        return zIndex;
    }

    private int getzIndex(int index) {
        int tmpzIndex = DEFAULT_Z_INDEX;
        // If a base value is defined in code, it will overwrite the default
        // value
        try {
            String tmpElement = selenium.getEval(String.format(zIndexSearch, index));
            tmpzIndex = getzIndexFromStyle(tmpElement);
        }
        catch (SeleniumException e) {
            if (!(e.getMessage().contains("not found") && e.getMessage().contains("z-index"))) {
                throw new TechnicalException("Error retrieving z-index", e);
            }
        }
        return tmpzIndex;
    }

    private int getzIndexFromStyle(String style) {
        // a normal element without z-Index defined
        if (style.endsWith("undefined")) {
            return getBaseZIndex();
            // an element with z-Index
        }
        else {
            for (String tmp : style.split(";")) {
                if (tmp.trim().startsWith("z-index")) {
                    return Integer.parseInt(tmp.replace("z-index:", "").trim());
                }
            }
        }
        return getBaseZIndex();
    }

    private boolean isFirefoxError(SeleniumException se) {
        return se.getMessage() != null && se.getMessage().contains("NS_ERROR_");
    }

    private boolean isWindowClosedError(SeleniumException se) {
        return se.getMessage() != null && se.getMessage().trim().equalsIgnoreCase("ERROR: Current window or frame is closed!");
    }
}
