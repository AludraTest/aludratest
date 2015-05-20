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

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

import org.aludratest.AludraTest;
import org.aludratest.config.AludraTestConfig;
import org.aludratest.impl.AludraTestConstants;
import org.databene.commons.BeanUtil;
import org.databene.commons.SystemInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates Excel documents for AludraTest classes from the command line.
 * In Eclipse, create a Run Configuration for this class
 * running in the project for which you want to generate Excel documents
 * and in the field 'Program Arguments' enter ${java_type_name}.
 * In order to execute the generator, select the test class in the
 * package explorer and start the Run Configuration.
 * @author Volker Bergmann
 */
public class CLIExcelCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(CLIExcelCreator.class);

    private CLIExcelCreator() { }

    /** Main method of the class.
     * @param args an array of length one, containing the fully qualified name of the class for which to generate Excel documents
     * @throws Exception if an error occurs */
    public static void main(String[] args) throws Exception {
        checkArguments(args);

        // create a new AludraTest instance to have configuration framework available
        AludraTest aludraTest = AludraTest.startFramework();
        try {
            AludraTestConfig config = aludraTest.getServiceManager().newImplementorInstance(AludraTestConfig.class);
            Class<?> testClass = getTestClass(args);
            List<Method> testMethodsWithExcelSource = WizardUtil.getTestMethodsWithExcelSource(testClass);
            if (!testMethodsWithExcelSource.isEmpty()) {
                LOGGER.info("Generating Excel documents for test class {}", testClass.getName());
                for (Method testMethod : testMethodsWithExcelSource) {
                    File testDataRootFolder = new File(SystemInfo.getCurrentDir(), config.getXlsRootPath()).getCanonicalFile();
                    JavaBeanExcelDocumentMapper.createOrMergeDocuments(testMethod, null, testDataRootFolder);
                }
            }
            else {
                LOGGER.info("Test class {} does not have test methods with Excel sources", testClass.getName());
            }
        }
        finally {
            aludraTest.stopFramework();
        }
    }

    private static Class<?> getTestClass(String[] args) {
        String className = args[0].trim();
        if (className.endsWith(".java")) {
            className = className.substring(0, className.length() - ".java".length());
        }
        return BeanUtil.forName(className);
    }

    private static void checkArguments(String[] args) {
        if (args.length != 1) {
            System.out.println("Please provide the name of the test class or test suite"); //NOSONAR
            System.exit(AludraTestConstants.EXIT_ILLEGAL_ARGUMENT);
        }
    }

}
