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
package org.aludratest.content.flat;

import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.Locale;

import org.aludratest.content.ContentHandler;
import org.aludratest.content.flat.data.FlatFileBeanData;
import org.aludratest.content.flat.data.RowTypeData;

/**
 * Interface for reading and writing flat files.
 * @author Volker Bergmann
 */
public interface FlatContent extends ContentHandler {

    /** Sets the locale of the content handler.
     *  @param locale the locale to set */
    void setLocale(Locale locale);

    /** Creates a writer for persisting
     *  FlatFileBeans or JavaBean data structures.
     *  @param out the writer to use for persisting the flat file content
     *  @return the id of the new writer */
    Serializable createWriter(Writer out);

    /** Appends a row to the flat file writer denoted by the writerId.
     *  @param rowBean the FlatFileBean holding the data
     *  @param writerId the id of the writer with which to store the formatted text */
    void writeRow(Object rowBean, Object writerId);

    /** Closes the writer and returns its content as string.
     *  @param writerId the id of the writer to close */
    void closeWriter(Object writerId);

    /** Creates a reader object for reading JavaBeans.
     *  @param source the source reader that provides the flat file's character data
     *  @return the id of the writer */
    Object createReader(Reader source);

    /** Adds a RowType to a BeanFlatFileReader.
     *  @param rowType a {@link RowTypeData} for reader setup
     *  @param readerId the id of the writer */
    public void addRowType(RowTypeData rowType, Object readerId);

    /** Reads a flat file cell and provides it as Java object.
     *  @param readerId the id of the reader
     *  @return the id of the new reader */
    FlatFileBeanData readRow(Object readerId);

    /** Closes a reader.
     *  @param readerId the id of the reader to close */
    void closeReader(Object readerId);

}
