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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import org.aludratest.testcase.AludraTestCase;
import org.aludratest.testcase.data.Source;
import org.aludratest.util.ExcelUtil;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.databene.commons.BeanUtil;
import org.databene.commons.FileUtil;
import org.databene.commons.SystemInfo;
import org.junit.Test;

/** Tests the {@link JavaBeanExcelDocumentMapper}.
 * @author Volker Bergmann */
@SuppressWarnings("javadoc")
public class JavaBeanExcelDocumentMapperTest {

    private static final File SRC_TEST_RESOURCES;
    private static final File TARGET_CLASSES;
    private static final Class<?> TEST_CLASS = AludraTestCaseImpl.class;
    private static final Class<?>[] TEST_PARAMS = new Class<?>[] { MergerTestBean.class };

    static {
        try {
            SRC_TEST_RESOURCES = new File(SystemInfo.getCurrentDir(), "src/test/resources").getCanonicalFile();
            TARGET_CLASSES = new File(SystemInfo.getCurrentDir(), "target/test-classes").getCanonicalFile();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCreationFromScratch() throws Exception {
        File createdFile = null;
        try {
            Method testMethod = BeanUtil.getMethod(TEST_CLASS, "creationFromScratch", TEST_PARAMS);
            Collection<WorkbookTracker> trackers = JavaBeanExcelDocumentMapper.createOrMergeDocuments(testMethod, null,
                    TARGET_CLASSES);
            createdFile = trackers.iterator().next().getFile();
            verifyExcelDocument(trackers, "test_from_scratch.xls", new String[] { "name1", "name2" },
                    new String[] { "sub.name" },
                    null);
        }
        finally {
            if (createdFile != null) {
                FileUtil.deleteIfExists(createdFile);
            }
        }
    }

    @Test
    public void testMatchingExcelDocument() throws Exception {
        final String EXCEL_URI = "test_match.xls";
        copyToTarget(EXCEL_URI);
        Method testMethod = BeanUtil.getMethod(TEST_CLASS, "matchingExcelDocument", TEST_PARAMS);
        Collection<WorkbookTracker> trackers = JavaBeanExcelDocumentMapper.createOrMergeDocuments(testMethod, null,
                TARGET_CLASSES);
        verifyExcelDocument(trackers, EXCEL_URI, new String[] { "name1", "name2" },
                new String[] { "sub.name" },
                new String[] { "Alice", "Bob", "Charly" });
    }

    @Test
    public void testAdditionalExcelColumns() throws Exception {
        final String EXCEL_URI = "test_additional_columns.xls";
        copyToTarget(EXCEL_URI);
        Method testMethod = BeanUtil.getMethod(TEST_CLASS, "additionalColumns", TEST_PARAMS);
        Collection<WorkbookTracker> trackers = JavaBeanExcelDocumentMapper.createOrMergeDocuments(testMethod, null,
                TARGET_CLASSES);
        verifyExcelDocument(trackers, EXCEL_URI, new String[] { "name1", "name2" }, new String[] { "sub.name" },
                new String[] { "Alice", "Bob", "x", "Charly", "y" });
        // verify warnings
        WorkbookTracker tracker = trackers.iterator().next();
        List<String> warnings = tracker.getWarnings();
        assertEquals(2, warnings.size());
        assertEquals("Unmappable column 'add' in sheet 'sheet1' of file 'test_additional_columns.xls'", warnings.get(0));
        assertEquals("Unmappable column 'sub.add' in sheet 'sheet1' of file 'test_additional_columns.xls'", warnings.get(1));
        Workbook workbook = tracker.getWorkbook();
        Sheet sheet1 = workbook.getSheet("sheet1");
        Row headerRow = sheet1.getRow(0);
        assertNotNull(headerRow);
        assertEquals(HSSFColor.RED.index, headerRow.getCell(2).getCellStyle().getFillForegroundColor());
        assertEquals(HSSFColor.RED.index, headerRow.getCell(4).getCellStyle().getFillForegroundColor());
    }

    @Test
    public void testMissingExcelColumns() throws Exception {
        final String EXCEL_URI = "test_missing_columns.xls";
        copyToTarget(EXCEL_URI);
        Method testMethod = BeanUtil.getMethod(TEST_CLASS, "missingColumns", TEST_PARAMS);
        Collection<WorkbookTracker> trackers = JavaBeanExcelDocumentMapper.createOrMergeDocuments(testMethod, null,
                TARGET_CLASSES);
        verifyExcelDocument(trackers, EXCEL_URI, new String[] { "name1", "name2" }, new String[] { "sub.name" }, new String[] {
                "Alice", null, "Charly" });
    }

    // helper methods ----------------------------------------------------------

    private static void copyToTarget(String uri) throws FileNotFoundException, IOException {
        File sourceFile = JavaBeanExcelDocumentMapper.resolveExcelFile(SRC_TEST_RESOURCES, TEST_CLASS, uri);
        File targetFile = JavaBeanExcelDocumentMapper.resolveExcelFile(TARGET_CLASSES, TEST_CLASS, uri);
        FileUtil.copy(sourceFile, targetFile, true);
    }

    private static void verifyExcelDocument(Collection<WorkbookTracker> files, String expectedFileName,
            String[] expectedMainHeaders,
            String[] expectedSubHeaders, String[] expectedData) throws IOException, InvalidFormatException {
        assertEquals(1, files.size());
        File file = files.iterator().next().getFile();
        assertEquals(expectedFileName, file.getName());
        Workbook workbook = WorkbookFactory.create(file);
        Sheet sheet1 = workbook.getSheet("sheet1");
        assertNotNull(sheet1);
        Row headerRow = sheet1.getRow(0);
        assertNotNull(headerRow);
        for (String expectedMainHeader : expectedMainHeaders) {
            int index = ExcelUtil.findCellWithText(expectedMainHeader, headerRow);
            assertTrue(index >= 0);
        }
        for (String expectedSubHeader : expectedSubHeaders) {
            int index = ExcelUtil.findCellWithText(expectedSubHeader, headerRow);
            assertTrue(index >= expectedMainHeaders.length);
        }
        if (expectedData != null) {
            Row dataRow1 = sheet1.getRow(1);
            assertNotNull(dataRow1);
            for (int i = 0; i < expectedData.length; i++) {
                Cell dataCell = dataRow1.getCell(i);
                String expectedContent = expectedData[i];
                if (expectedContent != null) {
                    assertNotNull(dataCell);
                    assertEquals(expectedContent, dataCell.getStringCellValue());
                }
                else {
                    assertNull(dataCell);
                }
            }
        }
    }

    public static class AludraTestCaseImpl extends AludraTestCase {
        @org.aludratest.testcase.Test
        public void creationFromScratch(@Source(uri = "test_from_scratch.xls", segment = "sheet1") MergerTestBean bean) {
        }

        @org.aludratest.testcase.Test
        public void matchingExcelDocument(@Source(uri = "test_match.xls", segment = "sheet1") MergerTestBean bean) {
        }

        @org.aludratest.testcase.Test
        public void additionalColumns(@Source(uri = "test_additional_columns.xls", segment = "sheet1") MergerTestBean bean) {
        }

        @org.aludratest.testcase.Test
        public void missingColumns(@Source(uri = "test_missing_columns.xls", segment = "sheet1") MergerTestBean bean) {
        }
    }

    public static class MergerTestBean {
        protected String name1;
        public String name2;
        public SubBean sub;

        public String getName1() {
            return name1;
        }

        public void setName1(String name1) {
            this.name1 = name1;
        }

    }

    public static class SubBean {
        public String name;
    }

}
