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
package org.aludratest.service.separatedfile.impl;

import java.io.StringWriter;

import org.aludratest.content.flat.FlatContent;
import org.aludratest.content.separated.SeparatedContent;
import org.aludratest.content.separated.data.SeparatedFileBeanData;

/** 
 * Wraps a reader's character buffer and 'target data' 
 * (filePath and a flag whether a pre-existing file may be overwritten).
 * @author Volker Bergmann
 */
class SeparatedWriterConfig {

    /** The target file path of the flat file */
    private final String filePath;

    /** Flag indicating whether pre-existing files may be overwritten */
    private final boolean overwrite;

    /** Character buffer that receives the formatted flat file data */
    private final StringWriter buffer;

    /**
     * Constructor.
     * @param filePath The target file path of the flat file
     * @param overwrite Flag indicating whether pre-existing files may be overwritten
     */
    SeparatedWriterConfig(String filePath, boolean overwrite) {
        this.filePath = filePath;
        this.overwrite = overwrite;
        this.buffer = new StringWriter();
    }

    /** @return the {@link #filePath} */
    public String getFilePath() {
        return filePath;
    }

    /** @return the value of {@link #overwrite} */
    public boolean isOverwrite() {
        return overwrite;
    }

    /** @return the string content of the buffer */
    public String getContent() {
        return buffer.toString();
    }

    /**
     * Uses the {@link FlatContent} to create a flat file writer 
     * that writes to the internal {@link #buffer}. 
     * @param contentHandler the {@link FlatContent} handler to use
     * @param beanClass 
     * @param separator 
     * @param header 
     * @return the id of the new writer
     */
    public Object createWriter(SeparatedContent contentHandler, 
            Class<? extends SeparatedFileBeanData> beanClass, char separator, String header) {
        return contentHandler.createWriter(this.buffer, beanClass, separator, header);
    }

}
