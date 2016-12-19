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

import org.aludratest.content.xml.AggregateXmlDiff;
import org.aludratest.content.xml.XmlDiffDetail;
import org.databene.formats.compare.AggregateDiff;
import org.databene.formats.compare.DiffDetail;
import org.databene.formats.compare.DiffDetailType;

/** Provides utility methods for converting XML comparison results between the representation of the library Databene Formats and
 * the classes used in the AludraTest interface.
 * @author Volker Bergmann */
public class DatabeneXmlUtil {

    /** Private constructor for preventing instantiation of this utility class. */
    private DatabeneXmlUtil() {
        // Private constructor for preventing instantiation of this utility class.
    }

    /** Creates an {@link XmlDiffDetail} that represents a missing element.
     * @param expected the expected element
     * @param objectClassifier the object classifier
     * @param locatorOfExpected the locator of the expected element
     * @return an {@link XmlDiffDetail} that represents a missing element */
    public static XmlDiffDetail missing(Object expected, String objectClassifier, String locatorOfExpected) {
        return new DatabeneXmlDiffDetail(expected, null, objectClassifier, DiffDetailType.MISSING, locatorOfExpected, null);
    }

    /** Creates an {@link XmlDiffDetail} that represents an actual element which was not expected.
     * @param actual the actual element
     * @param objectClassifier the object classifier
     * @param locatorOfActual the locator of the actual element
     * @return an {@link XmlDiffDetail} that represents an actual element which was not expected */
    public static XmlDiffDetail unexpected(Object actual, String objectClassifier, String locatorOfActual) {
        return new DatabeneXmlDiffDetail(null, actual, objectClassifier, DiffDetailType.UNEXPECTED, null, locatorOfActual);
    }

    /** Creates an {@link XmlDiffDetail} that represents an element at an unexpected place.
     * @param actual the actual element
     * @param objectClassifier the object classifier
     * @param locatorOfExpected the locator of the expected element
     * @param locatorOfActual the locator of the actual element
     * @return an {@link XmlDiffDetail} that represents an element at an unexpected place */
    public static XmlDiffDetail moved(Object actual, String objectClassifier, String locatorOfExpected, String locatorOfActual) {
        return new DatabeneXmlDiffDetail(actual, actual, objectClassifier, DiffDetailType.MOVED, locatorOfExpected, locatorOfActual);
    }

    /** Creates an {@link XmlDiffDetail} that represents different values or contents between an expected and an actual element.
     * @param expected the expected element
     * @param actual the actual element
     * @param objectClassifier the object classifier
     * @param locatorOfExpected the locator of the expected element
     * @param locatorOfActual the locator of the actual element
     * @return an {@link XmlDiffDetail} that represents different values or contents between an expected and an actual element */
    public static XmlDiffDetail different(Object expected, Object actual, String objectClassifier, String locatorOfExpected,
            String locatorOfActual) {
        return new DatabeneXmlDiffDetail(expected, actual, objectClassifier, DiffDetailType.DIFFERENT, locatorOfExpected,
                locatorOfActual);
    }

    /** Converts an {@link AggregateDiff} instance from the Databene Formats library to an {@link AggregateXmlDiff} for the
     * AludraTest XML content handler.
     * @param diff the diff to convert
     * @return an {@link AggregateXmlDiff} representation of the provided diff */
    public static AggregateXmlDiff toXmlDiff(AggregateDiff diff) {
        DatabeneAggregateXmlDiff xdiff = new DatabeneAggregateXmlDiff(diff.getExpected(), diff.getActual(),
                (DatabeneXmlComparisonSettings) diff.getComparisonSettings());
        for (DiffDetail detail : diff.getDetails()) {
            DatabeneXmlDiffDetail xmlDetail = new DatabeneXmlDiffDetail(detail.getExpected(), detail.getActual(),
                    detail.getObjectClassifier(), detail.getType(), detail.getLocatorOfExpected(),
                    detail.getLocatorOfActual());
            xdiff.addDetail(xmlDetail);
        }
        return xdiff;
    }

}
