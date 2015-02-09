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
package org.aludratest.data;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.aludratest.AludraTest;
import org.aludratest.testcase.AludraTestCase;
import org.aludratest.testcase.Offset;
import org.aludratest.testcase.data.Source;

/**
 * Tests the @Offset annotation.
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public class OffsetTest {

    static List<PersonBean> data0 = new ArrayList<PersonBean>();
    static List<PersonBean> data1 = new ArrayList<PersonBean>();

    @org.junit.Test
    public void test() {
        AludraTest aludraTest = AludraTest.startFramework();
        aludraTest.run(OffsetTestClass.class);
        assertEquals(2, data0.size());
        assertEquals(1, data1.size());
        aludraTest.stopFramework();
    }

    public static class OffsetTestClass extends AludraTestCase {

        @org.aludratest.testcase.Test
        @Offset(0)
        public void testOffset0(@Source(uri = "persons.xls", segment = "persons") PersonBean person) {
            System.out.println("Test invoked with: " + person);
            data0.add(person);
        }

        @org.aludratest.testcase.Test
        @Offset(1)
        public void testOffset1(@Source(uri = "persons.xls", segment = "persons") PersonBean person) {
            System.out.println("Test invoked with: " + person);
            data1.add(person);
        }
    }

}
