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
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPathExpressionException;

import org.aludratest.content.ContentHandler;
import org.aludratest.content.xml.XMLContent;
import org.aludratest.exception.TechnicalException;
import org.aludratest.testcase.event.attachment.Attachment;
import org.aludratest.testcase.event.attachment.StringAttachment;
import org.databene.commons.xml.XMLUtil;
import org.databene.edifatto.Edifatto;
import org.w3c.dom.Document;

/**
 * XML {@link ContentHandler}.
 * @author Volker Bergmann
 */
public class XMLContentImpl implements XMLContent {

    private Document recentExpectedDocument;
    private Document recentActualDocument;

    public XMLContentImpl() {
        memorizeDocuments(null, null);
    }

    // Action interface implementation -----------------------------------------

    public List<Attachment> saveDebugAttachments() {
        List<Attachment> attachments = new ArrayList<Attachment>();
        if (this.recentExpectedDocument != null) {
            attachments.add(createAttachment("expected document", this.recentExpectedDocument));
        }
        if (this.recentActualDocument != null) {
            attachments.add(createAttachment("actual document", this.recentActualDocument));
        }
        return attachments;
    }

    // XMLInteraction interface implementation --------------------------------

    public Document parse(InputStream in) {
        try {
            Document document = XMLUtil.parse(in);
            memorizeDocuments(null, document);
            return document;
        } catch (IOException e) {
            throw new TechnicalException("Error reading XML document", e);
        }
    }

    public void save(Document document, String encoding, OutputStream out) {
        try {
            memorizeDocuments(null, document);
            XMLUtil.saveDocument(document, encoding, out);
        } catch (Exception e) {
            throw new TechnicalException("Error saving XML document", e);
        }
    }

    public Object queryXPath(Document document, String expression, QName returnType) {
        try {
            memorizeDocuments(null, document);
            return Edifatto.queryXML(document, expression, returnType);
        } catch (XPathExpressionException e) {
            throw new TechnicalException("Error querying XML", e);
        }
    }

    // XMLVerification interface implementation --------------------------------
    /*
    public void assertEquals(Document expected, Document actual) {
    	if (!isEqualExcludingXPaths(expected, actual)) {
    		throw new ValueNotAsExpectedException("Documents do not match");
    	}
    }
    
    public void assertEqualsExcludingXPaths(Document expected, Document actual, String... exclusionPaths) {
    	if (!isEqualExcludingXPaths(expected, actual, exclusionPaths)) {
    		throw new ValueNotAsExpectedException("Documents do not match");
    	}
    }
    
    // XMLCondition interface implementation -----------------------------------
    
    public boolean isEqual(Document expected, Document actual) {
    	return isEqualExcludingXPaths(expected, actual);
    }
    
    public boolean isEqualExcludingXPaths(Document expected, Document actual, String... exclusionPaths) {
    	try {
    		memorizeDocuments(expected, actual);
    		List<Diff<?>> diff = EdiChecker.diffExcludingXPaths(expected, actual, exclusionPaths);
    		return diff.size() > 0; 
    	} catch (XPathExpressionException e) {
    		throw new TechnicalException("Error comparing XML documents", e);
    	}
    }
    
    public List<Diff<?>> diff(Document expected, Document actual) {
    	return diffExcludingXPaths(expected, actual);
    }
    
    public List<Diff<?>> diffExcludingXPaths(Document expected, Document actual, String... exclusionPaths) {
    	try {
    		memorizeDocuments(expected, actual);
    		return EdiChecker.diffExcludingXPaths(expected, actual, exclusionPaths);
    	} catch (XPathExpressionException e) {
    		throw new TechnicalException("Error comparing XML documents", e);
    	}
    }
    */

    // private helpers ---------------------------------------------------------

    private void memorizeDocuments(Document expected, Document actual) {
        this.recentExpectedDocument = expected;
        this.recentActualDocument = actual;
    }

    private StringAttachment createAttachment(String title, Document document) {
        String text = XMLUtil.format(document.getDocumentElement());
        return new StringAttachment(title, text, "txt");
    }

}
