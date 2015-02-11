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
package org.aludratest.service.impl;

import java.io.IOException;

import org.aludratest.config.ComponentConfigurator;
import org.aludratest.config.Configurable;
import org.aludratest.config.MutablePreferences;
import org.aludratest.config.Preferences;
import org.slf4j.LoggerFactory;

public class AludraServiceRegistry implements Configurable {

    private Preferences configuration;

    @Override
    public String getPropertiesBaseName() {
        return "aludraservice";
    }

    @Override
    public void fillDefaults(MutablePreferences preferences) {
        try {
            ComponentConfigurator.fillPreferencesFromPropertiesResource(preferences, "aludraservice.properties.default",
                    AludraServiceRegistry.class.getClassLoader());
        }
        catch (IOException e) {
            LoggerFactory.getLogger(AludraServiceRegistry.class).error(
                    "Could not load default preferences from properties resource", e);
        }
    }

    @Override
    public void configure(Preferences preferences) {
        this.configuration = preferences;
    }

    public String[] getRegisteredInterfaceNames() {
        return configuration.getKeyNames();
    }

    public String getImplementationClassName(String interfaceName) {
        return configuration.getStringValue(interfaceName);
    }

}
