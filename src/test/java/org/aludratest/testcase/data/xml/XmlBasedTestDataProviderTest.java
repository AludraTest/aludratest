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
package org.aludratest.testcase.data.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aludratest.config.AludraTestConfig;
import org.aludratest.config.impl.AludraTestConfigImpl;
import org.aludratest.config.impl.AludraTestingTestConfigImpl;
import org.aludratest.config.impl.DefaultConfigurator;
import org.aludratest.exception.AutomationException;
import org.aludratest.testcase.Ignored;
import org.aludratest.testcase.Offset;
import org.aludratest.testcase.data.Source;
import org.aludratest.testcase.data.TestCaseData;
import org.aludratest.testcase.data.impl.xml.DefaultScriptLibrary;
import org.aludratest.testcase.data.impl.xml.ScriptLibrary;
import org.aludratest.testcase.data.impl.xml.XmlBasedTestDataProvider;
import org.aludratest.util.data.StringData;
import org.codehaus.plexus.util.ReflectionUtils;
import org.junit.Test;

public class XmlBasedTestDataProviderTest {

    private XmlBasedTestDataProvider createProvider(AludraTestConfig config) throws Exception {
        XmlBasedTestDataProvider provider = new XmlBasedTestDataProvider();
        if (config == null) {
            config = new AludraTestConfigImpl();
            DefaultConfigurator configurator = new DefaultConfigurator();
            configurator.configure(config);
        }

        Map<String, ScriptLibrary> libs = new HashMap<String, ScriptLibrary>();
        libs.put("default", new DefaultScriptLibrary());

        ReflectionUtils.setVariableValueInObject(provider, "aludraConfig", config);
        ReflectionUtils.setVariableValueInObject(provider, "scriptLibraries", libs);
        return provider;
    }

    private XmlBasedTestDataProvider createProvider() throws Exception {
        return createProvider(null);
    }

    @Test
    public void testComplexObjects() throws Exception {
        XmlBasedTestDataProvider provider = createProvider();
        List<TestCaseData> testData = provider.getTestDataSets(XmlBasedTestDataProviderTest.class.getDeclaredMethod(
                "testMethod1", ComplexData.class, StringData.class));
        assertEquals(1, testData.size());
        assertEquals(2, testData.get(0).getData().length);

        ComplexData cd = (ComplexData) testData.get(0).getData()[0];
        assertEquals("The Config1", cd.getName());
        assertNotNull(cd.getSubData());
        assertEquals("SubData Here.", cd.getSubData().getValue());

        StringData sd = (StringData) testData.get(0).getData()[1];
        assertEquals("Some value ending with space ", sd.getValue());
    }

    @Test
    public void testReferencingScript() throws Exception {
        XmlBasedTestDataProvider provider = createProvider();
        List<TestCaseData> testData = provider.getTestDataSets(XmlBasedTestDataProviderTest.class.getDeclaredMethod(
                "testMethod3", ComplexData.class, StringData.class));
        assertEquals(1, testData.size());
        assertEquals(2, testData.get(0).getData().length);

        ComplexData cd = (ComplexData) testData.get(0).getData()[0];
        assertEquals("The Config1", cd.getName());
        assertEquals("The Config1Value", cd.getThirdField());
        assertEquals("The Config1Value is great", cd.getSecondField());
    }

    @Test
    public void testMultiAndNull() throws Exception {
        XmlBasedTestDataProvider provider = createProvider();
        List<TestCaseData> testData = provider.getTestDataSets(XmlBasedTestDataProviderTest.class.getDeclaredMethod(
                "testMethod2", ComplexData.class, StringData.class));
        assertEquals(3, testData.size());
        assertEquals(2, testData.get(0).getData().length);
        assertEquals(2, testData.get(1).getData().length);
        assertEquals(2, testData.get(2).getData().length);
        assertFalse(testData.get(0).isIgnored());
        assertFalse(testData.get(1).isIgnored());
        assertTrue(testData.get(2).isIgnored());
        assertEquals("Ignored for test", testData.get(2).getIgnoredReason());

        ComplexData cd = (ComplexData) testData.get(0).getData()[0];
        assertNull(cd.getSubData());

        cd = (ComplexData) testData.get(1).getData()[0];
        assertNotNull(cd.getSubData());
        assertNull(cd.getSubData().getValue());

        assertEquals("The Config2", cd.getName());
    }

    @Test
    public void testMultiWithOffset() throws Exception {
        XmlBasedTestDataProvider provider = createProvider();
        List<TestCaseData> testData = provider.getTestDataSets(XmlBasedTestDataProviderTest.class.getDeclaredMethod(
                "testMethod2WithOffset", ComplexData.class, StringData.class));
        assertEquals(2, testData.size());
        assertEquals(2, testData.get(0).getData().length);
        assertEquals(2, testData.get(1).getData().length);
        assertFalse(testData.get(0).isIgnored());
        assertTrue(testData.get(1).isIgnored());
        assertEquals("Ignored for test", testData.get(1).getIgnoredReason());

        ComplexData cd = (ComplexData) testData.get(1).getData()[0];
        assertNotNull(cd.getSubData());
        assertNull(cd.getSubData().getValue());

        assertEquals("The Config2", cd.getName());
    }

    @Test
    public void testMissingConfigs() throws Exception {
        XmlBasedTestDataProvider provider = createProvider();
        List<TestCaseData> testData = provider.getTestDataSets(XmlBasedTestDataProviderTest.class.getDeclaredMethod(
                "testInvalidMethod", ComplexData.class, StringData.class));
        assertNull(testData.get(0).getException());
        assertTrue(testData.get(1).getException() instanceof AutomationException);
    }

    @Test
    public void testIgnoredMethod() throws Exception {
        XmlBasedTestDataProvider provider = createProvider();
        List<TestCaseData> testData = provider.getTestDataSets(XmlBasedTestDataProviderTest.class.getDeclaredMethod(
                "testIgnoredMethod", ComplexData.class, StringData.class));
        assertNull(testData.get(0).getException());
        assertTrue(testData.get(0).isIgnored());
        assertTrue(testData.get(1).isIgnored());
        assertTrue(testData.get(2).isIgnored());
        assertEquals("Some reason", testData.get(0).getIgnoredReason());
        assertEquals("Some reason", testData.get(1).getIgnoredReason());
        assertEquals("Some reason", testData.get(2).getIgnoredReason());
    }

    @Test
    public void testTimetravel() throws Exception {
        AludraTestingTestConfigImpl config = new AludraTestingTestConfigImpl();
        DefaultConfigurator configurator = new DefaultConfigurator();
        configurator.configure(config);
        config.setScriptSecondsOffset(Integer.valueOf(-86400));
        XmlBasedTestDataProvider provider = createProvider(config);
        List<TestCaseData> testData = provider
                .getTestDataSets(XmlBasedTestDataProviderTest.class.getDeclaredMethod("testMethodTimetravel", StringData.class));
        assertEquals("2015-03-07 23:58", ((StringData) testData.get(0).getData()[0]).getValue());
    }

    @Test
    public void testFormatting() throws Exception {
        XmlBasedTestDataProvider provider = createProvider();
        List<TestCaseData> testData = provider.getTestDataSets(XmlBasedTestDataProviderTest.class.getDeclaredMethod(
                "testMethodFormat", ComplexData.class));

        ComplexData cd = (ComplexData) testData.get(0).getData()[0];
        assertEquals("22. Juli 2015", cd.getName());
        cd = (ComplexData) testData.get(1).getData()[0];
        assertEquals("09. Juli 2015", cd.getName());
    }

    public void testMethod1(@Source(uri = "complex.testdata.xml", segment = "complexObject") ComplexData object,
            @Source(uri = "complex.testdata.xml", segment = "stringObject") StringData object2) {
        if (object == null) {
            // do nothing
        }
    }

    public void testMethod2(@Source(uri = "multi.testdata.xml", segment = "complexObject") ComplexData object,
            @Source(uri = "multi.testdata.xml", segment = "stringObject") StringData object2) {
        if (object == null) {
            // do nothing
        }
    }

    @Offset(1)
    public void testMethod2WithOffset(@Source(uri = "multi.testdata.xml", segment = "complexObject") ComplexData object,
            @Source(uri = "multi.testdata.xml", segment = "stringObject") StringData object2) {
        if (object == null) {
            // do nothing
        }
    }

    public void testMethod3(@Source(uri = "referencing.testdata.xml", segment = "complexObject") ComplexData object,
            @Source(uri = "referencing.testdata.xml", segment = "stringObject") StringData object2) {
        if (object == null) {
            // do nothing
        }
    }

    public void testInvalidMethod(@Source(uri = "multi.testdata.xml", segment = "complexObject") ComplexData object,
            @Source(uri = "complex.testdata.xml", segment = "stringObject") StringData object2) {
        if (object == null) {
            // do nothing
        }
    }

    @Ignored("Some reason")
    public void testIgnoredMethod(@Source(uri = "multi.testdata.xml", segment = "complexObject") ComplexData object,
            @Source(uri = "multi.testdata.xml", segment = "stringObject") StringData object2) {
        if (object == null) {
            // do nothing
        }
    }

    public void testMethodFormat(@Source(uri = "formatting.testdata.xml", segment = "complexObject") ComplexData object) {
        if (object == null) {
            // do nothing
        }
    }

    public void testMethodTimetravel(@Source(uri = "timetravel.testdata.xml", segment = "stringObject") StringData object) {
        if (object == null) {
            // do nothing
        }
    }

}