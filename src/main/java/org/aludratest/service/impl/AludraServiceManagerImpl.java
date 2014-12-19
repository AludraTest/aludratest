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
import java.util.List;

import org.aludratest.config.ConfigurationException;
import org.aludratest.config.InternalComponent;
import org.aludratest.config.impl.DefaultConfigurator;
import org.aludratest.exception.AludraTestException;
import org.aludratest.exception.AutomationException;
import org.aludratest.exception.TechnicalException;
import org.aludratest.service.AludraContext;
import org.aludratest.service.AludraService;
import org.aludratest.service.AludraServiceContext;
import org.aludratest.service.AludraServiceManager;
import org.aludratest.service.ComponentId;
import org.aludratest.service.ServiceWrapper;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.component.composition.CycleDetectedInComponentGraphException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

/** TODO FAL javadoc
 * @author falbrech */
@Component(role = AludraServiceManager.class, instantiationStrategy = "singleton")
public class AludraServiceManagerImpl implements AludraServiceManager {

    @Requirement
    private PlexusContainer container;

    private AludraServiceRegistry serviceRegistry;

    @Requirement(role = ServiceWrapper.class)
    private List<ServiceWrapper> serviceWrappers;

    /** TODO FAL javadoc */
    public AludraServiceManagerImpl() {
        serviceRegistry = new AludraServiceRegistry();
        new DefaultConfigurator().configure(serviceRegistry);
    }

    private <T> ComponentDescriptor<T> createComponentDescriptor(Class<T> iface, String instanceName, ClassRealm realm)
            throws ClassNotFoundException {
        ComponentDescriptor<T> cd = new ComponentDescriptor<T>();
        cd.setRole(iface.getName());
        cd.setRoleClass(iface);
        if (instanceName != null) {
            cd.setRoleHint(instanceName);
        }

        String implClass = serviceRegistry.getImplementationClassName(iface.getName());
        if (implClass == null) {
            throw new AutomationException("No implementor class configured for interface " + iface.getName());
        }
        cd.setImplementation(implClass);
        cd.setInstantiationStrategy(isSingleton(iface) ? "singleton" : "per-lookup");
        cd.setRealm(realm);

        // lookup Requirements in class
        Class<?> implClassClass = realm.loadClass(implClass);
        for (Field f : implClassClass.getDeclaredFields()) {
            Requirement req = f.getAnnotation(Requirement.class);
            if (req != null) {
                cd.addRequirement(createComponentRequirement(f, req));
            }
        }

        return cd;
    }

    private ComponentRequirement createComponentRequirement(Field field, Requirement requirement) {
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

    @Override
    public <T extends AludraService> T createAndConfigureService(ComponentId<T> serviceId, AludraContext context, boolean wrap) {
        // get service details
        Class<T> serviceInterface = serviceId.getInterfaceClass();
        String instanceName = serviceId.getInstanceName();

        // check preconditions
        if (serviceInterface == null) {
            throw new IllegalArgumentException("no serviceInterface specified");
        }
        if (!serviceInterface.isInterface()) {
            throw new IllegalArgumentException(serviceInterface.getName() + " is not an interface");
        }

        AludraServiceContext serviceContext = new AludraServiceContextImpl(context, instanceName);

        // instantiate and initialize service
        try {
            T service = newImplementorInstance(serviceInterface, serviceId.getInstanceName());
            service.init(serviceContext);

            // wrap service with registered wrappers
            if (serviceWrappers != null && wrap) {
                for (ServiceWrapper wrapper : serviceWrappers) {
                    service = wrapper.wrap(service, serviceId, context);
                }
            }

            return service;
        }
        catch (Exception e) {
            if (e instanceof AludraTestException) {
                throw (AludraTestException) e;
            }
            else if (e instanceof ConfigurationException) {
                throw new AutomationException("Configuration exception when initializing " + serviceId, e);
            }
            else {
                throw new TechnicalException("Error initializing " + serviceId, e);
            }
        }
    }

    @Override
    public <T> T newImplementorInstance(Class<T> iface) {
        return newImplementorInstance(iface, null);
    }

    @Override
    public void removeSingleton(Class<?> iface) {
        Object o;
        try {
            o = container.lookup(iface);
            // release calls close() if available
            container.release(o);
        }
        catch (ComponentLookupException e) {
            // ignore - component not found
        }
        catch (ComponentLifecycleException e) {
            throw new TechnicalException("Exception when closing singleton of class " + iface.getName(), e);
        }
    }

    private boolean isSingleton(Class<?> iface) {
        // check if interface class is marked as singleton; if so, check singleton map
        if (iface.isAnnotationPresent(InternalComponent.class)) {
            InternalComponent ic = iface.getAnnotation(InternalComponent.class);
            return ic.singleton();
        }
        return false;
    }

    private <T> T newImplementorInstance(Class<T> iface, String instanceName) {
        String implementorClassName = serviceRegistry.getImplementationClassName(iface.getName());
        if ((implementorClassName == null || implementorClassName.trim().isEmpty())
                && container.getComponentDescriptor(iface.getName(), instanceName) == null) {
            // if Plexus also does not know it, exception
            throw new AutomationException("No implementor class configured for interface " + iface.getName());
        }

        // use Plexus container for lookup
        if (container.getComponentDescriptor(iface.getName(), instanceName) == null) {
            try {
                ClassRealm realm = container.getLookupRealm();
                if (realm == null) {
                    realm = container.getContainerRealm();
                }
                container.addComponentDescriptor(createComponentDescriptor(iface, instanceName, realm));
            }
            catch (CycleDetectedInComponentGraphException e) {
                throw new TechnicalException("Unexpected IoC error when instantiating object", e);
            }
            catch (ClassNotFoundException e) {
                throw new AutomationException("Could not find implementation class", e);
            }
        }

        try {
            if (instanceName == null) {
                return container.lookup(iface);
            }
            else {
                return container.lookup(iface, instanceName);
            }
        }
        catch (ComponentLookupException e) {
            throw new TechnicalException("Could not lookup implementation for class " + iface.getName(), e);
        }
    }

}
