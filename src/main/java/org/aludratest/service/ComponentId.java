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

import org.databene.commons.StringUtil;

/** Identifier for an AludraTest component, e.g. a service. It is used to uniquely identify a component instance. <br>
 * An identifier consists of three parts:
 * <ul>
 * <li>The component's interface class, e.g. <code>FileService.class</code>.</li>
 * <li>The component's ID. This is automatically derived from the interface class by converting the first letter of the
 * interface's simple name to lowercase, e.g. <code>fileService</code>.</li>
 * <li>The component's <i>instance name</i>. This is normally only used for services, to be able to distinguish several service
 * instances with different configuration from each other.</li>
 * </ul>
 *
 * @param <T> The type (interface) of components being described by the component ID.
 *
 * @author Volker Bergmann
 * @author falbrech */
public final class ComponentId<T> {

    private String instanceName;

    private final Class<T> interfaceClass;

    /** Creates a new ComponentId for a given interface class only. This method should only be used for component types where no
     * different configuration between instances is required. For services, consider using {@link #create(Class, String)}.
     * @param <T> the type of the component to create
     * @param componentInterfaceClass Interface class of the component.
     * 
     * @return An identifier for the given component type, which can be used to create instances of this component. */
    public static final <T> ComponentId<T> create(Class<T> componentInterfaceClass) {
        if (componentInterfaceClass == null) {
            throw new IllegalArgumentException("No interface class specified");
        }

        return new ComponentId<T>(componentInterfaceClass);
    }

    /** Creates a new ComponentId for a given interface class and a given instance name. This allows for instance specific
     * configuration. Always consider using instance names to allow users and integrators to specify more fine-grained
     * configuration, if needed.
     * @param <T> the type of the component to create
     * @param componentInterfaceClass Interface class of the component.
     * @param instanceName Instance name for the component instance.
     * 
     * @return An identifier for the given component type and instance name, which can be used to create instances of this
     *         component. */
    public static final <T> ComponentId<T> create(Class<T> componentInterfaceClass, String instanceName) {
        if (componentInterfaceClass == null) {
            throw new IllegalArgumentException("No interface class specified");
        }
        if (StringUtil.isEmpty(instanceName)) {
            throw new IllegalArgumentException("Instance name is empty");
        }

        return new ComponentId<T>(componentInterfaceClass, instanceName);
    }

    private ComponentId(Class<T> interfaceClass) {
        this(interfaceClass, null);
    }

    private ComponentId(Class<T> interfaceClass, String instanceName) {
        this.interfaceClass = interfaceClass;
        this.instanceName = instanceName;
    }

    /** Returns the instance name of the component ID. Can be <code>null</code> to indicate the "global" variant of the component,
     * or any name to distinguish it from other components of the same type.
     *
     * @return The instance name part of this component ID, maybe <code>null</code>. */
    public String getInstanceName() {
        return instanceName;
    }

    /** @return the {@link #interfaceClass} */
    public Class<T> getInterfaceClass() {
        return interfaceClass;
    }

    @Override
    public int hashCode() {
        return 31 * interfaceClass.hashCode() + (instanceName == null ? 0 : instanceName.hashCode()); // NOSONAR
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ComponentId<?> that = (ComponentId<?>) obj;
        return this.interfaceClass.equals(that.interfaceClass)
                && (this.instanceName == null ? (that.instanceName == null) : this.instanceName.equals(that.instanceName));
    }

    @Override
    public String toString() {
        return interfaceClass.getSimpleName() + (instanceName == null ? "" : ("@" + instanceName));
    }

}
