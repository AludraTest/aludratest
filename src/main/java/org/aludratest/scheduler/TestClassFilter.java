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
package org.aludratest.scheduler;

import org.aludratest.testcase.AludraTestCase;

/** Interface for filters which filter test classes. Used in conjunction with {@link AnnotationBasedExecution}.
 * 
 * @author falbrech */
public interface TestClassFilter {

    /** Checks the given class if it fulfills the criteria of this filter.
     * 
     * @param testClass Class to check.
     * 
     * @return <code>true</code> if the class fulfills the criteria of this filter, <code>false</code> otherwise. */
    public boolean matches(Class<? extends AludraTestCase> testClass);

}
