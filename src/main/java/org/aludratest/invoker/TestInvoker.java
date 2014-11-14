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

/** Abstraction of a test invoker. This is provided as extension point for hypothetical other test execution mechanisms than the
 * invocation of AludraTest Java test methods.
 * @see AludraTestMethodInvoker
 * @author Volker Bergmann */
public interface TestInvoker {

    /** Callback method to inject the {@link AludraTestContext}
     *  @param context */
    void setContext(AludraTestContext context);

    /** Callback method to perform the test
     *  @throws Throwable */
    void invoke() throws Throwable; //NOSONAR

}
