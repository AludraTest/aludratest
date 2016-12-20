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

import java.lang.reflect.Field;

import org.aludratest.config.InternalComponent;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.component.repository.ComponentRequirement;

/** Utility class for {@link AludraServiceManagerImpl}.
 * @author falbrech
 * @author Volker Bergmann */
public class AludraServiceUtil {

    private AludraServiceUtil() {
    }

    public static ComponentRequirement createComponentRequirement(Field field, Requirement requirement) {
        ComponentRequirement cr = new ComponentRequirement();
        cr.setFieldName(field.getName());
        cr.setOptional(requirement.optional());
        if (requirement.role() != null && requirement.role() != Object.class) {
            cr.setRole(requirement.role().getName());
        }
        else {
            cr.setRole(field.getType().getName());
        }
        if (requirement.hint() != null) {
            cr.setRoleHint(requirement.hint());
        }

        return cr;
    }

    public static boolean isSingleton(Class<?> iface) {
        // check if interface class is marked as singleton; if so, check singleton map
        if (iface.isAnnotationPresent(InternalComponent.class)) {
            InternalComponent ic = iface.getAnnotation(InternalComponent.class);
            return ic.singleton();
        }
        return false;
    }

}
