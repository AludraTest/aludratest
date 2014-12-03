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

import org.aludratest.service.gui.GUIInteraction;
import org.aludratest.testcase.data.ParamConverter;

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

    /** TODO move to AludraWebGui maby SeliumWebGui and maybe as configuration
     * 
     * Tells the Selenium server to add the specified key and value as a custom outgoing request header. This only works if the
     * browser is configured to use the built in Selenium proxy.
     * @param key the header name
     * @param value the header value */
    void addCustomHttpHeaderCommand(String key, @ParamConverter(HttpHeaderFormat.class) String value);

}
