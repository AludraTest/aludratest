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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

import org.databene.commons.BeanUtil;

/**
 * Displays a list of test classes and filters them accordings
 * to the settings imposed by {@link #updateFilter(String)}.
 * @author Volker Bergmann
 */
// JList classes are parameterized since Java 7, but we want to be backwards compatible to 6
@SuppressWarnings({ "unchecked", "rawtypes" })
public class TestClassList extends JList {

    private static final long serialVersionUID = 1L;

    private List<Class<?>> testClasses;

    /** Constructor.
     *  @param rootPackage The root package */
    public TestClassList(String rootPackage) {
        super(new DefaultListModel());
        setCellRenderer(new ClassNameCellRenderer());
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        List<Class<?>> classes = BeanUtil.getClasses(rootPackage);
        Iterator<Class<?>> iterator = classes.iterator();
        while (iterator.hasNext()) {
            Class<?> clazz = iterator.next();
            if (!WizardUtil.isAludraTestClassWithExcelBasedTests(clazz)) {
                iterator.remove();
            }
        }
        this.testClasses = new ArrayList<Class<?>>(classes);
        updateFilter("");
    }

    @Override
    public DefaultListModel getModel() {
        return (DefaultListModel) super.getModel();
    }

    @Override
    public Class<?> getSelectedValue() {
        return (Class<?>) super.getSelectedValue();
    }

    /** Updates the filter.
     *  @param filter the new filter string to apply */
    public void updateFilter(String filter) {
        getModel().clear();
        for (Class<?> testClass : testClasses) {
            if (matchesFilter(filter, testClass)) {
                getModel().addElement(testClass);
            }
        }
    }

    private boolean matchesFilter(String filter, Class<?> testClass) {
        String term = filter.trim().toLowerCase(Locale.US);
        return (term.isEmpty() || testClass.getName().toLowerCase(Locale.US).contains(term));
    }

}
