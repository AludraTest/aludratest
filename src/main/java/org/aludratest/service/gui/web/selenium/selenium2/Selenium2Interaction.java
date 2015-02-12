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

import java.util.Map;

import org.aludratest.exception.AutomationException;
import org.aludratest.exception.FunctionalFailure;
import org.aludratest.service.gui.web.WebGUIInteraction;
import org.aludratest.service.gui.web.selenium.ConditionCheck;
import org.aludratest.service.locator.Locator;
import org.aludratest.service.locator.element.GUIElementLocator;
import org.aludratest.service.locator.element.XPathLocator;
import org.aludratest.service.locator.option.IndexLocator;
import org.aludratest.service.locator.option.LabelLocator;
import org.aludratest.service.locator.option.OptionLocator;
import org.aludratest.service.locator.window.TitleLocator;
import org.aludratest.service.locator.window.WindowLocator;
import org.aludratest.util.DataUtil;
import org.databene.commons.StringUtil;
import org.openqa.selenium.NoSuchWindowException;
import org.w3c.dom.NodeList;

/**
 * Provides to Web GUI interaction features.
 * @author Marcel Malitz
 * @author Joerg Langnickel
 * @author Volker Bergmann
 */
public class Selenium2Interaction extends AbstractSelenium2Action implements WebGUIInteraction {

    /**
     * Constructor.
     * 
     * @param seleniumWrapper
     *            the {@link Selenium2Wrapper} to use
     */
    public Selenium2Interaction(Selenium2Wrapper seleniumWrapper) {
        super(seleniumWrapper);
    }

    @Override
    public void open() {
        wrapper.open(wrapper.getConfiguration().getUrlOfAut());
    }

    // life cycle operations ---------------------------------------------------

    @Override
    public void refresh() {
        wrapper.refresh();
    }

    // click operations --------------------------------------------------------

    @Override
    public void click(String elementType, String operation, Locator locator,
            int taskCompletionTimeout) {
        wrapper.click(getDefaultElementLocator(locator), operation, taskCompletionTimeout);
    }

    @Override
    public void clickNotEditable(String elementType, String operation, Locator locator,
            int taskCompletionTimeout) {
        wrapper.clickNotEditable(getDefaultElementLocator(locator), operation, taskCompletionTimeout);
    }

    @Override
    public void doubleClickNotEditable(String elementType, String elementName, Locator locator, int taskCompletionTimeout) {
        wrapper.doubleClickNotEditable(getDefaultElementLocator(locator), elementName, taskCompletionTimeout);
    }

    @Override
    public void doubleClick(String elementType, String operation, Locator locator,
            int taskCompletionTimeout) {
        GUIElementLocator elementLocator = getDefaultElementLocator(locator);
        wrapper.doubleClick(elementLocator, operation, taskCompletionTimeout);
    }

    // radio button selection --------------------------------------------------

    @Override
    public void selectRadiobutton(String elementType, String operation, Locator locator,
            int taskCompletionTimeout) {
        GUIElementLocator elementLocator = getDefaultElementLocator(locator);
        wrapper.click(elementLocator, operation, taskCompletionTimeout);
    }

    // check box selection -----------------------------------------------------

    @Override
    public void changeCheckbox(String elementType, String operation, Locator locator,
            int taskCompletionTimeout) {
        GUIElementLocator elementLocator = getDefaultElementLocator(locator);
        wrapper.click(elementLocator, operation, taskCompletionTimeout);
    }

    @Override
    public void selectCheckbox(String elementType, String operation, Locator locator,
            int taskCompletionTimeout) {
        GUIElementLocator elementLocator = getDefaultElementLocator(locator);
        if (!wrapper.isChecked(elementLocator)) {
            wrapper.click(elementLocator, operation, taskCompletionTimeout);
        }
    }

    @Override
    public void deselectCheckbox(String elementType, String operation, Locator locator,
            int taskCompletionTimeout) {
        GUIElementLocator elementLocator = getDefaultElementLocator(locator);
        if (wrapper.isChecked(elementLocator)) {
            wrapper.click(elementLocator, operation, taskCompletionTimeout);
        }
    }

    // drop down box operations ------------------------------------------------

    @Override
    public void selectDropDownEntry(String elementType, String operation,
            Locator locator, OptionLocator optionLocator,
            int taskCompletionTimeout) {
        GUIElementLocator elementLocator = getDefaultElementLocator(locator);
        wrapper.isElementPresent(elementLocator);
        checkLabels(operation, optionLocator, elementLocator);
        wrapper.select(elementLocator, optionLocator, taskCompletionTimeout);
    }

    // text operations ---------------------------------------------------------

    @Override
    public void type(String elementType, String operation, Locator locator, String text,
            int taskCompletionTimeout) {
        GUIElementLocator elementLocator = getDefaultElementLocator(locator);
        wrapper.type(elementLocator, text == null ? "" : text, taskCompletionTimeout);
    }

    @Override
    public void keyPress(int keycode) {
        wrapper.keyPress(keycode);
    }

    @Override
    public String getInputFieldValue(String elementType, String operation, Locator locator) {
        final GUIElementLocator elementLocator = getDefaultElementLocator(locator);
        StringBuilder inputFieldValue = new StringBuilder(0);
        wrapper.retryUntilTimeout(new ConditionCheck() {
            @Override
            public boolean eval() {
                String actualValue = wrapper.getValue(elementLocator);
                return !StringUtil.isEmpty(actualValue);
            }
        });
        String readValue = wrapper.getValue(elementLocator);
        if (readValue != null) {
            inputFieldValue.append(readValue);
        }
        return inputFieldValue.toString();
    }

    @Override
    public String getInputFieldSelectedLabel(String elementType, String operation, Locator locator) {
        final GUIElementLocator elementLocator = getDefaultElementLocator(locator);
        StringBuilder inputFieldValue = new StringBuilder(0);
        wrapper.retryUntilTimeout(new ConditionCheck() {
            @Override
            public boolean eval() {
                String actualValue = wrapper.getSelectedLabel(elementLocator);
                return !StringUtil.isEmpty(actualValue);
            }
        });
        String readValue = wrapper.getSelectedLabel(elementLocator);
        if (readValue != null) {
            inputFieldValue.append(readValue);
        }
        return inputFieldValue.toString();
    }

    @Override
    public String getText(String elementType, String operation, Locator locator) {
        return getText(elementType, operation, locator, true);
    }

    @Override
    public String getText(String elementType, String operation, Locator locator, boolean checkVisible) {
        GUIElementLocator elementLocator = getDefaultElementLocator(locator);
        StringBuilder textValue = new StringBuilder(0);
        String readValue = wrapper.getText(elementLocator, checkVisible);
        if (readValue != null) {
            textValue.append(readValue);
        }
        return textValue.toString();
    }

    // file operations ---------------------------------------------------------

    @Override
    public void assignFileResource(String elementType, String elementName, Locator locator, String filePath,
            int taskCompletionTimeout) {
        GUIElementLocator elementLocator = getDefaultElementLocator(locator);
        wrapper.sendKeys(elementLocator, filePath, taskCompletionTimeout);
    }

    // window operations -------------------------------------------------------

    @Override
    public void selectWindow(Locator locator) {
        WindowLocator elementLocator = getDefaultWindowLocator(locator);
        wrapper.selectWindow(elementLocator);
    }

    @Override
    public void windowMaximize() {
        wrapper.windowMaximize();
    }

    @Override
    public void windowFocus() {
        wrapper.windowFocus();
    }

    @Override
    public void closeOtherWindows(String elementType, String operation, Locator locator) {
        WindowLocator locatorOfRemainingWindow = getDefaultWindowLocator(locator);

        String[] windowTitles = wrapper.getAllWindowTitles();
        for (String windowTitle : windowTitles) {
            WindowLocator windowToClose = getDefaultWindowLocator(windowTitle);
            if (!windowToClose.equals(locatorOfRemainingWindow)) {
                wrapper.selectWindowDirectly(windowToClose);
                wrapper.close();
            }
        }
        wrapper.selectWindowDirectly(locatorOfRemainingWindow);
    }

    @Override
    public void closeWindows(String elementType, String operation, TitleLocator locator) {
        Map<String, String> handlesAndTitles = wrapper.getAllWindowHandlesAndTitles();
        String remainingWindowHandle = null;
        try {
            // get the title of the current window...
            String currentWindowHandle = wrapper.getWindowHandle();
            String currentTitle = handlesAndTitles.get(currentWindowHandle);
            if (!locator.getTitle().equals(currentTitle)) {
                // ...ad save it as remaining window title (if it is not giong to be closed
                remainingWindowHandle = currentWindowHandle;
            }
        } catch (NoSuchWindowException e) {
            // This may happen when calling driver.getTitle() after having close()d the recent window.
            // In such a case, we select an arbitrary window among the remaining ones
        }
        boolean found = false;
        for (Map.Entry<String, String> handleAndTitle : handlesAndTitles.entrySet()) {
            String windowHandle = handleAndTitle.getKey();
            String windowTitle = handleAndTitle.getValue();
            if (locator.getTitle().equals(windowTitle)) {
                wrapper.selectWindowByTechnicalName(windowHandle);
                wrapper.close();
                found = true;
            } else if (remainingWindowHandle == null) {
                remainingWindowHandle = windowHandle;
            }
        }
        if (found) {
            if (remainingWindowHandle == null) {
                wrapper.tearDown();
            } else {
                wrapper.selectWindowByTechnicalName(remainingWindowHandle);
            }
        } else {
            throw new AutomationException("Window not found");
        }
    }

    // special features --------------------------------------------------------

    @Override
    public void switchToIFrame(Locator iframeLocator) {
        wrapper.switchToIFrame(iframeLocator);

    }

    @Override
    public void focus(String elementType, String operation, Locator locator) {
        GUIElementLocator elementLocator = getDefaultElementLocator(locator);
        wrapper.focus(elementLocator);
    }

    @Override
    public void addCustomHttpHeaderCommand(String key, String value) {
        wrapper.addCustomRequestHeader(key, value);
    }

    @Override
    public void wrongPageFlow(String msg) {
        throw new FunctionalFailure(msg);
    }

    @Override
    public void functionalError(String msg) {
        throw new FunctionalFailure(msg);
    }

    @Override
    public NodeList evalXPath(XPathLocator locator) {
        // TODO check if this can be implemented using Selenium2's By.xpath
        String html = new String(wrapper.getPageSource().getFileData(), DataUtil.UTF_8);
        return DataUtil.evalXPathInHTML(locator, html);
    }

    @Override
    public NodeList evalXPath(String xpath) {
        // TODO check if this can be implemented using Selenium2's By.xpath
        String html = new String(wrapper.getPageSource().getFileData(), DataUtil.UTF_8);
        return DataUtil.evalXPathInHTML(xpath, html);
    }

    @Override
    public String evalXPathAsString(String xpath) {
        String html = new String(wrapper.getPageSource().getFileData(), DataUtil.UTF_8);
        return DataUtil.evalXPathInHTMLAsString(xpath, html);
    }

    // private methods ---------------------------------------------------------

    private void checkLabels(String operation, final OptionLocator optLocator, final GUIElementLocator elementLocator) {
        wrapper.retryUntilTimeout(new ConditionCheck() {
            @Override
            public boolean eval() {
                String[] actualLabels = wrapper.getLabels(elementLocator);
                if (optLocator instanceof LabelLocator) {
                    String mismatches = DataUtil.containsString(optLocator.toString(), actualLabels);
                    return (mismatches.length() == 0);
                } else {
                    return ((optLocator instanceof IndexLocator) && ((IndexLocator) optLocator).getIndex() < actualLabels.length);
                }
            }
        });
        String[] actualLabels = wrapper.getLabels(elementLocator);
        if (optLocator instanceof LabelLocator) {
            String mismatches = DataUtil.containsString(optLocator.toString(), actualLabels);
            if (mismatches.length() > 0) {
                throw new AutomationException("The expected labels are not contained "
                        +
                        "in the actual labels. Following Label(s) is/are missing: " +
                        mismatches);
            }
        } else if (optLocator instanceof IndexLocator) {
            IndexLocator iLoc = (IndexLocator) optLocator;
            if (iLoc.getIndex() >= actualLabels.length) {
                throw new AutomationException("The requested index " + iLoc.getIndex() + " does not exist");
            }
        }
    }

}
