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
package org.aludratest.dict;

import org.aludratest.service.gui.GUIInteraction;

/**
 * Common interface for all test classes that make use of the ActionWordLibrary pattern.
 * @param <E> Chosen by child classes in a way that {@link #verifyState()} is parameterized 
 * 		to return the individual child class. 
 * @author Volker Bergmann
 */
public interface ActionWordLibrary<E extends ActionWordLibrary<E>> {

    /** Verifies the state. If the state is wrong, 
     *  the implementor shall call an appropriate service method 
     *  for reporting the inconsistency.
     *  {@link GUIInteraction#wrongPageFlow(String)} 
     *  @return a reference to itself (this). */
    public abstract E verifyState();

}
