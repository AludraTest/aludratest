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

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.aludratest.config.AludraTestConfig;
import org.aludratest.data.DataConfiguration;
import org.aludratest.dict.Data;
import org.aludratest.exception.AutomationException;
import org.aludratest.testcase.Offset;
import org.aludratest.testcase.data.Source;
import org.aludratest.testcase.data.TestCaseData;
import org.aludratest.testcase.data.TestDataProvider;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.codehaus.plexus.component.annotations.Requirement;
import org.databene.commons.StringUtil;
import org.databene.commons.SystemInfo;
import org.databene.commons.converter.util.ConstantClassProvider;
import org.databene.formats.DataContainer;
import org.databene.formats.DataIterator;
import org.databene.formats.util.OffsetDataIterator;
import org.databene.formats.xls.XLSJavaBeanIterator;

/**
 * TestDataProvider implementation based on Databene Formats.
 * @author Volker Bergmann
 */
public class DatabeneFormatsTestDataProvider implements TestDataProvider {

    private String basePath;

    @Requirement
    private AludraTestConfig aludraConfig;

    @Requirement
    private DataConfiguration dataConfig;

    // TestDataProvider interface implementation -------------------------------

    /** Called by the framework to create and initialize the data provider. Do not call this constructor directly. Use
     * AludraServiceManager to retrieve a TestDataProvider. */
    public DatabeneFormatsTestDataProvider() {
        this.basePath = SystemInfo.getCurrentDir();
    }

    /** Reads the data source and returns its data sets
     *  as a {@link List} of object arrays. */
    @Override
    public List<TestCaseData> getTestDataSets(Method method) {
        if (method.getParameterTypes().length == 0) {
            // unparameterized method
            return createTestDataForUnparameterizedMethod();
        } else {
            return createTestDataForParameterizedMethod(method);
        }
    }


    // private helper methods --------------------------------------------------

    private List<TestCaseData> createTestDataForUnparameterizedMethod() {
        return Collections.singletonList(new TestCaseData("0", null, null));
    }

    private List<TestCaseData> createTestDataForParameterizedMethod(Method method) {
        try {
            // iterate parameters and retrieve their data values
            Class<?>[] parameterTypes = method.getParameterTypes();
            int paramCount = parameterTypes.length;
            Annotation[][] paramsAnnos = method.getParameterAnnotations();
            List<List<Data>> paramValueLists = new ArrayList<List<Data>>();
            Class<?> testClass = method.getDeclaringClass();
            int minDataSetCount = -1;
            Offset offsetAnno = method.getAnnotation(Offset.class);
            for (int i = 0; i < paramCount; i++) {
                String paramName = method.getName() + " param #" + i;
                Source sourceAnno = getRequiredSourceAnnotation(paramsAnnos[i], paramName);
                List<Data> paramValues;
                try {
                    paramValues = parseValuesForParam(sourceAnno, offsetAnno, parameterTypes[i], testClass);
                }
                catch (ArrayIndexOutOfBoundsException ae) {
                    throw new AutomationException("Error when parsing values for parameter " + paramName, ae);
                }
                paramValueLists.add(paramValues);
                int dataSetCount = paramValues.size();
                if (minDataSetCount < 0) {
                    minDataSetCount = dataSetCount;
                } else {
                    minDataSetCount = Math.min(minDataSetCount, dataSetCount);
                }
            }

            // verify data
            if (minDataSetCount <= 0) {
                throw new AutomationException("No data sets defined for method " + method);
            }

            // map the data values to a list of TestCaseData
            List<TestCaseData> dataSets = new ArrayList<TestCaseData>(minDataSetCount);
            TestConfigInfoHelper helper = new TestConfigInfoHelper(aludraConfig);
            List<TestDataLoadInfo> testInfos = helper.testInfos(method, minDataSetCount);
            for (int iSet = 0; iSet < testInfos.size(); iSet++) {
                Data[] args = new Data[paramCount];
                for (int iParam = 0; iParam < paramCount; iParam++) {
                    if (iSet < paramValueLists.get(iParam).size()) {
                        args[iParam] = paramValueLists.get(iParam).get(iSet);
                    }
                }

                TestDataLoadInfo ti = testInfos.get(iSet);
                Object info = ti.getInfo();
                if (info instanceof Throwable) {
                    dataSets.add(new TestCaseData(getNextAutoId(dataSets, true), (Throwable) info));
                }
                else {
                    dataSets.add(new TestCaseData(info == null ? getNextAutoId(dataSets, false) : info.toString(), null, args, ti
                            .isIgnored()));
                }
            }
            return dataSets;
        } catch (Exception e) {
            if (e instanceof AutomationException) {
                throw (AutomationException) e;
            } else {
                throw new AutomationException("Error creating test parameters: " + e, e);
            }
        }
    }

    private String getNextAutoId(List<TestCaseData> dataSets, boolean error) {
        String prefix = error ? "error-" : "";
        int nextAutoId = dataSets.size();
        boolean found;
        do {
            found = false;
            for (TestCaseData tcd : dataSets) {
                if (tcd.getId().equals(prefix + nextAutoId)) {
                    found = true;
                    break;
                }
            }
            if (found) {
                nextAutoId++;
            }
        }
        while (found);
        return prefix + nextAutoId;
    }

    private Source getRequiredSourceAnnotation(Annotation[] annotations, String parameterName) {
        for (Annotation anno : annotations) {
            if (anno instanceof Source) {
                return (Source) anno;
            }
        }
        throw new AutomationException("Parameter does not have a @Source annotation: " + parameterName);
    }

    private List<Data> parseValuesForParam(Source sourceAnno, Offset offsetAnno,
            Class<?> paramClass, Class<?> testClass)
                    throws InvalidFormatException, IOException {
        // check URL
        String uri = sourceAnno.uri();
        if (!uri.toLowerCase(Locale.US).endsWith("xls") && !uri.toLowerCase(Locale.US).endsWith("xlsx")) {
            throw new UnsupportedOperationException("Not a supported file format: " + uri);
        }

        // check segment
        String segment = sourceAnno.segment();
        if (StringUtil.isEmpty(segment)) {
            throw new UnsupportedOperationException("No segment specified: " + segment);
        }

        // check offset
        int offset = (offsetAnno != null ? offsetAnno.value() : 0);

        // create iterator
        uri = getPathFor(uri, testClass);
        DataIterator<Object> iterator = new XLSJavaBeanIterator(uri, segment, true, null, null,
                new ConstantClassProvider<Object>(paramClass));
        if (offset > 0) {
            iterator = new OffsetDataIterator<Object>(iterator, offset);
        }

        // iterate rows and collect values
        DataContainer<Object> wrapper = new DataContainer<Object>();
        List<Data> values = new ArrayList<Data>();
        while (iterator.next(wrapper) != null) {
            Object o = wrapper.getData();
            if (o != null && !(o instanceof Data)) {
                throw new AutomationException("Invalid parameter type for test method: " + o.getClass().getName());
            }
            values.add((Data) wrapper.getData());
        }
        if (values.size() == 0) {
            throw new AutomationException("Empty sheet '" + segment + "' in file " + uri);
        }
        return values;
    }

    private String getPathFor(String uri, Class<?> testClass) {
        char sep = File.separatorChar;
        return basePath + sep + aludraConfig.getXlsRootPath() + sep + testClass.getName().replace('.', sep)
                + sep + uri;
    }

}
