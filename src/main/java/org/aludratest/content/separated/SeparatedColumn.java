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
package org.aludratest.content.separated;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.aludratest.content.flat.FlatFileColumn;
import org.aludratest.content.separated.data.SeparatedFileBeanData;

/**
 * Assigns a separated-file coöumn index to an attribute of a {@link SeparatedFileBeanData} class. 
 * @author Volker Bergmann
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface SeparatedColumn {

    /** The 1-based  column index of the flat file column */
    int columnIndex();

    /** The format description of the column, as described in the class Javadoc: {@link FlatFileColumn} */
    String format() default "";

}
