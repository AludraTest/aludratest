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
package org.aludratest.content.edifact.edifatto;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPathExpressionException;

import org.aludratest.content.edifact.AggregateEdiDiff;
import org.aludratest.content.edifact.EdiComparisonSettings;
import org.aludratest.content.edifact.EdifactContent;
import org.aludratest.content.xml.XmlComparisonSettings;
import org.aludratest.content.xml.util.DatabeneXmlComparisonSettings;
import org.aludratest.exception.TechnicalException;
import org.databene.commons.SystemInfo;
import org.databene.edifatto.EdiChecker;
import org.databene.edifatto.EdiFormatSymbols;
import org.databene.edifatto.EdiGenerator;
import org.databene.edifatto.EdiParserSettings;
import org.databene.edifatto.EdiWriter;
import org.databene.edifatto.Edifatto;
import org.databene.edifatto.model.Interchange;
import org.databene.edifatto.util.TypeBasedXMLComparisonModel;
import org.databene.edifatto.xml.NameBasedEdiToXMLConverter;
import org.databene.formats.compare.AggregateDiff;
import org.databene.formats.compare.ComparisonSettings;

/** Parses and saves EDIFACT and X12 documents from and to streams.
 * @author Volker Bergmann */
public class EdifattoContent implements EdifactContent {

    /** Parses an EDIFACT or X12 interchange available in an {@link InputStream}. */
    @Override
    public Interchange readInterchange(InputStream in) {
        try {
            return Edifatto.parseEdiFile(in, new EdiParserSettings());
        } catch (IOException e) {
            throw new TechnicalException("Error parsing EDI document", e);
        }
    }

    /** Writes an EDIFACT or X12 interchange to an {@link OutputStream}. */
    @Override
    public void writeInterchange(Interchange interchange, OutputStream out, boolean useLinefeed) {
        try {
            String lineFeed = (useLinefeed ? SystemInfo.getLineSeparator() : null);
            new EdiWriter(lineFeed).writeInterchange(interchange, out);
        } catch (IOException e) {
            throw new TechnicalException("Error writing EDI document", e);
        }
    }

    @Override
    public Interchange createInterchange(String templateUri, EdiFormatSymbols symbols, Map<String, Object> variables) {
        try {
            return EdiGenerator.createInterchange(templateUri, symbols, variables);
        } catch (IOException e) {
            throw new TechnicalException("Error creating EDI document", e);
        }
    }

    /** Creates an XML representation of the interchange and performs an XPath query on it.
     * @param interchange the interchange to query
     * @param expression the XPath query to perform
     * @param returnType determines the type of the returned object: {@link javax.xml.xpath.XPathConstants#STRING} for a single
     *            {@link java.lang.String}, {@link javax.xml.xpath.XPathConstants#NODE} for a single {@link org.w3c.dom.Element},
     *            {@link javax.xml.xpath.XPathConstants#NODESET} for a {@link org.w3c.dom.NodeList}
     * @return the found nodes of the interchange in the form of XML elements */
    @Override
    public Object query(Interchange interchange, String expression, QName returnType) {
        try {
            return Edifatto.queryXML(interchange, expression, returnType);
        } catch (XPathExpressionException e) {
            throw new TechnicalException("Error in XPath query '" + expression + "'", e);
        }
    }

    /** @return an instance of the {@link XmlComparisonSettings} appropriate for comparing EDIFACT or X12 interchanges */
    @Override
    public EdiComparisonSettings createDefaultComparisonSettings() {
        return new DatabeneEdiComparisonSettings(new TypeBasedXMLComparisonModel());
    }

    /** Finds out the differences between two EDIFACT or X12 interchanges, ignoring elements that are tolerated by the
     * {@link XmlComparisonSettings}.
     * @param expected
     * @param actual
     * @return an {@link AggregateDiff} between the documents */
    @Override
    public AggregateEdiDiff compare(Interchange expected, Interchange actual, EdiComparisonSettings settings) {
        try {
            NameBasedEdiToXMLConverter converter = new NameBasedEdiToXMLConverter();
            EdiChecker checker = new EdiChecker((DatabeneXmlComparisonSettings) settings, converter);
            AggregateDiff genericDiff = checker.diff(expected, actual);
            // TODO In the result we use XML Document objects as expected an actual,
            // because they will be used for rendering the compared content.
            // From the semantically point of view, they should be Edifact Interchanges
            return new EdifattoAggregateEdiDiff(genericDiff.getExpected(), genericDiff.getActual(),
                    (ComparisonSettings) settings, genericDiff);
        } catch (Exception e) {
            throw new TechnicalException("Error comparing Edifact interchanges", e);
        }
    }

    /** Formats a full interchange structure recursively as String.
     * @param interchange the Edifact interchange to format
     * @return a string representation of the interchange */
    @Override
    public String formatRecursively(Interchange interchange) {
        return Edifatto.formatRecursively(interchange);
    }

}
