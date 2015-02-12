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
package org.aludratest.data;

import org.aludratest.config.Configurable;
import org.aludratest.config.MutablePreferences;
import org.aludratest.config.Preferences;
import org.aludratest.service.Implementation;

/** Internal default implementation for the DataConfiguration interface.
 * 
 * @author falbrech */
@Implementation({ DataConfiguration.class })
public class DataConfigurationImpl implements DataConfiguration, Configurable {

    private static final String PROPERTIES_BASE_NAME = "dataConfiguration";

    private static final String NULL_MARKER_PROP_KEY = "NULL";
    private static final String EMPTY_MARKER_PROP_KEY = "EMPTY";
    private static final String ALL_MARKER_PROP_KEY = "ALL";

    private Preferences preferences;


    @Override
    public void fillDefaults(MutablePreferences preferences) {
    }

    @Override
    public void configure(Preferences preferences) {
        this.preferences = preferences;
    }

    @Override
    public String getPropertiesBaseName() {
        return PROPERTIES_BASE_NAME;
    }

    @Override
    public String getNullMarker() {
        return preferences.getStringValue(NULL_MARKER_PROP_KEY);
    }

    @Override
    public String getEmptyMarker() {
        return preferences.getStringValue(EMPTY_MARKER_PROP_KEY);
    }

    @Override
    public String getAllMarker() {
        return preferences.getStringValue(ALL_MARKER_PROP_KEY);
    }

}
