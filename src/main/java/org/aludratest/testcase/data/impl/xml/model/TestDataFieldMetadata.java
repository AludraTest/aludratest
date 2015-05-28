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
package org.aludratest.testcase.data.impl.xml.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "http://aludratest.org/testdata")
public class TestDataFieldMetadata {

    @XmlAttribute(name = "name", required = true)
    private String name;

    @XmlAttribute(name = "fieldType")
    private TestDataFieldType type;

    @XmlAttribute(name = "subTypeClassName")
    private String subTypeClassName;

    @XmlAttribute(name = "formatterPattern")
    private String formatterPattern;

    @XmlAttribute(name = "formatterLocale")
    private String formatterLocale;

    public String getName() {
        return name;
    }

    public TestDataFieldType getType() {
        return type == null ? TestDataFieldType.STRING : type;
    }

    public String getSubTypeClassName() {
        return subTypeClassName;
    }

    public String getFormatterLocale() {
        return formatterLocale;
    }

    public String getFormatterPattern() {
        return formatterPattern;
    }


}
