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
package org.aludratest.service.edifactfile.edifatto;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPathConstants;

import org.aludratest.content.edifact.EdifactContent;
import org.aludratest.content.xml.AggregateXmlDiff;
import org.aludratest.content.xml.XmlComparisonSettings;
import org.aludratest.content.xml.XmlDiffDetail;
import org.aludratest.content.xml.util.DatabeneXmlComparisonSettings;
import org.aludratest.exception.FunctionalFailure;
import org.aludratest.exception.TechnicalException;
import org.aludratest.service.SystemConnector;
import org.aludratest.service.edifactfile.EdifactFileCondition;
import org.aludratest.service.edifactfile.EdifactFileInteraction;
import org.aludratest.service.edifactfile.EdifactFileVerification;
import org.aludratest.service.file.FileService;
import org.aludratest.testcase.event.attachment.Attachment;
import org.aludratest.testcase.event.attachment.StringAttachment;
import org.databene.commons.SystemInfo;
import org.databene.edifatto.EdiFormatSymbols;
import org.databene.edifatto.compare.HTMLDiffFormatter;
import org.databene.edifatto.format.StandardInterchangeFormatter;
import org.databene.edifatto.format.TextTreeInterchangeFormatter;
import org.databene.edifatto.model.Interchange;
import org.databene.formats.compare.AggregateDiff;
import org.databene.formats.xml.compare.DefaultXMLComparisonModel;

/**
 * Action class for {@link EdifattoFileService}, implementing all Edifact action interfaces.
 * @author Volker Bergmann
 */
public class EdifattoFileAction implements EdifactFileInteraction, EdifactFileVerification, EdifactFileCondition {

    /** A reference to the underlying FileService. */
    private FileService fileService;

    EdifactContent contentHandler;

    /** The recently expected interchange to be stored as attachment if requested. */
    private Interchange recentExpectedInterchange;

    /** The recent actual interchange to be stored as attachment if requested. */
    private Interchange recentActualInterchange;

    private AggregateDiff recentDiff;

    /** Constructor.
     *  @param contentHandler
     *  @param fileService */
    public EdifattoFileAction(EdifactContent contentHandler, FileService fileService) {
        this.contentHandler = contentHandler;
        this.fileService = fileService;
        memorizeInterchanges(null, null, null);
    }

    @Override
    public void setSystemConnector(SystemConnector systemConnector) {
        // empty implementation
    }

    // general file operations -------------------------------------------------

    @Override
    public boolean exists(String elementType, String elementName, String filePath) {
        return fileService.check().exists(filePath);
    }

    @Override
    public void delete(String elementType, String elementName, String filePath) {
        fileService.perform().delete(filePath);
    }

    @Override
    public void waitUntilExists(String elementType, String elementName, String filePath) {
        fileService.perform().waitUntilExists(elementType, filePath);
    }

    @Override
    public void waitUntilNotExists(String elementType, String elementName, String filePath) {
        fileService.perform().waitUntilNotExists(filePath);
    }

    // EdifactInteraction interface implementation -----------------------------

    /** Writes an EDIFACT or X12 interchange to an {@link OutputStream}. */
    @Override
    public void writeInterchange(String elementType, String elementName, Interchange interchange, String filePath, boolean overwrite) {
        memorizeInterchanges(null, null, null);
        String content = toString(interchange, false);
        fileService.perform().writeBinaryFile(filePath, content.getBytes(), overwrite); // ENHANCE which encoding to use?
    }

    @Override
    public Interchange readInterchange(String elementType, String elementName, String filePath) {
        String content = fileService.perform().readTextFile(filePath);
        ByteArrayInputStream in = new ByteArrayInputStream(content.getBytes()); // ENHANCE which encoding to use?
        Interchange interchange = contentHandler.readInterchange(in);
        memorizeInterchanges(null, interchange, null);
        return interchange;
    }

    @Override
    public Interchange createInterchange(String elementType, String elementName, String templateUri, EdiFormatSymbols symbols, Map<String, Object> variables) {
        Interchange interchange = contentHandler.createInterchange(templateUri, symbols, variables);
        memorizeInterchanges(null, interchange, null);
        return interchange;
    }

    // EdifactVerification interface implementation ----------------------------

    /** Asserts that two EDIFACT or X12 interchanges are equal.
     *  @throws ValueNotAsExpectedException if they are not equal. */
    @Override
    public void assertInterchangesMatch(String elementType, String elementName,
            Interchange expected, Interchange actual,
            XmlComparisonSettings settings) {
        AggregateXmlDiff diffs = contentHandler.compare(expected, actual, settings);
        memorizeInterchanges(expected, actual, diffs);
        int detailCount = diffs.getXmlDetails().size();
        if (detailCount > 0) {
            String lf = SystemInfo.getLineSeparator();
            StringBuilder message = new StringBuilder("Interchanges do not match. Found " + detailCount + " difference");
            if (detailCount > 1) {
                message.append("s");
            }
            for (XmlDiffDetail diff : diffs.getXmlDetails()) {
                message.append(lf).append(diff);
            }
            throw new FunctionalFailure(message.toString());
        }
    }

    // EdifactCondition interface implementation -------------------------------

    /**
     * Creates an XML representation of the interchange and performs an XPath query on it.
     * @param interchange the interchange to query
     * @param expression the XPath query to perform
     * @param returnType determines the type of the returned object:
     *   {@link XPathConstants#STRING} for a single {@link java.lang.String},
     *   {@link XPathConstants#NODE} for a single {@link org.w3c.dom.Element},
     *   {@link XPathConstants#NODESET} for a {@link org.w3c.dom.NodeList}
     */
    @Override
    public Object queryXML(String elementType, String elementName, Interchange interchange, String expression, QName returnType) {
        memorizeInterchanges(null, interchange, null);
        return contentHandler.queryXML(interchange, expression, returnType);
    }

    @Override
    public XmlComparisonSettings createDefaultComparisonSettings() {
        return new DatabeneXmlComparisonSettings(new DefaultXMLComparisonModel());
    }

    /** Finds out the differences between two EDIFACT or X12 interchanges,
     *  ignoring elements that match the XPath exclusion paths.
     *  @param elementType
     *  @param elementName
     *  @param expected
     *  @param actual
     *  @param settings
     *  @param model
     *  @return an {@link AggregateDiff} of the interchanges */
    @Override
    public AggregateXmlDiff diff(String elementType, String elementName, Interchange expected, Interchange actual,
            XmlComparisonSettings settings) {
        try {
            memorizeInterchanges(expected, actual, null);
            return contentHandler.compare(expected, actual, settings);
        } catch (Exception e) {
            throw new TechnicalException("Error comparing Edifact interchanges", e);
        }
    }

    // Action interface implementation -----------------------------------------

    /** Saves the XML document(s) used in the most recent invocation as attachment */
    @Override
    public List<Attachment> createDebugAttachments() {
        List<Attachment> attachments = new ArrayList<Attachment>();
        if (this.recentExpectedInterchange != null) {
            attachments.add(createAttachment("expected interchange", this.recentExpectedInterchange));
        }
        if (this.recentActualInterchange != null) {
            attachments.add(createAttachment("actual interchange", this.recentActualInterchange));
        }
        if (this.recentDiff != null) {
            attachments.add(createDiffAttachment("diff", this.recentDiff));
        }
        return attachments;
    }

    @Override
    public List<Attachment> createAttachments(Object object, String label) {
        if (object instanceof Interchange) {
            List<Attachment> attachments = new ArrayList<Attachment>();
            Interchange interchange = (Interchange) object;
            attachments.add(new StringAttachment(label + " as raw data", toString(interchange, false), "edi"));
            attachments.add(new StringAttachment(label + " as text", toString(interchange, true), "txt"));
            attachments.add(new StringAttachment(label + " as text tree", new TextTreeInterchangeFormatter(false, "\n").format(interchange), "txt"));
            return attachments;
        } else {
            throw new TechnicalException("Not a supported type: " + object);
        }
    }

    // private helpers ---------------------------------------------------------

    /** saves the most recently used interchange(s)
     * in order to provide them as attachment on request. */
    private void memorizeInterchanges(Interchange expected, Interchange actual, AggregateXmlDiff diff) {
        this.recentExpectedInterchange = expected;
        this.recentActualInterchange = actual;
        this.recentDiff = (AggregateDiff) diff;
    }

    /** Creates an attachment that contains the provides EDIFACT or X12 interchange. */
    private StringAttachment createAttachment(String title, Interchange interchange) {
        String text = contentHandler.formatRecursively(interchange);
        return new StringAttachment(title, text, "txt");
    }

    private StringAttachment createDiffAttachment(String title, AggregateDiff diff) {
        String cssPath = "org/databene/edifatto/edifatto-gui.css";
        String text = new HTMLDiffFormatter(cssPath).formatDiffAsHtml(diff);
        return new StringAttachment(title, text, "html");
    }

    private String toString(Interchange interchange, boolean linefeed) {
        return new StandardInterchangeFormatter(linefeed ? "\n" : null).format(interchange);
    }

}
