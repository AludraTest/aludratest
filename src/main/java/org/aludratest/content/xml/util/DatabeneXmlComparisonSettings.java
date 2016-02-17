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

import org.aludratest.content.xml.XmlComparisonSettings;
import org.aludratest.content.xml.XmlDiffDetailType;
import org.databene.formats.compare.DiffDetailType;
import org.databene.formats.xml.compare.DefaultXMLComparisonModel;
import org.databene.formats.xml.compare.XMLComparisonModel;

/** Implementation of the {@link XmlComparisonSettings} interface for the Databene Formats library.
 * @author Volker Bergmann */
public class DatabeneXmlComparisonSettings extends org.databene.formats.xml.compare.XMLComparisonSettings implements
XmlComparisonSettings {

    /** Default constructor which uses the {@link DefaultXMLComparisonModel}. */
    public DatabeneXmlComparisonSettings() {
        this(new DefaultXMLComparisonModel());
    }

    /** Constructor that takes an {@link XMLComparisonModel}.
     * @param model the model to use in comparisons */
    public DatabeneXmlComparisonSettings(XMLComparisonModel model) {
        super(model);
    }

    @Override
    public void tolerateGenericDiff(XmlDiffDetailType type, String xPath) {
        super.tolerateGenericDiff(DiffDetailType.valueOf(type.name()), xPath);
    }

}
