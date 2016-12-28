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

/**
 * Defines the data source for a parameterized test method.
 * @author Volker Bergmann
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER })
public @interface Source {

    /** @return may be used declare the data source's URI - the preferred tag for that is {@link #uri()}. */
    String value() default "";

    /** @return an ID value as reference to a JavaBean object in the context that serves as data source. */
    String id() default "";

    /** @return a selector clause specific for the data source, e.g. a SQL query term for data bases. */
    String selector() default "";

    /** @return the data source's URI */
    String uri() default "";

    /** @return a data segment (e.g. Excel sheet) to use */
    String segment() default "";

    /** @return DatabeneScript filter string to filter row data */
    String filter() default "";

    /** @return a separator declares the (e.g. CSV) separator character used in the data source */
    String separator() default "";

    /** @return may be used to declare a sub set of a collection of given data files. */
    String dataset() default "";

    /** @return optional value to declare the type of {@link #dataset()} selection. */
    String nesting() default "";

    /** @return the data source's character encoding */
    String encoding() default "";

    /** @return marker text used for declaring empty strings in the data source */
    String emptyMarker() default "";

    /** @return The marker text used for declaring null values in the data source */
    String nullMarker() default "";

    /** @return 'true' if the data sets are rows, 'false' if they are columns */
    boolean rowBased() default true;

}
