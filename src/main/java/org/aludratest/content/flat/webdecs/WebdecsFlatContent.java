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

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.aludratest.content.flat.FlatContent;
import org.aludratest.content.flat.data.FlatFileBeanData;
import org.aludratest.content.flat.data.RowTypeData;
import org.aludratest.exception.AutomationException;
import org.aludratest.exception.TechnicalException;
import org.databene.commons.IOUtil;

/**
 * {@link FlatContent} implementation based on the Databene Webdecs library.
 * @author Volker Bergmann
 */
public class WebdecsFlatContent implements FlatContent {

    private Locale locale;

    /** Atomic integer that provides stream ids which are unique within the process. */
    private AtomicInteger streamIdProvider;

    /** Map that assigns stream ids to readers. */
    private Map<Object, FlatFileBeanReader> readers;

    /** Map that assigns stream ids to writers. */
    private Map<Object, FlatFileBeanWriter> writers;

    /** Constructor. */
    public WebdecsFlatContent() {
        this(Locale.getDefault());
    }

    /** Constructor. 
     *  @param locale the locale to use for rendering numbers and dates */
    public WebdecsFlatContent(Locale locale) {
        this.locale = locale;
        this.streamIdProvider = new AtomicInteger();
        this.readers = new HashMap<Object, FlatFileBeanReader>();
        this.writers = new HashMap<Object, FlatFileBeanWriter>();
    }

    /** Sets the {@link #locale} to use for rendering numbers and dates */
    @Override
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    /** Creates a writer for persisting FlatFileBeans or JavaBean data structures. 
     *  @param out the writer to use for persisting the flat file content 
     *  @return the id of the new writer */
    @Override
    public Object createWriter(Writer out) {
        FlatFileBeanWriter writer = new FlatFileBeanWriter(out, locale, null);
        Object id = "writer#" + streamIdProvider.incrementAndGet();
        writers.put(id, writer);
        return id;
    }

    /** Appends a row to the flat file writer denoted by the writerId. 
     *  @param rowBean the FlatFileBean holding the data 
     *  @param writerId the id of the writer with which to store the formatted text */
    @Override
    public void writeRow(Object rowBean, Object writerId) {
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
        FlatFileBeanWriter writer = getWriter(writerId, false);
        IOUtil.close(writer);
    }

    /** Creates a reader object for reading JavaBeans. 
     *  @param source the source reader that provides the flat file's character data
     *  @return the id of the writer */
    @Override
    public Object createReader(Reader source) {
        FlatFileBeanReader reader = new FlatFileBeanReader(source, locale);
        String id = "reader#" + streamIdProvider.incrementAndGet();
        readers.put(id, reader);
        return id;
    }

    /** Adds a RowType to a BeanFlatFileReader. 
     *  @param rowType a {@link RowTypeData} for reader setup
     *  @param readerId the id of the writer */
    @Override
    public void addRowType(RowTypeData rowType, Object readerId) {
        getReader(readerId, true).addRowType(rowType);
    }

    /** Reads a flat file cell and provides it as Java object. 
     *  @param readerId the id of the reader 
     *  @return the id of the new reader */
    @Override
    public FlatFileBeanData readRow(Object readerId) {
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
    private FlatFileBeanWriter getWriter(Object writerId, boolean required) {
        FlatFileBeanWriter writer = writers.get(writerId);
        if (writer == null && required) {
            throw new AutomationException("Writer not found: " + writerId);
        }
        return writer;
    }

    /** @return the writer with the provided readerId
     *  @throws AutomationException if required is <code>true</code>, 
     *  but no reader with the provided readerId was found */
    private FlatFileBeanReader getReader(Object readerId, boolean required) {
        FlatFileBeanReader reader = readers.get(readerId);
        if (reader == null && required) {
            throw new AutomationException("Reader not found: " + readerId);
        }
        return reader;
    }

}
