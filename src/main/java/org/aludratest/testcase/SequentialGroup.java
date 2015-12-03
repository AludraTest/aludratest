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
package org.aludratest.testcase;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Annotation for Classes or methods specifying their Sequential behaviour "upwards" in the execution tree. While
 * <code>@Sequential</code> specifies that the <i>elements</i> of the annotated object (methods of a class, test configurations of
 * a method) shall be executed sequentially, this annotation specifies some kind of "Precondition" for executing the annotated
 * element. All elements also annotated with <code>@SequentialGroup</code>, having the same group name attribute on that
 * annotation, but a lower index, must be executed first before this element can be executed.
 * 
 * @author falbrech */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface SequentialGroup {

    /** The name of the sequential group this test class or method belongs to. All tests belonging to the same group but with a
     * lower index will be executed first before this test is being executed.
     * 
     * @return The name of the sequential group this test class or method belongs to. */
    public String groupName();

    /** The index of this element within the sequential group. Elements with the same group name but a lower index will be executed
     * before this element.
     * 
     * @return The index of this element within the sequential group. */
    public int index();

}
