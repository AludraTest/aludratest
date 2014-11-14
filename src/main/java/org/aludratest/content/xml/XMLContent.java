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

import javax.xml.namespace.QName;

import org.aludratest.content.ContentHandler;
import org.aludratest.impl.log4testing.TechnicalArgument;
import org.aludratest.impl.log4testing.TechnicalLocator;
import org.w3c.dom.Document;

public interface XMLContent extends ContentHandler {

    /** Parses an XML document from a stream. */
    Document parse(InputStream in);

    /** Saves an XML document in a stream. */
    void save(Document document, String ecoding, OutputStream out);

    /** Performs an XPath query on an XML document. */
    Object queryXPath(
            Document document, 
            @TechnicalLocator String expression, 
            @TechnicalArgument QName returnType);

    /** Asserts that two XML documents are equal and throws a 
     *  {@link ValueNotAsExpectedException} if they are not. 
     *  @param expected the expected document
     *  @param actual the actual document */
    /*
    void assertEquals(Document expected, Document actual);
    */
    
    /** Checks if two XML documents are equal */
    /*
    public boolean isEqual(Document expected, Document actual);
    */

    /** Checks if two XML documents are equal ignoring elements 
     *  that match the provided xpath expressions. */
    /*
    public boolean isEqualExcludingXPaths(Document expected, Document actual, String... exclusionPaths);
    */

    /** Reports the differences between two XML documents. */
    /*
    public List<Diff<?>> diff(Document expected, Document actual);
    */
    /** Reports the differences between two XML documents 
     *  ignoring elementsthat match the provided xpath expressions. */
    /*
    public List<Diff<?>> diffExcludingXPaths(Document expected, Document actual, String... exclusionPaths);
    */
    
}
