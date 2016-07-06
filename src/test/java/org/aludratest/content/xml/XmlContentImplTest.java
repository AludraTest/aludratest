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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aludratest.content.xml.impl.XmlContentImpl;
import org.aludratest.content.xml.util.DatabeneXmlUtil;
import org.databene.commons.Encodings;
import org.databene.commons.SystemInfo;
import org.databene.commons.xml.XMLUtil;
import org.databene.commons.xml.XPathUtil;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/** Tests the {@link XmlContentImpl}
 * @author Volker Bergmann */
@SuppressWarnings("javadoc")
public class XmlContentImplTest {

    private static final String LF = SystemInfo.getLineSeparator();

    private static final String SIMPLE_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + LF
            + "<root att=\"val\">" + LF + "  <node>text</node>" + LF + "</root>" + LF;

    private static final String RESOURCE_PATH = "org/aludratest/content/xml/";

    private static final String FTL_TEMPLATE = RESOURCE_PATH + "xml_template.ftl";

    private XmlContent content;

    @Before
    public void setUp() {
        this.content = new XmlContentImpl();
    }

    @Test
    public void testReadDocument() throws Exception {
        Document doc = content.readDocument(getSimpleXmlInputStream());
        verifySimpleDocument(doc);
    }

    @Test
    public void testWriteDocument() throws Exception {
        Document doc = XMLUtil.createDocument("root");
        Element root = doc.getDocumentElement();
        root.setAttribute("att", "val");
        Element node = doc.createElement("node");
        root.appendChild(node);
        node.setTextContent("text");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        content.writeDocument(doc, Encodings.UTF_8, out);
        assertEquals(SIMPLE_XML, new String(out.toByteArray(), Encodings.UTF_8));
    }

    @Test
    public void testQueryAttribute() throws Exception {
        Document doc = parseSimpleXml();
        assertEquals("val", content.queryAttribute(doc, "/root", "att"));
        assertNull(content.queryAttribute(doc, "/root", "noatt"));
    }

    @Test
    public void testQueryElementText() throws Exception {
        Document doc = parseSimpleXml();
        assertEquals("text", content.queryElementText(doc, "/root/node"));
        assertEquals("\n  text\n", content.queryElementText(doc, "/root"));
        assertNull(content.queryElementText(doc, "/root/nonode"));
    }

    @Test
    public void testQueryElement() throws Exception {
        Document doc = parseSimpleXml();
        Element node = content.queryElement(doc, "/root/node");
        assertEquals("node", node.getNodeName());
        assertEquals(null, content.queryElement(doc, "/root/nonode"));
    }

    @Test
    public void testQueryElements() throws Exception {
        Document doc = parseSimpleXml();
        List<Element> nodes = content.queryElements(doc, "/root/node");
        assertEquals(1, nodes.size());
        assertEquals("node", nodes.get(0).getNodeName());
        assertEquals(0, content.queryElements(doc, "/root/nonode").size());
    }

    // document manipulation ---------------------------------------------------

    @Test
    public void testSetElementText() {
        Document doc = parseSimpleXml();
        assertEquals("text", node(doc).getTextContent());
        content.setElementText(doc, "/root/node", "newText");
        assertEquals("newText", node(doc).getTextContent());
    }

    @Test
    public void testSetAttrbuteValue() {
        Document doc = parseSimpleXml();
        assertEquals("val", att(doc));
        content.setAttributeValue(doc, "/root/@att", "newVal");
        assertEquals("newVal", att(doc));
    }

    // document creation -------------------------------------------------------

    @Test
    public void testCreateDocument() throws Exception {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("attvar", "val");
        variables.put("textvar", "text");
        Document doc = content.createDocument(FTL_TEMPLATE, Encodings.UTF_8, variables);
        verifySimpleDocument(doc);
    }

    // document comparison -----------------------------------------------------

    @Test
    public void testIsEqual_identical() {
        Document doc = parseSimpleXml();
        assertTrue(content.isEqual(doc, doc, content.createDefaultComparisonSettings()));
    }

    @Test
    public void testIsEqual_different() {
        Document expected = parseSimpleXml();
        Document actual = parseSimpleXml();
        actual.getDocumentElement().setAttribute("att", "val2");
        assertFalse(content.isEqual(expected, actual, content.createDefaultComparisonSettings()));
    }

    @Test
    public void testDiff_identical() {
        Document doc = parseSimpleXml();
        assertTrue(content.compare(doc, doc, content.createDefaultComparisonSettings()).isEmpty());
    }

    // test attribute diffs ----------------------------------------------------

    @Test
    public void testDiff_otherAttributeVal() {
        Document expected = parseSimpleXml();
        Document actual = parseSimpleXml();
        actual.getDocumentElement().setAttribute("att", "val2");
        AggregateXmlDiff diff = content.compare(expected, actual, content.createDefaultComparisonSettings());
        assertEquals(1, diff.getXmlDetails().size());
        assertEquals(DatabeneXmlUtil.different("val", "val2", "attribute value", "/root/@att", "/root/@att"),
                diff.getXmlDetails().get(0));
    }

    @Test
    public void testDiff_otherAttributeValTolerated() {
        Document expected = parseSimpleXml();
        Document actual = parseSimpleXml();
        actual.getDocumentElement().setAttribute("att", "val2");
        XmlComparisonSettings settings = content.createDefaultComparisonSettings();
        settings.tolerateDifferentAt("/root/@att");
        AggregateXmlDiff diff = content.compare(expected, actual, settings);
        assertTrue("Unexpected diff", diff.isEmpty());
    }

    @Test
    public void testDiff_unexpectedAttribute() {
        Document expected = parseSimpleXml();
        Document actual = parseSimpleXml();
        actual.getDocumentElement().setAttribute("att2", "val2");
        AggregateXmlDiff diff = content.compare(expected, actual, content.createDefaultComparisonSettings());
        assertEquals(1, diff.getXmlDetails().size());
        assertEquals(DatabeneXmlUtil.unexpected("val2", "attribute", "/root/@att2"), diff.getXmlDetails().get(0));
    }

    @Test
    public void testDiff_UnexpectedAttributeTolerated() {
        Document expected = parseSimpleXml();
        Document actual = parseSimpleXml();
        actual.getDocumentElement().setAttribute("att2", "val2");
        XmlComparisonSettings settings = content.createDefaultComparisonSettings();
        settings.tolerateUnexpectedAt("/root/@att2");
        AggregateXmlDiff diff = content.compare(expected, actual, settings);
        assertTrue("Unexpected diff", diff.isEmpty());
    }

    @Test
    public void testDiff_missingAttribute() {
        Document expected = parseSimpleXml();
        Document actual = parseSimpleXml();
        actual.getDocumentElement().removeAttribute("att");
        AggregateXmlDiff diff = content.compare(expected, actual, content.createDefaultComparisonSettings());
        assertEquals(1, diff.getXmlDetails().size());
        assertEquals(DatabeneXmlUtil.missing("val", "attribute", "/root/@att"), diff.getXmlDetails().get(0));
    }

    @Test
    public void testDiff_missingAttributeTolerated() {
        Document expected = parseSimpleXml();
        Document actual = parseSimpleXml();
        actual.getDocumentElement().removeAttribute("att");
        XmlComparisonSettings settings = content.createDefaultComparisonSettings();
        settings.tolerateMissingAt("/root/@att");
        AggregateXmlDiff diff = content.compare(expected, actual, settings);
        assertTrue("Unexpected diff", diff.isEmpty());
    }

    // test element diffs ------------------------------------------------------

    @Test
    public void testDiff_otherElementText() throws Exception {
        Document expected = parseSimpleXml();
        Document actual = parseSimpleXml();
        node(actual).setTextContent("otherText");
        AggregateXmlDiff diff = content.compare(expected, actual, content.createDefaultComparisonSettings());
        assertEquals(1, diff.getXmlDetails().size());
        Node expectedText = XPathUtil.queryNode(expected, "/root/node/text()");
        assertEquals("text", expectedText.getTextContent());
        Node actualText = XPathUtil.queryNode(actual, "/root/node/text()");
        assertEquals("otherText", actualText.getTextContent());
        XmlDiffDetail expectedDiff = DatabeneXmlUtil.different(expectedText, actualText, "element text", "/root/node/text()",
                "/root/node/text()");
        assertEquals(expectedDiff, diff.getXmlDetails().get(0));
    }

    @Test
    public void testDiff_otherElementTextTolerated() {
        Document expected = parseSimpleXml();
        Document actual = parseSimpleXml();
        node(actual).setTextContent("otherText");
        XmlComparisonSettings settings = content.createDefaultComparisonSettings();
        settings.tolerateDifferentAt("/root/node/text()");
        AggregateXmlDiff diff = content.compare(expected, actual, settings);
        assertTrue("Unexpected diff: " + diff, diff.isEmpty());
    }

    @Test
    public void testDiff_missingElement() {
        Document expected = parseSimpleXml();
        Document actual = parseSimpleXml();
        actual.getDocumentElement().removeChild(node(actual));
        AggregateXmlDiff diff = content.compare(expected, actual, content.createDefaultComparisonSettings());
        assertEquals(1, diff.getXmlDetails().size());
        XmlDiffDetail expectedDiff = DatabeneXmlUtil.missing(node(expected), "list element", "/root/node");
        XmlDiffDetail actualDiff = diff.getXmlDetails().get(0);
        assertEquals(expectedDiff, actualDiff);
    }

    @Test
    public void testDiff_missingElementTolerated() {
        Document expected = parseSimpleXml();
        Document actual = parseSimpleXml();
        actual.getDocumentElement().removeChild(node(actual));
        XmlComparisonSettings settings = content.createDefaultComparisonSettings();
        settings.tolerateMissingAt("/root/node");
        AggregateXmlDiff diff = content.compare(expected, actual, settings);
        assertTrue("Unexpected diff: " + diff, diff.isEmpty());
    }

    @Test
    public void testDiff_additionalElement() {
        Document expected = parseSimpleXml();
        Document actual = parseSimpleXml();
        Element node2 = actual.createElement("node2");
        actual.getDocumentElement().appendChild(node2);
        AggregateXmlDiff diff = content.compare(expected, actual, content.createDefaultComparisonSettings());
        assertEquals(1, diff.getXmlDetails().size());
        XmlDiffDetail expectedDiff = DatabeneXmlUtil.unexpected(node2, "list element", "/root/node2");
        XmlDiffDetail actualDiff = diff.getXmlDetails().get(0);
        assertEquals(expectedDiff, actualDiff);
    }

    @Test
    public void testDiff_additionalElementTolerated() {
        Document expected = parseSimpleXml();
        Document actual = parseSimpleXml();
        Element node2 = actual.createElement("node2");
        actual.getDocumentElement().appendChild(node2);
        XmlComparisonSettings settings = content.createDefaultComparisonSettings();
        settings.tolerateUnexpectedAt("/root/node2");
        AggregateXmlDiff diff = content.compare(expected, actual, settings);
        assertTrue("Unexpected diff", diff.isEmpty());
    }

    @Test
    public void testDiff_movedElement() throws Exception {
        Document expected = XMLUtil.parse(RESOURCE_PATH + "list_1_alice_2_bob.xml");
        Element alice = XMLUtil.getChildElements(expected.getDocumentElement(), false, "item")[0];
        Document actual = XMLUtil.parse(RESOURCE_PATH + "list_2_bob_1_alice.xml");
        AggregateXmlDiff diff = content.compare(expected, actual, content.createDefaultComparisonSettings());
        assertEquals(1, diff.getXmlDetails().size());
        XmlDiffDetail expectedDiff = DatabeneXmlUtil.moved(alice, "list element", "/list/item[1]", "/list/item[2]");
        XmlDiffDetail actualDiff = diff.getXmlDetails().get(0);
        assertEquals(expectedDiff, actualDiff);
    }

    @Test
    public void testDiff_movedElementTolerated() throws Exception {
        Document expected = XMLUtil.parse(RESOURCE_PATH + "list_1_alice_2_bob.xml");
        Document actual = XMLUtil.parse(RESOURCE_PATH + "list_2_bob_1_alice.xml");
        XmlComparisonSettings settings = content.createDefaultComparisonSettings();
        settings.tolerateMovedAt("/list/item");
        AggregateXmlDiff diff = content.compare(expected, actual, settings);
        assertTrue("Unexpected diff", diff.isEmpty());
    }

    // private helpers ---------------------------------------------------------

    private Document parseSimpleXml() {
        return XMLUtil.parseString(SIMPLE_XML);
    }

    private ByteArrayInputStream getSimpleXmlInputStream() throws UnsupportedEncodingException {
        return new ByteArrayInputStream(SIMPLE_XML.getBytes(Encodings.UTF_8));
    }

    private void verifySimpleDocument(Document doc) {
        assertEquals("UTF-8", doc.getXmlEncoding());
        Element rootElement = doc.getDocumentElement();
        assertEquals("root", rootElement.getNodeName());
        assertEquals("val", rootElement.getAttribute("att"));
        Element node = (Element) rootElement.getElementsByTagName("node").item(0);
        assertEquals("node", node.getNodeName());
        assertEquals("text", node.getTextContent());
    }

    private String att(Document doc) {
        return doc.getDocumentElement().getAttribute("att");
    }

    private Element node(Document doc) {
        Element rootElement = doc.getDocumentElement();
        return (Element) rootElement.getElementsByTagName("node").item(0);
    }

}
