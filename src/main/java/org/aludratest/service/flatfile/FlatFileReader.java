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
package org.aludratest.service.flatfile;

import org.aludratest.content.flat.data.FlatFileBeanData;
import org.aludratest.content.flat.data.RowTypeData;
import org.aludratest.content.flat.data.WrappedRowData;
import org.aludratest.service.file.FileStream;

/** 
 * Parses flat files and provides each line as a Java object.
 * @param <E> the generic type to return as 'this'
 * @author Volker Bergmann
 */
public abstract class FlatFileReader<E> extends FileStream<FlatFileReader<E>> {

    private FlatFileService service;

    /** The internal id of the FlatFileReader inside the controlling 
     *  {@link FlatFileService} instance */
    private Object readerId;

    /** Constructor.
     *  @param filePath the file path of the resource from which to read the formatted file content
     *  @param service The {@link FlatFileService} which will take control of the reader */
    public FlatFileReader(String filePath, FlatFileService service) {
        super(filePath, service.getFileService());
        this.service = service;
        this.readerId = service.perform().createReader(filePath);
        verifyState();
    }

    /** Adds a {@link RowTypeData} to the reader. 
     *  @param rowType a {@link RowTypeData} that recognizes one or more record types of the underlying flat file format
     *  @return a reference to 'this' */
    public FlatFileReader<E> addRowType(RowTypeData rowType) {
        this.service.perform().addRowType(rowType, this.readerId);
        return this.verifyState();
    }

    /** Reads a single row from the flat file and maps it to a Java object instance. 
     *  @param result a FlatFileBean holding the parsed data of the flat file row
     *  @return a reference to 'this' */
    public FlatFileReader<E> readRow(WrappedRowData result) {
        FlatFileBeanData bean = this.service.perform().readRow(this.readerId);
        result.setValue(bean);
        return this.verifyState();
    }

    /** Closes the reader 
     *  @return a reference to 'this' */
    public FlatFileReader<E> close() {
        this.service.perform().closeReader(this.readerId);
        return this.verifyState();
    }

    @Override
    public final FlatFileReader<E> verifyState() {
        // no possibility of having an invalid state
        return this;
    }

    /** Creates a string representation of the reader. */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "#" + readerId;
    }

}
