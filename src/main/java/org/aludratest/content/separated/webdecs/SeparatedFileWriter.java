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

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;

import org.aludratest.content.separated.data.SeparatedFileBeanData;
import org.aludratest.content.separated.util.SeparatedUtil;
import org.databene.commons.IOUtil;
import org.databene.commons.StringUtil;
import org.databene.commons.SystemInfo;
import org.databene.formats.csv.BeanCSVWriter;
import org.databene.formats.script.ConstantScript;

/**
 * Writes {@link SeparatedFileBeanData} objects to a stream using separators.
 * @author Volker Bergmann
 */
public class SeparatedFileWriter implements Closeable {

    /** Internally used writer */
    private BeanCSVWriter<SeparatedFileBeanData> writer;

    /**
     * Constructor which instantiates the internal writer and forwards the argument to it. 
     * @param out the writer to send the formatted character data to
     * @param separator the separator character to use
     * @param beanType the type of the beans to persist
     * @param header 
     */
    public SeparatedFileWriter(Writer out, Class<? extends SeparatedFileBeanData> beanType, char separator, String header) {
        this.writer = new BeanCSVWriter<SeparatedFileBeanData>(out, separator, false, SeparatedUtil.featureNames(beanType));
        if (!StringUtil.isEmpty(header)) {
            this.writer.setHeaderScript(new ConstantScript(header + SystemInfo.getLineSeparator()));
        }
    }

    /** 
     * Chooses the row format by the bean class, formats the bean graph elements accordingly
     * and writes them to the output stream. 
     * @param bean 
     * @throws IOException
     */
    public void writeRow(SeparatedFileBeanData bean) throws IOException {
        this.writer.writeElement(bean);
    }

    /** Closes the internal writer. */
    public void close() throws IOException {
        IOUtil.close(this.writer);
    }

}
