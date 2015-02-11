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
package org.aludratest.service.gui;

import org.aludratest.impl.log4testing.ElementName;
import org.aludratest.impl.log4testing.ElementType;
import org.aludratest.impl.log4testing.TechnicalArgument;
import org.aludratest.impl.log4testing.TechnicalLocator;
import org.aludratest.service.Interaction;
import org.aludratest.service.Verification;
import org.aludratest.service.locator.Locator;
import org.aludratest.service.locator.option.OptionLocator;
import org.aludratest.service.locator.window.TitleLocator;

/** The interface {@link Interaction} provides several methods to interact with the active screen of the application under test.
 * This means that the application under test can be controlled with the help of these methods. Every class which implements this
 * interface must assure that a call of one of these methods results in a interaction with the application under test.<br/>
 * For verifications of the application under test see {@link Verification}.
 * @author Marcel Malitz
 * @author Volker Bergmann */
public interface GUIInteraction extends Interaction {

    /** Selects a radio button. If this radio button belongs to a group of radio buttons, the other radio buttons of this group
     * will be unselected.
     * @param elementType the type of the related radio button to log
     * @param elementName the name of the related radio button to log
     * @param locator to locate one specific radio button in the SUT
     * @param taskCompletionTimeout */
    void selectRadiobutton(@ElementType String elementType, @ElementName String elementName, @TechnicalLocator Locator locator,
            @TechnicalArgument int taskCompletionTimeout);

    /** Changes the selection state of a checkbox. If this method will be called on a checkbox which is still selected, the
     * checkbox will be unselected.
     * @param elementType the type of the related checkbox to log
     * @param elementName the name of the related checkbox to log
     * @param locator to locate one specific checkbox in the application under test
     * @param taskCompletionTimeout */
    void changeCheckbox(@ElementType String elementType, @ElementName String elementName, @TechnicalLocator Locator locator,
            @TechnicalArgument int taskCompletionTimeout);

    /** Selects a checkbox. If the checkbox is already selected, this method will do nothing.
     * @param elementType the type of the related checkbox to log
     * @param elementName the name of the related checkbox to log
     * @param locator to locate one specific checkbox in the application under test
     * @param taskCompletionTimeout */
    void selectCheckbox(@ElementType String elementType, @ElementName String elementName, @TechnicalLocator Locator locator,
            @TechnicalArgument int taskCompletionTimeout);

    /** Deselects a checkbox. If the checkbox is not selected, this method will do nothing.
     * @param elementType the type of the related checkbox to log
     * @param elementName the name of the related checkbox to log
     * @param locator to locate one specific checkbox in the application under test
     * @param taskCompletionTimeout */
    void deselectCheckbox(@ElementType String elementType, @ElementName String elementName, @TechnicalLocator Locator locator,
            @TechnicalArgument int taskCompletionTimeout);

    /** Selects an entry in a dropdownbox with the help of a <code>OptionLocator</code>. First it locates the element with the help
     * of the <code>locator</code>, then it tries to select an entry defined by <code>optionLocator</code>.
     * @param elementType the type of the related dropdownbox to log
     * @param elementName the name of the related dropdownbox to log
     * @param locator to locate one specific dropdownbox in the application under test
     * @param optionLocator defines which entry of the located dropdownbox shall be selected
     * @param taskCompletionTimeout */
    void selectDropDownEntry(@ElementType String elementType, @ElementName String elementName, @TechnicalLocator Locator locator,
            OptionLocator optionLocator,
            @TechnicalArgument int taskCompletionTimeout);

    /** Types in some text without conversion/manipulation of the passed through text. The content of the locator will be deleted,
     * if the expected text is not set.
     * @param elementType the type of the related GUI element to log
     * @param elementName the name of the related GUI element to log
     * @param locator to locate one specific inputfield in the application under test. An inputfield is any GUI element which
     *            accepts user inputs.
     * @param text which shall be typed in without conversion/manipulation
     * @param taskCompletionTimeout */
    void type(@ElementType String elementType, @ElementName String elementName, @TechnicalLocator Locator locator, String text,
            @TechnicalArgument int taskCompletionTimeout);

    /** Assigns a file resource of the test project file system to the file chooser specified by the locator.
     * @param elementType the type of the related GUI element to log
     * @param elementName the name of the related GUI element to log
     * @param locator to locate the related file selection field in the application under test
     * @param filePath the absolute path of the file to be assigned to the file chooser
     * @param taskCompletionTimeout the maximum number of milliseconds to wait for the completion of system activities */
    void assignFileResource(String elementType, String elementName, Locator locator, String filePath, int taskCompletionTimeout);

    /** Clicks with a single click on any kind of element which reacts on click events. A common example are buttons and links.
     * @param elementType the type of the related GUI element to log
     * @param elementName the name of the related GUI element to log
     * @param locator to locate buttons, links or any other elements which react on mouse clicks.
     * @param taskCompletionTimeout */
    void click(@ElementType String elementType, @ElementName String elementName, @TechnicalLocator Locator locator,
            @TechnicalArgument int taskCompletionTimeout);

    /** Clicks with a single click on any kind of element which reacts on click events and is not editable. A common example are
     * buttons and links.
     * @param elementType the type of the related GUI element to log
     * @param elementName the name of the related GUI element to log
     * @param locator to locate buttons, links or any other elements which react on mouse clicks.
     * @param taskCompletionTimeout */
    void clickNotEditable(@ElementType String elementType, @ElementName String elementName, @TechnicalLocator Locator locator,
            @TechnicalArgument int taskCompletionTimeout);

    /** Clicks with a double click on any kind of element which reacts on click events and is not editable. A common example are
     * buttons and links.
     * @param elementType the type of the related GUI element to log
     * @param elementName the name of the related GUI element to log
     * @param locator to locate buttons, links or any other elements which react on mouse clicks.
     * @param taskCompletionTimeout */
    void doubleClickNotEditable(@ElementType String elementType, @ElementName String elementName,
            @TechnicalLocator Locator locator, @TechnicalArgument int taskCompletionTimeout);

    /** Reads the value of an inputfield and returns it as a String without conversion/manipulation.
     * @param elementType the type of the related GUI element to log
     * @param elementName the name of the related GUI element to log
     * @param locator to locate the inputfield in the application under test where the inputfield must be an element for user
     *            inputs. Two examples are single line inputfields and text areas in web applications. This action works also with
     *            disabled inputfields.
     * @return the value of the inputfield. If the inputfield could not be found, <code>null</code> will be returned */
    String getInputFieldValue(@ElementType String elementType, @ElementName String elementName, @TechnicalLocator Locator locator);

    /** Reads the selected label of an input field and returns it as a String without conversion/manipulation.
     * @param elementType the type of the related GUI element to log
     * @param elementName the name of the related GUI element to log
     * @param locator to locate the input field in the application under test where the inputfield must be an element for user
     *            inputs. Two examples are dropdown boxes and lists in web applications. This action works also with disabled
     *            input fields.
     * @return the value of the input field. If the input field could not be found, <code>null</code> will be returned */
    String getInputFieldSelectedLabel(@ElementType String elementType, @ElementName String elementName,
            @TechnicalLocator Locator locator);

    /** Selects a window using a window locator. Once a window has been selected, all commands go to that window.
     * 
     * @param locator to locate one specific window of the application under test */
    void selectWindow(@TechnicalLocator Locator locator);

    /** Gets the text of an element.
     * @param elementType the type of the related GUI element to log
     * @param elementName the name of the related GUI element to log
     * @param locator points to one element
     * @return the unmodified text of an element */
    String getText(@ElementType String elementType, @ElementName String elementName, @TechnicalLocator Locator locator);

    /** Gets the text of an element and is adjustable to the check of the visibility of the element
     * @param elementType the type of the related GUI element to log
     * @param elementName the name of the related GUI element to log
     * @param locator points to one element, visible: to check visibility of the element
     * @param checkVisible
     * @return the unmodified text of an element */
    String getText(@ElementType String elementType, @ElementName String elementName, @TechnicalLocator Locator locator,
            boolean checkVisible);

    /** Gives focus on an element.
     * @param elementType the type of the related GUI element to log
     * @param elementName the name of the related GUI element to log
     * @param locator of the element which shall get the focus */
    void focus(@ElementType String elementType, @ElementName String elementName, @TechnicalLocator Locator locator);

    /** Presses a key on the keyboard. Available key codes can be found in {@link java.awt.event.KeyEvent}.
     * @param keycode is the key which shall be pressed.
     * @see java.awt.event.KeyEvent */
    void keyPress(int keycode);

    /** Does a double click on the element which is identified by the locator.
     * @param elementType the type of the related GUI element to log
     * @param elementName the name of the related GUI element to log
     * @param locator which identifies the element which shall be double clicked
     * @param taskCompletionTimeout */
    void doubleClick(@ElementType String elementType, @ElementName String elementName, @TechnicalLocator Locator locator,
            @TechnicalArgument int taskCompletionTimeout);

    /** Closes all windows identified by their name. That means, that if there are several windows with same name all will be
     * closed. This method is not waiting for a window to open.
     * @param elementType the type of the target windows to log
     * @param elementName the name of the target windows to log
     * @param locator - name of the window */
    void closeWindows(@ElementType String elementType, @ElementName String elementName, @TechnicalLocator TitleLocator locator);

    /** Closes all open windows which do not have the specified title.
     * @param elementType the type of the target windows to log
     * @param elementName the name of the target windows to log
     * @param locator is a window locator or just a String which will be automatically converted to one of the default locators
     *            depending on the underlying driver and the used default localization mechanism */
    void closeOtherWindows(@ElementType String elementType, @ElementName String elementName, @TechnicalLocator Locator locator);

    void wrongPageFlow(String message);

    void functionalError(String message);

}
