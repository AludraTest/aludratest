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
package org.aludratest.config.impl;

import org.aludratest.config.ConfigProperties;
import org.aludratest.config.ConfigProperty;
import org.aludratest.config.Configurable;
import org.aludratest.config.Configurator;
import org.codehaus.plexus.component.annotations.Component;

@Component(role = Configurator.class)
public class DefaultConfigurator implements Configurator {

    public DefaultConfigurator() {
    }

    @Override
    public void configure(Configurable configurable) {
        SimplePreferences defPrefs = new SimplePreferences();

        // parse annotations into preferences, if present
        fillPreferencesFromAnnotations(defPrefs, configurable.getClass());
        configurable.fillDefaults(defPrefs);

        String componentId = configurable.getPropertiesBaseName();

        PropertyPriorityPreferences prefs = new PropertyPriorityPreferences(componentId, defPrefs);
        configurable.configure(prefs);
    }

    @Override
    public void configure(String instanceName, Configurable configurable) {
        SimplePreferences defPrefs = new SimplePreferences();

        // parse annotations into preferences, if present
        fillPreferencesFromAnnotations(defPrefs, configurable.getClass());
        configurable.fillDefaults(defPrefs);

        String componentId = configurable.getPropertiesBaseName();

        PropertyPriorityPreferences prefs = new PropertyPriorityPreferences(componentId, instanceName, defPrefs);
        configurable.configure(prefs);
    }

    private static void fillPreferencesFromAnnotations(SimplePreferences prefs, Class<?> clazz) {
        // parent class and interfaces first
        if (clazz.getSuperclass() != null && !Object.class.equals(clazz.getSuperclass())) {
            fillPreferencesFromAnnotations(prefs, clazz.getSuperclass());
        }
        for (Class<?> ifClass : clazz.getInterfaces()) {
            fillPreferencesFromAnnotations(prefs, ifClass);
        }

        ConfigProperties props = clazz.getAnnotation(ConfigProperties.class);
        if (props != null) {
            for (ConfigProperty prop : props.value()) {
                fillPreferencesFromAnnotation(prefs, prop);
            }
        }
        ConfigProperty prop = clazz.getAnnotation(ConfigProperty.class);
        if (prop != null) {
            fillPreferencesFromAnnotation(prefs, prop);
        }
    }

    private static void fillPreferencesFromAnnotation(SimplePreferences prefs, ConfigProperty prop) {
        if (prop.defaultValue() != null && !"".equals(prop.defaultValue())) {
            prefs.setValue(prop.name(), prop.defaultValue());
        }
    }
}
