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
import org.aludratest.service.gui.web.WebGUIInteraction;
import org.aludratest.service.gui.web.selenium.ConditionCheck;
import org.aludratest.service.locator.element.GUIElementLocator;
import org.aludratest.service.locator.element.XPathLocator;
import org.aludratest.service.locator.option.IndexLocator;
import org.aludratest.service.locator.option.LabelLocator;
import org.aludratest.service.locator.option.OptionLocator;
import org.aludratest.service.locator.window.TitleLocator;
import org.aludratest.service.locator.window.WindowLocator;
import org.aludratest.util.DataUtil;
import org.databene.commons.StringUtil;
import org.w3c.dom.NodeList;

/** Provides to Web GUI interaction features.
 * @author Marcel Malitz
 * @author Joerg Langnickel
 * @author Volker Bergmann */
public class Selenium1Interaction extends AbstractSeleniumAction implements WebGUIInteraction {

    /** Constructor.
     * @param seleniumWrapper The {@link SeleniumWrapper} to use. */
    public Selenium1Interaction(SeleniumWrapper seleniumWrapper) {
        super(seleniumWrapper);
    }

    @Override
    public void selectRadiobutton(String elementType, String operation, GUIElementLocator locator, int taskCompletionTimeout) {
        wrapper.click(locator, taskCompletionTimeout);
    }

    @Override
    public void changeCheckbox(String elementType, String operation, GUIElementLocator locator, int taskCompletionTimeout) {
        wrapper.click(locator, taskCompletionTimeout);
    }

    @Override
    public void selectCheckbox(String elementType, String operation, GUIElementLocator locator, int taskCompletionTimeout) {
        if (!wrapper.isChecked(locator)) {
            wrapper.click(locator, taskCompletionTimeout);
        }
    }

    @Override
    public void deselectCheckbox(String elementType, String operation, GUIElementLocator locator, int taskCompletionTimeout) {
        if (wrapper.isChecked(locator)) {
            wrapper.click(locator, taskCompletionTimeout);
        }
    }

    @Override
    public void selectDropDownEntry(String elementType, String operation, GUIElementLocator locator, OptionLocator optionLocator,
            int taskCompletionTimeout) {
        wrapper.isElementPresent(locator);
        checkLabels(optionLocator, locator);
        wrapper.select(locator, optionLocator, taskCompletionTimeout);
    }

    @Override
    public void type(String elementType, String operation, GUIElementLocator locator, String text, int taskCompletionTimeout) {
        wrapper.type(locator, (text == null ? "" : text), taskCompletionTimeout);
    }

    @Override
    public void assignFileResource(String elementType, String elementName, GUIElementLocator locator, String filePath,
            int taskCompletionTimeout) {
        wrapper.type(locator, filePath, taskCompletionTimeout);
    }

    @Override
    public void click(String elementType, String operation, GUIElementLocator locator, int taskCompletionTimeout) {
        wrapper.click(locator, taskCompletionTimeout);
    }

    @Override
    public String clickForDownload(String elementType, String elementName, GUIElementLocator locator, int taskCompletionTimeout) {
        // unsupported operation for Selenium 1
        throw new UnsupportedOperationException();
    }

    @Override
    public void hover(String elementType, String elementName, GUIElementLocator locator, int taskCompletionTimeout) {
        wrapper.hover(locator, taskCompletionTimeout);
    }

    @Override
    public String getInputFieldValue(String elementType, String operation, final GUIElementLocator locator) {
        wrapper.retryUntilTimeout(new ConditionCheck() {
            @Override
            public boolean eval() {
                String actualValue = null;
                actualValue = wrapper.getValue(locator);
                if (actualValue == null || "".equals(actualValue)) {
                    return false;
                }
                return true;
            }
        });
        String readValue = wrapper.getValue(locator);
        return readValue == null ? "" : readValue;
    }

    @Override
    public String getInputFieldSelectedLabel(String elementType, String operation, final GUIElementLocator locator) {
        wrapper.retryUntilTimeout(new ConditionCheck() {
            @Override
            public boolean eval() {
                String actualValue = null;
                actualValue = wrapper.getSelectedLabel(locator);
                if (actualValue == null || "".equals(actualValue)) {
                    return false;
                }
                return true;
            }
        });
        String readValue = wrapper.getSelectedLabel(locator);
        return readValue == null ? "" : readValue;
    }

    @Override
    public void selectWindow(WindowLocator locator) {
        wrapper.selectWindow(locator);
    }

    @Override
    public String getText(String elementType, String operation, GUIElementLocator locator) {
        return getText(elementType, operation, locator, true);
    }

    @Override
    public String getText(String elementType, String operation, GUIElementLocator locator, boolean checkVisible) {
        String text = wrapper.getText(locator, checkVisible);
        return StringUtil.nullToEmpty(text);
    }

    @Override
    public void open() {
        wrapper.open(wrapper.getConfiguration().getUrlOfAut());
    }

    @Override
    public void refresh() {
        wrapper.refresh();
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
    public void switchToIFrame(GUIElementLocator iframeLocator) {
        if (iframeLocator == null) {
            wrapper.switchToIFrame(null);
        }
        else {
            wrapper.switchToIFrame(iframeLocator);
        }
    }

    @Override
    public void focus(String elementType, String operation, GUIElementLocator locator) {
        wrapper.focus(locator);
    }

    @Override
    public void keyPress(int keycode) {
        wrapper.keyPress(keycode);
    }

    @Override
    public void doubleClick(String elementType, String operation, GUIElementLocator locator, int taskCompletionTimeout) {
        wrapper.doubleClick(locator, taskCompletionTimeout);
    }

    @Override
    public void closeOtherWindows(String elementType, String operation, TitleLocator locator) {
        WindowLocator currentWindow = locator;
        String[] windowTitles = wrapper.getAllWindowTitles();
        for (String windowTitle : windowTitles) {
            WindowLocator windowToClose = new TitleLocator(windowTitle);
            if (!windowToClose.equals(currentWindow)) {
                wrapper.selectWindow(windowToClose);
                wrapper.close();
            }
        }
        wrapper.selectWindow(currentWindow);
    }

    @Override
    public void closeWindows(String elementType, String operation, TitleLocator locator) {
        WindowLocator remainingWindowLocator = null;
        boolean found = false;
        String[] windowTitles = wrapper.getAllWindowTitles();
        if (windowTitles != null) {
            for (int i = 0; i < windowTitles.length; i++) {
                String windowTitle = windowTitles[i];
                if (locator.getTitle().equals(windowTitle)) {
                    wrapper.selectWindowByTechnicalName(windowTitle);
                    wrapper.close();
                    found = true;
                }
                else if (windowTitle.length() > 0) {
                    remainingWindowLocator = new TitleLocator(windowTitle);
                }
            }
            if (found) {
                if (remainingWindowLocator == null) {
                    wrapper.tearDown();
                }
            }
            else {
                wrapper.selectWindow(remainingWindowLocator);
                throw new AutomationException("Window not found");
            }
        }
        else {
            throw new AutomationException("Window not found");
        }
    }

    @Override
    public void waitForWindowToBeClosed(String elementType, String elementName, TitleLocator locator, int taskCompletionTimeout) {
        wrapper.waitForWindowToBeClosed(locator, taskCompletionTimeout);
    }

    @Override
    public void addCustomHttpHeaderCommand(String key, String value) {
        wrapper.addCustomRequestHeader(key, value);
    }

    private void checkLabels(final OptionLocator optLocator, final GUIElementLocator elementLocator) {
        wrapper.retryUntilTimeout(new ConditionCheck() {
            @Override
            public boolean eval() {
                String[] actualLabels = wrapper.getLabels(elementLocator);
                if (optLocator instanceof LabelLocator) {
                    String mismatches = DataUtil.containsString(optLocator.toString(), actualLabels);
                    return (mismatches.length() == 0);
                }
                else {
                    return ((optLocator instanceof IndexLocator) && actualLabels.length > ((IndexLocator) optLocator).getIndex());
                }
            }
        });
        String[] actualLabels = wrapper.getLabels(elementLocator);
        if (optLocator instanceof LabelLocator) {
            String mismatches = DataUtil.containsString(optLocator.toString(), actualLabels);
            if (mismatches.length() > 0) {
                throw new AutomationException("The expected labels are not contained "
                        + "in the actual labels. Following Label(s) is/are missing: " + mismatches);
            }
        }
        else if (optLocator instanceof IndexLocator) {
            IndexLocator iLoc = (IndexLocator) optLocator;
            if (iLoc.getIndex() >= actualLabels.length) {
                throw new AutomationException("The requested index " + iLoc.getIndex() + " does not exist");
            }
        }
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
        String html = new String(wrapper.getPageSource().getFileData(), DataUtil.UTF_8);
        return DataUtil.evalXPathInHTML(locator, html);
    }

    @Override
    public NodeList evalXPath(String xpath) {
        String html = new String(wrapper.getPageSource().getFileData(), DataUtil.UTF_8);
        return DataUtil.evalXPathInHTML(xpath, html);
    }

    @Override
    public String evalXPathAsString(String xpath) {
        String html = new String(wrapper.getPageSource().getFileData(), DataUtil.UTF_8);
        return DataUtil.evalXPathInHTMLAsString(xpath, html);
    }

}
