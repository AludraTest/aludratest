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
import org.aludratest.service.Interaction;

/**
 * {@link Interaction} interface of the {@link FlatFileService}.
 * @author Volker Bergmann
 */
public interface FlatFileInteraction extends Interaction {

    /** Polls the file system until a file at the given path is found 
     *  or a timeout occurs. 
     *  @param elementType 
     *  @param filePath the full path of the requested file */
    void waitUntilExists(String elementType, String filePath);

    /** Polls the file system until no file is found at the given path.
     *  @param filePath */
    void waitUntilNotExists(String filePath);

    /** Deletes a file.
     *  @param filePath the path of the file to delete */
    public void delete(String filePath);

    /** Creates a writer for persisting 
     *  FlatFileBeans or JavaBean data structures. 
     *  @param filePath the path of the file to write
     *  @param overwrite flag that indicates whether pre-existing files may be overwritten
     *  @return the id of the created writer */
    Object createWriter(String filePath, boolean overwrite);

    /** Writes an object as flat file row. 
     *  @param bean a FlatFileBean to write
     *  @param writerId the id of the writer */
    void writeRow(Object bean, Object writerId);

    /** Closes the writer. 
     *  @param writerId the id of the writer */
    void closeWriter(Object writerId);

    /** Creates a reader for reading JavaBeans. 
     *  @param  filePath the path of the file to read
     *  @return the id of the created reader */
    Object createReader(String filePath);

    /** Adds a RowType to a BeanFlatFileReader. 
     *  @param rowType a {@link RowTypeData} to be applied by the flat file reader
     *  @param readerId the id of the reader */
    void addRowType(RowTypeData rowType, Object readerId);

    /** Reads a flat file row and provides it as Java object. 
     *  @param  readerId the id of the reader
     *  @return a FlatFileBean holding the content of the parsed flat file row */
    FlatFileBeanData readRow(Object readerId);

    /** Closes the reader. 
     *  @param readerId the id of the reader */
    void closeReader(Object readerId);

}
