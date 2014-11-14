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

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.aludratest.config.ComponentConfigurator;
import org.aludratest.config.Configurable;
import org.aludratest.config.ConfigurationException;
import org.aludratest.config.Configurator;
import org.aludratest.config.InternalComponent;
import org.aludratest.config.MutablePreferences;
import org.aludratest.config.Preferences;
import org.aludratest.exception.AludraTestException;
import org.aludratest.exception.AutomationException;
import org.aludratest.exception.TechnicalException;
import org.aludratest.impl.log4testing.data.TestCaseLog;
import org.aludratest.impl.log4testing.util.LogUtil;
import org.aludratest.service.AludraService;
import org.aludratest.service.AludraServiceContext;
import org.aludratest.service.ComponentId;
import org.aludratest.testcase.AludraTestContext;
import org.aludratest.testcase.TestStatus;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.component.composition.CycleDetectedInComponentGraphException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.databene.commons.BeanUtil;
import org.slf4j.LoggerFactory;

/**
 * The bootstrap for Aludra Components like e.g. services. This manager is itself a configurable component, and its only
 * configuration is the list of interfaces and the implementing classes. Unlike other services, this implementation is not to be
 * replaced, and directly referenced by AludraTest startup core.
 * 
 * @author falbrech
 * 
 */
public final class AludraServiceManager implements Configurable {

    /** The init() method of the interface {@link AludraService}. */
    private static final Method CONFIGURE_METHOD = BeanUtil.getMethod(AludraService.class, "init",
            new Class[] { AludraServiceContext.class });

    private Preferences configuration;

    private PlexusContainer container;

    /** Constructs a new AludraServiceManager instance and loads its configuration.
     * @param iocContainer The IoC container to use for object lookup and instantiation. */
    public AludraServiceManager(PlexusContainer iocContainer) {
        this.container = iocContainer;

        try {
            Configurator configurator = iocContainer.lookup(Configurator.class);
            configurator.configure(this);
        }
        catch (ComponentLookupException e) {
            throw new TechnicalException("Could not lookup configurator object", e);
        }
    }

    @Override
    public String getPropertiesBaseName() {
        return "aludraservice";
    }

    @Override
    public void fillDefaults(MutablePreferences preferences) {
        try {
            ComponentConfigurator.fillPreferencesFromPropertiesResource(preferences, "aludraservice.properties.default",
                    AludraServiceManager.class.getClassLoader());
        }
        catch (IOException e) {
            LoggerFactory.getLogger(AludraServiceManager.class).error(
                    "Could not load default preferences from properties resource", e);
        }
    }

    @Override
    public void configure(Preferences preferences) {
        this.configuration = preferences;
    }

    private <T> ComponentDescriptor<T> createComponentDescriptor(Class<T> iface, String instanceName, ClassRealm realm)
            throws ClassNotFoundException {
        ComponentDescriptor<T> cd = new ComponentDescriptor<T>();
        cd.setRole(iface.getName());
        cd.setRoleClass(iface);
        if (instanceName != null) {
            cd.setRoleHint(instanceName);
        }

        String implClass = configuration.getStringValue(iface.getName());
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

    /**
     * Creates and configures a service instance.
     * 
     * @param serviceId
     *            A characterization of the requested service.
     * @param context
     *            Current test context.
     * 
     * @return the service instance
     **/
    public <T extends AludraService> T createAndConfigureService(ComponentId<T> serviceId, AludraTestContext context) {
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

        TestCaseLog log = context.getTestCaseLog();
        AludraServiceContext serviceContext = new AludraServiceContextImpl(context, instanceName);

        // instantiate and initialize service
        try {
            T service = newImplementorInstance(serviceInterface, serviceId.getInstanceName());
            service.init(serviceContext);
            if (log != null) {
                log.newTestStepGroup(serviceId + ": " + service.getDescription());
            }
            return service;
        }
        catch (Exception e) {
            if (log != null) {
                log.newTestStepGroup(serviceId.toString());
                LogUtil.log(log, serviceId, CONFIGURE_METHOD, TestStatus.INCONCLUSIVE, e, new Object[] { serviceContext });
            }
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

    /**
     * Selects the default implementor class configured for the requested interface and returns a new instance of it. If the
     * implementor class implements the {@link Configurable} interface, the object is configured before returning it.
     * 
     * @param iface
     *            the interface for which to create an implementor instance
     * @return a new instance of the class configured as standard implementor of the interface
     */
    public <T, U extends T> U newImplementorInstance(Class<T> iface) {
        return newImplementorInstance(iface, null);
    }

    /** Removes an instantiated singleton from the internal map of singletons, if it has already been instantiated. If it
     * implements the <code>AludraCloseable</code> interface, its <code>close()</code> method is invoked first.
     * 
     * @param iface Component or service interface class of which to remove the instantiated singleton implementation, if any. */
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

    @SuppressWarnings("unchecked")
    private <T, U extends T> U newImplementorInstance(Class<T> iface, String instanceName) {
        String implementorClassName = configuration.getStringValue(iface.getName());
        if (implementorClassName == null || implementorClassName.trim().isEmpty()) {
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
            return (U) container.lookup(iface);
        }
        catch (ComponentLookupException e) {
            throw new TechnicalException("Could not lookup implementation for class " + iface.getName(), e);
        }
    }

}
