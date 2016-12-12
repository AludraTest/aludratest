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
package org.aludratest.service.edifactfile;

import java.io.IOException;
import java.io.InputStream;

import org.aludratest.content.edifact.EdiComparisonSettings;
import org.aludratest.content.edifact.EdiDiffDetailType;
import org.aludratest.dict.ActionWordLibrary;
import org.aludratest.exception.AutomationException;
import org.aludratest.exception.FunctionalFailure;
import org.aludratest.service.edifactfile.data.KeyExpressionData;
import org.aludratest.service.file.FileFilter;
import org.aludratest.util.data.StringData;
import org.databene.commons.IOUtil;
import org.databene.edifatto.model.Interchange;

/**
 * Provides access to EDI files.
 * @param <E>
 * @author Volker Bergmann
 */
@SuppressWarnings("unchecked")
public class EdifactFileVerifier<E extends EdifactFileVerifier<E>> implements ActionWordLibrary<E> {

    protected String filePath;
    private final EdifactFileService service;
    private final EdiComparisonSettings settings;
    private final String elementType;

    /** Constructor.
     *  @param filePath
     *  @param service */
    public EdifactFileVerifier(String filePath, EdifactFileService service) {
        this.filePath = filePath;
        this.service = service;
        this.settings = service.check().createDefaultComparisonSettings();
        this.elementType = getClass().getSimpleName();
    }

    /** Adds an XPath expression of which all matching Edifact elements are ignored
     *  @param path an XPath expressions of the EDI elements to ignore in comparison
     *  @return a reference to the invoked EdifactFileVerifier instance */
    public E addExclusionPath(StringData path) {
        this.settings.tolerateAnyDiffAt(path.getValue());
        return (E) this;
    }

    /** Allows the given diff type in comparisons.
     * @param type the type of difference
     * @param xPath the path where the difference is tolerated
     * @return a reference to the invoked EdifactFileVerifier instance */
    public E addToleratedDiff(EdiDiffDetailType type, String xPath) {
        this.settings.tolerateGenericDiff(type, xPath);
        return (E) this;
    }

    /** Adds a key expression to the verifier. It is an XPath expression by which EdifactContent
     *  can determine the identity of an EDI element
     *  @param keyExpression the XPath expression that provides the id
     *  @return a reference to the invoked EdifactFileVerifier instance
     */
    public E addKeyExpression(KeyExpressionData keyExpression) {
        this.settings.addKeyExpression(keyExpression.getElementName(), keyExpression.getKeyExpression());
        return (E) this;
    }

    /** Deletes the file.
     *  @return a reference to the FileStream object itself */
    public E delete() {
        service.perform().delete(elementType, null, filePath);
        return (E) this;
    }

    /** Polls the file system until a file at the given path is found
     *  or a timeout occurs.
     *  @return a reference to the FileStream object itself */
    public E waitUntilExists() {
        service.perform().waitUntilExists(elementType, null, filePath);
        return (E) this;
    }

    /** Polls the file system until no file is found at the given path.
     *  @return a reference to the FileStream object itself */
    public E waitUntilNotExists() {
        service.perform().waitUntilNotExists(elementType, null, filePath);
        return (E) this;
    }

    /** Polls the file system until a file at the given path is found or a timeout occurs. If a file matched, the value of the
     * {@link #filePath} field is set to the path of that file. If the timeout is exceeded without a matching file, a
     * {@link FunctionalFailure} is thrown.
     * 
     * Usage Example:
     * 
     * <pre>
     *   MyEdifactFileVerifier verifier = new MyEdifactFileVerifier(null, service);
     *   FileFilter filter = new RegexFilePathFilter(".*IFTDGN_1\\.edi");
     *   verifier.waitForFirstMatch("/", filter);
     *   verifier.verifyWith(new StringData("target/test-classes/ediTest/IFTDGN_1.edi"));
     * </pre>
     * 
     * @param parentPath the path of the directory in which to search for the file
     * @param filter a filter object that decides which file is to be accepted
     * @exception FunctionalFailure if the timeout is exceeded without a matching file
     * @return a reference to the FileStream object itself */
    public E waitForFirstMatch(String parentPath, FileFilter filter) {
        this.filePath = service.perform().waitForFirstMatch(parentPath, filter);
        return (E) this;
    }

    /** Asserts that the interchange stored in this document is equals to the provided interchange, ignoring the provided paths.
     * @param referenceFileUri the URI of the reference file to verify against
     * @return a reference to the invoked EdifactFileVerifier instance */
    public E verifyWith(StringData referenceFileUri) {
        InputStream referenceFileStream = null;
        try {
            referenceFileStream = IOUtil.getInputStreamForURI(referenceFileUri.getValue());
        }
        catch (IOException e) {
            IOUtil.close(referenceFileStream);
            throw new AutomationException("Failed to read reference file", e);
        }
        Interchange expected = service.perform().readInterchange(elementType, "reference file", referenceFileStream);
        Interchange actual = service.perform().readInterchange(
                elementType, "outbound file", this.filePath);
        service.verify().assertInterchangesMatch(elementType, null, expected, actual, settings);
        return (E) this;
    }

    @Override
    public E verifyState() {
        return (E) this;
    }

}
