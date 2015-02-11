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
import org.aludratest.service.Interaction;

/**
 * {@link Interaction} interface of the {@link SeparatedFileService}.
 * @author Volker Bergmann
 */
public interface SeparatedFileInteraction extends Interaction {

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
     *  SeparatedFileBeans or JavaBean data structures. 
     *  @param filePath the path of the file to write
     *  @param overwrite flag that indicates whether pre-existing files may be overwritten
     *  @param beanClass the type of the bean Objects to write
     *  @param separator the separator character to use
     * @param header 
     *  @return the id of the created writer */
    Object createWriter(String filePath, boolean overwrite, 
            Class<? extends SeparatedFileBeanData> beanClass, char separator, String header);

    /** Writes an object as separated file row. 
     *  @param bean a SeparatedFileBean to write
     *  @param writerId the id of the writer */
    void writeRow(SeparatedFileBeanData bean, Object writerId);

    /** Closes the writer. 
     *  @param writerId the id of the writer */
    void closeWriter(Object writerId);

    /** Creates a reader for reading JavaBeans. 
     *  @param  filePath the path of the file to read
     *  @param beanClass the type of the bean Objects to write
     *  @param separator the separator character to use
     *  @return the id of the created reader */
    Object createReader(String filePath, 
            Class<? extends SeparatedFileBeanData> beanClass, char separator);

    /**
     * Reads a header line from a separated file.
     * @param readerId
     * @return the content of the header
     */
    String readHeader(Object readerId);

    /** Reads a separated file row and provides it as Java object. 
     *  @param  readerId the id of the reader
     *  @return a SeparatedFileBean holding the content of the parsed separated file row */
    SeparatedFileBeanData readRow(Object readerId);

    /** Closes the reader. 
     *  @param readerId the id of the reader */
    void closeReader(Object readerId);

}
