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
package org.aludratest.content.separated.util;

import java.lang.reflect.Field;

import org.aludratest.content.separated.data.SeparatedFileBeanData;

/**
 * Represents a column in a separated file.
 * @author Volker Bergmann
 */
public class ColumnConfig {

    private final Field attribute;
    private final int index;
    private final String format;

    /** Constructor.
     * @param attribute the Java {@link Field} to which to map the column
     * @param index the index of the column to map
     * @param format the format specification */
    public ColumnConfig(Field attribute, int index, String format) {
        this.attribute = attribute;
        this.index = index;
        this.format = format;
    }

    /** @return the class that decalres the attribute */
    @SuppressWarnings("unchecked")
    public Class<? extends SeparatedFileBeanData> getDeclaringClass() {
        return (Class<? extends SeparatedFileBeanData>) attribute.getDeclaringClass();
    }

    /** @return the name of the attribute */
    public String getName() {
        return attribute.getName();
    }

    /** @return the column index in the separated file */
    public int getIndex() {
        return index;
    }

    /** @return the format information */
    public String getFormat() {
        return format;
    }

}
