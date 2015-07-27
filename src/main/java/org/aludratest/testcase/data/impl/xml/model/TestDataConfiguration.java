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
public class TestDataConfiguration {

    @XmlAttribute(name = "name", required = true)
    private String name;

    @XmlAttribute(name = "ignored")
    private Boolean ignored;

    @XmlAttribute(name = "ignoredReason")
    private String ignoredReason;

    @XmlElementWrapper(namespace = "http://aludratest.org/testdata", name = "segments")
    @XmlElement(namespace = "http://aludratest.org/testdata", name = "segment", type = TestDataConfigurationSegment.class)
    private List<TestDataConfigurationSegment> segments;

    public List<TestDataConfigurationSegment> getSegments() {
        return segments;
    }

    public String getName() {
        return name;
    }

    public boolean isIgnored() {
        return ignored == null ? false : ignored.booleanValue();
    }

    public String getIgnoredReason() {
        return ignoredReason;
    }

}
