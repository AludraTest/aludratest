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

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.aludratest.config.AludraTestConfig;
import org.aludratest.exception.AutomationException;
import org.aludratest.testcase.Ignored;
import org.aludratest.testcase.Offset;
import org.aludratest.testcase.data.Source;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.databene.commons.IOUtil;
import org.databene.commons.StringUtil;
import org.databene.commons.SystemInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Provides information from an the testConfiguration tab of a test data Excel document. By default, test names are created from
 * method name plus an incremental number (one test and number for each data set). A {@link TestConfigInfoHelper} can be used to
 * provide verbal test information instead of the number. This can be done by adding a tab named 'config' to the Excel document
 * and enter the text description for data set {@literal #}n in row {@literal #}n if row based, otherwise in column {@literal #}n.
 * The column (or row) bearing the description must have the header 'testConfiguration'.
 * @author Volker Bergmann
 * @author Yibo Wang */
public class TestConfigInfoHelper {

    /** The class' slf4j logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(TestConfigInfoHelper.class);

    /** The name of the configuration tab in the Excel document. */
    private static final String CONFIG_TAB_NAME = "config";

    /** The name of the configuration column in the Excel document. */
    private static final String CONFIG_COLUMN_NAME = "testConfiguration";

    private static final Object IGNORE_COLUMN_NAME = "ignore";

    private AludraTestConfig aludraConfig;

    public TestConfigInfoHelper(AludraTestConfig aludraConfig) {
        this.aludraConfig = aludraConfig;
    }

    // interface ---------------------------------------------------------------

    /** Reads the test configuration information from the corresponding tab of the test data file.
     * @param method the method for which to determine the test infos
     * @param invocations the expected number of invocations
     * @return a List of the test infos for the method */
    public List<TestDataLoadInfo> testInfos(Method method, int invocations) {
        // check if the Excel document is available
        String excelFilePath = testConfigurationFilePath(method);
        if (excelFilePath == null) {
            return defaultTestInfos(invocations, isIgnored(method));
        }
        if (!IOUtil.isURIAvailable(excelFilePath)) {
            Exception e = new AutomationException("Data file not found: " + excelFilePath);
            return errorInfos(e, invocations);
        }

        // fetch the tab (sheet) with the config info
        try {
            Workbook workbook = WorkbookFactory.create(IOUtil.getInputStreamForURI(excelFilePath));
            Sheet sheet = workbook.getSheet(CONFIG_TAB_NAME);
            if (sheet == null) {
                if (aludraConfig.isConfigTabRequired()) {
                    Exception e = new AutomationException("Sheet '" + CONFIG_TAB_NAME + "' not found in file " + excelFilePath);
                    return errorInfos(e, invocations);
                }
                else {
                    return defaultTestInfos(invocations, isIgnored(method));
                }
            }
            return parseTestInfos(sheet, invocations, method, excelFilePath);
        }
        catch (IOException e) {
            throw new AutomationException("Error reading Excel document " + excelFilePath, e);
        }
        catch (InvalidFormatException e) {
            throw new AutomationException("Error reading Excel document " + excelFilePath, e);
        }
    }

    // private helper methods ------------------------------------------------

    private List<TestDataLoadInfo> parseTestInfos(Sheet sheet, int invocations, Method method, String excelFilePath) {
        int configColumn = findConfigColumn(sheet, excelFilePath);
        int ignoreColumnIndex = findIgnoreColumnIndex(sheet, excelFilePath);
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
                boolean ignored = isIgnored(method) || parseIgnoredCell(infoRow, ignoreColumnIndex);
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

    private boolean isIgnored(Method method) {
        return (method.getAnnotation(Ignored.class) != null);
    }

    private String getIgnoredReason(Method method) {
        Ignored annot = method.getAnnotation(Ignored.class);
        return annot == null || "".equals(annot.value()) ? null : annot.value();
    }

    private boolean parseIgnoredCell(Row infoRow, int ignoreColumnIndex) {
        boolean ignored = false;
        if (aludraConfig.isIgnoreEnabled() && infoRow != null && ignoreColumnIndex >= 0) {
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

    private static int getMethodOffset(Method method) {
        Offset offset = method.getAnnotation(Offset.class);
        return (offset != null ? offset.value() : 0);
    }

    private static int findConfigColumn(Sheet sheet, String excelFilePath) {
        int configColumn = -1;
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            throw new AutomationException("Config tab '" + CONFIG_TAB_NAME + "' is empty in Excel document " + excelFilePath);
        }
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

    private static int findIgnoreColumnIndex(Sheet sheet, String excelFilePath) {
        int index = -1;
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            throw new AutomationException("Config tab '" + CONFIG_TAB_NAME + "' is empty in Excel document " + excelFilePath);
        }
        for (int i = 0; i <= headerRow.getLastCellNum(); i++) {
            if (IGNORE_COLUMN_NAME.equals(String.valueOf(headerRow.getCell(i)))) {
                index = i;
                break;
            }
        }
        return index;
    }

    private static List<TestDataLoadInfo> defaultTestInfos(int invocationCount, boolean ignored) {
        List<TestDataLoadInfo> infos = new ArrayList<TestDataLoadInfo>();
        for (int i = 0; i < invocationCount; i++) {
            infos.add(new TestDataLoadInfo(String.valueOf(i), ignored));
        }
        return infos;
    }

    private static List<TestDataLoadInfo> errorInfos(Exception e, int invocationCount) {
        LOGGER.error("Error occured", e);
        List<TestDataLoadInfo> infos = new ArrayList<TestDataLoadInfo>();
        for (int i = 0; i < invocationCount; i++) {
            infos.add(new TestDataLoadInfo(e));
        }
        return infos;
    }

    /** Returns the file path of the test configuration file for a given method. */
    private String testConfigurationFilePath(Method method) {
        Class<?> testClass = method.getDeclaringClass();
        String localFileName = getTestConfigFileName(method);
        if (localFileName == null) {
            return null;
        }
        String absoluteFilePath = SystemInfo.getCurrentDir() + File.separator
                + aludraConfig.getXlsRootPath();
        absoluteFilePath = absoluteFilePath.replace('/', File.separatorChar);
        absoluteFilePath = absoluteFilePath + File.separatorChar + testClass.getName().replace('.', File.separatorChar);
        return absoluteFilePath + File.separator + localFileName;
    }

    /** Returns the name of the test configuration file for a given method. */
    private String getTestConfigFileName(Method method) {
        Source source = firstSourceAnnotation(method);
        if (source != null) {
            String uri = source.uri();
            if (uri == null || uri.isEmpty()) {
                throw new AutomationException("No source URL defined in @Source");
            }
            return uri;
        }
        else {
            return null;
        }
    }

    /** Scans a method and its parameters for @Source annotations and returns the first one found. */
    private static Source firstSourceAnnotation(Method method) {
        Source source = method.getAnnotation(Source.class);
        if (source != null) {
            return source;
        }
        Annotation[][] paramAnnos = method.getParameterAnnotations();
        for (int iP = 0; iP < paramAnnos.length; iP++) {
            for (int iA = 0; iA < paramAnnos[iP].length; iA++) {
                if (paramAnnos[iP][iA] instanceof Source) {
                    return (Source) paramAnnos[iP][iA];
                }
            }
        }
        return null;
    }

}
