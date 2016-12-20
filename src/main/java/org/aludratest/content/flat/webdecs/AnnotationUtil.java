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

import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import org.aludratest.content.flat.FlatFileColumn;
import org.aludratest.exception.AutomationException;
import org.databene.formats.fixedwidth.FixedWidthColumnDescriptor;
import org.databene.formats.fixedwidth.FixedWidthRowTypeDescriptor;
import org.databene.formats.fixedwidth.FixedWidthUtil;

/**
 * Parses the structure of FlatFileBeans and maps it
 * to arrays of {@link FixedWidthColumnDescriptor}s.
 * @author Volker Bergmann
 */
public class AnnotationUtil {

    /** Private constructor of utility class preventing instantiation by other classes */
    private AnnotationUtil() {
    }

    /** Parses the structure of a FlatFileBean class and maps it
     * to an array of {@link FixedWidthColumnDescriptor}s.
     * @param beanClass the FlatFileBean class to analyze
     * @param defaultLocale the locale to apply by default
     * @return a {@link FixedWidthRowTypeDescriptor} holding the format data of the bean attributes */
    public static FixedWidthRowTypeDescriptor parseFlatFileColumns(Class<?> beanClass, Locale defaultLocale) {
        // parse all fields with their descriptors and put them into a set,
        // sorted by startIndex
        Set<ColumnDescriptor> columnDescriptors = new TreeSet<ColumnDescriptor>(new DescriptorComparator());

        // scan the attributes of the beanClass and all its parent classes
        Class<?> clazz = beanClass;
        do {
            parseFields(clazz, defaultLocale, columnDescriptors);
            clazz = clazz.getSuperclass();
        } while (clazz != null);

        // Check consistency: Iterate the set and verify matching boundaries for each column
        ColumnDescriptor previous = null;
        int index = 1;
        for (ColumnDescriptor descriptor : columnDescriptors) {
            if (descriptor.startIndex != index) {
                if (previous == null) {
                    throw new AutomationException("Flat file column indices must start at index 1, " + "but the first column '" + beanClass.getName() + "." + descriptor.name + "'"
                            + " starts at index " + descriptor.startIndex);
                } else {
                    throw new AutomationException("Flat file column '" + beanClass.getName() + "." + descriptor.name + "' " + "is expected at index " + index + ", " + "but was found at index "
                            + descriptor.startIndex);
                }
            }
            index += descriptor.descriptor.getWidth();
            previous = descriptor;
        }

        // create and apply the format array
        FixedWidthColumnDescriptor[] columns = new FixedWidthColumnDescriptor[columnDescriptors.size()];
        Iterator<ColumnDescriptor> iterator = columnDescriptors.iterator();
        for (int i = 0; i < columnDescriptors.size(); i++) {
            columns[i] = iterator.next().descriptor;
        }
        return new FixedWidthRowTypeDescriptor(beanClass.getSimpleName(), columns);
    }

    // private helpers ---------------------------------------------------------

    /** Scans the attributes of a class and its parents, mapping their
     *  {@link FlatFileColumn} annotation information to a sorted set of
     *  {@link ColumnDescriptor}s. */
    private static void parseFields(Class<?> beanClass, Locale defaultLocale, Set<ColumnDescriptor> columnDescriptors) {
        for (Field field : beanClass.getDeclaredFields()) {
            String fieldName = field.getName();
            // if the attribute has already been defined by a child class
            // (which was iterated before), then ignore it - child classes
            // override parent classes' attributes and settings
            if (containsDescriptorForField(fieldName, columnDescriptors)) {
                continue;
            }

            // parse the annotation of the attribute
            FlatFileColumn annotation = field.getAnnotation(FlatFileColumn.class);
            try {
                FixedWidthColumnDescriptor fwColDescriptor = FixedWidthUtil.parseColumnFormat(annotation.format(), "", defaultLocale);
                ColumnDescriptor descriptor = new ColumnDescriptor(fieldName, annotation.startIndex(), fwColDescriptor);
                for (ColumnDescriptor tmp : columnDescriptors) {
                    if (tmp.startIndex == descriptor.startIndex) {
                        throw new AutomationException("Multiple column definitions at index " + descriptor.startIndex + ": '" + tmp.name + "', and '" + descriptor.name + "'");
                    }
                }
                // put the resulting descriptor into the columnDescriptors set
                columnDescriptors.add(descriptor);
            } catch (ParseException e) {
                throw new AutomationException("Error in flat file column format: " + annotation.format(), e);
            }
        }
    }

    /** Tells if a collection contains a {@link ColumnDescriptor} for the given attribute name. */
    private static boolean containsDescriptorForField(String fieldName, Collection<ColumnDescriptor> columnDescriptors) {
        for (ColumnDescriptor descriptor : columnDescriptors) {
            if ((fieldName.equals(descriptor.name))) {
                return true;
            }
        }
        return false;
    }

    /** Compares two {@link ColumnDescriptor}s by its {@link ColumnDescriptor#startIndex} value. */
    private static class DescriptorComparator implements Comparator<ColumnDescriptor>, Serializable {
        private static final long serialVersionUID = 1L;

        @Override
        public int compare(ColumnDescriptor col1, ColumnDescriptor col2) {
            return ((Integer) col1.startIndex).compareTo(col2.startIndex);
        }
    }

    /** Collects the format information of a flat file column.
     *  Internally it uses the {@link FixedWidthColumnDescriptor} class
     *  of the Databene Webdecs library and adds a startIndex. */
    private static class ColumnDescriptor {

        /** The name of the described attribute. */
        private final String name;

        /** The start index of the related column. */
        private final int startIndex;

        /** fixed-width descriptor as provided by Databene Webdecs. */
        private final FixedWidthColumnDescriptor descriptor;

        /** Constructor
         *  @param name the column name
         *  @param startIndex the index of the character at which the associated column begins
         *  @param descriptor the associated {@link FixedWidthColumnDescriptor} */
        public ColumnDescriptor(String name, int startIndex, FixedWidthColumnDescriptor descriptor) {
            this.name = name;
            this.startIndex = startIndex;
            this.descriptor = descriptor;
            this.descriptor.setName(name);
        }

        /** Creates a string representation of the descriptor. */
        @Override
        public String toString() {
            return name + "[startIndex=" + startIndex + ", format='" + descriptor + "']";
        }
    }

}
