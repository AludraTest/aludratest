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

import java.util.List;

import org.aludratest.content.xml.AggregateXmlDiff;
import org.aludratest.content.xml.XmlComparisonSettings;
import org.aludratest.content.xml.XmlDiffDetail;
import org.databene.formats.compare.AggregateDiff;
import org.databene.formats.compare.ComparisonSettings;

/** {@link AggregateXmlDiff} implementation that wraps an AggregateDiff class from the Databene Formats library.
 * @author Volker Bergmann */
public class DatabeneAggregateXmlDiff extends AggregateDiff implements AggregateXmlDiff {

    /** Full constructor.
     * @param expected
     * @param actual
     * @param comparisonSettings */
    public DatabeneAggregateXmlDiff(Object expected, Object actual, XmlComparisonSettings comparisonSettings) {
        super(expected, actual, (ComparisonSettings) comparisonSettings);
    }

    @Override
    public XmlComparisonSettings getXmlComparisonSettings() {
        return (XmlComparisonSettings) getComparisonSettings();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public List<XmlDiffDetail> getXmlDetails() {
        return (List) getDetails();
    }

}
