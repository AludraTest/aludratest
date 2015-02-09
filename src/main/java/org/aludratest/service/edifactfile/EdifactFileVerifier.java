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

import org.aludratest.dict.ActionWordLibrary;
import org.aludratest.service.edifactfile.data.KeyExpressionData;
import org.aludratest.service.edifactfile.data.LocalDiffTypeData;
import org.aludratest.util.data.StringData;
import org.databene.edifatto.ComparisonSettings;
import org.databene.edifatto.compare.DiffType;
import org.databene.edifatto.model.Interchange;
import org.databene.edifatto.util.NameBasedXMLComparisonModel;

/**
 * Provides access to EDI files.
 * @param <E>
 * @author Volker Bergmann
 */
@SuppressWarnings("unchecked")
public class EdifactFileVerifier<E extends EdifactFileVerifier<E>> implements ActionWordLibrary<E> {

    public static final String ANY = null;
    public static final String DIFFERENT = DiffType.DIFFERENT.name();
    public static final String MISSING = DiffType.MISSING.name();
    public static final String MOVED = DiffType.MOVED.name();
    public static final String UNEXPECTED = DiffType.UNEXPECTED.name();

    private final String filePath;
    private final EdifactFileService service;
    private final NameBasedXMLComparisonModel model;
    private final ComparisonSettings settings;
    private final String elementType;

    /** Constructor.
     *  @param filePath
     *  @param service */
    public EdifactFileVerifier(String filePath, EdifactFileService service) {
        this.filePath = filePath;
        this.service = service;
        this.model = new NameBasedXMLComparisonModel();
        this.settings = new ComparisonSettings();
        this.elementType = getClass().getSimpleName();
    }

    /** Adds an XPath expression of which all matching Edifact elements are ignored
     *  @param path an XPath expressions of the EDI elements to ignore in comparison
     *  @return a reference to the invoked EdifactFileVerifier instance */
    public E addExclusionPath(StringData path) {
        this.settings.addToleratedDiff(null, path.getValue());
        return (E) this;
    }

    /** Allows the given diff type in comparisons.
     * @param toleratedDiffType the diff type to tolerate
     * @return a reference to the invoked EdifactFileVerifier instance */
    public E addToleratedDiff(LocalDiffTypeData toleratedDiffType) {
        DiffType type = diffType(toleratedDiffType.getType());
        this.settings.addToleratedDiff(type, toleratedDiffType.getXpath());
        return (E) this;
    }

    /** Adds a key expression to the verifier. It is an XPath expression by which EdifactContent
     *  can determine the identity of an EDI element
     *  @param keyExpression the XPath expression that provides the id
     *  @return a reference to the invoked EdifactFileVerifier instance
     */
    public E addKeyExpression(KeyExpressionData keyExpression) {
        this.model.addKeyExpression(keyExpression.getElementName(), keyExpression.getKeyExpression());
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

    /** Asserts that the interchange stored in this document is equals to the provided
     *  interchange, ignoring the provided paths.
     *  @param referenceFileName the name of the reference file to verify against
     *  @return a reference to the invoked EdifactFileVerifier instance */
    public E verifyWith(StringData referenceFileName) {
        Interchange expected = service.perform().readInterchange(
                elementType, "reference file", referenceFileName.getValue());
        Interchange actual = service.perform().readInterchange(
                elementType, "outbound file", this.filePath);
        service.verify().assertInterchangesMatch(
                elementType, null, expected, actual, settings, model);
        return (E) this;
    }

    @Override
    public E verifyState() {
        return (E) this;
    }

    private DiffType diffType(String name) {
        return (name != null ? DiffType.valueOf(name) : null);
    }

}
