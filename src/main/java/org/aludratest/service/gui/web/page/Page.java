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
package org.aludratest.service.gui.web.page;

import org.aludratest.dict.ActionWordLibrary;
import org.aludratest.service.SystemConnector;
import org.aludratest.service.gui.GUIInteraction;
import org.aludratest.service.gui.web.AludraWebGUI;

/** A Page is the base class for every new class which shall provide access to a web page of the application under test. With
 * extension of this class every sub class is of the type AludraTest, it can perform low level actions on the application under
 * test and it gets the power to use common UIMaps.
 * @author Marcel Malitz
 * @author Volker Bergmann */
public abstract class Page implements ActionWordLibrary<Page> {

    /**
     * Instance of AludraTest which is used to delegate all the methods calls of
     * the implemented interface AludraTest.
     */
    protected AludraWebGUI aludraGUI;

    /** The passed through instance aludraTest is used to
     *  perform low level actions on the application under test.
     *  @param aludraGUI is used by the Page to access the application under test
     */
    public Page(AludraWebGUI aludraGUI) {
        this.aludraGUI = aludraGUI;
    }

    /** Sets the {@link SystemConnector}
     * @param systemConnector the {@link SystemConnector} to set */
    protected void setSystemConnector(SystemConnector systemConnector) {
        this.aludraGUI.setSystemConnector(systemConnector);
    }

    /**
     * Calls aludra to indicate that the client is on a wrong page.
     * Page flow validation failed.
     * @param msg Page flow validation message
     */
    protected void wrongPageFlow(String msg) {
        aludraGUI.perform().wrongPageFlow(msg);
    }

    /**
     * Calls aludra to indicate that an functional exception e.g. a popup has opened.
     * @param msg functional exception message
     */
    protected void functionalError(String msg) {
        aludraGUI.perform().functionalError(msg);
    }

    // Default implementation of the ActionWordLibrary interface ---------------

    @Override
    public Page verifyState() {
        checkCorrectPage();
        return this;
    }

    /** Verifies that the client is on the right page. If the page is wrong, the method has to call
     * {@link GUIInteraction#wrongPageFlow(String)} for reporting the issue. */
    protected abstract void checkCorrectPage();

}
