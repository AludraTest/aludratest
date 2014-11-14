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

import java.lang.reflect.Method;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

/**
 * Lists the test methods of a class which have a Source annotation.
 * @author Volker Bergmann
 */
// JList classes are parameterized since Java 7, but we want to be backwards compatible to 6
@SuppressWarnings({ "rawtypes", "unchecked" })
public class MethodSelector extends JComboBox {

    private static final long serialVersionUID = 1L;

    /** Default constructor. */
    public MethodSelector() {
        super(new DefaultComboBoxModel());
        setRenderer(new MethodNameCellRenderer());
    }

    // properties --------------------------------------------------------------

    /** Sets the test class of which to display the test methods.
     *  @param testClass the test class to use */
    public void setTestClass(Class<?> testClass) {
        getModel().removeAllElements();
        for (Method testMethod : WizardUtil.getTestMethodsWithExcelSource(testClass)) {
            getModel().addElement(testMethod);
        }
    }

    // JCombobox overrides -----------------------------------------------------

    @Override
    public DefaultComboBoxModel getModel() {
        return (DefaultComboBoxModel) super.getModel();
    }

    @Override
    public Method getSelectedItem() {
        return (Method) super.getSelectedItem();
    }

}
