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
package org.aludratest.dict.data;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.aludratest.dict.Data;
import org.junit.Test;

@SuppressWarnings("unused")
public class DataTest extends TestCase {
    
    @Test
    public void testDescriptionStringSimple() {
        Data1 data = new Data1();
        data.setSimpleAttr1("Test1");
        data.setSimpleAttr2("Test2");

        assertEquals("Data1 [simpleAttr1=\"Test1\", simpleAttr2=\"Test2\"]", data.toString());
    }
    
    @Test
    public void testDescriptionStringNull() {
        Data1 data = new Data1();
        data.setSimpleAttr1("Test1");
        // data.setSimpleAttr2("Test2");

        assertEquals("Data1 [simpleAttr1=\"Test1\", simpleAttr2=null]", data.toString());
    }

    @Test
    public void testDescriptionStringRecursive() {
        Data1 data = new Data1();
        data.setSimpleAttr1("Test1");
        data.setSimpleAttr2("Test2");

        Data2 data2 = new Data2();
        data2.setDataAttr(data);
        data2.setNameAttr("TestName");
        assertEquals("Data2 [dataAttr=Data1 [simpleAttr1=\"Test1\", simpleAttr2=\"Test2\"], nameAttr=\"TestName\"]",
                data2.toString());
    }
    
    @Test
    public void testDescriptionStringInherited() {
        Data3 data = new Data3();
        data.setSimpleAttr1("Test1");
        data.setSimpleAttr2("Test2");
        data.setSimpleAttr3("Test3");

        // note: attribute from specialized class first
        assertEquals("Data3 [simpleAttr3=\"Test3\", simpleAttr1=\"Test1\", simpleAttr2=\"Test2\"]", data.toString());
    }

    @Test
    public void testDescriptionStringCollection() {
        Data4 data = new Data4();

        List<Data1> ls = new ArrayList<Data1>();
        Data1 data1 = new Data1();
        data1.setSimpleAttr1("Test1");
        ls.add(data1);
        data1 = new Data1();
        data1.setSimpleAttr1("Test2");
        data1.setSimpleAttr2("Test3");
        ls.add(data1);
        data.setData1List(ls);
        data.setSimpleAttr("Test99");

        assertEquals(
                "Data4 [data1List=[Data1 [simpleAttr1=\"Test1\", simpleAttr2=null], Data1 [simpleAttr1=\"Test2\", simpleAttr2=\"Test3\"]], simpleAttr=\"Test99\"]",
                data.toString());
    }
    
    @Test
    public void testDescriptionStringEndlessRecursion() {
        Data5 d5 = new Data5();
        Data6 d6 = new Data6();

        d5.setData6Attr(d6);
        d6.setDataAttr(d5);
        
        assertEquals("Data5 [data6Attr=Data6 [dataAttr=Data5 [data6Attr=<endless recursion>]]]", d5.toString());
        assertEquals("Data6 [dataAttr=Data5 [data6Attr=Data6 [dataAttr=<endless recursion>]]]", d6.toString());
    }
    

    private static class Data1 extends Data {
        
        private String simpleAttr1;
        
        private String simpleAttr2;
        
        public void setSimpleAttr1(String simpleAttr1) {
            this.simpleAttr1 = simpleAttr1;
        }

        public void setSimpleAttr2(String simpleAttr2) {
            this.simpleAttr2 = simpleAttr2;
        }
        
        @Override
        public String toString() {
            return buildDescriptionString(new StringBuilder()).toString();
        }
    }

    private static class Data2 extends Data {

        private Data1 dataAttr;

        private String nameAttr;

        public void setNameAttr(String nameAttr) {
            this.nameAttr = nameAttr;
        }

        public void setDataAttr(Data1 dataAttr) {
            this.dataAttr = dataAttr;
        }

        @Override
        public String toString() {
            return buildDescriptionString(new StringBuilder()).toString();
        }
    }

    private static class Data3 extends Data1 {

        private String simpleAttr3;

        public void setSimpleAttr3(String simpleAttr3) {
            this.simpleAttr3 = simpleAttr3;
        }

    }

    private static class Data4 extends Data {

        private List<Data1> data1List;

        private String simpleAttr;

        public void setData1List(List<Data1> data1List) {
            this.data1List = data1List;
        }

        public void setSimpleAttr(String simpleAttr) {
            this.simpleAttr = simpleAttr;
        }

        @Override
        public String toString() {
            return buildDescriptionString(new StringBuilder()).toString();
        }
    }

    private static class Data5 extends Data {

        private Data6 data6Attr;

        public void setData6Attr(Data6 data6Attr) {
            this.data6Attr = data6Attr;
        }

        @Override
        public String toString() {
            return buildDescriptionString(new StringBuilder()).toString();
        }

    }

    private static class Data6 extends Data {

        private Data5 dataAttr;

        public void setDataAttr(Data5 dataAttr) {
            this.dataAttr = dataAttr;
        }

        @Override
        public String toString() {
            return buildDescriptionString(new StringBuilder()).toString();
        }
    }

}
