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

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.aludratest.content.separated.SeparatedContent;
import org.aludratest.content.separated.data.SeparatedFileBeanData;
import org.aludratest.exception.AutomationException;
import org.aludratest.exception.TechnicalException;
import org.databene.commons.IOUtil;

/**
 * Webdecs-based content handler for separated files
 * @author Volker Bergmann
 */
public class WebdecsSeparatedContent implements SeparatedContent {

    /** Atomic integer that provides stream ids which are unique within the process. */
    private AtomicInteger streamIdProvider;

    /** Map that assigns stream ids to readers. */
    private Map<Object, SeparatedFileReader<?>> readers;

    /** Map that assigns stream ids to writers. */
    private Map<Object, SeparatedFileWriter> writers;

    /** Constructor. */
    public WebdecsSeparatedContent() {
        this.streamIdProvider = new AtomicInteger();
        this.readers = new HashMap<Object, SeparatedFileReader<?>>();
        this.writers = new HashMap<Object, SeparatedFileWriter>();
    }

    /**
     * Creates a writer for persisting SeparatedFileBeans or JavaBean data structures. 
     * @param out the writer to use for persisting the separated file content 
     * @param separator 
     * @param beanClass 
     * @param header 
     * @return the id of the new writer
     */
    @Override
    public Object createWriter(Writer out, Class<? extends SeparatedFileBeanData> beanClass, 
            char separator, String header) {
        SeparatedFileWriter writer = new SeparatedFileWriter(out, beanClass, separator, header);
        Object id = "writer#" + streamIdProvider.incrementAndGet();
        writers.put(id, writer);
        return id;
    }

    /** Appends a row to the separated file writer denoted by the writerId. 
     *  @param rowBean the SeparatedFileBean holding the data 
     *  @param writerId the id of the writer with which to store the formatted text */
    @Override
    public void writeRow(SeparatedFileBeanData rowBean, Object writerId) {
        try {
            getWriter(writerId, true).writeRow(rowBean);
        } catch (IOException e) {
            throw new TechnicalException("Error writing row: " + rowBean, e);
        }
    }

    /** Closes the writer and returns its content as string. 
     *  @param writerId the id of the writer to close */
    @Override
    public void closeWriter(Object writerId) {
        SeparatedFileWriter writer = getWriter(writerId, false);
        IOUtil.close(writer);
    }

    /** Creates a reader object for reading JavaBeans. 
     *  @param source the source reader that provides the separated file's character data
     *  @return the id of the writer */
    @Override
    public <T extends SeparatedFileBeanData> Object createReader(Reader source, Class<T> rowType, char separator) {
        try {
            SeparatedFileReader<T> reader = new SeparatedFileReader<T>(source, rowType, separator);
            String id = "reader#" + streamIdProvider.incrementAndGet();
            readers.put(id, reader);
            return id;
        } catch (IOException e) {
            throw new TechnicalException("Error creating reader", e);
        }
    }

    @Override
    public String readHeader(Object readerId) {
        String[] cells = getReader(readerId, true).readRaw();
        return cells[0];
    }

    /** Reads a separated file row and provides it as Java object. 
     *  @param readerId the id of the reader 
     *  @return the id of the new reader */
    @Override
    public SeparatedFileBeanData readRow(Object readerId) {
        try {
            return getReader(readerId, true).readRow();
        } catch (IOException e) {
            throw new TechnicalException("Error reading bean from reader #" + readerId, e);
        }
    }

    /** Closes a reader. 
     *  @param readerId the id of the reader to close */
    @Override
    public void closeReader(Object readerId) {
        IOUtil.close(getReader(readerId, false));
    }

    // private helper methods --------------------------------------------------

    /** @return the writer with the provided writerId
     *  @throws AutomationException if required is <code>true</code>, 
     *  but no writer with the provided writerId was found */
    private SeparatedFileWriter getWriter(Object writerId, boolean required) {
        SeparatedFileWriter writer = writers.get(writerId);
        if (writer == null && required) {
            throw new AutomationException("Writer not found: " + writerId);
        }
        return writer;
    }

    /** @return the writer with the provided readerId
     *  @throws AutomationException if required is <code>true</code>, 
     *  but no reader with the provided readerId was found */
    private SeparatedFileReader<?> getReader(Object readerId, boolean required) {
        SeparatedFileReader<?> reader = readers.get(readerId);
        if (reader == null && required) {
            throw new AutomationException("Reader not found: " + readerId);
        }
        return reader;
    }

}
