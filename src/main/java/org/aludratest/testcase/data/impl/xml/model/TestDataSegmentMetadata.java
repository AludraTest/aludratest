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

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

@XmlAccessorType(XmlAccessType.FIELD)
public class TestDataSegmentMetadata {

    @XmlAttribute(name = "name", required = true)
    private String name;

    @XmlAttribute(name = "dataClassName", required = true)
    private String dataClassName;

    @XmlElementWrapper(name = "fields")
    @XmlElement(name = "field", type = TestDataFieldMetadata.class)
    private List<TestDataFieldMetadata> fields;

    public String getName() {
        return name;
    }

    public String getDataClassName() {
        return dataClassName;
    }

    public List<TestDataFieldMetadata> getFields() {
        return fields;
    }

}
