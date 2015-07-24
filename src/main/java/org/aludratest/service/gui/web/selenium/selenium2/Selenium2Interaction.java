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
import org.aludratest.service.locator.element.GUIElementLocator;
import org.aludratest.service.locator.element.XPathLocator;
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

    /** Constructor.
     * @param seleniumWrapper the {@link Selenium2Wrapper} to use */
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
    public void click(String elementType, String elementName, GUIElementLocator locator,
            int taskCompletionTimeout) {
        wrapper.click(locator, elementName, taskCompletionTimeout);
    }

    @Override
    public void doubleClick(String elementType, String elementName, GUIElementLocator locator,
            int taskCompletionTimeout) {
        wrapper.doubleClick(locator, elementName, taskCompletionTimeout);
    }

    @Override
    public void hover(String elementType, String elementName, GUIElementLocator locator, int taskCompletionTimeout) {
        wrapper.hover(locator, elementName, taskCompletionTimeout);
    }

    // radio button selection --------------------------------------------------

    @Override
    public void selectRadiobutton(String elementType, String operation, GUIElementLocator locator,
            int taskCompletionTimeout) {
        wrapper.click(locator, operation, taskCompletionTimeout);
    }

    // check box selection -----------------------------------------------------

    @Override
    public void changeCheckbox(String elementType, String operation, GUIElementLocator locator,
            int taskCompletionTimeout) {
        wrapper.click(locator, operation, taskCompletionTimeout);
    }

    @Override
    public void selectCheckbox(String elementType, String operation, GUIElementLocator locator,
            int taskCompletionTimeout) {
        if (!wrapper.isChecked(locator)) {
            wrapper.click(locator, operation, taskCompletionTimeout);
        }
    }

    @Override
    public void deselectCheckbox(String elementType, String operation, GUIElementLocator locator,
            int taskCompletionTimeout) {
        if (wrapper.isChecked(locator)) {
            wrapper.click(locator, operation, taskCompletionTimeout);
        }
    }

    // drop down box operations ------------------------------------------------

    @Override
    public void selectDropDownEntry(String elementType, String operation,
            GUIElementLocator dropDownLocator, OptionLocator entryLocator,
            int taskCompletionTimeout) {
        wrapper.waitForDropDownEntryLocatablity(entryLocator, dropDownLocator);
        wrapper.select(dropDownLocator, entryLocator, taskCompletionTimeout);
    }

    // text operations ---------------------------------------------------------

    @Override
    public void type(String elementType, String operation, GUIElementLocator locator, String text,
            int taskCompletionTimeout) {
        wrapper.type(locator, (text == null ? "" : text), taskCompletionTimeout);
    }

    @Override
    public void keyPress(int keycode) {
        wrapper.keyPress(keycode);
    }

    @Override
    public String getInputFieldValue(String elementType, String operation, final GUIElementLocator locator) {
        return wrapper.waitForValue(locator);
    }

    @Override
    public String getInputFieldSelectedLabel(String elementType, String operation, final GUIElementLocator locator) {
        return wrapper.waitForSelection(locator);
    }

    @Override
    public String getText(String elementType, String operation, GUIElementLocator locator) {
        return getText(elementType, operation, locator, true);
    }

    @Override
    public String getText(String elementType, String operation, GUIElementLocator locator, boolean checkVisible) {
        return StringUtil.nullToEmpty(wrapper.getText(locator, checkVisible));
    }

    // file operations ---------------------------------------------------------

    @Override
    public void assignFileResource(String elementType, String elementName, GUIElementLocator locator, String filePath,
            int taskCompletionTimeout) {
        wrapper.sendKeys(locator, filePath, taskCompletionTimeout);
    }

    // window operations -------------------------------------------------------

    @Override
    public void selectWindow(WindowLocator locator) {
        wrapper.selectWindow(locator);
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
    public void closeOtherWindows(String elementType, String operation, TitleLocator locatorOfRemainingWindow) {
        String[] windowTitles = wrapper.getAllWindowTitles();
        for (String windowTitle : windowTitles) {
            WindowLocator locatorOfCurrentWindow = new TitleLocator(windowTitle);
            if (!locatorOfCurrentWindow.equals(locatorOfRemainingWindow)) {
                try {
                    wrapper.selectWindowImmediately(locatorOfCurrentWindow);
                    wrapper.close();
                }
                catch (AutomationException e) {
                    // ignore; window has been closed in the meantime
                }
            }
        }
        wrapper.selectWindowImmediately(locatorOfRemainingWindow);
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

    @Override
    public void waitForWindowToBeClosed(String elementType, String elementName, TitleLocator locator, int taskCompletionTimeout) {
        wrapper.waitForWindowToBeClosed(locator, taskCompletionTimeout);
    }

    // special features --------------------------------------------------------

    @Override
    public void switchToIFrame(GUIElementLocator iframeLocator) {
        wrapper.switchToIFrame(iframeLocator);

    }

    @Override
    public void focus(String elementType, String operation, GUIElementLocator locator) {
        wrapper.focus(locator);
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
