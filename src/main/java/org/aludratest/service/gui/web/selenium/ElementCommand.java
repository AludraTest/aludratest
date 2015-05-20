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
package org.aludratest.service.gui.web.selenium;

import org.aludratest.service.locator.element.GUIElementLocator;

/**
 * The ElementCommand is used for execution operations on a web page e.g. click on a button
 * @param <E> the return type of the command
 * @author Marcel Malitz
 * @author Joerg Langnickel
 * @author Volker Bergmann
 */
public abstract class ElementCommand<E> {

    private String description;
    private boolean interaction;

    /**
     * Constructor.
     * @param description 
     * @param interaction
     */
    public ElementCommand(String description, boolean interaction) {
        this.description = description;
        this.interaction = interaction;
    }

    /** @return true if the ElementCommand is going to perform an interaction, otherwise false */
    public boolean isInteraction() {
        return interaction;
    }

    /** Execute an command on an element
     *  @param locators - to identify the element
     *  @return value returned from e.g. Selenium1
     *  @throws LocatorNotSupportedException */
    public abstract E call(GUIElementLocator locators);

    @Override
    public String toString() {
        return description;
    }

}
