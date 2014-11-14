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
package org.aludratest.util;

import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Arrays;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.aludratest.exception.AutomationException;
import org.aludratest.service.locator.element.XPathLocator;
import org.databene.commons.ParseUtil;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

/**
 * Provides data related utility methods.
 * @author Volker Bergmann
 */
public class DataUtil {

    /** {@link Charset} instance for the UTF-8 character encoding */
    public static final Charset UTF_8 = Charset.forName("UTF-8");

    /** Private constructor for preventing instantiation of utility class. */
    private DataUtil() {
    }

    /** Parses a boolean value in a string ("tru" or "false").
     *  @param text the text to parse
     *  @return <code>true</code> or <code>false</code> */
    public static boolean parseBoolean(String text) {
        return ParseUtil.parseBoolean(text);
    }

    /**
     * Compares two string arrays
     * @param expectedStrings an array of the expected strings
     * @param actualStrings an array of the actual strings
     * @return a message of the differences or an empty string if the arrays are equal
     */
    public static String expectEqualArrays(Object[] expectedStrings, Object[] actualStrings) {
        // ENHANCE apply ArrayComparator to recognize missing or added elements
        if (actualStrings == null || expectedStrings == null) {
            return "No Values found for comparison. ";
        }
        if (!Arrays.equals(expectedStrings, actualStrings)) {
            return "Arrays do not match. Expected " + Arrays.toString(expectedStrings) + ", " +
                    "but found " + Arrays.toString(actualStrings) + ". ";
        }
        return "";
    }

    /**
     * Verifies that each expected string is found in the array of actual strings.
     * @param expectedStrings
     * @param actualStrings
     * @return a String with a diff message, or an empty string if all strings were contained.
     */
    public static String containsStrings(String[] expectedStrings, String[] actualStrings) {
        StringBuilder mismatches = new StringBuilder();
        StringBuilder actualString = new StringBuilder();
        String combinedActualString = null;
        for (int i = 0; i < actualStrings.length; i++) {
            actualString.append(actualStrings[i]).append(",");
        }
        combinedActualString = actualString.toString();
        for (int i = 0; i < expectedStrings.length; i++) {
            if (!(combinedActualString.contains(expectedStrings[i]))) {
                mismatches.append(expectedStrings[i]).append(" ");
            }
        }
        return mismatches.toString();
    }

    public static String containsString(String expectedString, String[] actualStrings) {
        boolean found = false;
        String actualStringUpperCase = null;
        String expectedStringUpperCase = expectedString.toUpperCase();
        for (int i = 0; i < actualStrings.length; i++) {
            actualStringUpperCase = actualStrings[i].toUpperCase();
            if (actualStringUpperCase.equals(expectedStringUpperCase)) {
                found = true;
                break;
            }
        }
        if (!found) {
            return expectedString;
        }
        return "";
    }

    private static final MostRecentUseCache.Factory<String, Document> docFactory = new MostRecentUseCache.Factory<String, Document>() {
        @Override
        public Document create(String html) {
            Tidy tidy = new Tidy();
            tidy.setQuiet(true);
            tidy.setShowWarnings(false);
            tidy.setShowErrors(0);
            tidy.setXmlOut(false);
            Document document = tidy.parseDOM(new StringReader(html), null);
            return document;
        }
    };

    /* Use an MRU cache for HTML -> DOM mappings. If executing several XPaths on same HTML, this increases performance
     * significantly. */
    private static final MostRecentUseCache<String, Document> docCache = new MostRecentUseCache<String, Document>(docFactory, 50);

    public static NodeList evalXPathInHTML(XPathLocator locator, String html) {
        return evalXPathInHTML(locator.toString(), html);
    }

    public static NodeList evalXPathInHTML(String xpath, String html) {
        try {
            Document document = docCache.get(html);
            XPathExpression expression = XPathFactory.newInstance().newXPath().compile(xpath);
            return (NodeList) expression.evaluate(document, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            throw new AutomationException("Illegal XPath: " + xpath, e);
        }
    }

}
