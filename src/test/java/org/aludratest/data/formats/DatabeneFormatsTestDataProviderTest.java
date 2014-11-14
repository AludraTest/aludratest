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
package org.aludratest.data.formats;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.List;

import org.aludratest.config.impl.AludraTestConfigImpl;
import org.aludratest.config.impl.DefaultConfigurator;
import org.aludratest.data.AddressBean;
import org.aludratest.data.PersonBean;
import org.aludratest.dict.Data;
import org.aludratest.testcase.Offset;
import org.aludratest.testcase.data.Source;
import org.aludratest.testcase.data.TestCaseData;
import org.aludratest.testcase.data.impl.DatabeneFormatsTestDataProvider;
import org.codehaus.plexus.util.ReflectionUtils;
import org.databene.commons.BeanUtil;
import org.junit.Test;

/**
 * Tests the {@link DatabeneFormatsTestDataProvider}.
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public class DatabeneFormatsTestDataProviderTest {

    private DatabeneFormatsTestDataProvider createProvider() throws Exception {
        DatabeneFormatsTestDataProvider provider = new DatabeneFormatsTestDataProvider();
        AludraTestConfigImpl config = new AludraTestConfigImpl();
        DefaultConfigurator configurator = new DefaultConfigurator();
        configurator.configure(config);

        ReflectionUtils.setVariableValueInObject(provider, "aludraConfig", config);
        return provider;
    }

    @Test
    public void testNoArgMethod() throws Exception {
        Method noArgMethod = BeanUtil.getMethod(DatabeneFormatsTestDataProviderTest.class, "noArgMethod");
        List<TestCaseData> testDataSets = createProvider().getTestDataSets(noArgMethod);
        assertTrue("No test data sets created for no-arg method", testDataSets.size() >= 1);
        assertTrue("Unexpected test data sets for no-arg method", testDataSets.size() <= 1);
        assertTrue("Unexpected parameters loaded for no-arg method", testDataSets.get(0).getData().length == 0);
    }

    @Test
    public void testSingleArgMethod() throws Exception {
        Method singleArgMethod = BeanUtil.getMethod(DatabeneFormatsTestDataProviderTest.class, "singleArgMethod", PersonBean.class);
        List<TestCaseData> testDataSets = createProvider().getTestDataSets(singleArgMethod);
        assertTrue("No test data sets created for single-arg method", testDataSets.size() >= 1);
        assertEquals(2, testDataSets.size());
        assertPerson("Alice", 23, "London", "Dover", testDataSets.get(0).getData());
        assertPerson("Bob", 34, "New York", "Hauppauge", testDataSets.get(1).getData());
    }

    @Test
    public void testSingleArgMethodWithOffset() throws Exception {
        Method singleArgMethod = BeanUtil.getMethod(DatabeneFormatsTestDataProviderTest.class, "singleArgMethodWithOffset", PersonBean.class);
        List<TestCaseData> testDataSets = createProvider().getTestDataSets(singleArgMethod);
        assertTrue("No test data sets created for single-arg method", testDataSets.size() >= 1);
        assertEquals(1, testDataSets.size());
        assertPerson("Bob", 34, "New York", "Hauppauge", testDataSets.get(0).getData());
    }

    @Test
    public void testMultiArgMethod() throws Exception {
        Method multiArgMethod = BeanUtil.getMethod(DatabeneFormatsTestDataProviderTest.class, "multiArgMethod", PersonBean.class, AddressBean.class);
        List<TestCaseData> testDataSets = createProvider().getTestDataSets(multiArgMethod);
        assertTrue("No test data sets created for multi-arg method", testDataSets.size() >= 1);
        assertEquals(2, testDataSets.size());
        assertPersonAndAddress("Max", 23, "Munich", testDataSets.get(0).getData());
        assertPersonAndAddress("Jens", 34, "Hamburg", testDataSets.get(1).getData());
    }

    // private helper methods --------------------------------------------------

    private void assertPerson(String name, int age, String city1, String city2, Data[] args) {
        assertEquals(1, args.length);
    }

    private void assertPersonAndAddress(String name, int age, String city, Data[] args) {
        assertEquals(2, args.length);
    }


    // methods to be used for testing ------------------------------------------

    public void noArgMethod() {
        // not intended to be invoked
    }

    public void singleArgMethod(@Source(uri = "persons.xlsx", segment = "persons") PersonBean person) {
        // not intended to be invoked
    }

    @Offset(1)
    public void singleArgMethodWithOffset(@Source(uri = "persons.xlsx", segment = "persons") PersonBean person) {
        // not intended to be invoked
    }

    public void multiArgMethod(
            @Source(uri = "persons-plain.xlsx", segment = "persons") PersonBean person,
            @Source(uri = "persons-plain.xlsx", segment = "addresses") AddressBean address
            ) {
        // not intended to be invoked
    }

}
