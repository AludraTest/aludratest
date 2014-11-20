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
package org.aludratest.invoker;

import org.aludratest.testcase.AludraTestContext;

/** Abstraction of a test invoker. A test invoker is responsible to set up a test to execute with a given test context and to
 * execute it.
 * 
 * @author Volker Bergmann */
public interface TestInvoker {

    /** Sets the test context to use for execution. The invoker is reponsible to provide it to the test to execute.
     * @param context Test context to use for execution. */
    void setContext(AludraTestContext context);

    /** Performs the test.
     * @throws Throwable */
    void invoke() throws Throwable; //NOSONAR

    /** Returns the class which contains the test for execution, if any. <b>This is for documentational purposes only<b>, and
     * <code>null</code> is permitted as return value if the test does not base on a class.
     * 
     * @return The class which contains the test for execution, or <code>null</code> if no class information is available for this
     *         test. */
    Class<?> getTestClass();

}
