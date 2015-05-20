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
package org.aludratest.service;

import java.io.IOException;

import org.aludratest.config.ComponentConfigurator;
import org.aludratest.config.Configurable;
import org.aludratest.config.MutablePreferences;
import org.slf4j.LoggerFactory;

/**
 * Abstract parent class for AludraTest service implementations which are configurable. Additionally to the common implementations
 * provided by {@link AbstractAludraService}, this class provides helpers and default implementations dealing with configuration
 * aspects.
 * 
 * @author falbrech
 * 
 */
public abstract class AbstractConfigurableAludraService extends AbstractAludraService implements Configurable {

    /**
     * Returns the name of the properties resource containing the default values for this service. This resource is loaded using
     * the class' ClassLoader and its <code>getResource()</code> method. The default implementation returns the value of
     * {@link #getPropertiesBaseName()}, concatenated with the suffix <code>".properties.default"</code>. Subclasses can override.
     * 
     * @return The name of the properties resource containing the default values for this service, e.g.
     *         <code>fileService.properties.default</code>. A value of <code>null</code> indicates that no default configuration
     *         is available.
     */
    protected String getDefaultsResourceName() {
        return getPropertiesBaseName() + ".properties.default";
    }

    /**
     * This default implementation asks {@link #getDefaultsResourceName()} for the resource to load from the ClassLoader and
     * retrieve its contents as default configuration values.
     * 
     * @see org.aludratest.config.Configurable#fillDefaults(org.aludratest.config.MutablePreferences)
     */
    @Override
    public void fillDefaults(MutablePreferences preferences) {
        String resourceName = getDefaultsResourceName();
        // the following line causes a false positive of Sonar. Subclasses could override getDefaultsResourceName() and return
        // null.
        if (resourceName == null) { // NOSONAR
            return;
        }
        try {
            ComponentConfigurator.fillPreferencesFromPropertiesResource(preferences, resourceName, getClass().getClassLoader());
        }
        catch (IOException e) {
            LoggerFactory.getLogger(getClass()).error("Could not load default preferences from properties resource", e);
        }
    }

}
