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
package org.aludratest.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks a component or service interface as "internal". Only interfaces marked with this annotation can be marked
 * as singleton, which ensures that they will only be instantiated once per AludraTest invocation.
 * 
 * @author falbrech
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface InternalComponent {

    /** If <code>true</code>, ensures that this service will only be instantiated once. Additional calls to
     * {@link org.aludratest.service.AludraServiceManager#newImplementorInstance(Class)} will receive the same instance.
     * 
     * @return <code>true</code> if only one instance of the marked class shall be instantiated at runtime, <code>false</code>
     *         otherwise. */
    public boolean singleton() default false;

}
