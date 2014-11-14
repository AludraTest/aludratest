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
package org.aludratest.app.excelwizard;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.aludratest.testcase.AludraTestCase;
import org.aludratest.testcase.Test;
import org.aludratest.testcase.data.Source;

/**
 * Provides utility methods for AludraTest's wizards.
 * @author Volker Bergmann
 */
public class WizardUtil {

    private WizardUtil() { }

    /**
     * Tells if the specified class is an AludraTest test class with Excel sources.
     * @param clazz the class to examine
     * @return true if the specified class is an AludraTest test class with Excel sources, otherwise false
     */
    public static boolean isAludraTestClassWithExcelBasedTests(Class<?> clazz) {
        return (AludraTestCase.class.isAssignableFrom(clazz) && 
                !getTestMethodsWithExcelSource(clazz).isEmpty());
    }
    

    /** Determines all methods of a class that have a Test annotation and 
     * at least one parameter marked with a Source annotation that references 
     * the URI of an Excel document.
     * @param testClass the class to examine
     * @return a List with all matching methods of the class
     */
    public static List<Method> getTestMethodsWithExcelSource(Class<?> testClass) {
        List<Method> testMethods = new ArrayList<Method>();
        if (testClass != null) {
            for (Method method : testClass.getMethods()) {
                if (isTestMethodWithSource(method)) {
                    testMethods.add(method);
                }
            }
        }
        return testMethods;
    }


    // private helpers ---------------------------------------------------------

    private static boolean isTestMethodWithSource(Method method) {
        return isPublic(method) && 
                !isAbstract(method) && 
                method.getAnnotation(Test.class) != null &&
                hasExcelSourceAnnotation(method);
    }

    private static boolean isPublic(Method method) {
        return ((method.getModifiers() & Modifier.PUBLIC) == Modifier.PUBLIC);
    }

    private static boolean isAbstract(Method method) {
        return ((method.getModifiers() & Modifier.ABSTRACT) == Modifier.ABSTRACT);
    }

    private static boolean hasExcelSourceAnnotation(Method method) {
        for (Annotation[] paramAnnos : method.getParameterAnnotations()) {
            for (Annotation paramAnno : paramAnnos) {
                if (paramAnno instanceof Source) {
                    String uri = ((Source) paramAnno).uri();
                    if (uri.endsWith(".xls") || uri.endsWith(".xlsx")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
