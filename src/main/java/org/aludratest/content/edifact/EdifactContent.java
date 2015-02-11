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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.xml.namespace.QName;

import org.aludratest.content.ContentHandler;
import org.databene.edifatto.ComparisonSettings;
import org.databene.edifatto.EdiFormatSymbols;
import org.databene.edifatto.compare.AggregateDiff;
import org.databene.edifatto.compare.ComparisonModel;
import org.databene.edifatto.model.Interchange;
import org.w3c.dom.Element;

/**
 * Parses and saves EDIFACT and X12 documents from and to streams.
 * @author Volker Bergmann
 */
public interface EdifactContent extends ContentHandler {

    /** Parses an EDIFACT or X12 interchange available in an {@link InputStream}. 
     *  @param in  the {@link InputStream} from which to read the EDI document
     *  @return an object representation of the EDI {@link Interchange} */
    Interchange readInterchange(InputStream in);

    /** Writes an EDIFACT or X12 interchange to an {@link OutputStream}. 
     *  @param interchange the EDI {@link Interchange} to write
     *  @param out the {@link OutputStream} to write to 
     *  @param linefeed */
    void writeInterchange(Interchange interchange, OutputStream out, boolean linefeed);

    /** Uses a FreeMarker template to create an EDI message based on the content of a variables map.
     *  @param templateUri
     *  @param symbols
     *  @param variables
     *  @return an {@link Interchange} with the data configured in the variables map */
    Interchange createInterchange(String templateUri, EdiFormatSymbols symbols, Map<String, Object> variables);

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
    Object queryXML(Interchange interchange, String expression, QName returnType);

    /** Finds out the differences between two EDIFACT or X12 interchanges, 
     *  ignoring elements that match the XPath exclusion paths.
     *  @param expected 
     *  @param actual 
     *  @param settings 
     *  @param model 
     *  @return an aggregated diff of the documents */
    AggregateDiff diff(Interchange expected, Interchange actual, 
            ComparisonSettings settings, ComparisonModel<Element> model);

    /** Formats a full interchange structure recursively as String.
     *  @param interchange the Edifact interchange to format
     *  @return a string representation of the interchange */
    String formatRecursively(Interchange interchange);

}
