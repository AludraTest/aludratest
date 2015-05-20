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

import org.aludratest.service.locator.element.GUIElementLocator;
import org.aludratest.service.locator.window.TitleLocator;

/** Factory for creating components for a GUI service. You can retrieve the component factory for a given guiService by calling
 * 
 * <pre>
 * guiService.getComponentFactory();
 * </pre>
 * 
 * @author falbrech */
public interface GUIComponentFactory {

    /** Creates a new Button component for the given locator.
     * 
     * @param locator Locator for the new component.
     * 
     * @return A new Button component for the given locator. */
    public Button createButton(GUIElementLocator locator);

    /** Creates a new Button component for the given locator, carrying the given element name.
     * 
     * @param locator Locator for the new component.
     * @param elementName Element name for the new component.
     * 
     * @return A new Button component for the given locator. */
    public Button createButton(GUIElementLocator locator, String elementName);

    /** Creates a new Checkbox component for the given locator.
     * 
     * @param locator Locator for the new component.
     * 
     * @return A new Checkbox component for the given locator. */
    public Checkbox createCheckbox(GUIElementLocator locator);

    /** Creates a new Checkbox component for the given locator, carrying the given element name.
     * 
     * @param locator Locator for the new component.
     * @param elementName Element name for the new component.
     * 
     * @return A new Checkbox component for the given locator. */
    public Checkbox createCheckbox(GUIElementLocator locator, String elementName);

    /** Creates a new Dropdownbox component for the given locator.
     * 
     * @param locator Locator for the new component.
     * 
     * @return A new Dropdownbox component for the given locator. */
    public Dropdownbox createDropdownbox(GUIElementLocator locator);

    /** Creates a new Dropdownbox component for the given locator, carrying the given element name.
     * 
     * @param locator Locator for the new component.
     * @param elementName Element name for the new component.
     * 
     * @return A new Dropdownbox component for the given locator. */
    public Dropdownbox createDropdownbox(GUIElementLocator locator, String elementName);

    /** Creates a new FileField component for the given locator.
     * 
     * @param locator Locator for the new component.
     * 
     * @return A new FileField component for the given locator. */
    public FileField createFileField(GUIElementLocator locator);

    /** Creates a new FileField component for the given locator, carrying the given element name.
     * 
     * @param locator Locator for the new component.
     * @param elementName Element name for the new component.
     * 
     * @return A new FileField component for the given locator. */
    public FileField createFileField(GUIElementLocator locator, String elementName);

    /** Creates a new InputField component for the given locator.
     * 
     * @param locator Locator for the new component.
     * 
     * @return A new InputField component for the given locator. */
    public InputField createInputField(GUIElementLocator locator);

    /** Creates a new InputField component for the given locator, carrying the given element name.
     * 
     * @param locator Locator for the new component.
     * @param elementName Element name for the new component.
     * 
     * @return A new InputField component for the given locator. */
    public InputField createInputField(GUIElementLocator locator, String elementName);

    /** Creates a new Label component for the given locator.
     * 
     * @param locator Locator for the new component.
     * 
     * @return A new Label component for the given locator. */
    public Label createLabel(GUIElementLocator locator);

    /** Creates a new Label component for the given locator, carrying the given element name.
     * 
     * @param locator Locator for the new component.
     * @param elementName Element name for the new component.
     * 
     * @return A new Label component for the given locator. */
    public Label createLabel(GUIElementLocator locator, String elementName);

    /** Creates a new Link component for the given locator.
     * 
     * @param locator Locator for the new component.
     * 
     * @return A new Link component for the given locator. */
    public Link createLink(GUIElementLocator locator);

    /** Creates a new Link component for the given locator, carrying the given element name.
     * 
     * @param locator Locator for the new component.
     * @param elementName Element name for the new component.
     * 
     * @return A new Link component for the given locator. */
    public Link createLink(GUIElementLocator locator, String elementName);

    /** Creates a new RadioButton component for the given locator.
     * 
     * @param locator Locator for the new component.
     * 
     * @return A new RadioButton component for the given locator. */
    public RadioButton createRadioButton(GUIElementLocator locator);

    /** Creates a new RadioButton component for the given locator, carrying the given element name.
     * 
     * @param locator Locator for the new component.
     * @param elementName Element name for the new component.
     * 
     * @return A new RadioButton component for the given locator. */
    public RadioButton createRadioButton(GUIElementLocator locator, String elementName);

    /** Creates a new Window component for the given locator.
     * 
     * @param locator Locator for the new component.
     * 
     * @return A new Window component for the given locator. */
    public Window createWindow(TitleLocator locator);

    /** Creates a new Window component for the given locator, carrying the given element name.
     * 
     * @param locator Locator for the new component.
     * @param elementName Element name for the new component.
     * 
     * @return A new Window component for the given locator. */
    public Window createWindow(TitleLocator locator, String elementName);

    /** Creates a new generic element component for the given locator.
     * 
     * @param locator Locator for the new component.
     * 
     * @return A new generic element component for the given locator. */
    public GenericElement createGenericElement(GUIElementLocator locator);

    /** Creates a new generic element component for the given locator, carrying the given element name.
     * 
     * @param locator Locator for the new component.
     * @param elementName Element name for the new component.
     * 
     * @return A new generic element component for the given locator. */
    public GenericElement createGenericElement(GUIElementLocator locator, String elementName);

}
