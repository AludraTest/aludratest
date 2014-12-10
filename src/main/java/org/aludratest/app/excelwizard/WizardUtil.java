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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.aludratest.testcase.AludraTestCase;
import org.aludratest.testcase.Test;
import org.aludratest.testcase.data.Source;
import org.databene.commons.BeanUtil;
import org.databene.commons.OrderedMap;

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

    /** Searches an array of annotations for a Source annotation that points to an Excel document.
     * @param annotations the array of annotations to examine
     * @return the first Source annotation that points to an Excel document or null if none is in the array */
    public static Source findExcelSourceAnnotation(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof Source) {
                Source source = (Source) annotation;
                String uri = source.uri();
                if (uri.endsWith(".xls") || uri.endsWith(".xlsx")) {
                    return source;
                }
            }
        }
        return null;
    }

    /** Provides a {@link Map} of all properties and public attributes of a class assigned to their Java type.
     * @param type the Java class to examine
     * @return a {@link Map} of all properties and public attributes of a class assigned to their Java type */
    public static Map<String, Class<?>> getFeatures(Class<?> type) {
        Map<String, Class<?>> features = new OrderedMap<String, Class<?>>();
        for (Field field : type.getDeclaredFields()) {
            if (WizardUtil.isPublic(field) || BeanUtil.hasProperty(type, field.getName())) {
                features.put(field.getName(), field.getType());
            }
        }
        return features;
    }

    /** Tells if a Java attribute is public.
     * @param field the {@link Field} to examine
     * @return true if the field is public, otherwise false */
    public static boolean isPublic(Field field) {
        return (field.getModifiers() & Modifier.PUBLIC) == Modifier.PUBLIC;
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
            if (findExcelSourceAnnotation(paramAnnos) != null) {
                return true;
            }
        }
        return false;
    }

}
