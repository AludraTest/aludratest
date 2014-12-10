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
package org.aludratest.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/** Provides Excel-related utility methods.
 * @author Volker Bergmann */
public class ExcelUtil {

    /** Searches a {@link Row} for a {@link Cell} with the provided text and returns its index.
     * @param text the text to search
     * @param row the row in which to search
     * @return the index of the located cell or -1 if the text was not found in the row */
    public static int findCellWithText(String text, Row row) {
        int lastCellIndex = row.getLastCellNum();
        for (int i = 0; i < lastCellIndex; i++) {
            Cell cell = row.getCell(i);
            if (cell != null && text.equals(cell.getStringCellValue())) {
                return i;
            }
        }
        return -1;
    }

    /** Inserts a column without cells into a sheet at the given index.
     * @param sheet the sheet in which to insert the column
     * @param insertionIndex the column index at which to insert the new column */
    public static void insertEmptyColumn(Sheet sheet, int insertionIndex) {
        for (int i = sheet.getLastRowNum(); i >= 0; i--) {
            Row row = sheet.getRow(i);
            short lastCellNum = row.getLastCellNum();
            if (lastCellNum >= insertionIndex) {
                shiftCellsRight(row, insertionIndex, lastCellNum);
            }
        }
    }

    // private helpers ---------------------------------------------------------

    private static void shiftCellsRight(Row row, int firstCellNum, short lastCellNum) {
        for (int i = lastCellNum; i >= firstCellNum; i--) {
            moveCell(row, i, i + 1);
        }
    }

    private static void moveCell(Row row, int fromIndex, int toIndex) {
        Cell oldCell = row.getCell(fromIndex);
        if (oldCell != null) {
            Cell newCell = row.createCell(toIndex, oldCell.getCellType());
            newCell.setCellStyle(oldCell.getCellStyle());
            newCell.setCellValue(oldCell.getStringCellValue());
            row.removeCell(oldCell);
        }
    }

}
