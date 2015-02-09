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

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import org.aludratest.config.Configurator;
import org.aludratest.config.InternalComponent;
import org.aludratest.service.impl.AludraServiceRegistry;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.component.discovery.ComponentDiscoverer;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.component.repository.ComponentSetDescriptor;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.context.Context;

public class AludraTestComponentDiscoverer implements ComponentDiscoverer {

    private Configurator configurator;

    public AludraTestComponentDiscoverer(Configurator configurator) {
        this.configurator = configurator;
    }

    @Override
    public List<ComponentSetDescriptor> findComponents(Context context, ClassRealm classRealm)
            throws PlexusConfigurationException {
        AludraServiceRegistry registry = new AludraServiceRegistry();
        configurator.configure(registry);

        ComponentSetDescriptor descriptor = new ComponentSetDescriptor();

        for (String roleName : registry.getRegisteredInterfaceNames()) {
            String implClass = registry.getImplementationClassName(roleName);
            if (implClass == null || implClass.trim().isEmpty()) {
                throw new PlexusConfigurationException("Missing implementation class for interface " + roleName);
            }
            implClass = implClass.trim();
            try {
                Class<?> clazz = classRealm.loadClass(implClass);
                descriptor.addComponentDescriptor(buildComponentDescriptor(classRealm.loadClass(roleName), clazz, classRealm));
            }
            catch (ClassNotFoundException e) {
                throw new PlexusConfigurationException("Implementation class not found", e);
            }
        }

        return Collections.singletonList(descriptor);
    }

    private <T> ComponentDescriptor<T> buildComponentDescriptor(Class<?> ifaceClass, Class<T> implClass, ClassRealm realm) {
        ComponentDescriptor<T> desc = new ComponentDescriptor<T>(implClass, realm);
        desc.setRole(ifaceClass.getName());

        // check for AludraTest singleton strategy
        InternalComponent ic = ifaceClass.getAnnotation(InternalComponent.class);
        if (ic != null && ic.singleton()) {
            desc.setInstantiationStrategy("singleton");
        }

        // check for Plexus Requirements
        for (Field f : implClass.getDeclaredFields()) {
            Requirement req = f.getAnnotation(Requirement.class);
            if (req != null) {
                desc.addRequirement(buildRequirement(f, req, realm));
            }
        }

        return desc;
    }

    private ComponentRequirement buildRequirement(Field field, Requirement requirement, ClassRealm realm) {
        ComponentRequirement req = new ComponentRequirement();
        req.setFieldName(field.getName());

        if (requirement.role() != null && requirement.role() != Object.class) {
            req.setRole(requirement.role().getName());
        }
        else {
            req.setRole(field.getType().getName());
        }

        if (requirement.hint() != null && requirement.hint().length() > 0) {
            req.setRoleHint(requirement.hint());
        }

        if (requirement.optional()) {
            req.setOptional(true);
        }

        return req;
    }

}
