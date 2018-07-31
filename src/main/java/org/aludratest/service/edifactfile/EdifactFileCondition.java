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

import javax.xml.namespace.QName;
import javax.xml.xpath.XPathConstants;

import org.aludratest.content.edifact.AggregateEdiDiff;
import org.aludratest.content.edifact.EdiComparisonSettings;
import org.aludratest.service.Condition;
import org.aludratest.service.ElementName;
import org.aludratest.service.ElementType;
import org.aludratest.service.TechnicalArgument;
import org.aludratest.service.TechnicalLocator;
import org.databene.edifatto.model.Interchange;

/**
 * Performs queries on EDIFACT or X12 interchanges and analyzes their differences.
 * @author Volker Bergmann
 */
public interface EdifactFileCondition extends Condition {

    /** Tells if a file exists at the given path.
     * @param elementType the element type to use in logging
     * @param elementName the element name to use in logging
     * @param filePath the file path to query
     * @return true if a file with the provided path exists, otherwise false */
    boolean exists(
            @ElementType String elementType,
            @ElementName String elementName,
            @TechnicalLocator String filePath);

    /** Creates an XML representation of the interchange and performs an XPath query on it.
     * @param elementType the element type to use in logging
     * @param elementName the element name to use in logging
     * @param interchange the interchange to query
     * @param xpathQuery the XPath query to perform
     * @param returnType determines the type of the returned object: {@link XPathConstants#STRING} for a single
     *            {@link java.lang.String}, {@link XPathConstants#NODE} for a single {@link org.w3c.dom.Element},
     *            {@link XPathConstants#NODESET} for a {@link org.w3c.dom.NodeList}
     * @return the query result */
    Object query(
            @ElementType String elementType,
            @ElementName String elementName,
            Interchange interchange,
            @TechnicalLocator String xpathQuery,
            @TechnicalArgument QName returnType);

    /** Finds out the differences between two EDIFACT or X12 interchanges, ignoring elements that match the XPath exclusion paths.
     * @param elementType the element type to use in logging
     * @param elementName the element name to use in logging
     * @param expected the expected interchange data
     * @param actual the actual interface data
     * @param settings the {@link EdiComparisonSettings} to apply
     * @return an AggregateDiff that represent the differences between the interchanges */
    AggregateEdiDiff diff(
            @ElementType String elementType,
            @ElementName String elementName,
            Interchange expected,
            Interchange actual, @TechnicalArgument EdiComparisonSettings settings);

    /** @return an {@link EdiComparisonSettings} with default settings for comparing XML documents */
    EdiComparisonSettings createDefaultComparisonSettings();

}
