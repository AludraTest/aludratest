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
package org.aludratest.service.gui.component.impl;

import org.aludratest.service.gui.AludraGUI;
import org.aludratest.service.gui.GUICondition;
import org.aludratest.service.gui.GUIInteraction;
import org.aludratest.service.gui.GUIVerification;
import org.aludratest.service.gui.component.GUIComponent;
import org.aludratest.service.locator.Locator;

/** Parent class for all Components of a Graphical User Interface.
 * @author Marcel Malitz
 * @author Volker Bergmann */
public abstract class AbstractGUIComponent implements GUIComponent {

    protected AludraGUI aludraGui;
    protected Locator locator;
    protected String elementType;
    protected String elementName;

    protected final void configure(AludraGUI aludraGui, Locator locator, String elementType, String elementName) {
        if (aludraGui == null) {
            throw new IllegalArgumentException("aludraGui is null");
        }
        if (locator == null) {
            throw new IllegalArgumentException("locator is null");
        }
        if (elementType == null) {
            throw new IllegalArgumentException("elementType is null");
        }

        this.aludraGui = aludraGui;
        this.locator = locator;
        this.elementType = elementType;

        // FIXME replace with clean logic
        this.elementName = (elementName != null ? elementName : defaultElementName());
    }

    // utility methods for child classes ---------------------------------------

    protected GUIInteraction perform() {
        return aludraGui.perform();
    }

    protected GUIVerification verify() {
        return aludraGui.verify();
    }

    protected GUICondition check() {
        return aludraGui.check();
    }

    // private helpers ---------------------------------------------------------

    private String defaultElementName() {
        return "- no name -";
    }

}
