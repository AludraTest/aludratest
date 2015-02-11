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
package org.test.testclasses.service;

import org.aludratest.config.ConfigProperty;
import org.aludratest.config.Configurable;
import org.aludratest.config.MutablePreferences;
import org.aludratest.config.Preferences;
import org.aludratest.service.Implementation;

@Implementation({ MoreComplexComponent.class })
@ConfigProperty(name = "implProp", type = float.class, description = "A float value", defaultValue = "2.0")
public class MoreComplexComponentImpl implements Configurable, MoreComplexComponent {

    private MutablePreferences defaultPreferences;

    public boolean configured;

    public MutablePreferences getDefaultPreferences() {
        return defaultPreferences;
    }

    @Override
    public String getPropertiesBaseName() {
        return "mcc";
    }

    @Override
    public void fillDefaults(MutablePreferences preferences) {
        preferences.setValue("dynamicValue", "!");
        this.defaultPreferences = preferences;
    }

    @Override
    public void configure(Preferences preferences) {
        configured = true;
    }

}
