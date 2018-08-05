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
package org.aludratest.testcase.data.impl;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.aludratest.exception.AutomationException;
import org.aludratest.testcase.Ignored;
import org.aludratest.testcase.Offset;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.databene.commons.IOUtil;
import org.databene.commons.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Provides utility methods for test configurations.
 * @author Volker Bergmann */
public class ExcelConfigUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelConfigUtil.class);

    /** The name of the configuration tab in the Excel document. */
    private static final String CONFIG_TAB_NAME = "config";

    /** The name of the configuration column in the Excel document. */
    private static final String CONFIG_COLUMN_NAME = "testConfiguration";

    private static final Object IGNORE_COLUMN_NAME = "ignore";

    private ExcelConfigUtil() {
    }

    public static List<TestDataLoadInfo> parseConfigSheet(String excelFilePath, Method method, int invocations,
            boolean configTabRequired, boolean ignoreEnabled) {
        if (excelFilePath == null) {
            return defaultTestInfos(invocations, isIgnored(method));
        }
        if (!IOUtil.isURIAvailable(excelFilePath)) {
            Exception e = new AutomationException("Data file not found: " + excelFilePath);
            return errorInfos(e, invocations);
        }
        try {
            Workbook workbook = WorkbookFactory.create(IOUtil.getInputStreamForURI(excelFilePath));
            Sheet sheet = workbook.getSheet(ExcelConfigUtil.CONFIG_TAB_NAME);
            if (sheet == null) {
                if (configTabRequired) {
                    Exception e = new AutomationException("Sheet '" + CONFIG_TAB_NAME + "' not found in file " + excelFilePath);
                    return errorInfos(e, invocations);
                }
                else {
                    return defaultTestInfos(invocations, isIgnored(method));
                }
            }
            else {
                return parseTestInfos(sheet, invocations, method, excelFilePath, ignoreEnabled);
            }
        }
        catch (IOException e) {
            throw new AutomationException("Error reading Excel document " + excelFilePath, e);
        }
        catch (InvalidFormatException e) {
            throw new AutomationException("Error reading Excel document " + excelFilePath, e);
        }
    }

    public static int findConfigColumn(Sheet sheet, String excelFilePath) {
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            throw new AutomationException("Config tab '" + CONFIG_TAB_NAME + "' is empty in Excel document " + excelFilePath);
        }
        int configColumn = -1;
        for (int i = 0; i <= headerRow.getLastCellNum(); i++) {
            if (CONFIG_COLUMN_NAME.equals(String.valueOf(headerRow.getCell(i)))) {
                configColumn = i;
                break;
            }
        }
        if (configColumn == -1) {
            throw new AutomationException("No '" + CONFIG_COLUMN_NAME + "' column found" + " in '" + CONFIG_TAB_NAME
                    + "' tab of file " + excelFilePath);
        }
        return configColumn;
    }

    public static int findIgnoreColumnIndex(Sheet sheet, String excelFilePath) {
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            throw new AutomationException("Config tab '" + CONFIG_TAB_NAME + "' is empty in Excel document " + excelFilePath);
        }
        int index = -1;
        for (int i = 0; i <= headerRow.getLastCellNum(); i++) {
            if (IGNORE_COLUMN_NAME.equals(String.valueOf(headerRow.getCell(i)))) {
                index = i;
                break;
            }
        }
        return index;
    }

    public static boolean parseIgnoredCell(Row infoRow, int ignoreColumnIndex, boolean ignoreEnabled) {
        boolean ignored = false;
        if (ignoreEnabled && infoRow != null && ignoreColumnIndex >= 0) {
            Cell ignoreCell = infoRow.getCell(ignoreColumnIndex);
            if (ignoreCell != null) {
                if (ignoreCell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
                    ignored = ignoreCell.getBooleanCellValue();
                }
                else if (ignoreCell.getCellType() == Cell.CELL_TYPE_STRING) {
                    String spec = ignoreCell.getStringCellValue();
                    if ("true".equals(spec)) {
                        ignored = true;
                    }
                    else if (!(spec == null || "".equals(spec) || "false".equals(spec))) {
                        throw new AutomationException("Illegal value for '" + IGNORE_COLUMN_NAME + "' column: " + spec);
                    }
                }
            }
        }
        return ignored;
    }

    public static List<TestDataLoadInfo> parseTestInfos(Sheet sheet, int invocations, Method method, String excelFilePath,
            boolean ignoreEnabled) {
        int configColumn = ExcelConfigUtil.findConfigColumn(sheet, excelFilePath);
        int ignoreColumnIndex = ExcelConfigUtil.findIgnoreColumnIndex(sheet, excelFilePath);
        int offset = getMethodOffset(method);
        List<TestDataLoadInfo> testInfos = new ArrayList<TestDataLoadInfo>();
        for (int invocationNumber = offset + 1; invocationNumber <= sheet.getLastRowNum(); invocationNumber++) {
            Row infoRow = sheet.getRow(invocationNumber);
            Cell infoCell = (infoRow != null ? infoRow.getCell(configColumn) : null);
            String infoText = (infoCell != null ? infoCell.getStringCellValue() : null);
            if (invocationNumber - offset > invocations) {
                Exception e = new AutomationException("Configuration " + infoText + " has no data");
                testInfos.add(new TestDataLoadInfo(e));
            }
            else if (StringUtil.isEmpty(infoText)) {
                Exception e = new AutomationException("Data set #" + invocationNumber + " has no configuration");
                testInfos.add(new TestDataLoadInfo(e));
            }
            else {
                boolean ignored = isIgnored(method)
                        || ExcelConfigUtil.parseIgnoredCell(infoRow, ignoreColumnIndex, ignoreEnabled);
                testInfos.add(new TestDataLoadInfo(infoText, ignored, getIgnoredReason(method)));
            }
            LOGGER.debug("testConfiguration for invocation {} on method {} is: {}", new Object[] { invocationNumber, method,
                    infoText });
        }
        for (int i = sheet.getLastRowNum() + 1; i <= invocations; i++) {
            Exception e = new AutomationException("Test data without test config: #" + (i - 1));
            testInfos.add(new TestDataLoadInfo(e));
        }
        return testInfos;
    }

    private static int getMethodOffset(Method method) {
        Offset offset = method.getAnnotation(Offset.class);
        return (offset != null ? offset.value() : 0);
    }

    public static boolean isIgnored(Method method) {
        return (method.getAnnotation(Ignored.class) != null);
    }

    public static List<TestDataLoadInfo> errorInfos(Exception e, int invocationCount) {
        LOGGER.error("Error occured", e);
        List<TestDataLoadInfo> infos = new ArrayList<TestDataLoadInfo>();
        for (int i = 0; i < invocationCount; i++) {
            infos.add(new TestDataLoadInfo(e));
        }
        return infos;
    }

    // private helper methods --------------------------------------------------

    private static List<TestDataLoadInfo> defaultTestInfos(int invocationCount, boolean ignored) {
        List<TestDataLoadInfo> infos = new ArrayList<TestDataLoadInfo>();
        for (int i = 0; i < invocationCount; i++) {
            infos.add(new TestDataLoadInfo(String.valueOf(i), ignored));
        }
        return infos;
    }

    private static String getIgnoredReason(Method method) {
        Ignored annot = method.getAnnotation(Ignored.class);
        return ((annot == null || "".equals(annot.value())) ? null : annot.value());
    }

}
