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

    /** May be used to declare the URI of the data source - 
     *  the preferred tag for this is {@link #uri()}. */
    String value() default "";

    /** References the id value of a JavaBean object in the context that 
     *  serves as data source. */
    String id() default "";

    /** Selector clause specific for the data source, 
     *  e.g. a SQL query term for data bases. */
    String selector() default "";

    /** Declares the URI of the data source */
    String uri() default "";

    /** Declares which data segment (e.g. Excel sheet) to use */
    String segment() default "";

    /** DatabeneScript filter string to filter row data */
    String filter() default "";

    /** Declares the (e.g. CSV) separator character used in the data source */
    String separator() default "";

    /** May be used to declare a sub set of a collection of given data files. */
    String dataset() default "";

    /** May be used to declare the type of {@link #dataset()} selection. */
    String nesting() default "";

    /** Declares the character encoding used in the data file. */
    String encoding() default "";

    /** Marker text used for declaring empty strings in the data source */
    String emptyMarker() default "";

    /** Marker text used for declaring null values in the data source */
    String nullMarker() default "";

    /** If true, the data sets are rows in the data source, otherwise columns. */
    boolean rowBased() default true;

}
