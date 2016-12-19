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
package org.aludratest.testcase.data.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.aludratest.config.AludraTestConfig;
import org.aludratest.data.DataConfiguration;
import org.aludratest.exception.AutomationException;
import org.aludratest.exception.TechnicalException;
import org.aludratest.testcase.data.Source;
import org.aludratest.testcase.data.TestCaseData;
import org.aludratest.testcase.data.TestDataProvider;
import org.aludratest.testcase.data.impl.xml.ScriptLibrary;
import org.aludratest.testcase.data.impl.xml.XmlBasedTestDataProvider;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.util.ReflectionUtils;

/** A Test Data provider which checks referenced source data files. If they are all XML files, the XmlBasedTestDataProvider is
 * used. Otherwise, the DatabeneFormatsTestDataProvider is used.
 * 
 * @author falbrech */
public class DefaultTestDataProvider implements TestDataProvider {

    @Requirement
    private AludraTestConfig aludraConfig;

    @Requirement
    private DataConfiguration dataConfig;

    @Requirement(role = ScriptLibrary.class)
    private Map<String, ScriptLibrary> scriptLibraries;

    private DatabeneFormatsTestDataProvider databeneProvider = new DatabeneFormatsTestDataProvider();

    private XmlBasedTestDataProvider xmlProvider = new XmlBasedTestDataProvider();

    private boolean initialized;

    private void initProviders() {
        // inject values into providers; cannot use Requirement to get them because WE are the TestDataProvider
        try {
            ReflectionUtils.setVariableValueInObject(databeneProvider, "aludraConfig", aludraConfig);
            ReflectionUtils.setVariableValueInObject(databeneProvider, "dataConfig", dataConfig);
            ReflectionUtils.setVariableValueInObject(xmlProvider, "aludraConfig", aludraConfig);
            ReflectionUtils.setVariableValueInObject(xmlProvider, "scriptLibraries", scriptLibraries);
        }
        catch (IllegalAccessException e) {
            throw new TechnicalException("Could not set fields in providers using Reflection", e);
        }
    }

    @Override
    public List<TestCaseData> getTestDataSets(Method method) {
        if (!initialized) {
            initProviders();
            initialized = true;
        }

        if (method.getParameterTypes().length == 0) {
            // no-arg method
            return databeneProvider.getTestDataSets(method);
        }

        Set<String> extensions = extractExtensions(method);

        // extensions may contain at max two entries (xls and xlsx)
        boolean xml = extensions.contains("xml");
        boolean xls = extensions.contains("xls") || extensions.contains("xlsx");
        if (extensions.size() > 2 || (xml && xls) || (xml && extensions.size() > 1)) {
            throw new AutomationException(
                    "Cannot combine test data sources of different types (e.g. Excel and XML) within the same test method (" + method + ")");
        }
        if (!xml && !xls) {
            throw new AutomationException("Unknown file type for data source for test method " + method);
        }

        if (xml) {
            return xmlProvider.getTestDataSets(method);
        }
        else {
            return databeneProvider.getTestDataSets(method);
        }
    }

    private Set<String> extractExtensions(Method method) {
        // analyze annotations; select provider based on extension
        Set<String> extensions = new HashSet<String>();
        Annotation[][] annos = method.getParameterAnnotations();
        for (int i = 0; i < annos.length; i++) {
            Source src = getSourceAnnotation(annos[i]);
            if (src != null) {
                String uri = src.uri();
                if (uri != null && uri.contains(".")) {
                    extensions.add(uri.substring(uri.lastIndexOf('.') + 1).toLowerCase(Locale.US));
                }
            }
        }
        return extensions;
    }

    private Source getSourceAnnotation(Annotation[] annotations) {
        for (Annotation anno : annotations) {
            if (anno instanceof Source) {
                return (Source) anno;
            }
        }
        return null;
    }


}
