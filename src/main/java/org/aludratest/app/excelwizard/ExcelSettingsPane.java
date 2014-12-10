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

import java.awt.event.ActionEvent;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.databene.commons.SystemInfo;
import org.databene.commons.ui.swing.AlignedPane;

/**
 * Main pane of the ExcelWizard.
 * @author Volker Bergmann
 */
public class ExcelSettingsPane extends AlignedPane {

    private static final long serialVersionUID = 1L;

    private JTextField filter;
    private TestClassList classList;
    private MethodSelector methodSelector;

    private String xlsRootPath;

    /** Constructor.
     * @param rootPackage the root package of the relevant test classes
     * @param xlsRootPath the root path of the Excel documents that provide test data */
    public ExcelSettingsPane(String rootPackage, String xlsRootPath) {
        this.xlsRootPath = xlsRootPath;
        // set up 'Test Class Filter'
        this.filter = new JTextField();
        addRow("Test Class Filter", this.filter);
        this.filter.getDocument().addDocumentListener(new FilterListener());

        // set up 'Test Class' selector
        this.classList = new TestClassList(rootPackage);
        addLabel("Test Class");
        addTallRow(new JScrollPane(this.classList));
        this.classList.addListSelectionListener(new TestClassListener());

        // set up test 'Test Method' selector
        this.methodSelector = new MethodSelector();
        addRow("Test Method", this.methodSelector);

        filterUpdated();

        addRow(new JButton(new CreateExcelSheetAction()));
    }

    private void filterUpdated() {
        this.classList.updateFilter(filter.getText());
    }

    private void testClassChanged() {
        this.methodSelector.setTestClass(this.classList.getSelectedValue());
    }


    /** Listens to xchenges in the filter text and updates the test class list accordingly. */
    public class FilterListener implements DocumentListener {
        @Override
        public void changedUpdate(DocumentEvent evt) {
            filterUpdated();
        }

        @Override
        public void insertUpdate(DocumentEvent evt) {
            filterUpdated();
        }

        @Override
        public void removeUpdate(DocumentEvent evt) {
            filterUpdated();
        }
    }


    /** listens to the test class selections in the testClassList
     *  and updates the methodSelector accordingly. */
    public class TestClassListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent evt) {
            testClassChanged();
        }
    }


    /** {@link Action} for creating Excel sheets */
    public class CreateExcelSheetAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        /** Default constructor. */
        public CreateExcelSheetAction() {
            super("Create Excel Sheet");
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            try {
                Method testMethod = methodSelector.getSelectedItem();
                File testDataRootFolder = new File(SystemInfo.getCurrentDir(), xlsRootPath).getCanonicalFile();
                Collection<WorkbookTracker> workbooks = JavaBeanExcelDocumentMapper.createOrMergeDocuments(testMethod,
                        ExcelSettingsPane.this, testDataRootFolder);
                displayCreatedFiles(workbooks);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(ExcelSettingsPane.this, e.getMessage(),
                        "Error in Excel Creator", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void displayCreatedFiles(Collection<WorkbookTracker> workbooks) {
            if (workbooks.size() > 0) {
                List<Object> message = new ArrayList<Object>(workbooks.size() + 1);
                message.add("Processed Files:");
                for (WorkbookTracker workbook : workbooks) {
                    message.add(workbook);
                    for (String warning : workbook.getWarnings()) {
                        message.add("\tWarning: " + warning);
                    }
                }
                JOptionPane.showMessageDialog(ExcelSettingsPane.this, message.toArray(),
                        "Excel Creator", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

}
