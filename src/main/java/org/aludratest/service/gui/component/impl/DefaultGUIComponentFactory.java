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
package org.aludratest.service.gui.component.impl;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.aludratest.exception.TechnicalException;
import org.aludratest.service.gui.AludraGUI;
import org.aludratest.service.gui.component.Button;
import org.aludratest.service.gui.component.Checkbox;
import org.aludratest.service.gui.component.Dropdownbox;
import org.aludratest.service.gui.component.FileField;
import org.aludratest.service.gui.component.GUIComponent;
import org.aludratest.service.gui.component.GUIComponentFactory;
import org.aludratest.service.gui.component.GenericElement;
import org.aludratest.service.gui.component.InputField;
import org.aludratest.service.gui.component.Label;
import org.aludratest.service.gui.component.Link;
import org.aludratest.service.gui.component.RadioButton;
import org.aludratest.service.gui.component.Window;
import org.aludratest.service.locator.Locator;
import org.aludratest.service.locator.element.GUIElementLocator;
import org.aludratest.service.locator.window.TitleLocator;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.component.composition.CycleDetectedInComponentGraphException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

/** Default implementation of the {@link GUIComponentFactory} interface. Uses Plexus to instantiate the components. GUI service
 * implementors can subclass this class to provide own component implementor classes or additional component configuration.
 * 
 * @author falbrech */
@Component(role = GUIComponentFactory.class, hint = "default")
public class DefaultGUIComponentFactory implements GUIComponentFactory {

    private static final Map<Class<? extends GUIComponent>, Class<? extends GUIComponent>> componentImplClasses = new HashMap<Class<? extends GUIComponent>, Class<? extends GUIComponent>>();
    static {
        componentImplClasses.put(Button.class, ButtonImpl.class);
        componentImplClasses.put(Checkbox.class, CheckboxImpl.class);
        componentImplClasses.put(Dropdownbox.class, DropdownboxImpl.class);
        componentImplClasses.put(FileField.class, FileFieldImpl.class);
        componentImplClasses.put(GenericElement.class, GenericElementImpl.class);
        componentImplClasses.put(InputField.class, InputFieldImpl.class);
        componentImplClasses.put(Label.class, LabelImpl.class);
        componentImplClasses.put(Link.class, LinkImpl.class);
        componentImplClasses.put(RadioButton.class, RadioButtonImpl.class);
        componentImplClasses.put(Window.class, WindowImpl.class);
    }

    private AludraGUI aludraGUI;

    @Requirement
    private PlexusContainer plexusContainer;

    /** Sets the AludraGUI service to use for this component factory.
     * 
     * @param aludraGUI AludraGUI service to use. */
    public void setAludraGUI(AludraGUI aludraGUI) {
        this.aludraGUI = aludraGUI;
    }

    @Override
    public Button createButton(GUIElementLocator locator) {
        return createComponent(Button.class, locator);
    }

    @Override
    public Button createButton(GUIElementLocator locator, String elementName) {
        return createComponent(Button.class, locator, elementName);
    }

    @Override
    public Checkbox createCheckbox(GUIElementLocator locator) {
        return createComponent(Checkbox.class, locator);
    }

    @Override
    public Checkbox createCheckbox(GUIElementLocator locator, String elementName) {
        return createComponent(Checkbox.class, locator, elementName);
    }

    @Override
    public Dropdownbox createDropdownbox(GUIElementLocator locator) {
        return createComponent(Dropdownbox.class, locator);
    }

    @Override
    public Dropdownbox createDropdownbox(GUIElementLocator locator, String elementName) {
        return createComponent(Dropdownbox.class, locator, elementName);
    }

    @Override
    public FileField createFileField(GUIElementLocator locator) {
        return createComponent(FileField.class, locator);
    }

    @Override
    public FileField createFileField(GUIElementLocator locator, String elementName) {
        return createComponent(FileField.class, locator, elementName);
    }

    @Override
    public InputField createInputField(GUIElementLocator locator) {
        return createComponent(InputField.class, locator);
    }

    @Override
    public InputField createInputField(GUIElementLocator locator, String elementName) {
        return createComponent(InputField.class, locator, elementName);
    }

    @Override
    public Label createLabel(GUIElementLocator locator) {
        return createComponent(Label.class, locator);
    }

    @Override
    public Label createLabel(GUIElementLocator locator, String elementName) {
        return createComponent(Label.class, locator, elementName);
    }

    @Override
    public Link createLink(GUIElementLocator locator) {
        return createComponent(Link.class, locator);
    }

    @Override
    public Link createLink(GUIElementLocator locator, String elementName) {
        return createComponent(Link.class, locator, elementName);
    }

    @Override
    public RadioButton createRadioButton(GUIElementLocator locator) {
        return createComponent(RadioButton.class, locator);
    }

    @Override
    public RadioButton createRadioButton(GUIElementLocator locator, String elementName) {
        return createComponent(RadioButton.class, locator, elementName);
    }

    @Override
    public Window createWindow(TitleLocator locator) {
        return createComponent(Window.class, locator);
    }

    @Override
    public Window createWindow(TitleLocator locator, String elementName) {
        return createComponent(Window.class, locator, elementName);
    }

    @Override
    public GenericElement createGenericElement(GUIElementLocator locator) {
        return createComponent(GenericElement.class, locator);
    }

    @Override
    public GenericElement createGenericElement(GUIElementLocator locator, String elementName) {
        return createComponent(GenericElement.class, locator, elementName);
    }

    /** Returns the implementor class for the given component class. Subclasses can override to specify own implementation classes,
     * but you <b>must</b> also override {@link #getRoleHint()}.
     * 
     * @param componentClass Class of the component, e.g. <code>Button.class</code>.
     * 
     * @return The implementing component class, or <code>null</code> if no implementor class is available for the given component
     *         class. Do <b>not</b> throw any exception; the calling method will deal with this. */
    protected <T extends GUIComponent> Class<? extends T> getImplementorClass(Class<T> componentClass) {
        @SuppressWarnings("unchecked")
        Class<? extends T> implClass = (Class<? extends T>) componentImplClasses.get(componentClass);
        return implClass;
    }

    /** Returns the role hint which is used by this component factory. If you want to register your own implementations of the
     * component classes, you also have to override this method to avoid name clashes in the internal Plexus component registry.
     * 
     * @return The role hint to use for this component factory. */
    protected String getRoleHint() {
        return "default";
    }

    /** Configures the given freshly instantiated component to use the given identifiers. Subclasses can add more configuration
     * logic, but should always invoke the super implementation.
     * 
     * @param component Component to configure.
     * @param locator Locator to set in the component.
     * @param componentClass Component class identifying the type of the component, e.g. <code>Button.class</code>.
     * @param elementName Element name which was set in the factory method call or automatically determined if possible, or
     *            <code>null</code>. */
    protected void configureComponent(GUIComponent component, Locator locator, Class<?> componentClass, String elementName) {
        if (component instanceof AbstractGUIComponent) {
            ((AbstractGUIComponent) component).configure(aludraGUI, locator, componentClass.getSimpleName(), elementName);
        }
    }

    protected final <T extends GUIComponent> T createComponent(Class<T> componentClass, Locator locator) {
        return createComponent(componentClass, locator, determineDefaultElementName());
    }

    protected final <T extends GUIComponent> T createComponent(Class<T> componentClass, Locator locator, String elementName) {
        Class<? extends T> implClass = getImplementorClass(componentClass);
        if (implClass == null) {
            throw new TechnicalException("No implementation class found for component class " + componentClass.getName());
        }

        try {
            // check if Plexus container already "knows" this component class
            if (plexusContainer.getComponentDescriptor(componentClass.getName(), getRoleHint()) == null) {
                registerComponentDescriptor(componentClass, implClass);
            }

            T component = plexusContainer.lookup(componentClass, getRoleHint());
            configureComponent(component, locator, componentClass, elementName);
            return component;
        }
        catch (ComponentLookupException e) {
            throw new TechnicalException("Could not create component of type " + componentClass.getSimpleName(), e);
        }
        catch (CycleDetectedInComponentGraphException e) {
            throw new TechnicalException("Could not register component implementation of type " + componentClass.getSimpleName(),
                    e);
        }
    }

    private <T> void registerComponentDescriptor(Class<T> componentClass, Class<? extends T> implClass)
            throws CycleDetectedInComponentGraphException {
        if (!componentClass.isAssignableFrom(implClass)) {
            throw new RuntimeException("Class " + implClass.getName() + " does not implement interface "
                    + componentClass.getName());
        }

        ComponentDescriptor<T> desc = new ComponentDescriptor<T>();
        desc.setRole(componentClass.getName());
        desc.setRoleClass(componentClass);
        desc.setRoleHint(getRoleHint());
        desc.setImplementationClass(implClass);
        desc.setIsolatedRealm(false);
        desc.setInstantiationStrategy("per-lookup");

        ClassRealm realm = plexusContainer.getLookupRealm();
        if (realm == null) {
            realm = plexusContainer.getContainerRealm();
        }
        desc.setRealm(realm);

        // lookup Requirements in class and parent classes
        Class<?> clazz = implClass;
        while (clazz != null && clazz != Object.class) {
            for (Field f : implClass.getDeclaredFields()) {
                Requirement req = f.getAnnotation(Requirement.class);
                if (req != null) {
                    desc.addRequirement(createComponentRequirement(f, req));
                }

            }
            clazz = clazz.getSuperclass();
        }

        plexusContainer.addComponentDescriptor(desc);
    }

    private static ComponentRequirement createComponentRequirement(Field field, Requirement requirement) {
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

    private static String determineDefaultElementName() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String lastMethodName = null;

        for (StackTraceElement ste : stackTrace) {
            String className = ste.getClassName();
            if (className != null && className.contains("UIMap")) {
                lastMethodName = ste.getMethodName();
            }
        }

        // getCloseButton() => closeButton
        if (lastMethodName != null && lastMethodName.startsWith("get") && lastMethodName.length() > 3) {
            char ch = lastMethodName.charAt(3);
            // could also be "getterButton()", so only remove when uppercase follows
            if (Character.isUpperCase(ch)) {
                lastMethodName = Character.toLowerCase(ch) + lastMethodName.substring(4);
            }
        }

        return lastMethodName;
    }

}
