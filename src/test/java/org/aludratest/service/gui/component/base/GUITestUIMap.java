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
package org.aludratest.service.gui.component.base;

import org.aludratest.service.gui.component.Button;
import org.aludratest.service.gui.component.Checkbox;
import org.aludratest.service.gui.component.Dropdownbox;
import org.aludratest.service.gui.component.FileField;
import org.aludratest.service.gui.component.InputField;
import org.aludratest.service.gui.component.Label;
import org.aludratest.service.gui.component.Link;
import org.aludratest.service.gui.component.RadioButton;
import org.aludratest.service.gui.web.AludraWebGUI;
import org.aludratest.service.gui.web.uimap.UIMap;
import org.aludratest.service.locator.Locator;
import org.aludratest.service.locator.element.IdLocator;
import org.aludratest.service.locator.element.XPathLocator;
import org.aludratest.service.locator.window.TitleLocator;

/**
 * UI map for the GUI componentes of this package.
 * @author YWANG
 */
@SuppressWarnings("javadoc")
public class GUITestUIMap extends UIMap {

    public final static TitleLocator TEST_PAGE_TITLE = new TitleLocator("Testwebsite");
    public final static TitleLocator LINKED_PAGE_TITLE = new TitleLocator("Beschreibung der Seite");

    public final static Locator DROPDOWNBOX_ID = new IdLocator("DropDown:after");
    public final static Locator DISABLED_DROPDOWNBOX_ID = new IdLocator("DisabledDropDown:after");

    public final static Locator TEXT_FIELD_ID = new IdLocator("TextField:after");
    public final static Locator NOID_TEXT_FIELD = new XPathLocator("//input[@class='myclass']");

    public final static Locator DISABLED_TEXT_FIELD_ID = new IdLocator("disabled_TextField:after");

    public final static Locator RADIO_BUTTON_AND = new IdLocator("RadioAnd:after");
    public final static Locator RADIO_BUTTON_OR = new IdLocator("RadioOR:after");

    public final static Locator FIRST_CHECKBOX_ID = new IdLocator("FirstCheckBox:after");
    public final static Locator SECOND_CHECKBOX_ID = new IdLocator("SecondCheckBox:after");
    public final static Locator DISABLED_CHECKBOX_ID = new IdLocator("DisabledCheckBox:after");

    public final static Locator TEST_LINK_ID = new IdLocator("LinktoTThis:after");

    public final static Locator FIND_BUTTON_ID = new IdLocator("FindButton:after");
    public final static Locator DISABLED_BUTTON_ID = new IdLocator("DisabledButton:after");
    public final static Locator HIDDEN_BUTTON_ID = new IdLocator("InvisibleButton:after");
    public final static Locator HIDDEN_DIV_BUTTON_ID = new IdLocator("InvisibleDIVButton:after");

    public final static Locator IMAGE_ID = new IdLocator("PictureLogo:after");
    public final static Locator IMAGE_BUTTON_ID = new IdLocator("gF:msgimg");

    public final static Locator LABEL_ID = new IdLocator("LabelTest:after");

    public final static Locator FILE_FIELD_ID = new IdLocator("FileField:after");
    public final static Locator SUBMIT_FILE_ID = new IdLocator("SubmitFile");
    public final static Locator FILE_NAME_ID = new IdLocator("FileName");
    public final static Locator FILE_CONTENT_ID = new IdLocator("FileContent");

    // There is no element defined on test web page with this ID
    public final static Locator NOT_EXISTING_BUTTON_ID = new IdLocator("test:test:test");

    public GUITestUIMap(AludraWebGUI aludraGUI) {
        super(aludraGUI);
    }

    public Dropdownbox dropDownBox() {
        return new Dropdownbox(aludraGUI, DROPDOWNBOX_ID);
    }

    public Dropdownbox disabledDropDownBox() {
        return new Dropdownbox(aludraGUI, DISABLED_DROPDOWNBOX_ID);
    }

    public InputField textField() {
        return new InputField(aludraGUI, TEXT_FIELD_ID);
    }

    public InputField noidTextField() {
        return new InputField(aludraGUI, NOID_TEXT_FIELD);
    }

    public InputField disabledTextField() {
        return new InputField(aludraGUI, DISABLED_TEXT_FIELD_ID);
    }

    public Button findButton() {
        return new Button(aludraGUI, FIND_BUTTON_ID);
    }

    public Button findButtonWithTimeout() {
        return new Button(aludraGUI, FIND_BUTTON_ID).waitingUntilTaskCompletion();
    }

    public Button disabledButton() {
        return new Button(aludraGUI, DISABLED_BUTTON_ID);
    }

    public Button hiddenButton() {
        return new Button(aludraGUI, HIDDEN_BUTTON_ID);
    }

    public Button hiddenDivButton() {
        return new Button(aludraGUI, HIDDEN_DIV_BUTTON_ID);
    }

    public Button notExistingButton() {
        return new Button(aludraGUI, NOT_EXISTING_BUTTON_ID);
    }

    public Checkbox firstCheckBox() {
        return new Checkbox(aludraGUI, FIRST_CHECKBOX_ID);
    }

    public Checkbox secondCheckBox() {
        return new Checkbox(aludraGUI, SECOND_CHECKBOX_ID);
    }

    public Checkbox disabledCheckBox() {
        return new Checkbox(aludraGUI, DISABLED_CHECKBOX_ID);
    }

    public RadioButton andRadioButton() {
        return new RadioButton(aludraGUI, RADIO_BUTTON_AND);
    }

    public RadioButton orRadioButton() {
        return new RadioButton(aludraGUI, RADIO_BUTTON_OR);
    }

    public Link testLink() {
        return new Link(aludraGUI, TEST_LINK_ID);
    }

    public Label image() {
        return new Label(aludraGUI, IMAGE_ID);
    }

    public Button imageButton() {
        return new Button(aludraGUI, IMAGE_BUTTON_ID);
    }

    public Label label() {
        return new Label(aludraGUI, LABEL_ID);
    }

    public FileField fileField() {
        return new FileField(aludraGUI, FILE_FIELD_ID);
    }

    public Button fileSubmitButton() {
        return new Button(aludraGUI, SUBMIT_FILE_ID);
    }

    public Label fileNameLabel() {
        return new Label(aludraGUI, FILE_NAME_ID);
    }

    public Label fileContentLabel() {
        return new Label(aludraGUI, FILE_CONTENT_ID);
    }

}
