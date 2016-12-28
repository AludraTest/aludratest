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
package org.aludratest.service.separatedfile;

import org.aludratest.content.separated.data.SeparatedFileBeanData;
import org.aludratest.content.separated.data.WrappedSeparatedData;
import org.aludratest.service.file.FileStream;
import org.aludratest.util.data.StringData;

/** Parses separated files and provides each line as a Java object.
 * @param <R> the generic type to return as 'this'
 * @param <B> the type of objects to read
 * @author Volker Bergmann */
public abstract class SeparatedFileReader<B extends SeparatedFileBeanData, R extends SeparatedFileReader<B, R>>
extends FileStream<R> {

    private SeparatedFileService service;

    /** The internal id of the SeparatedFileReader inside the controlling
     *  {@link SeparatedFileService} instance */
    private Object readerId;

    /** Constructor.
     * @param filePath the file path of the resource from which to read the formatted file content
     * @param service The {@link SeparatedFileService} which will take control of the reader
     * @param beanClass the type of Java objects to read
     * @param separator the separator character used */
    public SeparatedFileReader(String filePath, SeparatedFileService service,
            Class<? extends SeparatedFileBeanData> beanClass, char separator) {
        super(filePath, service.getFileService());
        this.service = service;
        this.readerId = service.perform().createReader(filePath, beanClass, separator);
        verifyState(); //NOSONAR
    }

    /** Reads a single row from the separated file and maps it to a Java object instance.
     *  @param data a SeparatedFileBean holding the parsed data of the separated file row
     *  @return a reference to 'this' */
    @SuppressWarnings("unchecked")
    protected R readRow(WrappedSeparatedData<B> data) {
        B bean = (B) this.service.perform().readRow(this.readerId);
        data.setValue(bean);
        return this.verifyState();
    }

    protected R readHeader(StringData result) {
        String header = this.service.perform().readHeader(this.readerId);
        result.setValue(header);
        return this.verifyState();
    }

    /** Closes the reader
     *  @return a reference to 'this' */
    public R close() {
        this.service.perform().closeReader(this.readerId);
        return this.verifyState();
    }

    @SuppressWarnings("unchecked")
    @Override
    public R verifyState() {
        // no possibility of having an invalid state
        return (R) this;
    }

    /** Creates a string representation of the reader. */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "#" + readerId;
    }

}
