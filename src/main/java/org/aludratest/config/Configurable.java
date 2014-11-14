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

import org.aludratest.exception.AutomationException;

/** Interface for classes which instances can be configured using the published AludraTest configuration mechanism. Service
 * implementors should derive from {@link org.aludratest.service.AbstractConfigurableAludraService}, while simple configurable
 * components would directly implement this interface. <br>
 * Instances implementing this interface will be configured automatically when they are retrieved via the
 * <code>getService()</code> or <code>newComponentInstance()</code> methods of the <code>AludraTestContext</code> object. Consider
 * annotating your classes with the {@link ConfigProperty} or {@link ConfigProperties} annotations to document available
 * configuration and provide default values for the framework's initialization methods.
 * 
 * @author falbrech */
public interface Configurable {

    /** Returns the base name for properties files for this configurable, e.g. "aludratest" if the properties files are named
     * "aludratest.properties", or "fileService" for "fileService.properties".
     * 
     * @return The base name for properties files for this configurable, never <code>null</code>. */
    public String getPropertiesBaseName();

    /** Called by the framework to let the configurable object fill its defaults in a Preferences structure. This could be a no-op,
     * or could even include creating complex sub-tree structures. <br>
     * The framework has already filled the passed preferences object with the default values derived from {@link ConfigProperty}
     * annotations, if any are present on the implementing class or any superclass or interface.
     * 
     * @param preferences The Preferences object to fill with the defaults for the configurable object. */
    public void fillDefaults(MutablePreferences preferences);

    /** Called by the framework to let a configurable object configure itself according to the passed Preferences object. The
     * object is allowed to store the passed Preferences reference and query it for configuration parameters when needed. It is
     * also OK to extract all required parameters in this method. <br>
     * In any case, this method should check the configuration for validness and throw an AutomationException when the
     * configuration is invalid.
     * 
     * @param preferences Configuration object for this configurable.
     * 
     * @throws AutomationException If the configuration contains invalid elements. */
    public void configure(Preferences preferences);

}
