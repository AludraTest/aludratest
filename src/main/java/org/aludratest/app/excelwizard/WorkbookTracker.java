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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.aludratest.util.ExcelUtil;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.databene.commons.IOUtil;
import org.databene.commons.StringUtil;

/** Encapsulates data needed for synchronizing and validating Excel documents that provide test data to an AludraTest test method.
 * @author Volker Bergmann */
public class WorkbookTracker {

    // status constants
    static final int STATUS_UNCHANGED = 0;
    static final int STATUS_CREATED = 1;
    static final int STATUS_MODIFIED = 2;

    // primary attributes
    private final File file;
    private final Workbook workbook;

    // tracking info
    private int status;
    private Map<String, List<String>> expectedSheetColumnHeaders;
    private List<String> warnings;

    /* Private constructor for avoiding instantiation by other classes - only the factory methods of this class may be used. */
    private WorkbookTracker(File file, Workbook workbook, int status) {
        this.file = file;
        this.workbook = workbook;
        this.status = status;
        this.expectedSheetColumnHeaders = new HashMap<String, List<String>>();
        this.warnings = new ArrayList<String>();
    }

    // public interface --------------------------------------------------------

    /** @return the {@link #file} of the related Excel document */
    public File getFile() {
        return this.file;
    }

    /** @return the tracked {@link Workbook} */
    public Workbook getWorkbook() {
        return this.workbook;
    }

    /** @return a list of the warnings that were raised in the execution of the {@link #validate()} method */
    public List<String> getWarnings() {
        return this.warnings;
    }

    @Override
    public String toString() {
        return file.toString();
    }

    // internal interface (default visibility) ---------------------------------

    boolean needsPersisting() {
        return (this.status != STATUS_UNCHANGED);
    }

    static WorkbookTracker importWorkbook(File file) throws InvalidFormatException, IOException {
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            Workbook woorkbook = WorkbookFactory.create(in);
            return new WorkbookTracker(file, woorkbook, STATUS_UNCHANGED);
        }
        finally {
            IOUtil.close(in);
        }
    }

    static WorkbookTracker createWorkbook(File file, Method testMethod) {
        Workbook workbook;
        if (file.getName().toLowerCase(Locale.US).endsWith(".xls")) {
            workbook = new HSSFWorkbook();
        }
        else {
            workbook = new XSSFWorkbook();
        }
        createConfigTab(workbook, testMethod);
        return new WorkbookTracker(file, workbook, STATUS_CREATED);
    }

    Sheet getOrCreateSheet(String name) {
        Sheet sheet = workbook.getSheet(name);
        if (sheet == null) {
            sheet = workbook.createSheet(name);
            sheet.createRow(0);
            this.status = STATUS_MODIFIED;
        }
        return sheet;
    }

    int synchronizeColumnsWithClassFeatures(Class<?> dataType, Sheet sheet, int insertionIndex, String parentPath) {
        // Get bean properties and public attributes
        Map<String, Class<?>> features = WizardUtil.getFeatures(dataType);
        // create String type columns first...
        for (Map.Entry<String, Class<?>> feature : features.entrySet()) {
            if (feature.getValue() == String.class) {
                String header = extendPath(parentPath, feature.getKey());
                insertionIndex = haveColumnWithHeader(header, sheet, insertionIndex) + 1;
            }
        }
        // ...then recur into bean graphs
        for (Map.Entry<String, Class<?>> feature : features.entrySet()) {
            if (feature.getValue() != String.class) {
                String localPath = extendPath(parentPath, feature.getKey());
                insertionIndex = synchronizeColumnsWithClassFeatures(feature.getValue(), sheet, insertionIndex, localPath);
            }
        }
        return insertionIndex;
    }

    void validate() {
        CellStyle warningCellStyle = this.workbook.createCellStyle();
        warningCellStyle.setFillForegroundColor(HSSFColor.RED.index);
        warningCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        for (Map.Entry<String, List<String>> headerConfig : expectedSheetColumnHeaders.entrySet()) {
            String sheetName = headerConfig.getKey();
            Sheet sheet = this.workbook.getSheet(sheetName);
            Row headerRow = sheet.getRow(0);
            List<String> expectedHeaders = headerConfig.getValue();
            int lastCellNum = headerRow.getLastCellNum();
            int firstCellNum = headerRow.getFirstCellNum();
            for (int i = firstCellNum; i < lastCellNum; i++) {
                Cell headerCell = headerRow.getCell(i);
                if (headerCell != null) {
                    String actualHeader = headerCell.getStringCellValue();
                    if (actualHeader != null && actualHeader.trim().length() > 0) {
                        if (!expectedHeaders.contains(actualHeader)) {
                            this.warnings.add("Unmappable column '" + actualHeader + "' in sheet '" + sheetName + "' of file '"
                                    + file.getName() + "'");
                            headerCell.setCellStyle(warningCellStyle);
                            this.status = STATUS_MODIFIED;
                        }
                    }
                }
            }
        }
    }

    boolean persistWoorkbook() throws IOException {
        File targetFolder = this.file.getCanonicalFile().getParentFile();
        if (!targetFolder.exists()) {
            targetFolder.mkdirs();
        }
        OutputStream out = null;
        try {
            out = new FileOutputStream(this.file);
            this.workbook.write(out);
        }
        finally {
            IOUtil.close(out);
        }
        return true;
    }

    // private helpers ---------------------------------------------------------

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

    private static String extendPath(String parentPath, String key) {
        return (StringUtil.isEmpty(parentPath) ? key : parentPath + '.' + key);
    }

    private int haveColumnWithHeader(String header, Sheet sheet, int insertionIndex) {
        addExpectedHeader(header, sheet.getSheetName());
        Row headerRow = sheet.getRow(0);
        int existingColumnIndex = ExcelUtil.findCellWithText(header, headerRow);
        if (existingColumnIndex >= 0) {
            return existingColumnIndex;
        }
        else {
            insertColumn(header, sheet, insertionIndex, Cell.CELL_TYPE_STRING);
            return insertionIndex;
        }
    }

    private void addExpectedHeader(String header, String sheetName) {
        List<String> headers = expectedSheetColumnHeaders.get(sheetName);
        if (headers == null) {
            headers = new ArrayList<String>();
            expectedSheetColumnHeaders.put(sheetName, headers);
        }
        headers.add(header);
    }

    private void insertColumn(String header, Sheet sheet, int insertionIndex, int cellType) {
        ExcelUtil.insertEmptyColumn(sheet, insertionIndex);
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            headerRow = sheet.createRow(0);
        }
        Cell headerCell = headerRow.createCell(insertionIndex, cellType);
        headerCell.setCellValue(header);
        this.status = STATUS_MODIFIED;
    }

}
