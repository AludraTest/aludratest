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
package org.aludratest.content.separated.webdecs;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;

import org.aludratest.content.flat.data.RowTypeData;
import org.aludratest.content.flat.webdecs.FlatFileBeanReader;
import org.aludratest.content.separated.data.SeparatedFileBeanData;
import org.aludratest.content.separated.util.SeparatedUtil;
import org.aludratest.exception.TechnicalException;
import org.databene.commons.IOUtil;
import org.databene.formats.DataContainer;
import org.databene.formats.csv.CSVToJavaBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reads data from a separated file.
 * @author Volker Bergmann
 * @param <E> 
 */
public class SeparatedFileReader<E extends SeparatedFileBeanData> implements Closeable {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FlatFileBeanReader.class);

    /** the reader that provides the flat file's character data. */
    private CSVToJavaBeanMapper<E> rowIterator;
    
    /**
     * Constructor of the WebdecsBeanFlatFileReader.
     * @param source the reader that provides the flat file's character data.
     * @param beanType 
     * @param separator 
     * @throws IOException 
     */
    public SeparatedFileReader(Reader source, Class<E> beanType, char separator) throws IOException {
        String[] featureNames = SeparatedUtil.featureNames(beanType);
        this.rowIterator = new CSVToJavaBeanMapper<E>(bufferedReader(source), beanType, separator, null, featureNames);
    }

    /** 
     * Reads a single text row of the flat file, determines its FlatFileBean type 
     * using the configured {@link RowTypeData}s and creates a FlatFileBean instance 
     * configured by the row data. 
     * @return a {@link SeparatedFileBeanData} object representing the next row of the source document
     * @throws IOException */
    public E readRow() throws IOException {
        if (this.rowIterator == null) {
            throw new TechnicalException("Row iterator has already been closed: " + this);
        }
        DataContainer<E> rowData = rowIterator.next(new DataContainer<E>());
        if (rowData == null) {
            return null; // reached end of data
        }
        LOGGER.debug("Imported data row: {}", rowData);
        return rowData.getData();
    }
    
    protected String[] readRaw() {
        if (this.rowIterator == null) {
            throw new TechnicalException("Row iterator has already been closed: " + this);
        }
        DataContainer<String[]> rowData = rowIterator.nextRaw(new DataContainer<String[]>());
        if (rowData == null) {
            return null; // reached end of data
        }
        LOGGER.debug("Imported data row: {}", rowData);
        return rowData.getData();
    }

    /** Closes the reader. */
    @Override
    public void close() throws IOException {
        if (this.rowIterator != null) {
            IOUtil.close(this.rowIterator);
            this.rowIterator = null;
        }
    }


    // private helper ----------------------------------------------------------

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
