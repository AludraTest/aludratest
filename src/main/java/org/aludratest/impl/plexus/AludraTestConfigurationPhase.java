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
package org.aludratest.impl.plexus;

import org.aludratest.config.Configurable;
import org.aludratest.config.Configurator;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.lifecycle.phase.Phase;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.PhaseExecutionException;

/** A Plexus Component Lifecycle Phase for configuration of AludraTest configurable objects (different from Plexus XML-based
 * configuration). Looks up a {@link Configurator} using the Plexus Container, and uses it for configuration.
 * 
 * @author falbrech */
public class AludraTestConfigurationPhase implements Phase {

    @Override
    public void execute(Object component, @SuppressWarnings("rawtypes") ComponentManager manager, ClassRealm realm)
            throws PhaseExecutionException {
        if (component instanceof Configurable) {
            // get a configurator
            Configurator configurator;
            try {
                configurator = manager.getContainer().lookup(Configurator.class);
            }
            catch (ComponentLookupException e) {
                throw new PhaseExecutionException("Could not allocate configurator", e);
            }

            String instanceName = manager.getRoleHint();
            if (instanceName == null || "default".equals(instanceName)) {
                configurator.configure((Configurable) component);
            }
            else {
                configurator.configure(instanceName, (Configurable) component);
            }
        }
    }

}
