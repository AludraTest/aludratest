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
package org.aludratest.testcase.data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.text.Format;

/** Annotation to use for method parameters. If such an annotation is present for a method parameter which invocation is reported
 * to an <i>test step</i>, its value is converted using this formatter before reported inside the test step.
 * 
 * @author falbrech */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ParamConverter {

    /** A formatter for the annotated method parameter. <code>Format.format(Object)</code> will be called with the parameter value
     * as parameter. Other methods will not be called.
     * 
     * @return A formatter for the annotated method parameter. */
    public Class<? extends Format> value();

}
