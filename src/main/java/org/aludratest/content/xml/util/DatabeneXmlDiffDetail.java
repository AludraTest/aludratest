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
package org.aludratest.content.xml.util;

import org.aludratest.content.xml.XmlDiffDetail;
import org.aludratest.content.xml.XmlDiffDetailType;
import org.databene.commons.converter.XMLNode2StringConverter;
import org.databene.formats.compare.DiffDetail;
import org.databene.formats.compare.DiffDetailType;

/** {@link XmlDiffDetail} implementation that wraps a {@link DiffDetail} instance from the Databene Formats library.
 * @author Volker Bergmann */
public class DatabeneXmlDiffDetail extends DiffDetail implements XmlDiffDetail {

    /** Full constructor.
     * @param expected the expected object
     * @param actual the actual object
     * @param objectClassifier the object classifier
     * @param type the diff detail type
     * @param locatorOfExpected the locator of the expected object
     * @param locatorOfActual the locator of the actual object */
    public DatabeneXmlDiffDetail(Object expected, Object actual, String objectClassifier, DiffDetailType type, String locatorOfExpected,
            String locatorOfActual) {
        super(expected, actual, objectClassifier, type, locatorOfExpected, locatorOfActual, new XMLNode2StringConverter());
    }

    @Override
    public XmlDiffDetailType getXmlDiffType() {
        return XmlDiffDetailType.valueOf(getType().name());
    }

}
