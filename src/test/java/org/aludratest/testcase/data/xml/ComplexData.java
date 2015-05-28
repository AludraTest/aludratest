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

import org.aludratest.dict.Data;
import org.aludratest.util.data.StringData;

public class ComplexData extends Data {

    private StringData subData;

    private String name;

    private String secondField;

    private String thirdField;

    public StringData getSubData() {
        return subData;
    }

    public void setSubData(StringData subData) {
        this.subData = subData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSecondField() {
        return secondField;
    }

    public void setSecondField(String secondField) {
        this.secondField = secondField;
    }

    public String getThirdField() {
        return thirdField;
    }

    public void setThirdField(String thirdField) {
        this.thirdField = thirdField;
    }

}
