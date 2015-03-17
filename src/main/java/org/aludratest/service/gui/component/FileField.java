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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.aludratest.exception.AutomationException;
import org.aludratest.service.gui.AludraGUI;
import org.aludratest.service.locator.element.GUIElementLocator;
import org.aludratest.util.data.helper.DataMarkerCheck;
import org.databene.commons.IOUtil;
import org.databene.commons.Validator;

/** Represents a file field in a GUI.
 * @author Volker Bergmann */
public class FileField extends InputComponent<InputField> implements ValueComponent {

    private ValueComponentHelper helper = new ValueComponentHelper(this, true);

    /** Constructor.
     * @param aludraGui the underlying {@link AludraGUI} service instance
     * @param locator a locator for the referenced input field */
    public FileField(AludraGUI aludraGui, GUIElementLocator locator) {
        super(aludraGui, locator);
    }

    /** Constructor.
     * @param aludraGui the underlying {@link AludraGUI} service instance
     * @param locator a locator for the referenced element
     * @param elementName an explicit name to use for the component */
    public FileField(AludraGUI aludraGui, GUIElementLocator locator, String elementName) {
        super(aludraGui, locator, elementName);
    }

    /** Saves the {@link InputStream}'s content in a new file with the given name.
     * @param fileName the name by which to save the file
     * @param in the provider of the file content to save */
    public void setResourceNameAndContent(String fileName, InputStream in) {
        if (!DataMarkerCheck.isNull(fileName)) {
            File tempFile = getTestResourceFile(fileName);
            try {
                saveStreamContent(in, tempFile);
                assignFileResource(fileName);
            }
            catch (IOException e) {
                throw new AutomationException("Error ", e);
            }
        }
    }

    @Override
    public String getText() {
        return perform().getInputFieldValue(elementType, elementName, getLocator());
    }

    @Override
    public void assertTextEquals(String expectedText) {
        helper.assertTextEquals(expectedText);
    }

    @Override
    public void assertTextNotEquals(String expectedText) {
        helper.assertTextNotEquals(expectedText);
    }

    @Override
    public void assertTextContains(String expectedText) {
        helper.assertTextContains(expectedText);
    }

    @Override
    public void assertTextContainsIgnoreCaseTrimmed(String expectedText) {
        helper.assertTextContainsIgnoreCaseTrimmed(expectedText);
    }

    @Override
    public void assertTextEqualsIgnoreCaseTrimmed(String expectedText) {
        helper.assertTextEqualsIgnoreCaseTrimmed(expectedText);
    }

    @Override
    public void assertTextMatches(Validator<String> validator) {
        helper.assertTextMatches(validator);
    }

    @Override
    public void assertValueGreaterThan(String value) {
        helper.assertValueGreaterThan(value);
    }

    @Override
    public void assertValueLessThan(String value) {
        helper.assertValueLessThan(value);
    }

    private void saveStreamContent(InputStream in, File tempFile) throws FileNotFoundException, IOException {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(tempFile);
            IOUtil.transfer(in, out);
        }
        finally {
            IOUtil.close(out);
        }
    }

    /** Enters text in the InputField. If the text is null or marked as null the operation will not be executed If the text is
     * marked as empty it will be replaced with ""
     * @param fileName the name of the file to assign to the file field */
    private void assignFileResource(String fileName) {
        if (!DataMarkerCheck.isNull(fileName)) {
            String filePath = getTestResourceFile(fileName).getAbsolutePath();
            perform().assignFileResource(elementType, elementName, getLocator(), DataMarkerCheck.convertIfEmpty(filePath),
                    taskCompletionTimeout);
        }
    }

    private File getTestResourceFile(String fileName) {
        return new File(getTestResourceFolder(), fileName);
    }

    private File getTestResourceFolder() {
        return new File("target/classes");
    }

}
