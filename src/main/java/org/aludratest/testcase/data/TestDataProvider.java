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
package org.aludratest.testcase.data;

import java.lang.reflect.Method;
import java.util.List;

/** Abstraction of a test data provider. This is an extension point to allow use of other test data providers than the one provided
 * with AludraTest.
 * 
 * @author Volker Bergmann */
public interface TestDataProvider {

    /** Callback method by which the framework requests
     *  test data for the given method.
     *  @param method the method for which to retreive the test data
     *  @return a List with the test data for the provided method */
    List<TestCaseData> getTestDataSets(Method method);

}
