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
package org.aludratest.content.edifact;

/** Comparison settings for EDI documents.
 * @author Volker Bergmann */
public interface EdiComparisonSettings {

    /** Adds a key expression for determining an identity of elements with the provided name.
     * @param elementName the name of the elements to which to apply the key expression
     * @param keyExpression the key expression to determine the identity of the related elements */
    void addKeyExpression(String elementName, String keyExpression);

    /** Configures that any type of difference is allowed on all elements that match the provided xPath expression.
     * @param xPath the path expressions for the elements on which this toleration applies */
    void tolerateAnyDiffAt(String xPath);

    /** Configures that value or content changes are tolerated on all elements that match the provided xPath expression.
     * @param xPath the path expressions for the elements on which this toleration applies */
    void tolerateDifferentAt(String xPath);

    /** Configures that missing elements are tolerated on all elements that match the provided xPath expression.
     * @param xPath the path expressions for the elements on which this toleration applies */
    void tolerateMissingAt(String xPath);

    /** Configures that additional elements are tolerated on all elements that match the provided xPath expression.
     * @param xPath the path expressions for the elements on which this toleration applies */
    void tolerateUnexpectedAt(String xPath);

    /** Configures that changes of order are tolerated on all elements that match the provided xPath expression.
     * @param xPath the path expressions for the elements on which this toleration applies */
    void tolerateMovedAt(String xPath);

    /** Configures that the specific provided type of difference is tolerated on all elements that match the provided xPath
     * expression.
     * @param type the diff type to tolerate
     * @param xPath the path expressions for the elements on which this toleration applies */
    void tolerateGenericDiff(EdiDiffDetailType type, String xPath);

}