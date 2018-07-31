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
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aludratest.testcase.data.Source;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Creates and updates Excel documents that match the annotations and data structure of a test method.
 * @author Volker Bergmann */
public class JavaBeanExcelDocumentMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavaBeanExcelDocumentMapper.class);

    /** Private constructor for preventing instantiation of the utility class. */
    private JavaBeanExcelDocumentMapper() {
    }

    /** Creates or updates Excel documents that match the annotations and data structure of a test method.
     * @param testMethod the the method for which to create the document(s)
     * @param testDataRootFolder Root folder for XLS documents
     * @return a {@link List} of {@link WorkbookTracker}s for the created documents
     * @throws IOException if document creation failed
     * @throws InvalidFormatException if a pre-existing Excel document has invalid file content */
    public static Collection<WorkbookTracker> createOrMergeDocuments(Method testMethod, File testDataRootFolder)
            throws IOException, InvalidFormatException {
        LOGGER.info("Creating/updating Excel document(s) for test method {}", testMethod);
        Map<File, WorkbookTracker> workbooks = new HashMap<File, WorkbookTracker>();
        createOrMergeWorkbooks(testMethod, testDataRootFolder, workbooks);
        validateWorkbooks(workbooks.values());
        printWarnings(workbooks.values());
        persistNewAndModifiedWorkbooks(workbooks);
        return workbooks.values();
    }


    // private helper methods --------------------------------------------------

    static File resolveExcelFile(File testDataRootFolder, Class<?> testClass, String uri) {
        File targetFolder = new File(testDataRootFolder, testClass.getName().replace('.', '/')); // NOSONAR
        return new File(targetFolder, uri);
    }

    private static void createOrMergeWorkbooks(Method testMethod, File testDataFolder, Map<File, WorkbookTracker> workbooks)
            throws InvalidFormatException, IOException {
        Class<?> testClass = testMethod.getDeclaringClass();
        Annotation[][] annosOfAllParams = testMethod.getParameterAnnotations();
        Class<?>[] paramTypes = testMethod.getParameterTypes();
        for (int i = 0; i < paramTypes.length; i++) {
            Source source = WizardUtil.findExcelSourceAnnotation(annosOfAllParams[i]);
            if (source != null) {
                File excelFile = resolveExcelFile(testDataFolder, testClass, source.uri());
                WorkbookTracker tracker = importOrCreateWorkbook(excelFile, testMethod, workbooks);
                Sheet sheet = tracker.getOrCreateSheet(source.segment());
                tracker.synchronizeColumnsWithClassFeatures(paramTypes[i], sheet, 0, null);
            }
        }
    }

    private static WorkbookTracker importOrCreateWorkbook(File file, Method testMethod, Map<File, WorkbookTracker> workbooks)
            throws InvalidFormatException, IOException {
        WorkbookTracker tracker = workbooks.get(file);
        if (tracker == null) {
            // if the workbook does not yet exist in the map, first look for a preexisting file, ...
            if (file.exists()) {
                tracker = WorkbookTracker.importWorkbook(file);
            }
            else {
                // ...if none exists yet, create a new document
                tracker = WorkbookTracker.createWorkbook(file, testMethod);
            }
            workbooks.put(file, tracker);
        }
        return tracker;
    }

    private static void validateWorkbooks(Collection<WorkbookTracker> workbooks) {
        for (WorkbookTracker workbook : workbooks) {
            workbook.validate();
        }
    }

    private static void printWarnings(Collection<WorkbookTracker> workbooks) {
        for (WorkbookTracker workbook : workbooks) {
            for (String warning : workbook.getWarnings()) {
                System.err.println("Warning: " + warning);
            }
        }
    }

    private static void persistNewAndModifiedWorkbooks(Map<File, WorkbookTracker> workbooks) throws IOException {
        for (WorkbookTracker tracker : workbooks.values()) {
            if (tracker.needsPersisting()) {
                LOGGER.info("Persisting file {}", tracker.getFile());
                tracker.persistWoorkbook();
            }
        }
    }

}
