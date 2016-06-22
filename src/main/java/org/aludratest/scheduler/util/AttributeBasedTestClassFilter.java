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
package org.aludratest.scheduler.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.aludratest.scheduler.TestClassFilter;
import org.aludratest.testcase.AludraTestCase;

public final class AttributeBasedTestClassFilter implements TestClassFilter {

    private String attributeName;

    private List<String> values;

    private boolean invert;

    public AttributeBasedTestClassFilter(String attributeName, List<String> values, boolean invert) {
        this.attributeName = attributeName;
        this.values = Collections.unmodifiableList(new ArrayList<String>(values));
        this.invert = invert;
    }

    String getAttributeName() {
        return attributeName;
    }

    List<String> getValues() {
        return values;
    }

    boolean isInvert() {
        return invert;
    }

    @Override
    public boolean matches(Class<? extends AludraTestCase> testClass) {
        Map<String, String> attributes = TestAttributeUtil.getTestAttributes(testClass);

        boolean matchValue = false;

        if (attributes.isEmpty() || !attributes.containsKey(attributeName)) {
            matchValue = values.contains("[]");
        }
        else {
            matchValue = values.contains(attributes.get(attributeName));
        }

        return invert ? !matchValue : matchValue;
    }

}
