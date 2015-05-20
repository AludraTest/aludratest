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
package org.aludratest.content.flat;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an attribute to be considered when parsing and writing flat files 
 * and specifies the column {@link #startIndex()} and well as the {@link #format()}.
 * A single column may be specified in three different manners: 
 * <ol>
 *   <li>a date/time format, declared by a leading D and followed by a date/timepattern as understood by the Java SimpleDateFormat class. Example: DyyyyMMdd</li>
 *   <li>a number format, declared by a leading N and followed by a number pattern as understood by the Java DecimalFormat class. Example: N000.00</li>
 *   <li>a padding format, declared by 1. a leading number which declares the column width and
 *     <ol>
 *       <li>optional decimal point followed by a number which specifies the count of decimal digits and</li>
 *       <li>optional following alignment code (l(eft), r(ight), c(enter) and</li>
 *       <li>optional padding character. if not specified otherwise, output is left-aligned and padded with space characters.</li>
 *     </ol>
 *     Examples: Example: 8, 10.2r0, 30c
 *   </li>
 * </ol>
 * @author Volker Bergmann
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface FlatFileColumn {

    /** The 1-based start-index of the flat file column */
    int startIndex();

    /** The format description of the column, as described in the class Javadoc: {@link FlatFileColumn} */
    String format();

}
