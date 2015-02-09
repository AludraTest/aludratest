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
package org.aludratest.service.gui.component;

import org.aludratest.service.gui.AludraGUI;
import org.aludratest.service.gui.GUICondition;
import org.aludratest.service.gui.GUIInteraction;
import org.aludratest.service.gui.GUIVerification;
import org.aludratest.service.locator.Locator;

/**
 * Parent class for all Components of a Graphical User Interface.
 * @author Marcel Malitz
 * @author Volker Bergmann
 */
public abstract class GUIComponent {
    
    protected AludraGUI aludraGui;
    protected Locator locator;
    protected String elementType;
    protected String elementName;
    
    
    // constructors ------------------------------------------------------------
    
    /** Constructor using the default component naming scheme.
     *  @param aludraGui the {@link AludraGUI} instance this GUI component is connected to
     *  @param locator the {@link Locator} by which the {@link AludraGUI} can query the component */
    protected GUIComponent(AludraGUI aludraGui, Locator locator) {
        this(aludraGui, locator, null);
    }
    
    /**
     * Constructor applying a custom component name.
     * 
     * @param aludraGui
     *            the {@link AludraGUI} instance this GUI component is connected
     *            to
     * @param locator
     *            the {@link Locator} by which the {@link AludraGUI} can query
     *            the component
     * @param elementName
     *            a custom name for the element
     * 
     * @throws IllegalArgumentException
     *             if aludraGui or locator is <code>null</code>.
     * 
     * */
    protected GUIComponent(AludraGUI aludraGui, Locator locator, String elementName) {
        // make null check here to avoid later, hard to trace NPEs
        if (aludraGui == null) {
            throw new IllegalArgumentException("aludraGui is null");
        }
        if (locator == null) {
            throw new IllegalArgumentException("locator is null");
        }
        this.aludraGui = aludraGui;
        this.locator = locator;
        this.elementType = getClass().getSimpleName();
        this.elementName = (elementName != null ? elementName : defaultElementName());
    }
    
    
    // utility methods for child classes ---------------------------------------
    
    protected GUIInteraction perform() {
        return aludraGui.perform();
    }
    
    protected GUIVerification verify() {
        return aludraGui.verify();
    }
    
    protected GUICondition check() {
        return aludraGui.check();
    }
    
    
    // private helpers ---------------------------------------------------------
    
    private String defaultElementName() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        int i = indexOfConstructorCall(stackTraceElements);
        return stackTraceElements[i + 1].getMethodName();
    }
    
    private int indexOfConstructorCall(StackTraceElement[] stackTraceElements) {
        String className = getClass().getName();
        for (int i = 0; i < stackTraceElements.length; i++) {
            StackTraceElement element = stackTraceElements[i];
            if (className.equals(element.getClassName()) && "<init>".equals(element.getMethodName())) {
                return i;
            }
        }
        return -1;
    }
    
}
