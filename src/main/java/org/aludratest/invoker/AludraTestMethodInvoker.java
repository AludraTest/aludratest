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
import java.lang.reflect.Modifier;

import org.aludratest.exception.AutomationException;
import org.aludratest.exception.TechnicalException;
import org.aludratest.testcase.After;
import org.aludratest.testcase.AludraTestCase;
import org.aludratest.testcase.AludraTestContext;
import org.aludratest.testcase.Before;
import org.databene.commons.ArrayFormat;
import org.databene.commons.Assert;

/** Default {@link TestInvoker} implementation for AludraTest. First it calls the {@literal @}Before methods, then the test method
 * itself, then the {@literal @}After methods, similar to the behavior familiar from JUnit.
 * @author Volker Bergmann */
public class AludraTestMethodInvoker implements TestInvoker {

    /** An instance of the test class to invoke. */
    private AludraTestCase testObject;

    /** The method to invoke in the test class instance. */
    private Method method;

    /** The arguments to provide to the test method. */
    private Object[] args;

    /** Constructor receiving values for all attributes of same name.
     * @param testObject the object on which to invoke the test method
     * @param method the test method to invoke
     * @param args the arguments to pass on method invocation */
    public AludraTestMethodInvoker(AludraTestCase testObject, Method method, Object[] args) {
        // check preconditions
        Assert.notNull(testObject, "testObject");
        Assert.notNull(method, "method");
        int expectedArgCount = method.getParameterTypes().length;
        int providedArgCount = (args != null ? args.length : 0);
        if (expectedArgCount != providedArgCount) {
            throw new IllegalArgumentException("Method" + method.getName() + " expects " + expectedArgCount + " parameters, " + "but got " + providedArgCount);
        }

        // assign fields
        this.testObject = testObject;
        this.method = method;
        this.args = (args != null ? args.clone() : null);
    }

    /** Sets the testCase property on the {@link #testObject}. */
    @Override
    public void setContext(AludraTestContext context) {
        this.testObject.setContext(context);
    }

    /** First executes all {@literal @}Before methods,
     *  then the test method and finally all {@literal @}After
     *  methods on the {@link #testObject}  */
    @Override
    public void invoke() throws Exception { //NOSONAR
        executeBefores(testObject.getClass());
        try {
            this.method.invoke(testObject, args);
        } finally {
            executeAfters(testObject.getClass());
        }
    }

    @Override
    public Class<?> getTestClass() {
        return testObject.getClass();
    }

    /** Creates a String representation of the object. */
    @Override
    public String toString() {
        return method.getDeclaringClass().getName() + '.' + method.getName() + '(' + ArrayFormat.format(args) + ')';
    }

    // private helper methods ------------------------------------------------------------------------------------------

    /** Executes all {@literal @}Before methods of the {@link #testObject}
     *  and its parent classes (parent classes first). */
    private void executeBefores(Class<?> type) {
        Class<?> superclass = type.getSuperclass();
        if (!Object.class.equals(superclass)) {
            executeBefores(superclass);
        }
        for (Method method : type.getDeclaredMethods()) {
            Before before = method.getAnnotation(Before.class);
            if (before != null) {
                verifyBeforeAfterMethod(method);
                invokeObjectMethod(method, testObject);
            }
        }
    }

    /** Executes all {@literal @}After methods of the {@link #testObject}
     *  and its parent classes (child classes first). */
    private void executeAfters(Class<?> type) {
        for (Method method : type.getDeclaredMethods()) {
            After after = method.getAnnotation(After.class);
            if (after != null) {
                verifyBeforeAfterMethod(method);
                invokeObjectMethod(method, testObject);
            }
        }
        Class<?> superclass = type.getSuperclass();
        if (!Object.class.equals(superclass)) {
            executeAfters(superclass);
        }
    }

    /** Invokes the given {@literal @}Before or {@literal @}After method
     * on the given object */
    private static void invokeObjectMethod(Method method, Object targetObject) {
        try {
            method.invoke(targetObject);
        } catch (Exception e) {
            throw new TechnicalException("Before or After method fails! " + "Please contact Testautomation Team! \n" + "Message: " + e.getMessage() + "\n" + "Cause: " + e.getCause().getMessage(), e);
        }
    }

    /** Verifies that a {@literal @}Before or {@literal @}After method
     *  fulfills all syntactical requirements to be invoked properly. */
    private static void verifyBeforeAfterMethod(Method method) {
        if ((method.getModifiers() & Modifier.PUBLIC) != Modifier.PUBLIC) {
            throw new AutomationException("Before/After methods must be public: " + method);
        }
        if ((method.getModifiers() & Modifier.STATIC) == Modifier.STATIC) {
            throw new AutomationException("Before/After methods must not be static: " + method);
        }
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes != null && parameterTypes.length > 0) {
            throw new AutomationException("Before/After methods must not have parameters: " + method);
        }
    }

}
