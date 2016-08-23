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
import org.aludratest.testcase.TestAttribute;

/** Filter for test classes based on their {@link TestAttribute} annotation(s) and according values. For a given name of a test
 * attribute, one or more valid values can be specified. A value of <code>[]</code> specifies that a test case matches where this
 * test attribute is not set at all. <br>
 * If the test case has multiple {@link TestAttribute} annotations with the same name, the test case matches as soon as one of the
 * associated values are contained in the list of valid values. <br>
 * If the <code>invert</code> flag is set, the calculated match flag (as described above) is inverted before being returned by
 * {@link #matches(Class)}.
 *
 * @author falbrech */
public final class AttributeBasedTestClassFilter implements TestClassFilter {

    private String attributeName;

    private List<String> values;

    private boolean invert;

    /** Constructs a new attribute based test class filter.
     *
     * @param attributeName Name of the TestAttribute to match on.
     * @param values List of accepted values for the TestAttribute.
     * @param invert If <code>true</code>, the match result is inverted before being returned. */
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
        Map<String, List<String>> attributes = TestAttributeUtil.getTestAttributes(testClass);

        boolean matchValue = false;

        if (attributes.isEmpty() || !attributes.containsKey(attributeName)) {
            matchValue = values.contains("[]");
        }
        else {
            // one of the attributes set must be contained in values
            for (String attrValue : attributes.get(attributeName)) {
                matchValue |= values.contains(attrValue);
            }
        }

        return invert ? !matchValue : matchValue;
    }

}
