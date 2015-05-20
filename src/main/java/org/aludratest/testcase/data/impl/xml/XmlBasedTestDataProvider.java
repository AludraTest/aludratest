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
package org.aludratest.testcase.data.impl.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.aludratest.config.AludraTestConfig;
import org.aludratest.dict.Data;
import org.aludratest.exception.AutomationException;
import org.aludratest.testcase.Offset;
import org.aludratest.testcase.data.Source;
import org.aludratest.testcase.data.TestCaseData;
import org.aludratest.testcase.data.TestDataProvider;
import org.aludratest.testcase.data.impl.xml.model.TestData;
import org.aludratest.testcase.data.impl.xml.model.TestDataConfiguration;
import org.aludratest.testcase.data.impl.xml.model.TestDataConfigurationSegment;
import org.aludratest.testcase.data.impl.xml.model.TestDataFieldMetadata;
import org.aludratest.testcase.data.impl.xml.model.TestDataFieldType;
import org.aludratest.testcase.data.impl.xml.model.TestDataFieldValue;
import org.aludratest.testcase.data.impl.xml.model.TestDataSegmentMetadata;
import org.apache.commons.io.IOUtils;
import org.codehaus.plexus.component.annotations.Requirement;
import org.databene.commons.BeanUtil;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class XmlBasedTestDataProvider implements TestDataProvider {

    @Requirement
    private AludraTestConfig aludraConfig;

    @Requirement(role = ScriptLibrary.class)
    private Map<String, ScriptLibrary> scriptLibraries = new HashMap<String, ScriptLibrary>();

    @Override
    public List<TestCaseData> getTestDataSets(Method method) {
        // get @Source annotated parameters
        Annotation[][] annots = method.getParameterAnnotations();

        Offset offsetAnno = method.getAnnotation(Offset.class);

        List<TestCaseData> result = new ArrayList<TestCaseData>();

        // cache for loaded file models
        Map<String, TestData> loadedFileModels = new HashMap<String, TestData>();

        // load param by param; transpose into test case data afterwards
        List<List<Data>> allData = new ArrayList<List<Data>>();
        for (int i = 0; i < annots.length; i++) {
            List<Data> paramData = getDataObjects(method, i, loadedFileModels, result);
            if (offsetAnno != null) {
                int offset = offsetAnno.value();
                if (offset < paramData.size()) {
                    paramData = paramData.subList(offset, paramData.size());
                }
                else {
                    paramData = Collections.emptyList();
                }
            }
            allData.add(paramData);
        }

        // transposition with the help of first loaded XML file
        Source firstSource = getRequiredSourceAnnotation(annots[0], "first parameter");
        TestData data = loadedFileModels.get(firstSource.uri());

        List<TestDataConfiguration> configs = data.getConfigurations();
        for (int i = 0; i < configs.size(); i++) {
            TestDataConfiguration config = configs.get(i);

            List<Data> dataForConfig = new ArrayList<Data>();
            // ensure that all data lists contain enough entries
            for (List<Data> ls : allData) {
                if (ls.size() <= i) {
                    result.add(new TestCaseData(getNextAutoId(result, false), new AutomationException("For method " + method
                            + ", not all referenced XML files contain the same amount of test configurations.")));
                    dataForConfig = null;
                    break;
                }
                else {
                    dataForConfig.add(ls.get(i));
                }
            }

            if (dataForConfig != null) {
                result.add(new TestCaseData(getNextAutoId(result, false), config.getName(), dataForConfig.toArray(new Data[0]),
                        config.isIgnored()));
            }
        }

        return result;
    }

    private List<Data> getDataObjects(Method method, int paramIndex, Map<String, TestData> loadedFileModels,
            List<TestCaseData> allTestCaseDatas) {
        Annotation[][] annots = method.getParameterAnnotations();
        String paramName = method.getName() + " param #" + paramIndex;
        Source src = getRequiredSourceAnnotation(annots[paramIndex], paramName);

        // try to load the referenced file, or reuse from cache
        String uri = src.uri();
        if (uri == null || "".equals(uri)) {
            throw new AutomationException("@Source annotation does not specify required uri parameter");
        }
        if (src.segment() == null || "".equals(src.segment())) {
            throw new AutomationException("@Source annotation does not specify required segment parameter");
        }

        TestData testData;
        if (loadedFileModels.containsKey(uri)) {
            testData = loadedFileModels.get(uri);
        }
        else {
            InputStream in = null;
            try {
                // real URI?
                in = tryFindXml(uri, method);
                testData = TestData.read(in);
                // some base validations
                if (testData.getMetadata() == null || testData.getMetadata().getSegments() == null
                        || testData.getConfigurations() == null) {
                    throw new AutomationException("Test data XML " + uri + " has an invalid format or is incomplete.");
                }
                loadedFileModels.put(uri, testData);
            }
            catch (Exception e) {
                throw new AutomationException("Could not read test data XML", e);
            }
            finally {
                IOUtils.closeQuietly(in);
            }
        }

        List<Data> dataElements = new ArrayList<Data>();

        // get metadata for requested segment
        TestDataSegmentMetadata segmentMeta = null;
        for (TestDataSegmentMetadata segment : testData.getMetadata().getSegments()) {
            if (segment.getName().equals(src.segment())) {
                segmentMeta = segment;
                break;
            }
        }
        if (segmentMeta == null) {
            throw new AutomationException("Could not find segment " + src.segment() + " in XML file " + uri);
        }

        // for each configuration entry, find values
        for (TestDataConfiguration config : testData.getConfigurations()) {
            if (containsSegment(config, segmentMeta.getName())) {
                dataElements.add(buildObject(testData, config, segmentMeta.getName()));
            }
            else {
                dataElements.add(null);
            }
        }

        return dataElements;
    }

    private TestDataSegmentMetadata getSegmentMetadata(TestData testData, String segmentName) {
        for (TestDataSegmentMetadata segment : testData.getMetadata().getSegments()) {
            if (segmentName.equals(segment.getName())) {
                return segment;
            }
        }
        return null;
    }

    private Object getFieldValue(TestDataConfiguration configuration, String segmentName, TestDataFieldMetadata fieldMeta) {
        String fieldName = fieldMeta.getName();
        for (TestDataConfigurationSegment segment : configuration.getSegments()) {
            if (segmentName.equals(segment.getName())) {
                for (TestDataFieldValue field : segment.getFieldValues()) {
                    if (fieldName.equals(field.getName())) {
                        Object value = field.getFieldValueAsJavaType();
                        if (field.isScript() && (value instanceof String)) {
                            value = evaluate(value.toString(), fieldMeta.getFormatterPattern(),
                                    toLocale(fieldMeta.getFormatterLocale()));
                        }
                        return value;
                    }
                }
            }
        }
        return null;
    }

    private boolean containsSegment(TestDataConfiguration configuration, String segmentName) {
        for (TestDataConfigurationSegment segment : configuration.getSegments()) {
            if (segmentName.equals(segment.getName())) {
                return true;
            }
        }
        return false;
    }

    private Data buildObject(TestData testData, TestDataConfiguration configuration, String segmentName) {
        TestDataSegmentMetadata segmentMeta = getSegmentMetadata(testData, segmentName);
        if (segmentMeta == null) {
            throw new AutomationException("Segment " + segmentName + " is not defined in test data XML metadata.");
        }

        if (!containsSegment(configuration, segmentName)) {
            return null;
        }

        String dcn = segmentMeta.getDataClassName();

        try {
            @SuppressWarnings("unchecked")
            Class<? extends Data> clazz = (Class<? extends Data>) Class.forName(dcn);
            Data data = clazz.newInstance();

            // populate data
            for (TestDataFieldMetadata field : segmentMeta.getFields()) {
                Object value;
                // if field is reference to another segment or segments, recurse into object creation
                if (field.getType() == TestDataFieldType.OBJECT) {
                    value = buildObject(testData, configuration, segmentMeta.getName() + "." + field.getName());
                }
                else if (field.getType() == TestDataFieldType.OBJECT_LIST) {
                    value = buildObjectList(testData, configuration, segmentMeta.getName() + "." + field.getName());
                }
                else {
                    value = getFieldValue(configuration, segmentName, field);
                }

                if (value != null) {
                    BeanUtil.setPropertyValue(data, field.getName(), value);
                }
            }

            return data;
        }
        catch (Exception e) {
            throw new AutomationException("Could create data object for segment " + segmentMeta.getName(), e);
        }
    }

    private List<Data> buildObjectList(TestData testData, TestDataConfiguration configuration, String segmentName) {
        List<Data> result = new ArrayList<Data>();

        for (int i = 1; i < 1000; i++) {
            String segName = segmentName + "-" + i;
            if (!containsSegment(configuration, segName)) {
                break;
            }

            result.add(buildObject(testData, configuration, segName));
        }

        return result;
    }

    private Source getRequiredSourceAnnotation(Annotation[] annotations, String parameterName) {
        for (Annotation anno : annotations) {
            if (anno instanceof Source) {
                return (Source) anno;
            }
        }
        throw new AutomationException("Parameter does not have a @Source annotation: " + parameterName);
    }

    private InputStream tryFindXml(String uri, Method testMethod) throws IOException, AutomationException, URISyntaxException {
        if (uri.matches("[a-z]+://.*")) {
            URI realUri = new URI(uri);
            URL url = realUri.toURL();
            return url.openStream();
        }

        // first search try: full path, as used for Excels
        StringBuilder sbPath = new StringBuilder();
        sbPath.append(aludraConfig.getXlsRootPath());

        if (sbPath.toString().endsWith(File.separator)) {
            sbPath.delete(sbPath.length() - 1, sbPath.length());
        }
        sbPath.append(File.separator);
        sbPath.append(testMethod.getDeclaringClass().getName().replace(".", File.separator));
        sbPath.append(File.separator);
        sbPath.append(uri);
        File f = new File(sbPath.toString());
        if (f.isFile()) {
            return new FileInputStream(f);
        }

        // second try: directly under root path
        sbPath = new StringBuilder();
        sbPath.append(aludraConfig.getXlsRootPath());

        if (sbPath.toString().endsWith(File.separator)) {
            sbPath.delete(sbPath.length() - 1, sbPath.length());
        }
        sbPath.append(File.separator);
        sbPath.append(uri);
        f = new File(sbPath.toString());
        if (f.isFile()) {
            return new FileInputStream(f);
        }

        throw new AutomationException("Could not find test data XML file " + uri);
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

    /** Evaluates the given data script, applying the given format pattern and locale, if specified.
     * 
     * @param script Script to evaluate, e.g. <code>addDaysToNow(5)</code>
     * @param formatPattern Format pattern to apply. Can be a format accepted by <code>SimpleDateFormat</code> or
     *            <code>DecimalFormat</code>, depending on type of expression. If not specified, a type-specific default format is
     *            used. If the expression evaluates to a String, this parameter is ignored.
     * @param locale Locale to apply to the format pattern. If not specified, <code>Locale.US</code> is used (<b>NOT</b> the
     *            platform default, to ensure platform-independent operation).
     * @return */
    public String evaluate(String script, String formatPattern, Locale locale) {
        if (locale == null) {
            locale = Locale.US;
        }
        Context context = Context.enter();

        try {
            Scriptable scope = context.initStandardObjects();

            // find all script libraries and add their scripts
            for (ScriptLibrary lib : scriptLibraries.values()) {
                lib.addFunctionsToContext(context, scope);
            }

            Object result = context.evaluateString(scope, script, "<cmd>", 1, null);
            result = toJavaObject(result);

            // apply patterns, if required
            if (result instanceof Date) {
                if (formatPattern == null) {
                    formatPattern = "yyyy-MM-dd";
                }
                SimpleDateFormat sdf = new SimpleDateFormat(formatPattern, locale);
                return sdf.format(result);
            }
            if (result instanceof Number) {
                if (formatPattern == null) {
                    formatPattern = "#.#";
                }
                DecimalFormat df = new DecimalFormat(formatPattern, DecimalFormatSymbols.getInstance(locale));
                return df.format(result);
            }

            return result.toString();
        }
        finally {
            Context.exit();
        }
    }

    private Object toJavaObject(Object jsObject) {
        if (!(jsObject instanceof ScriptableObject)) {
            // already Java
            return jsObject;
        }
        // analyze object class name to determine target type
        if (jsObject.getClass().getName().endsWith("Date")) {
            return Context.jsToJava(jsObject, Date.class);
        }
        if (jsObject.getClass().getName().endsWith("Number")) {
            return Context.jsToJava(jsObject, Double.class);
        }
        return Context.toString(jsObject);
    }

    private static Locale toLocale(String s) {
        if (s == null || "".equals(s)) {
            return null;
        }

        // language only?
        if (s.matches("[a-z]{2}")) {
            return new Locale(s);
        }

        // language and country?
        if (s.matches("[a-z]{2}_[A-Z]{2}")) {
            String[] parts = s.split("_");
            return new Locale(parts[0], parts[1]);
        }

        // variant?
        if (s.matches("[a-z]{2}_[A-Z]{2}_[^_]+")) {
            String[] parts = s.split("_");
            return new Locale(parts[0], parts[1], parts[2]);
        }

        // invalid locale string
        throw new IllegalArgumentException("Invalid Locale value found in XML: " + s);
    }

}
