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
import org.aludratest.service.locator.element.GUIElementLocator;

/** Parent interface for GUI Elements e.g. Button.
 * 
 * TODO document waitingUntilTaskCompletion feature
 * 
 * @author Joerg Langnickel
 * @author Volker Bergmann
 * @param <E> Type of the concrete element, to be used by subclasses. */
public interface Element<E extends Element<E>> extends GUIComponent {

    /** @return the element's locator */
    public GUIElementLocator getLocator();

    /** Activates the <i>waiting until task completion</i> feature on the underlying aludraGUI with the default value for the task
     * completion timeout.
     * @return This object. */
    public E waitingUntilTaskCompletion();

    /** Sets the {@link #taskCompletionTimeout} to the specified value.
     * @param waitTime the wait time to apply. A negative number disables the wait feature, 0 implicitly activates the
     *            {@link AludraGUI}'s default, a positive value is used as explicit timeout.
     * @return This object. */
    public E waitingUntilTaskCompletion(int waitTime);

    /** Asserts that the element is editable. */
    public void assertEditable();

    /** Asserts that the element is not editable. */
    public void assertNotEditable();

    /** Asserts that the element is present */
    public void assertPresent();

    /** Asserts that the element is not present */
    public void assertNotPresent();

    /** Asserts that the element is visible */
    public void assertVisible();

    /** Asserts that the element has the focus. */
    public void assertFocus();

    /** Sets the focus on this element */
    public void focus();

    /** Double clicks the element. */
    public void doubleClick();

    /** Single-clicks the element. */
    public void click();

    /**
     * Selectable click- clicks only when provided string is not null, not marked as null and provided string is "true"
     * @param click -String
     */
    public void click(String click);

    /**
     * Selectable not editable click - clicks only when provided string is not null, not marked as null and provided string is "true"
     * @param click -String
     */
    public void clickNotEditable(String click);

    /**
     * Click on an element which is not editable (accept the non-editable state)
     */
    public void clickNotEditable();

    /**
     * Double click on an element which is not editable (accept the non-editable state)
     */
    public void doubleClickNotEditable();

    /** Checks if the specified element is somewhere on the page within the standard timeout.
     * 
     * @return <code>true</code> if the element was found present somewhere on the page within the standard timeout,
     *         <code>false</code> otherwise. */
    public boolean isPresent();

    /**
     * Checks if the specified element is somewhere on the page and in foreground
     * within the standard timeout.
     * 
     * @return <code>true</code> if the element was found present somewhere on the page and in foreground within the standard timeout,
     *         <code>false</code> otherwise. */
    public boolean isPresentAndInForeground();

    /** Checks if the specified element is somewhere on the page within a given timeout
     * 
     * @param timeout max time to wait for the element to become present.
     * @return <code>true</code> if the element was found present somewhere on the page within the given timeout,
     *         <code>false</code> otherwise. */
    public boolean isPresent(long timeout);

    /** Checks if the specified element is nowhere on the page within the standard timeout.
     * 
     * @return <code>true</code> if the element was <b>not</b> found present somewhere on the page within the standard timeout,
     *         <code>false</code> otherwise. */
    public boolean isNotPresent();

    /** Checks if the specified element is nowhere on the page within the standard timeout.
     * 
     * @param timeout time to check for the element not being present.
     * 
     * @return <code>true</code> if the element was <b>not</b> found present somewhere on the page within the given timeout,
     *         <code>false</code> otherwise. */
    public boolean isNotPresent(long timeout);

    /** Checks if the specified element is visible within the standard timeout.
     * 
     * @return <code>true</code> if the element is visible, <code>false</code> otherwise. */
    public boolean isVisible();

    /** Checks if the specified element is visible within the given timeout.
     * 
     * @param timeout Max time to wait for the element to become visible.
     * 
     * @return <code>true</code> if the element was found visible during the given timeout, <code>false</code> otherwise. */
    public boolean isVisible(long timeout);
}
