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
package org.aludratest.content.separated;

import java.io.Reader;
import java.io.Writer;

import org.aludratest.content.ContentHandler;
import org.aludratest.content.separated.data.SeparatedFileBeanData;

/**
 * Content Handler for separated file formats (like CSV or tab-separated).
 * @author Volker Bergmann
 */
public interface SeparatedContent extends ContentHandler {

    /** Creates a writer object for persisting SeparateFileBeans. 
     *  @param out the writer to use for persisting the file content 
     *  @param beanClass 
     *  @param separator 
     *  @param header 
     *  @return the id of the new writer */
    Object createWriter(Writer out, Class<? extends SeparatedFileBeanData> beanClass, 
            char separator, String header);

    /** Appends a row to the flat file writer denoted by the writerId. 
     *  @param rowBean the FlatFileBean holding the data 
     *  @param writerId the id of the writer with which to store the formatted text */
    void writeRow(SeparatedFileBeanData rowBean, Object writerId);

    /** Closes the writer and returns its content as string. 
     *  @param writerId the id of the writer to close */
    void closeWriter(Object writerId);

    /** Creates a reader object for reading JavaBeans. 
     *  @param source the source reader that provides the flat file's character data
     *  @param rowType 
     *  @param separator 
     *  @return the id of the writer */
    <T extends SeparatedFileBeanData> Object createReader(Reader source, Class<T> rowType, char separator);

    /**
     * Reads a file header row.
     * @param readerId
     * @return the content of the file header
     */
    String readHeader(Object readerId);

    /** Reads a flat file cell and provides it as Java object. 
     *  @param readerId the id of the reader 
     *  @return the id of the new reader */
    SeparatedFileBeanData readRow(Object readerId);

    /** Closes a reader. 
     *  @param readerId the id of the reader to close */
    void closeReader(Object readerId);

}
