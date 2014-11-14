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
package org.aludratest.content.flat.webdecs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.aludratest.content.flat.data.RowTypeData;
import org.databene.formats.fixedwidth.FixedWidthColumnDescriptor;
import org.databene.formats.fixedwidth.FixedWidthRowTypeDescriptor;

/**
 * Manages a collection of {@link RowTypeData}s and 
 * provides the feature of finding the appropriate Java class 
 * for mapping a flat file row and mapping the file data 
 * to object features.
 * @author Volker Bergmann
 */
public class RowParser {

    /** The default locale to use for numbers and dates. */
    private Locale defaultLocale;

    /** The complete list  */
    private List<RowTypeData> rowTypes;

    /** Maps simple class names to descriptor arrays of their column formats. */
    private Map<String, FixedWidthRowTypeDescriptor> columnFormats;

    /** Constructor requiring the {@link #defaultLocale}. 
     *  @param defaultLocale the locale to use for parsing numbers and dates */
    public RowParser(Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
        this.rowTypes = new ArrayList<RowTypeData>();
        this.columnFormats = new HashMap<String, FixedWidthRowTypeDescriptor>();
    }

    /** Adds a {@link RowTypeData} to the parser instance. 
     *  @param rowType the row type to add */
    public void addRowType(RowTypeData rowType) {
        this.rowTypes.add(rowType);
    }

    /** Parses a flat file row with the aid of its {@link #rowTypes}. 
     * @param rowData the character data of the flat file row to parse 
     * @return a FlatFileBean instance representing the parsed flat file row data */
    public Object parseRow(String rowData) {
        for (RowTypeData rowType : rowTypes) {
            Class<?> beanClass = rowType.beanClassFor(rowData);
            if (beanClass != null) {
                FixedWidthRowTypeDescriptor format = columnFormatFor(beanClass);
                return format.parseAsBean(rowData, beanClass);
            }
        }
        throw new IllegalArgumentException("Unknown row type: " + rowData);
    }

    /** parses the provided beanClass' annotations and provides the related flat file format 
     *  as array of {@link FixedWidthColumnDescriptor}s. */
    private FixedWidthRowTypeDescriptor columnFormatFor(Class<?> beanClass) {
        FixedWidthRowTypeDescriptor format = columnFormats.get(beanClass.getSimpleName());
        if (format == null) {
            format = AnnotationUtil.parseFlatFileColumns(beanClass, defaultLocale);
            columnFormats.put(beanClass.getSimpleName(), format);
        }
        return format;
    }

}
