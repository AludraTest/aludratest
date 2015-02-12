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

import org.aludratest.content.edifact.EdifactContent;
import org.aludratest.exception.TechnicalException;
import org.databene.commons.SystemInfo;
import org.databene.edifatto.ComparisonSettings;
import org.databene.edifatto.EdiChecker;
import org.databene.edifatto.EdiFormatSymbols;
import org.databene.edifatto.EdiGenerator;
import org.databene.edifatto.EdiWriter;
import org.databene.edifatto.Edifatto;
import org.databene.edifatto.compare.AggregateDiff;
import org.databene.edifatto.compare.ComparisonModel;
import org.databene.edifatto.model.Interchange;
import org.databene.edifatto.xml.NameBasedEdiToXMLConverter;
import org.w3c.dom.Element;

/**
 * Parses and saves EDIFACT and X12 documents from and to streams.
 * @author Volker Bergmann
 */
public class EdifattoContent implements EdifactContent {

    /** Constructor. */
    public EdifattoContent() {
    }

    /** Parses an EDIFACT or X12 interchange available in an {@link InputStream}. */
    public Interchange readInterchange(InputStream in) {
        try {
            return Edifatto.parseEdiFile(in);
        } catch (IOException e) {
            throw new TechnicalException("Error parsing EDI document", e);
        }
    }

    /** Writes an EDIFACT or X12 interchange to an {@link OutputStream}. */
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

    /** 
     * Creates an XML representation of the interchange and performs an XPath query on it.
     * @param interchange the interchange to query
     * @param expression the XPath query to perform
     * @param returnType determines the type of the returned object: 
     *   {@link javax.xml.xpath.XPathConstants#STRING} for a single {@link java.lang.String},
     *   {@link javax.xml.xpath.XPathConstants#NODE} for a single {@link org.w3c.dom.Element},
     *   {@link javax.xml.xpath.XPathConstants#NODESET} for a {@link org.w3c.dom.NodeList}
     * @return the found nodes of the interchange in the form of XML elements
     */
    public Object queryXML(Interchange interchange, String expression, QName returnType) {
        try {
            return Edifatto.queryXML(interchange, expression, returnType);
        } catch (XPathExpressionException e) {
            throw new TechnicalException("Error in XPath query '" + expression + "'", e);
        }
    }

    /** Finds out the differences between two EDIFACT or X12 interchanges, 
     *  ignoring elements that are tolerated by the {@link ComparisonSettings}.
     *  @param expected 
     *  @param actual 
     *  @return an {@link AggregateDiff} between the documents */
    public AggregateDiff diff(Interchange expected, Interchange actual, 
            ComparisonSettings settings, ComparisonModel<Element> model) {
        try {
            NameBasedEdiToXMLConverter converter = new NameBasedEdiToXMLConverter();
            EdiChecker checker = new EdiChecker(settings, model, converter);
            return checker.diff(expected, actual);
        } catch (Exception e) {
            throw new TechnicalException("Error comparing Edifact interchanges", e);
        }
    }

    /** Formats a full interchange structure recursively as String.
     *  @param interchange the Edifact interchange to format
     *  @return a string representation of the interchange */
    @Override
    public String formatRecursively(Interchange interchange) {
        return Edifatto.formatRecursively(interchange);
    }

}
