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

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Locale;

import org.databene.commons.IOUtil;
import org.databene.formats.fixedwidth.FixedWidthColumnDescriptor;
import org.databene.formats.fixedwidth.FixedWidthRowTypeDescriptor;
import org.databene.formats.fixedwidth.MultiTypeBeanFixedWidthWriter;


/**
 * Reads beans from a flat file using the Databene Webdecs library.
 * @author Volker Bergmann
 */
public class FlatFileBeanWriter implements Closeable {

    /** The default locale to use for number and date formats */
    private Locale defaultLocale;

    /** Internally used writer */
    private MultiTypeBeanFixedWidthWriter writer;

    /**
     * Constructor which instantiates the internal writer and forwards the argument to it.
     * @param out the writer to send the formatted character data to
     * @param defaultLocale the default locale to use for formatting numbers and dates
     * @param rowFormats the row formats to apply
     */
    public FlatFileBeanWriter(Writer out, Locale defaultLocale, List<FixedWidthRowTypeDescriptor> rowFormats) {
        this.writer = new MultiTypeBeanFixedWidthWriter(out, rowFormats);
        this.defaultLocale = defaultLocale;
    }

    /** Chooses the row format by the bean class, formats the bean graph elements accordingly and writes them to the output stream.
     * @param bean the Java object to export
     * @throws IOException if file output fails */
    public void writeRow(Object bean) throws IOException {
        String simpleBeanClassName = bean.getClass().getSimpleName();
        if (writer.getRowFormat(simpleBeanClassName) == null) {
            defineRowFormatByAnnotations(bean);
        }
        this.writer.write(bean);
    }

    /** Closes the internal writer. */
    @Override
    public void close() {
        IOUtil.close(this.writer);
    }

    // private helpers -------------------------------------------------------------------------------------------------

    /** Parses a FlatFileBean's attribute annotations into
     *  an array of {@link FixedWidthColumnDescriptor}s
     *  and configures the {@link #writer} accordingly. */
    private void defineRowFormatByAnnotations(Object bean) {
        Class<? extends Object> beanClass = bean.getClass();
        FixedWidthRowTypeDescriptor formats = AnnotationUtil.parseFlatFileColumns(beanClass, defaultLocale);
        writer.addRowFormat(beanClass.getSimpleName(), formats);
    }

}
