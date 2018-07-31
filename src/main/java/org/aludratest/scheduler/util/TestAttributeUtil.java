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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aludratest.testcase.AludraTestCase;
import org.aludratest.testcase.TestAttribute;
import org.aludratest.testcase.TestAttributes;

/** Helper class for dealing with {@link TestAttribute} annotations.
 *
 * @author falbrech */
public final class TestAttributeUtil {

    private TestAttributeUtil() {
    }

    /** Determines all test attributes of the given test case class.
     *
     * @param testClass Test case class.
     *
     * @return All test attributes of the given test case class. */
    public static Map<String, List<String>> getTestAttributes(Class<? extends AludraTestCase> testClass) {
        TestAttribute attr = testClass.getAnnotation(TestAttribute.class);
        TestAttributes attrs = testClass.getAnnotation(TestAttributes.class);

        Map<String, List<String>> result = new HashMap<String, List<String>>();

        if (attr != null) {
            result.put(attr.name(), Collections.singletonList(attr.value()));
        }
        if (attrs != null) {
            for (TestAttribute a : attrs.value()) {
                List<String> ls = result.get(a.name());
                if (ls == null) {
                    ls = new ArrayList<String>();
                    result.put(a.name(), ls);
                }
                ls.add(a.value());
            }
        }

        return result;
    }

}
