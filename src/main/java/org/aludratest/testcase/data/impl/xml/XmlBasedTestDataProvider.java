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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.aludratest.config.AludraTestConfig;
import org.aludratest.dict.Data;
import org.aludratest.exception.AutomationException;
import org.aludratest.testcase.Ignored;
import org.aludratest.testcase.Offset;
import org.aludratest.testcase.data.Source;
import org.aludratest.testcase.data.TestCaseData;
import org.aludratest.testcase.data.TestDataProvider;
import org.aludratest.testcase.data.TestDataSource;
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
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;

/** An XML based Test Data provider. XML files must have the AludraTest XML Testdata format; best use AludraTest VDE Plugin for
 * eclipse to create testdata files. The uri value of Source annotations must point to an XML file. This file is searched in these
 * two locations:
 * <ol>
 * <li>In a folder <i>xlsRootPath</i>/package/of/testclass/as/folder/MyTestClass/</li>
 * <li>Relative to <i>xlsRootPath</i>
 * </ol>
 * xlsRootPath value is configured in aludratest.properties.
 *
 * @author falbrech */
public class XmlBasedTestDataProvider implements TestDataProvider {

    private final SimpleDateFormat ISO_DATE = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    private final DecimalFormat JAVA_NUMBER = new DecimalFormat("#.#", DecimalFormatSymbols.getInstance(Locale.US));

    @Requirement
    private AludraTestConfig aludraConfig;

    @Requirement(role = ScriptLibrary.class)
    private Map<String, ScriptLibrary> scriptLibraries = new HashMap<String, ScriptLibrary>();

    @Override
    public List<TestCaseData> getTestDataSets(Method method) {
        if (method.getParameterTypes().length == 0) {
            return Collections.singletonList(new TestCaseData("0", null, null));
        }

        // get @Source annotated parameters
        Annotation[][] annots = method.getParameterAnnotations();

        Offset offsetAnno = method.getAnnotation(Offset.class);
        int offset = (offsetAnno != null ? offsetAnno.value() : 0);

        boolean ignored = method.getAnnotation(Ignored.class) != null;
        String ignoredReason = null;
        if (ignored) {
            ignoredReason = method.getAnnotation(Ignored.class).value();
            if ("".equals(ignoredReason)) {
                ignoredReason = null;
            }
        }

        List<TestCaseData> result = new ArrayList<TestCaseData>();

        // cache for loaded file models
        Map<String, TestData> loadedFileModels = new HashMap<String, TestData>();

        // load param by param; transpose into test case data afterwards
        List<List<InternalSingleDataSource>> allData = new ArrayList<List<InternalSingleDataSource>>();
        for (int i = 0; i < annots.length; i++) {
            List<InternalSingleDataSource> paramData = getDataObjects(method, i, loadedFileModels);
            if (offset > 0) {
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
        if (offset > 0) {
            int effectiveOffset = Math.min(offset, configs.size());
            configs = configs.subList(effectiveOffset, configs.size());
        }
        for (int i = 0; i < configs.size(); i++) {
            TestDataConfiguration config = configs.get(i);

            List<InternalSingleDataSource> dataForConfig = new ArrayList<InternalSingleDataSource>();
            // ensure that all data lists contain enough entries
            for (List<InternalSingleDataSource> ls : allData) {
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
                final List<InternalSingleDataSource> finalData = dataForConfig;
                TestDataSource source = new TestDataSource() {
                    @Override
                    public Data[] getData() {
                        Data[] dataArray = new Data[finalData.size()];
                        for (int i = 0; i < finalData.size(); i++) {
                            dataArray[i] = finalData.get(i).getObject();
                        }

                        return dataArray;
                    }
                };
                if (ignored) {
                    result.add(new TestCaseData(config.getName(), null, source, true, ignoredReason));
                }
                else {
                    result.add(new TestCaseData(config.getName(), null, source, config.isIgnored(),
                            config.isIgnored() ? config.getIgnoredReason() : null));
                }
            }
        }

        return result;
    }

    private List<InternalSingleDataSource> getDataObjects(Method method, int paramIndex, Map<String, TestData> loadedFileModels) {
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

        final TestData testData;
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
                throw new AutomationException("Could not read test data XML at " + uri, e);
            }
            finally {
                IOUtils.closeQuietly(in);
            }
        }

        List<InternalSingleDataSource> dataElements = new ArrayList<InternalSingleDataSource>();

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

        final TestDataSegmentMetadata finalSegmentMeta = segmentMeta;

        // for each configuration entry, find values
        for (final TestDataConfiguration config : testData.getConfigurations()) {
            if (containsSegment(config, segmentMeta.getName())) {
                dataElements.add(new InternalSingleDataSource() {
                    @Override
                    public Data getObject() {
                        return buildObject(testData, config, finalSegmentMeta.getName());
                    }
                });
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
                            return new ScriptToEvaluate(value.toString(), fieldMeta.getFormatterPattern(),
                                    toLocale(fieldMeta.getFormatterLocale()));
                        }

                        // perform auto-conversion based on type
                        if (value instanceof String && !"".equals(value)) {
                            switch (fieldMeta.getType()) {
                                case BOOLEAN:
                                    value = Boolean.parseBoolean(value.toString());
                                    break;
                                case DATE:
                                    try {
                                        value = ISO_DATE.parse(value.toString());
                                    }
                                    catch (ParseException e) {
                                        // ignore; value is presented as-is
                                        return value;
                                    }
                                    break;
                                case NUMBER:
                                    try {
                                        value = JAVA_NUMBER.parseObject(value.toString());
                                    }
                                    catch (ParseException e) {
                                        // ignore; value is presented as-is
                                        return value;
                                    }
                                    break;
                                default:
                                    // nothing
                            }

                            return format(value, fieldMeta.getFormatterPattern(), toLocale(fieldMeta.getFormatterLocale()))
                                    .toString();
                        }
                        else if ("".equals(value)) {
                            return null;
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
            Map<String, ScriptToEvaluate> scriptValues = new HashMap<String, ScriptToEvaluate>();
            Map<String, Object> plainValues = new HashMap<String, Object>();

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

                if (!(value instanceof ScriptToEvaluate)) {
                    // also put null in map to avoid "no such reference"
                    plainValues.put(field.getName(), value);
                }
                else {
                    scriptValues.put(field.getName(), (ScriptToEvaluate) value);
                }

                if (value != null && !(value instanceof ScriptToEvaluate)) {
                    BeanUtil.setPropertyValue(data, field.getName(), value);
                }
            }

            // now evaluate scripts. Offer already calculated fields as Context variables
            int lastErrorCount;
            int errorCount = 0;
            AutomationException lastException = null;
            do {
                lastErrorCount = errorCount;
                errorCount = 0;
                Iterator<Map.Entry<String, ScriptToEvaluate>> scriptIter = scriptValues.entrySet().iterator();
                while (scriptIter.hasNext()) {
                    Map.Entry<String, ScriptToEvaluate> entry = scriptIter.next();
                    try {
                        Object value = evaluate(entry.getValue().script, entry.getValue().formatPattern,
                                entry.getValue().formatLocale, plainValues);
                        plainValues.put(entry.getKey(), value);
                        if (value != null) {
                            BeanUtil.setPropertyValue(data, entry.getKey(), value);
                        }
                        scriptIter.remove();
                    }
                    catch (AutomationException e) {
                        lastException = e;
                        errorCount++;
                    }
                }
            }
            while (errorCount != lastErrorCount && !scriptValues.isEmpty());

            if (errorCount > 0 && lastException != null) {
                throw lastException;
            }

            return data;
        }
        catch (Exception e) {
            throw new AutomationException("Could not create data object for segment " + segmentMeta.getName(), e);
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
     * @param contextVariables Map with objects which should be offered in the script context as variables. Can be
     *            <code>null</code>.
     * @return */
    public String evaluate(String script, String formatPattern, Locale locale, Map<String, Object> contextVariables) {
        Context context = Context.enter();

        try {
            Scriptable scope = context.initStandardObjects();

            // find all script libraries and add their scripts
            for (ScriptLibrary lib : scriptLibraries.values()) {
                lib.addFunctionsToContext(context, scope);
            }

            // put context variables
            if (contextVariables != null) {
                for (Map.Entry<String, Object> entry : contextVariables.entrySet()) {
                    scope.put(entry.getKey(), scope, entry.getValue() == null ? null : Context.toObject(entry.getValue(), scope));
                }
            }

            try {
                Object result = context.evaluateString(scope, script, "<cmd>", 1, null);
                if (result instanceof Undefined) {
                    return null;
                }
                result = toJavaObject(result);

                // apply time travel
                if (result instanceof Date && aludraConfig.getScriptSecondsOffset() != 0) {
                    result = new Date(((Date) result).getTime() + aludraConfig.getScriptSecondsOffset() * 1000l);
                }

                // apply patterns, if required
                result = format(result, formatPattern, locale);

                return result.toString();
            }
            catch (RhinoException e) {
                throw new AutomationException("Cannot evaluate test data script '" + script + "'", e);
            }
        }
        finally {
            Context.exit();
        }
    }

    private Object format(Object object, String formatPattern, Locale locale) {
        if (locale == null) {
            locale = Locale.US;
        }

        if (object instanceof Date) {
            if (formatPattern == null) {
                formatPattern = "yyyy-MM-dd";
            }
            SimpleDateFormat sdf = new SimpleDateFormat(formatPattern, locale);
            return sdf.format(object);
        }
        if (object instanceof Number) {
            if (formatPattern == null) {
                formatPattern = "#.#";
            }
            DecimalFormat df = new DecimalFormat(formatPattern, DecimalFormatSymbols.getInstance(locale));
            return df.format(object);
        }

        return object;
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

    private static class ScriptToEvaluate {

        private String script;

        private String formatPattern;

        private Locale formatLocale;

        private ScriptToEvaluate(String script, String formatPattern, Locale formatLocale) {
            this.script = script;
            this.formatPattern = formatPattern;
            this.formatLocale = formatLocale;
        }

    }

    private static interface InternalSingleDataSource {

        public Data getObject();

    }

}
