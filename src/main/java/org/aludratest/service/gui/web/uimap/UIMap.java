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
package org.aludratest.service.gui.web.uimap;

import org.aludratest.service.gui.web.AludraWebGUI;

/**
 * A UIMap is a class which provides access to elements of a web page. Every
 * page related UIMap must extend this class.
 * @author Marcel Malitz
 */
public abstract class UIMap {

    /** Gives access to the application under test and is normally used to give GUI components an instance of the web GUI
     * service. */
    protected AludraWebGUI aludraGUI;

    /** Makes the passed through instance aludraGUI available to all the sub classes of this class. Sub classes can access this
     * instance over the member {@link #aludraGUI}.
     * @param aludraGUI will be available to all sub classes through the member {@link #aludraGUI} */
    public UIMap(AludraWebGUI aludraGUI) {
        this.aludraGUI = aludraGUI;
    }

}
