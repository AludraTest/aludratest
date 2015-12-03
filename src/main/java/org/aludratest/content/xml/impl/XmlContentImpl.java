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
package org.aludratest.content.xml.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.aludratest.content.ContentHandler;
import org.aludratest.content.xml.AggregateXmlDiff;
import org.aludratest.content.xml.XmlComparisonSettings;
import org.aludratest.content.xml.XmlContent;
import org.aludratest.content.xml.util.DatabeneXmlComparisonSettings;
import org.aludratest.content.xml.util.DatabeneXmlUtil;
import org.aludratest.exception.AutomationException;
import org.aludratest.exception.TechnicalException;
import org.databene.commons.Context;
import org.databene.commons.IOUtil;
import org.databene.commons.context.DefaultContext;
import org.databene.commons.xml.XMLUtil;
import org.databene.commons.xml.XPathUtil;
import org.databene.formats.compare.AggregateDiff;
import org.databene.formats.script.Script;
import org.databene.formats.script.freemarker.FreeMarkerScriptFactory;
import org.databene.formats.xml.compare.XMLComparator;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * XML {@link ContentHandler}.
 * @author Volker Bergmann
 */
public class XmlContentImpl implements XmlContent {

    /** Default constructor. */
    public XmlContentImpl() {
    }


    // XMLInteraction interface implementation --------------------------------

    @Override
    public Document readDocument(InputStream in) {
        try {
            Document document = XMLUtil.parse(in);
            return document;
        } catch (IOException e) {
            throw new TechnicalException("Error reading XML document", e);
        }
    }

    @Override
    public void writeDocument(Document document, String encoding, OutputStream out) {
        try {
            XMLUtil.saveDocument(document, encoding, out);
        } catch (Exception e) {
            throw new TechnicalException("Error saving XML document", e);
        }
    }

    // formatting --------------------------------------------------------------

    @Override
    public String format(Document document) {
        return XMLUtil.format(document.getDocumentElement());
    }

    // querying document parts -------------------------------------------------

    /** Performs an XPath query on an XML document and returns the query result's text content.
     * @param document the XML document to query
     * @param elementExpression the XPath query to perform
     * @return the query result's text content */
    @Override
    public String queryElementText(Document document, String elementExpression) {
        try {
            return XPathUtil.queryElementText(document, elementExpression);
        }
        catch (Exception e) {
            throw new TechnicalException("Error querying XML document", e);
        }
    }

    /** Performs an XPath query on an XML document and returns the query result's text content.
     * @param document the XML document to query
     * @param elementExpression the XPath query to perform
     * @param attributeName the name of the requested attribute
     * @return the query result's text content */
    @Override
    public String queryAttribute(Document document, String elementExpression, String attributeName) {
        try {
            return XPathUtil.queryAttribute(document, elementExpression, attributeName);
        }
        catch (Exception e) {
            throw new TechnicalException("Error querying XML document", e);
        }
    }

    /** Queries an XML document for an element using an XPath expression.
     * @param document the XML document to query
     * @param expression the XPath query to perform
     * @return the XML {@link Element} if the XPath expression was successful, otherwise null */
    @Override
    public Element queryElement(Document document, String expression) {
        try {
            return XPathUtil.queryElement(document, expression);
        }
        catch (Exception e) {
            throw new TechnicalException("Error querying XML document", e);
        }
    }

    /** Queries an XML document for elements using an XPath expression.
     * @param document the XML document to query
     * @param expression the XPath query to perform
     * @return a {@link List} of {@link Element}s with the query results */
    @Override
    public List<Element> queryElements(Document document, String expression) {
        try {
            return XPathUtil.queryElements(document, expression);
        }
        catch (Exception e) {
            throw new TechnicalException("Error querying XML document", e);
        }
    }

    @Override
    public Object queryXPath(Document document, String expression, QName returnType) {
        try {
            return XPathUtil.query(document, expression, returnType);
        } catch (XPathExpressionException e) {
            throw new TechnicalException("Error querying XML document", e);
        }
    }

    // document manipulation ---------------------------------------------------

    @Override
    public void setElementText(Document document, String elementXPath, String newText) {
        Element element = queryElement(document, elementXPath);
        if (element == null) {
            throw new AutomationException("No element found with XPath: " + elementXPath);
        }
        element.setTextContent(newText);
    }

    @Override
    public void setAttributeValue(Document document, String attributeXPath, String newValue) {
        Object object = queryXPath(document, attributeXPath, XPathConstants.NODE);
        if (object == null) {
            throw new AutomationException("No attribute found with XPath: " + attributeXPath);
        }
        if (!(object instanceof Attr)) {
            throw new AutomationException("XPath " + attributeXPath + " does not point to an attribute node, but to a "
                    + object.getClass().getSimpleName());
        }
        Attr attribute = (Attr) object;
        attribute.setValue(newValue);
    }

    // document creation -------------------------------------------------------

    @Override
    public Document createDocument(String templateUri, String encoding, Map<String, Object> variables) {
        try {
            // prepare generator
            FreeMarkerScriptFactory factory = new FreeMarkerScriptFactory(Locale.ENGLISH);
            Script script = factory.parseText(IOUtil.getContentOfURI(templateUri));
            Context context = new DefaultContext(variables);
            context.set("documentEncoding", "UTF-8");
            // apply template
            String xmlText = String.valueOf(script.evaluate(context));
            // return result
            return XMLUtil.parseString(xmlText);
        }
        catch (IOException e) {
            throw new TechnicalException("Error creating XML document", e);
        }
    }


    // comparing documents -----------------------------------------------------

    @Override
    public XmlComparisonSettings createDefaultComparisonSettings() {
        return new DatabeneXmlComparisonSettings();
    }

    @Override
    public boolean isEqual(Document expected, Document actual, XmlComparisonSettings settings) {
        return compare(expected, actual, settings).isEmpty();
    }

    @Override
    public AggregateXmlDiff compare(Document expected, Document actual, XmlComparisonSettings settings) {
        try {
            XMLComparator comparator = new XMLComparator((DatabeneXmlComparisonSettings) settings);
            AggregateDiff diff = comparator.compare(expected, actual);
            return DatabeneXmlUtil.toXmlDiff(diff);
        }
        catch (XPathExpressionException e) {
            throw new AutomationException("XML comparison failed", e);
        }
    }

}
