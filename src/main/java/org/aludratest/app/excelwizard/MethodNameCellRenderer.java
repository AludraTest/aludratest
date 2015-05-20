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
package org.aludratest.app.excelwizard;

import java.awt.Component;
import java.lang.reflect.Method;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 * Renders the names of {@link Method}s as list cells.
 * @author Volker Bergmann
 */
public class MethodNameCellRenderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = 1L;

    // JList classes are parameterized since Java 7, but we want to be backwards compatible to 6
    @SuppressWarnings("rawtypes")
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {
        String methodName = (value != null ? ((Method) value).getName() : null);
        return super.getListCellRendererComponent(list, methodName, index, isSelected, cellHasFocus);
    }

}
