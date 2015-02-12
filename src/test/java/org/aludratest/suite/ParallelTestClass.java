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
package org.aludratest.suite;

import org.aludratest.testcase.AludraTestCase;
import org.aludratest.testcase.Parallel;
import org.aludratest.testcase.Sequential;
import org.aludratest.testcase.Test;

/** 
 * AludraTest test class for parallel test execution. 
 * @author Volker Bergmann
 */
@Parallel
@SuppressWarnings("javadoc")
public class ParallelTestClass extends AludraTestCase {

    @Test
    public void plainTest() {
    }

    @Test
    @Sequential
    public void sequentialTest() {
    }

    @Test
    @Parallel
    public void parallelTest() {
    }

}
