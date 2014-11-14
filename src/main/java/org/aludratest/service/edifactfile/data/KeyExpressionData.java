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
package org.aludratest.service.edifactfile.data;

import org.aludratest.dict.Data;

/**
 * Provides an XPath expression for querying an ID string of an XML element.
 * @author Volker Bergmann
 */
public class KeyExpressionData extends Data {

    /** The element name */
    private String elementName;

    /** The Xpath expression (relative to the element) by which to determine the ID. */
    private String keyExpression;

    /** Public default constructor. */
    public KeyExpressionData() {
        this(null, null);
    }

    /** Constructor with all property values.
     *  @param elementName
     *  @param keyExpression */
    public KeyExpressionData(String elementName, String keyExpression) {
        this.elementName = elementName;
        this.keyExpression = keyExpression;
    }

    /** @return the {@link #elementName} */
    public String getElementName() {
        return elementName;
    }

    /** Sets the {@link #elementName}.
     *  @param elementName */
    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    /** @return the {@link #keyExpression} */
    public String getKeyExpression() {
        return keyExpression;
    }

    /** Sets the {@link #keyExpression}.
     *  @param keyExpression the keyExpression to set */
    public void setKeyExpression(String keyExpression) {
        this.keyExpression = keyExpression;
    }

    @Override
    public String toString() {
        return elementName + ":" + keyExpression;
    }

}
