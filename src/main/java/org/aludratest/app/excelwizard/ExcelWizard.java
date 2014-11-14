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

import java.awt.BorderLayout;

import javax.swing.JFrame;

import org.aludratest.AludraTest;
import org.aludratest.config.AludraTestConfig;
import org.databene.commons.ui.swing.SwingUtil;

/**
 * Main class of the Excel wizard.
 * It exhibits GUI elements for selecting test classes and methods and
 * creating Excel documents for a selected test method.
 * @author Volker Bergmann
 */
public class ExcelWizard extends JFrame {

    private static final long serialVersionUID = 1L;

    private ExcelSettingsPane settings;

    /** Constructor.
     * @param rootPackage the root package of the relevant test classes.
     * @param xlsRootPath The root path to store the excel files to. */
    public ExcelWizard(String rootPackage, String xlsRootPath) {
        setTitle("Excel Wizard");
        this.settings = new ExcelSettingsPane(rootPackage, xlsRootPath);
        getContentPane().add(this.settings, BorderLayout.CENTER);
        setSize(600, 400);
        SwingUtil.center(this);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    /**
     * Main method that which sets up and displays the GUI.
     * 
     * @param args
     *            Array with one optional command line argument: the root name package, which defaults to org.aludratest
     */
    public static void main(String[] args) {
        String rootPackage = (args.length > 0 ? args[0] : "org.aludratest");

        // start an AludraTest to retrieve configuration
        AludraTest aludraTest = new AludraTest();
        AludraTestConfig config = aludraTest.getServiceManager().newImplementorInstance(AludraTestConfig.class);

        new ExcelWizard(rootPackage, config.getXlsRootPath()).setVisible(true);
    }


}
