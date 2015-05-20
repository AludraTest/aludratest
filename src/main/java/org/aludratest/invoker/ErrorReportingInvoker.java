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

import java.lang.reflect.Method;

import org.aludratest.testcase.AludraTestContext;
import org.databene.commons.Assert;

/**
 * {@link TestInvoker} implementation that is used to 
 * report initialization errors specific to a certain test. 
 * @author Volker Bergmann
 */
public class ErrorReportingInvoker implements TestInvoker {

    /** The method to invoke in the test class instance. */
    private Method method;

    /** The error to report. */
    private Throwable error;

    /** Constructor receiving values for all attributes of same name. 
     *  @param method 
     *  @param error */
    public ErrorReportingInvoker(Method method, Throwable error) {
        // check preconditions
        Assert.notNull(method, "method");
        this.method = method;
        this.error = error;
    }

    /** Is called to set the testCase property on the test object,
     *  but since the called instance of this class only exists to 
     *  report an exception, this does nothing. */
    @Override
    public void setContext(AludraTestContext context) {
    }

    /** rethrows the error.  */
    @Override
    public void invoke() throws Throwable { //NOSONAR
        throw error;
    }

    /** Creates a String representation of the object. */
    @Override
    public String toString() {
        return method.getDeclaringClass().getName() + '.' + method.getName() + '(' + error + ')';
    }

    @Override
    public Class<?> getTestClass() {
        return method == null ? null : method.getDeclaringClass();
    }

}
