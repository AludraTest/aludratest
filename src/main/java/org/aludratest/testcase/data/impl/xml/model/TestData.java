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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

@XmlRootElement(name = "testdata", namespace = "http://aludratest.org/testdata")
@XmlAccessorType(XmlAccessType.FIELD)
public class TestData {

    @XmlAttribute(name = "version", required = true)
    private String version;

    @XmlElement(namespace = "http://aludratest.org/testdata", name = "metadata", required = true)
    private TestDataMetadata metadata;

    @XmlElementWrapper(namespace = "http://aludratest.org/testdata", name = "configurations")
    @XmlElement(namespace = "http://aludratest.org/testdata", name = "configuration", type = TestDataConfiguration.class)
    private List<TestDataConfiguration> configurations;

    public TestDataMetadata getMetadata() {
        return metadata;
    }

    public List<TestDataConfiguration> getConfigurations() {
        return configurations;
    }

    public static TestData read(InputStream in) throws JAXBException {
        JAXBContext ctx = JAXBContext.newInstance(TestData.class);
        Unmarshaller marshaller = ctx.createUnmarshaller();
        return (TestData) marshaller.unmarshal(in);
    }

    private static void generateXmlSchema(JAXBContext ctx) throws IOException {
        SchemaOutputResolver resolver = new SchemaOutputResolver() {
            @Override
            public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
                File file = new File(suggestedFileName);
                StreamResult result = new StreamResult(file);
                result.setSystemId(file);
                return result;
            }
        };
        ctx.generateSchema(resolver);
    }

    public static void main(String[] args) throws JAXBException, IOException {
        JAXBContext ctx = JAXBContext.newInstance(TestData.class);
        generateXmlSchema(ctx);
    }
}
