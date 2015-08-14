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
package org.aludratest.content.flat.webdecs;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.util.Locale;

import org.aludratest.content.flat.FlatFileColumn;
import org.aludratest.content.flat.data.FlatFileBeanData;
import org.aludratest.content.flat.data.RowTypeData;
import org.aludratest.exception.AutomationException;
import org.aludratest.exception.TechnicalException;
import org.databene.commons.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Iterates flat files and maps each row to a Java Object.
 * It is configured using {@link RowTypeData} objects that recognize
 * row types and provide the related Java class for them
 * and uses the {@link FlatFileColumn} annotations on the
 * related Java classes' attributes to fetch parsing information.
 * @author Volker Bergmann
 */
public class FlatFileBeanReader implements Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlatFileBeanReader.class);

    /** the reader that provides the flat file's character data. */
    private BufferedReader reader;

    /** A {@link RowParser} to which the operations of determining row types, parsing column data
     *  and FlatFileBean creation is delegated. */
    private RowParser rowParser;

    /** Constructor of the WebdecsBeanFlatFileReader.
     *  @param reader the reader that provides the flat file's character data.
     *  @param defaultLocale the default locale to use for number and date formats.
     */
    public FlatFileBeanReader(Reader reader, Locale defaultLocale) {
        this.reader = bufferedReader(reader);
        this.rowParser = new RowParser(defaultLocale);
    }

    /** Adds a {@link RowTypeData} definition to the reader.
     * @param rowType */
    public void addRowType(RowTypeData rowType) {
        this.rowParser.addRowType(rowType);
    }

    /** Reads a single text row of the flat file, determines its FlatFileBean type
     *  using the configured {@link RowTypeData}s and creates a FlatFileBean instance
     *  configured by the row data.
     *  @return the next row mapped to a JavaBean
     *  @throws IOException */
    public FlatFileBeanData readRow() throws IOException {
        if (this.reader == null) {
            throw new TechnicalException("Reader has already been closed: " + this);
        }
        String rowData = reader.readLine();
        if (rowData == null) {
            return null; // reached end of data
        }
        LOGGER.debug("Imported data row: {}", rowData);
        Object bean = rowParser.parseRow(rowData);
        if (!(bean instanceof FlatFileBeanData)) {
            throw new AutomationException("Flat file bean type does not inherit " + FlatFileBeanData.class);
        }
        return (FlatFileBeanData) bean;
    }

    /** Closes the {@link #reader} and sets it to null. */
    @Override
    public void close() {
        if (this.reader != null) {
            IOUtil.close(this.reader);
            this.reader = null;
        }
    }

    /** Wraps a {@link Reader} with a {@link BufferedReader} if it is no instance of BufferedReader.
     *  @return a BufferedReader that provides the content of the Reader provided. */
    private static BufferedReader bufferedReader(Reader reader) {
        if (reader instanceof BufferedReader) {
            return (BufferedReader) reader;
        } else {
            return new BufferedReader(reader);
        }
    }

}
