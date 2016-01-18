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
package org.aludratest.config;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;


/** Helper class and entry point to AludraTest Configuration Service.
 * 
 * @author falbrech */
public class ComponentConfigurator {

    private ComponentConfigurator() {
    }

    /** Fills the given Preferences object with the Properties loaded from a given resource name. This is useful to implement
     * {@link Configurable#fillDefaults(MutablePreferences)}.
     * 
     * @param preferences Preferences object to fill.
     * @param resourceName Resource name to load as a Properties file.
     * @param classLoader ClassLoader to use to look for the resource.
     * 
     * @throws IOException If the resource could not be loaded. */
    public static void fillPreferencesFromPropertiesResource(MutablePreferences preferences, String resourceName,
            ClassLoader classLoader) throws IOException {
        Enumeration<URL> urls = classLoader.getResources(resourceName);

        while (urls.hasMoreElements()) {
            InputStream in = urls.nextElement().openStream();
            try {
                Properties p = new Properties();
                p.load(in);

                // copy to Preferences
                Enumeration<?> keyNames = p.propertyNames();
                while (keyNames.hasMoreElements()) {
                    String key = keyNames.nextElement().toString();
                    preferences.setValue(key, p.getProperty(key));
                }
            }
            finally {
                try {
                    in.close();
                }
                catch (IOException e2) { // NOSONAR
                }
            }
        }
    }

}
