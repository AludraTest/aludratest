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

import org.aludratest.exception.PerformanceFailure;
import org.aludratest.service.SystemConnector;
import org.aludratest.service.gui.AludraGUI;
import org.aludratest.service.gui.component.Element;
import org.aludratest.service.locator.element.GUIElementLocator;
import org.aludratest.util.data.helper.DataMarkerCheck;

/** Parent class for GUI Elements e.g. Button.
 * Each element can be configured to wait (or not) for the completion of tasks that are caused by user actions on the element. A
 * task completion timeout is configured by calling {@link #waitingUntilTaskCompletion(int)} and implicitly applied after the
 * execution of a {@link #click()} or {@link #doubleClick()}. Child classes are expected to behave similarly on relevant
 * operations (like e.g. select()). The wait mechanism first waits for activity to occur (indicated by a {@link SystemConnector}
 * for the related system and limited by a task start timeout). If no activity is reported, pending activities are assumed to have
 * already been finished before starting the check. If activity is reported, the task completion timeout is applied which waits
 * for the specified number of milliseconds until the connector reports the system is not busy any longer. If the timeout is
 * exceeded, the system throws a {@link PerformanceFailure}, otherwise returns normally.
 * 
 * @author Joerg Langnickel
 * @author Volker Bergmann
 * @param <E> Type of the concrete element, to be used by subclasses. */
public abstract class AbstractElement<E extends Element<E>> extends AbstractGUIComponent implements Element<E> {

    protected int taskCompletionTimeout = -1;

    @Override
    public GUIElementLocator getLocator() {
        return (GUIElementLocator) locator;
    }

    /** Activates the <i>waiting until task completion</i> feature on the underlying aludraGUI with the default value for the task
     * completion timeout. */
    @Override
    public E waitingUntilTaskCompletion() {
        return waitingUntilTaskCompletion(0);
    }

    /** Sets the {@link #taskCompletionTimeout} to the specified value.
     * @param waitTime the wait time to apply. A negative number disables the wait feature, 0 implicitly activates the
     *            {@link AludraGUI}'s default, a positive value is used as explicit timeout. */
    @Override
    @SuppressWarnings("unchecked")
    public E waitingUntilTaskCompletion(int waitTime) {
        this.taskCompletionTimeout = waitTime;
        return (E) this;
    }

    /** Asserts that the element is present */
    @Override
    public void assertPresent() {
        verify().assertElementPresent(elementType, elementName, getLocator());
    }

    /** Asserts that the element is not present */
    @Override
    public void assertNotPresent() {
        verify().assertElementNotPresent(elementType, elementName, getLocator());
    }

    /** Asserts that the element is visible */
    @Override
    public void assertVisible() {
        verify().assertVisible(elementType, elementName, getLocator());
    }

    /** Asserts that the element has the focus. */
    @Override
    public void assertFocus() {
        verify().assertHasFocus(elementType, elementName, getLocator());
    }

    /** Sets the focus on this element */
    @Override
    public void focus() {
        perform().focus(elementType, elementName, getLocator());
    }

    /** Double clicks the element. */
    @Override
    public void doubleClick() {
        perform().doubleClick(elementType, elementName, getLocator(), taskCompletionTimeout);
    }

    /** Single-clicks the element. */
    @Override
    public void click() {
        perform().click(elementType, elementName, getLocator(), taskCompletionTimeout);
    }

    /** Selectable click- clicks only when provided string is not null, not marked as null and provided string is "true"
     * @param click -String */
    @Override
    public void click(String click) {
        if (!DataMarkerCheck.isNull(click)) {
            if (Boolean.parseBoolean(click)) {
                click();
            }
        }
    }

    /** Checks if the specified element is somewhere on the page within the standard timeout.
     * 
     * @return <code>true</code> if the element was found present somewhere on the page within the standard timeout,
     *         <code>false</code> otherwise. */
    @Override
    public boolean isPresent() {
        return check().isElementPresent(elementType, elementName, getLocator());
    }

    /** Checks if the specified element is somewhere on the page and in foreground within the standard timeout.
     * 
     * @return <code>true</code> if the element was found present somewhere on the page and in foreground within the standard
     *         timeout, <code>false</code> otherwise. */
    @Override
    public boolean isPresentAndInForeground() {
        return check().isElementPresentandInForeground(elementType, elementName, getLocator());
    }

    /** Checks if the specified element is somewhere on the page within a given timeout
     * 
     * @param timeout max time to wait for the element to become present.
     * @return <code>true</code> if the element was found present somewhere on the page within the given timeout,
     *         <code>false</code> otherwise. */
    @Override
    public boolean isPresent(long timeout) {
        return check().isElementPresent(elementType, elementName, getLocator(), timeout);
    }

    /** Checks if the specified element is nowhere on the page within the standard timeout.
     * 
     * @return <code>true</code> if the element was <b>not</b> found present somewhere on the page within the standard timeout,
     *         <code>false</code> otherwise. */
    @Override
    public boolean isNotPresent() {
        return check().isElementNotPresent(elementType, elementName, getLocator());
    }

    /** Checks if the specified element is nowhere on the page within the standard timeout.
     * 
     * @param timeout time to check for the element not being present.
     * 
     * @return <code>true</code> if the element was <b>not</b> found present somewhere on the page within the given timeout,
     *         <code>false</code> otherwise. */
    @Override
    public boolean isNotPresent(long timeout) {
        return check().isElementNotPresent(elementType, elementName, getLocator(), timeout);
    }

    /** Checks if the specified element is visible within the standard timeout.
     * 
     * @return <code>true</code> if the element is visible, <code>false</code> otherwise. */
    @Override
    public boolean isVisible() {
        return check().isElementVisible(elementType, elementName, getLocator());
    }

    /** Checks if the specified element is visible within the given timeout.
     * 
     * @param timeout Max time to wait for the element to become visible.
     * 
     * @return <code>true</code> if the element was found visible during the given timeout, <code>false</code> otherwise. */
    @Override
    public boolean isVisible(long timeout) {
        return check().isElementVisible(elementType, elementName, getLocator(), timeout);
    }
}

