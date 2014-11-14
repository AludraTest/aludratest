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
package org.aludratest.content.flat.data;

import java.util.Map;

import org.aludratest.dict.Data;

/**
 * Row type recognizer for the BeanFlatFileReader.
 * For flat file parsing, the reader iterates the file linewise 
 * and calls its RowTypes' {@link #beanClassFor(String)} methods 
 * until one of them returns a {@link Class} for signaling that 
 * it has recognized the row type. Then the reader uses the 
 * data annotations of that class for determining the column 
 * formats, parsing the row, instantiating an object of the class 
 * and setting its attributes, JavaBean properties or {@link Map} 
 * entries accordingly.
 * The user is free to choose a single RowType that recognizes 
 * all row types of a flat file format, one RowType for each 
 * bean class or an individual mapping.
 * @author Volker Bergmann
 */
public abstract class RowTypeData extends Data {

    /** If it diagnoses the data line to represent a FlatFileBean type,
     *  then it returns its class, otherwise null. 
     *  @param line the flat file text line to parse 
     *  @return the associated bean class if supported, otherwise null */
    public abstract Class<?> beanClassFor(String line);

}
