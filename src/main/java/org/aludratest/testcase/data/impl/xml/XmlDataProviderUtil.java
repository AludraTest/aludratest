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
import java.util.Date;
import java.util.Locale;

import org.aludratest.exception.AutomationException;
import org.aludratest.testcase.data.Source;
import org.aludratest.testcase.data.impl.xml.model.TestData;
import org.aludratest.testcase.data.impl.xml.model.TestDataConfiguration;
import org.aludratest.testcase.data.impl.xml.model.TestDataConfigurationSegment;
import org.aludratest.testcase.data.impl.xml.model.TestDataFieldMetadata;
import org.aludratest.testcase.data.impl.xml.model.TestDataFieldValue;
import org.aludratest.testcase.data.impl.xml.model.TestDataSegmentMetadata;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;

/** Provides utility methods for XML data parsing.
 * @author falbrech
 * @author Volker Bergmann */
public class XmlDataProviderUtil {

    private static final SimpleDateFormat ISO_DATE = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private static final DecimalFormat JAVA_NUMBER = new DecimalFormat("#.#", DecimalFormatSymbols.getInstance(Locale.US));

    private XmlDataProviderUtil() {
    }

    public static Source getRequiredSourceAnnotation(Annotation[] annotations, String parameterName) {
        for (Annotation anno : annotations) {
            if (anno instanceof Source) {
                return (Source) anno;
            }
        }
        throw new AutomationException("Parameter does not have a @Source annotation: " + parameterName);
    }

    public static InputStream tryFindXml(String uri, Method testMethod, String xlsRootPath) throws IOException,
    AutomationException, URISyntaxException {
        if (uri.matches("[a-z]+://.*")) {
            URI realUri = new URI(uri);
            URL url = realUri.toURL();
            return url.openStream();
        }

        // first search try: full path, as used for Excels
        StringBuilder sbPath = new StringBuilder();
        sbPath.append(xlsRootPath);

        if (sbPath.toString().endsWith(File.separator)) {
            sbPath.delete(sbPath.length() - 1, sbPath.length());
        }
        sbPath.append(File.separator).append(testMethod.getDeclaringClass().getName().replace(".", File.separator));
        sbPath.append(File.separator).append(uri); // NOSONAR
        File f = new File(sbPath.toString()); // NOSONAR
        if (f.isFile()) {
            return new FileInputStream(f);
        }

        // second try: directly under root path
        sbPath = new StringBuilder();
        sbPath.append(xlsRootPath);

        if (sbPath.toString().endsWith(File.separator)) {
            sbPath.delete(sbPath.length() - 1, sbPath.length());
        }
        sbPath.append(File.separator).append(uri);
        f = new File(sbPath.toString()); // NOSONAR
        if (f.isFile()) {
            return new FileInputStream(f);
        }

        throw new AutomationException("Could not find test data XML file " + uri);
    }

    public static boolean containsSegment(TestDataConfiguration configuration, String segmentName) {
        for (TestDataConfigurationSegment segment : configuration.getSegments()) {
            if (segmentName.equals(segment.getName())) {
                return true;
            }
        }
        return false;
    }

    public static Object format(Object object, String patternSpec, Locale localeSpec) {

        // check locale
        Locale locale = ((localeSpec != null) ? localeSpec : Locale.US);

        String pattern = patternSpec;

        // apply pattern if object is Date
        if (object instanceof Date) {
            if (pattern == null) {
                pattern = "yyyy-MM-dd";
            }
            SimpleDateFormat sdf = new SimpleDateFormat(pattern, locale);
            return sdf.format(object);
        }

        // apply pattern if object is Number
        if (object instanceof Number) {
            if (pattern == null) {
                pattern = "#.#";
            }
            DecimalFormat df = new DecimalFormat(pattern, DecimalFormatSymbols.getInstance(locale));
            return df.format(object);
        }

        // else return the object itself
        return object;
    }

    public static Object toJavaObject(Object jsObject) {
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

    public static Locale toLocale(String s) {
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

    public static TestDataSegmentMetadata getSegmentMetadata(TestData testData, String segmentName) {
        for (TestDataSegmentMetadata segment : testData.getMetadata().getSegments()) {
            if (segmentName.equals(segment.getName())) {
                return segment;
            }
        }
        return null;
    }

    public static Object getFieldValue(TestDataConfiguration configuration, String segmentName, TestDataFieldMetadata fieldMeta) {
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

}
