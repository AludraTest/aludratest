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
package org.aludratest.content.xml;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.aludratest.content.ContentHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/** Content handler interface for XML.
 * @author Volker Bergmann */
public interface XmlContent extends ContentHandler {

    /** Parses an XML document from a stream.
     * @param in the {@link InputStream} from which to read the XML document
     * @return an XML document representing the parsed text */
    Document readDocument(InputStream in);

    /** Saves an XML document in a stream.
     * @param document the XML {@link Document} to write
     * @param encoding the encoding to apply
     * @param out the OutputStream to which to write the XML text */
    void writeDocument(Document document, String encoding, OutputStream out);

    /** Formats a full XML document structure recursively as String.
     * @param document the {@link Document} to format
     * @return a text representation of the document */
    String format(Document document);

    /** Performs an XPath query on an XML document and returns the query result's text content.
     * @param document the XML document to query
     * @param elementExpression the XPath query to perform
     * @return the query result's text content */
    String queryElementText(Document document, String elementExpression);

    /** Performs an XPath query on an XML document and returns the query result's text content.
     * @param document the XML document to query
     * @param elementExpression the XPath query to perform
     * @param attributeName the name of the requested attribute
     * @return the query result's text content */
    String queryAttribute(Document document, String elementExpression, String attributeName);

    /** Queries an XML document for an element using an XPath expression.
     * @param document the XML document to query
     * @param expression the XPath query to perform
     * @return the XML {@link Element} if the XPath expression was successful, otherwise null */
    Element queryElement(Document document, String expression);

    /** Queries an XML document for elements using an XPath expression.
     * @param document the XML document to query
     * @param expression the XPath query to perform
     * @return a {@link List} of {@link Element}s with the query results */
    List<Element> queryElements(Document document, String expression);

    /** Performs an XPath query on an XML document.
     * @param document the XML document to query
     * @param expression the XPath query to perform
     * @param returnType determines the type of the returned object: {@link javax.xml.xpath.XPathConstants#STRING} for a single
     *            {@link java.lang.String}, {@link javax.xml.xpath.XPathConstants#NODE} for a single {@link org.w3c.dom.Element},
     *            {@link javax.xml.xpath.XPathConstants#NODESET} for a {@link org.w3c.dom.NodeList}
     * @return the found nodes of the interchange in the form of XML elements, nodes or Strings, depending on the returnType
     *         parameter */
    Object queryXPath(Document document, String expression, QName returnType);

    /** Sets the text of an element that is found in a document using an XPath expression.
     * @param document the document that holds the element
     * @param elementXPath the xpath expression pointing to the element
     * @param newText the text to set as element text */
    void setElementText(Document document, String elementXPath, String newText);

    /** Sets the value of an element attribute that is found in a document using an XPath expression.
     * @param document the document that holds the element
     * @param attributeXPath the xpath expression pointing to the attribute
     * @param newValue the text to set as attribute value */
    void setAttributeValue(Document document, String attributeXPath, String newValue);

    /** Uses a FreeMarker template to create an XML document based on the content of a variables map.
     * @param templateUri the URI of the template to apply
     * @param encoding the encoding of the template file
     * @param variables values to provide as template variables
     * @return an XML {@link Document} with the data configured in the variables map */
    Document createDocument(String templateUri, String encoding, Map<String, Object> variables);

    /** Checks if two XML documents are equal ignoring elements that match the provided xpath expressions.
     * @param expected the expected document structure
     * @param actual the actual document structure
     * @param settings the settings for XML comparison
     * @return true if the documents are equal according to the settings, otherwise false */
    boolean isEqual(Document expected, Document actual, XmlComparisonSettings settings);

    /** @return an object that implements the XmlComparisonSettings interface. */
    XmlComparisonSettings createDefaultComparisonSettings();

    /** Reports the differences between two XML documents.
     * @param expected the expected document structure
     * @param actual the actual document structure
     * @param settings the settings for XML comparison
     * @return a list of the differences */
    AggregateXmlDiff compare(Document expected, Document actual, XmlComparisonSettings settings);

}
