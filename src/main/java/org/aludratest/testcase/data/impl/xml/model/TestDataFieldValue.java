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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

@XmlAccessorType(XmlAccessType.FIELD)
public class TestDataFieldValue {

    @XmlAttribute(name = "name", required = true)
    private String name;

    @XmlElements({ @XmlElement(name = "value", type = StringValue.class),
        @XmlElement(name = "stringValues", type = StringValueList.class) })
    private Object fieldValue;

    @XmlAttribute(name = "script")
    private boolean script;

    public String getName() {
        return name;
    }

    public Object getFieldValue() {
        return fieldValue;
    }

    public Object getFieldValueAsJavaType() {
        if (fieldValue instanceof StringValue) {
            return ((StringValue) fieldValue).getValue();
        }
        if (fieldValue instanceof StringValueList) {
            List<String> result = new ArrayList<String>();
            for (StringValue sv : ((StringValueList) fieldValue).getValues()) {
                result.add(sv.getValue());
            }
            return result;
        }
        return fieldValue;
    }

    public boolean isScript() {
        return script;
    }

}
