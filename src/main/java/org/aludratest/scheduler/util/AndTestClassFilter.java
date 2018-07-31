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

import org.aludratest.scheduler.TestClassFilter;
import org.aludratest.testcase.AludraTestCase;

public class AndTestClassFilter implements TestClassFilter {

    private List<TestClassFilter> filters;

    public AndTestClassFilter(List<? extends TestClassFilter> filters) {
        this.filters = Collections.unmodifiableList(new ArrayList<TestClassFilter>(filters));
    }

    List<TestClassFilter> getFilters() {
        return filters;
    }

    @Override
    public boolean matches(Class<? extends AludraTestCase> testClass) {
        for (TestClassFilter filter : filters) {
            if (!filter.matches(testClass)) {
                return false;
            }
        }

        return true;
    }

}
