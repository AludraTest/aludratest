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
import java.util.HashMap;
import java.util.Map;

import org.aludratest.content.separated.SeparatedColumn;
import org.aludratest.exception.AutomationException;

/**
 * Provides utilities for separated files.
 * @author Volker Bergmann
 */
public class SeparatedUtil {

    private SeparatedUtil() { }

    /**
     * Analyzes a bean class with its {@link SeparatedColumn} annotations 
     * and determines the column order of a separated file for persisting and reading data.
     * @param beanType the bean class to analyze
     * @return the names of the features at the 0-based index of the associated column.
     */
    public static String[] featureNames(Class<?> beanType) {
        Map<Integer, ColumnConfig> columns = new HashMap<Integer, ColumnConfig>();
        for (Field attribute : beanType.getDeclaredFields()) {
            SeparatedColumn annotation = attribute.getAnnotation(SeparatedColumn.class);
            if (annotation != null) {
                int index = annotation.columnIndex();
                ColumnConfig predefCol = columns.get(index);
                if (predefCol != null) {
                    throw new AutomationException(
                            "Separated column #" + index + " defined twice: " +
                            "For attributes '" + predefCol.getName() + "' " +
                            "and '" + attribute.getName() + "'");
                }
                columns.put(index, new ColumnConfig(attribute, index, annotation.format()));
            }
        }
        String[] result = new String[columns.size()];
        for (int i = 0; i < columns.size(); i++) {
            ColumnConfig config = columns.get(i + 1);
            if (config == null) {
                throw new AutomationException(
                        beanType + " does not declare an attribute for column #" + (i + 1));
            }
            result[i] = config.getName();
        }
        return result;
    }

}
