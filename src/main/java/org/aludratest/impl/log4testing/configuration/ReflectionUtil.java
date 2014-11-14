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
package org.aludratest.impl.log4testing.configuration;

import java.util.Map;

import org.apache.commons.lang.text.StrLookup;
import org.apache.commons.lang.text.StrSubstitutor;
import org.databene.commons.BeanUtil;
import org.dom4j.Element;

/** 
 * Utility class for object creation and manipulation.
 * @author Volker Bergmann
 */
public class ReflectionUtil {

    /** Private constructor for preventing instantiation of utility class */
    private ReflectionUtil() {
    }

    /**
     * Creates a JavaBean instance and initializes it as defined in the beanElement argument.
     * @param beanElement an Element holding the configuration
     * @param expectedType an expected parent class
     * @return a JavaBean instance configured as specified by the beanElement argument
     */
    public static <T> T createBean(Element beanElement, Class<T> expectedType) {
        // extract class name and instantiate object by default constructor
        Element classElement = beanElement.element("class");
        String className = classElement.getText().trim();
        T bean = newInstance(className, expectedType);
        // set properties
        Element propsElement = beanElement.element("properties");
        if (propsElement != null) {
            mapProperties(bean, propsElement);
        }
        if (bean instanceof Validatable) {
            ((Validatable) bean).validate();
        }
        return bean;
    }


    // private helper methods --------------------------------------------------
    @SuppressWarnings("unchecked")
    private static <T> T newInstance(String className, Class<T> expectedType) {
        Object bean = BeanUtil.newInstance(className);
        if (!expectedType.isAssignableFrom(bean.getClass())) {
            throw new ConfigurationError(bean.getClass() + " does not " + "implement or extend " + expectedType.getName());
        }
        return (T) bean;
    }

    private static void mapProperties(Object bean, Element propsElement) {
        for (Object node : propsElement.elements()) {
            String propertyName = ((Element) node).getName().trim();
            String propertyValue = substituteVariables(((Element) node).getText()).trim();
            BeanUtil.setPropertyValue(bean, propertyName, propertyValue, true, true);
        }
    }

    private static String substituteVariables(String param) {
        String result = substituteEnvVariables(param);
        result = substituteSystemProperties(result);
        return result;
    }

    private static String substituteSystemProperties(String param) {
        final StrLookup systemLookup = StrLookup.systemPropertiesLookup();
        final StrSubstitutor substitutor = new StrSubstitutor(systemLookup);
        return substitutor.replace(param);
    }

    private static String substituteEnvVariables(String param) {
        final Map<String, String> envVariables = System.getenv();
        return StrSubstitutor.replace(param, envVariables);
    }

}
