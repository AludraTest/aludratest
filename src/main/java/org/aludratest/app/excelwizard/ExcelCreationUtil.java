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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.aludratest.testcase.data.Source;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.databene.commons.BeanUtil;
import org.databene.commons.IOUtil;
import org.databene.commons.OrderedMap;
import org.databene.commons.StringUtil;
import org.databene.commons.SystemInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates Excel documents that match the annotations and data structure of a test method.
 * @author Volker Bergmann
 */
public class ExcelCreationUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelCreationUtil.class);

    /** Private constructor for preventing instantiation of the utility class. */
    private ExcelCreationUtil() {
    }

    /** Creates Excel documents that match the annotations and data structure of a test method.
     * @param testMethod the the method for which to create the document(s)
     * @param parentComponent
     * @param xlsRootPath Root path for XLS documents
     * @return a {@link List} of the URIs of the created documents
     * @throws IOException if document creation failed */
    public static List<File> createDocuments(Method testMethod, Component parentComponent, String xlsRootPath) throws IOException {
        LOGGER.info("Creating Excel Document(s) for method {}", testMethod);
        Map<String, Workbook> workbooks = createWorkbooks(testMethod);
        File testDataFolder = resolveTestDataFolder(xlsRootPath);
        return writeWorkbooks(workbooks, testDataFolder, testMethod.getDeclaringClass(), parentComponent);
    }


    // private helper methods --------------------------------------------------

    private static Map<String, Workbook> createWorkbooks(Method testMethod) {
        Map<String, Workbook> workbooks = new HashMap<String, Workbook>();
        createMappingSheets(testMethod, workbooks);
        return workbooks;
    }

    private static void createMappingSheets(Method testMethod, Map<String, Workbook> workbooks) {
        Annotation[][] annosOfAllParams = testMethod.getParameterAnnotations();
        Class<?>[] paramTypes = testMethod.getParameterTypes();
        for (int i = 0; i < paramTypes.length; i++) {
            for (Annotation anno : annosOfAllParams[i]) {
                if (anno instanceof Source) {
                    Source source = (Source) anno;
                    Workbook workbook = getOrCreateWorkbook(source.uri(), testMethod, workbooks);
                    Sheet sheet = getOrCreateSheet(source.segment(), workbook);
                    appendColumns(sheet, paramTypes[i]);
                }
            }
        }
    }

    private static void appendColumns(Sheet sheet, Class<?> paramType) {
        Row headerRow = sheet.getRow(0);
        int column = headerRow.getLastCellNum();
        if (column == -1) {
            column = 0;
        }
        appendColumns(headerRow, column, null, paramType);
    }

    private static int appendColumns(Row row, int col, String parentPath, Class<?> type) {
        int column = col;
        // Get bean properties and public attributes
        Map<String, Class<?>> features = getFeatures(type);
        // create String type columns first...
        for (Map.Entry<String, Class<?>> feature : features.entrySet()) {
            if (feature.getValue() == String.class) {
                Cell cell = row.createCell(column++, Cell.CELL_TYPE_STRING);
                cell.setCellValue(extendPath(parentPath, feature.getKey()));
            }
        }
        // ...then recur into bean graphs
        for (Map.Entry<String, Class<?>> feature : features.entrySet()) {
            if (feature.getValue() != String.class) {
                column = appendColumns(row, column, extendPath(parentPath, feature.getKey()), feature.getValue());
            }
        }
        return column;
    }

    private static String extendPath(String parentPath, String key) {
        return (StringUtil.isEmpty(parentPath) ? key : parentPath + '.' + key);
    }

    private static Map<String, Class<?>> getFeatures(Class<?> type) {
        Map<String, Class<?>> features = new OrderedMap<String, Class<?>>();
        for (Field field : type.getDeclaredFields()) {
            if (isPublic(field) || BeanUtil.hasProperty(type, field.getName())) {
                features.put(field.getName(), field.getType());
            }
        }
        return features;
    }

    private static boolean isPublic(Field field) {
        return (field.getModifiers() & Modifier.PUBLIC) == Modifier.PUBLIC;
    }

    private static File resolveTestDataFolder(String xlsRootPath) throws IOException {
        File folder = new File(SystemInfo.getCurrentDir(), xlsRootPath);
        return folder.getCanonicalFile();
    }

    private static Sheet getOrCreateSheet(String name, Workbook workbook) {
        Sheet sheet = workbook.getSheet(name);
        if (sheet == null) {
            sheet = workbook.createSheet(name);
            sheet.createRow(0);
        }
        return sheet;
    }

    private static Workbook getOrCreateWorkbook(String uri, Method testMethod, Map<String, Workbook> workbooks) {
        Workbook workbook = workbooks.get(uri);
        if (workbook == null) {
            workbook = createWorkbook(uri, testMethod);
            workbooks.put(uri, workbook);
        }
        return workbook;
    }

    private static Workbook createWorkbook(String uri, Method testMethod) {
        Workbook workbook;
        if (uri.toLowerCase().endsWith(".xls")) {
            workbook = new HSSFWorkbook();
        } else {
            workbook = new XSSFWorkbook();
        }
        createConfigTab(workbook, testMethod);
        return workbook;
    }

    private static void createConfigTab(Workbook workbook, Method testMethod) {
        Sheet configTab = workbook.createSheet("config");
        Row headerRow = configTab.createRow(0);
        headerRow.createCell(0, Cell.CELL_TYPE_STRING).setCellValue("testConfiguration");
        headerRow.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("ignore");
        Row dataRow = configTab.createRow(1);
        String configName = testMethod.getDeclaringClass().getSimpleName();
        if (configName.startsWith("ID_")) {
            configName = "C" + configName + "1";
        }
        dataRow.createCell(0, Cell.CELL_TYPE_STRING).setCellValue(configName);
    }

    private static List<File> writeWorkbooks(Map<String, Workbook> workbooks,
            File testDataFolder, Class<?> testClass, Component parentComponent) throws IOException {
        List<File> files = new ArrayList<File>();
        for (Map.Entry<String, Workbook> entry : workbooks.entrySet()) {
            File targetFile = resolveExcelFile(testDataFolder, testClass, entry.getKey());
            if (writeWorkbook(entry.getValue(), targetFile, parentComponent)) {
                files.add(targetFile);
            }
        }
        return files;
    }

    private static File resolveExcelFile(File testDataFolder, Class<?> testClass, String uri) {
        File targetFolder = new File(testDataFolder, testClass.getName().replace('.', '/'));
        File targetFile = new File(targetFolder, uri);
        return targetFile;
    }

    private static boolean writeWorkbook(Workbook workbook, File targetFile,
            Component parentComponent) throws IOException {
        File targetFolder = targetFile.getCanonicalFile().getParentFile();
        if (!targetFolder.exists()) {
            targetFolder.mkdirs();
        }
        if (targetFile.exists()) {
            int input = JOptionPane.showConfirmDialog(parentComponent,
                    "File " + targetFile + " already exists. Overwrite it?",
                    ExcelCreationUtil.class.getSimpleName(),
                    JOptionPane.YES_NO_CANCEL_OPTION);
            if (input != JOptionPane.OK_OPTION) {
                return false;
            }
        }
        OutputStream out = null;
        try {
            LOGGER.info("Writing file {}", targetFile);
            out = new FileOutputStream(targetFile);
            workbook.write(out);
        } finally {
            IOUtil.close(out);
        }
        return true;
    }

}
