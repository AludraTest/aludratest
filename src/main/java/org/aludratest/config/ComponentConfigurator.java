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
import java.util.Enumeration;
import java.util.Properties;

import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/** Helper class and entry point to AludraTest Configuration Service.
 * 
 * @author falbrech */
public class ComponentConfigurator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentConfigurator.class);

    private static Configurator configurator;

    private ComponentConfigurator() {
    }

    private static Configurator getConfigurator() {
        if (configurator == null) {
            // use on-the-fly Plexus to get instance
            DefaultPlexusContainer container = null;
            try {
                container = new DefaultPlexusContainer();
                configurator = container.lookup(Configurator.class);
            }
            catch (PlexusContainerException e) {
                LOGGER.error("Could not create on-the-fly IoC container", e);
            }
            catch (ComponentLookupException e) {
                LOGGER.error("Could not find configurator implementation", e);
            }
            finally {
                if (container != null) {
                    container.dispose();
                }
            }
        }
        return configurator;
    }

    /** Configures the given configurable object.
     * 
     * @param configurable Object to configure.
     * 
     * @deprecated Please prefer one of these methods to configure your objects:
     *             <ol>
     *             <li>Retrieve your objects via <code>AludraServiceManager</code>, <code>AludraTestContext</code> or
     *             <code>AludraServiceContext</code>, and their methods to instantiate new objects. These will automatically
     *             configure the objects.</li>
     *             <li>If you require to directly configure an object, retrieve an {@link Configurator} instance from one of the
     *             mentioned classes, and use it to configure your object.</li>
     *             </ol> */
    @Deprecated
    public static void configure(Configurable configurable) {
        getConfigurator().configure(configurable);
    }

    /** Configures the given configurable object, instantiated for the given instance name.
     * 
     * @param instanceName Name of the instance, which is used for configuration lookup.
     * @param configurable Object to configure.
     * 
     * @deprecated Please prefer one of these methods to configure your objects:
     *             <ol>
     *             <li>Retrieve your objects via <code>AludraServiceManager</code>, <code>AludraTestContext</code> or
     *             <code>AludraServiceContext</code>, and their methods to instantiate new objects. These will automatically
     *             configure the objects.</li>
     *             <li>If you require to directly configure an object, retrieve an {@link Configurator} instance from one of the
     *             mentioned classes, and use it to configure your object.</li>
     *             </ol> */
    @Deprecated
    public static void configure(String instanceName, Configurable configurable) {
        getConfigurator().configure(instanceName, configurable);
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
        InputStream in = classLoader.getResourceAsStream(resourceName);
        if (in == null) {
            return;
        }

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
