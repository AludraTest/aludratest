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
package org.aludratest.service.gui.web;

import org.aludratest.service.AttachResult;
import org.aludratest.service.ElementName;
import org.aludratest.service.ElementType;
import org.aludratest.service.TechnicalArgument;
import org.aludratest.service.TechnicalLocator;
import org.aludratest.service.gui.GUIInteraction;
import org.aludratest.service.locator.element.GUIElementLocator;
import org.aludratest.service.locator.element.XPathLocator;
import org.aludratest.testcase.data.ParamConverter;
import org.w3c.dom.NodeList;

/**
 * Specialization of the {@link GUIInteraction} interface
 * which adds features specific for Web GUIs.
 * @author Volker Bergmann
 */
public interface WebGUIInteraction extends GUIInteraction {

    /** Opens the main URL of the Application Unter Test (configuration property: <code>url.of.aut</code>) in a new browser window
     * and waits until the page is fully loaded. This method has to be called before most methods of the <code>check()</code>,
     * <code>perform()</code> and <code>verify()</code> objects can be used. */
    public void open();

    /**
     * Refreshes the page of the currently selected window and waits until the
     * page is fully loaded.
     */
    void refresh();

    /**
     * Maximizes the currently selected window.
     */
    void windowMaximize();

    /**
     * Gives focus to the currently selected window.
     */
    void windowFocus();

    /** Tells the Selenium server to add the specified key and value as a custom outgoing request header. This only works if the
     * browser is configured to use the built in Selenium proxy.
     * @param key the header name
     * @param value the header value */
    void addCustomHttpHeaderCommand(String key, @ParamConverter(HttpHeaderFormat.class) String value);

    /** Switches to the given iframe element of the current web page in the current window, or switches back to default content.
     * 
     * @param iframeLocator Locator which uniquely identifies the inner frame to switch to. Use <code>null</code> to switch back
     *            to default content. */
    void switchToIFrame(@TechnicalLocator GUIElementLocator iframeLocator);

    /** Does the same like {@link #evalXPath(String)} does.
     * @param locator whose XPath expression will be taken and evaluated
     * @return NodeList object containing XPath evaluation result */
    NodeList evalXPath(@TechnicalLocator XPathLocator locator);

    /** Evaluates arbitrary XPath and outputs its result as NodeList object.
     * @param xpath XPath expression to be executed
     * @return NodeList object containing XPath evaluation result */
    NodeList evalXPath(@TechnicalLocator String xpath);

    /** Evaluates arbitrary XPath and outputs its result as String.
     * @param xpath XPath expression to be executed
     * 
     * @return String containing XPath evaluation result */
    String evalXPathAsString(@TechnicalLocator String xpath);

    /** Clicks on an element and expects a download to start (e.g. a Download button or link). Downloads the file and returns it
     * binary contents, in an encoded form. <br>
     * If no download starts within the given task completion timeout, a <code>FunctionalFailure</code> is thrown. <br>
     * 
     * @param elementType the type of the related GUI element to log
     * @param elementName the name of the related GUI element to log
     * @param locator to locate buttons, links or any other elements which react on mouse clicks.
     * @param taskCompletionTimeout the maximum number of milliseconds to wait for the completion of the task
     * @return The binary contents of the file, as a Base64 encoded string; prefixed by the file name and a colon (e.g.
     *         <code>myExcelFile.xls:AP8uMKg...</code>). */
    @AttachResult("Downloaded File")
    String clickForDownload(@ElementType String elementType, @ElementName String elementName,
            @TechnicalLocator GUIElementLocator locator, @TechnicalArgument int taskCompletionTimeout);

    /** Waits for an AJAX operation to be finished. This is usually done with some JavaScript querying a variable of a known
     * JavaScript framework (e.g. jQuery). <br>
     * The framework may or may not be supported by the web GUI implementation. If it is not supported, an AutomationException is
     * thrown. <br>
     * If the maximum waiting time is reached without the Ajax operation being finished, a PerformanceException is thrown.
     * 
     * @param frameworkName Name of the JavaScript framework to check, e.g. "jquery". Implementations should convert this
     *            parameter to lowercase before checking it, so case does not matter for the caller.
     * @param maxWaitTime Maximum time, in milliseconds, to wait for the AJAX operation to be finished. */
    void waitForAjaxOperationEnd(@TechnicalArgument String frameworkName, @TechnicalArgument int maxWaitTime);

}
