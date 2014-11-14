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
package org.aludratest.service.gui.component;

import org.aludratest.service.gui.AludraGUI;
import org.aludratest.service.locator.Locator;

/** Abstract base class for input components. Adds an enabled / disabled state to the component.
 * 
 * @author falbrech
 * @param <E> Type of concrete subclasses. */
public abstract class InputComponent<E extends Element<E>> extends Element<E> {

    /** Constructor.
     * @param aludraGui The underlying {@link AludraGUI} service instance.
     * @param locator A locator for the referenced component. */
    protected InputComponent(AludraGUI aludraGui, Locator locator) {
        super(aludraGui, locator);
    }

    /** Constructor.
     * @param aludraGui The underlying {@link AludraGUI} service instance.
     * @param locator A locator for the referenced component.
     * @param elementName An explicit name to use for the component. */
    protected InputComponent(AludraGUI aludraGui, Locator locator, String elementName) {
        super(aludraGui, locator, elementName);
    }

    /** Checks whether this input element is enabled.
     * 
     * @return <code>true</code> if the input element is enabled, <code>false</code> otherwise. */
    public boolean isEnabled() {
        return check().isElementEnabled(elementType, elementName, locator);
    }

    /** Checks whether this input element is enabled.
     * 
     * @param timeout Max time to wait for this input element to become enabled.
     * 
     * @return <code>true</code> if the input element is enabled, <code>false</code> otherwise. */
    public boolean isEnabled(long timeout) {
        return check().isElementEnabled(elementType, elementName, locator, timeout);
    }

}
