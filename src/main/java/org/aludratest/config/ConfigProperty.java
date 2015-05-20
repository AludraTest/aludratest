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

/** Describes a configuration property used by the annotated service interface or implementation. Service interfaces can declare
 * general configuration properties, implementations can add implementation-specific configuration properties using this
 * interface. The default values are filled into the Preferences object passed to
 * {@link Configurable#fillDefaults(MutablePreferences)} by the framework. <br>
 * <br>
 * Use the {@link ConfigProperties} annotation to add more than one configuration property to a class or interface.
 * 
 * @author falbrech */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ConfigProperty {

    public String name();

    public String description();

    public Class<?> type();

    public String defaultValue() default "";

    public boolean required() default false;

}
