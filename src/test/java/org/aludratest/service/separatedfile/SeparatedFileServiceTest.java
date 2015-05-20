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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.aludratest.content.separated.SepPersonData;
import org.aludratest.content.separated.data.WrappedSeparatedData;
import org.aludratest.service.AbstractAludraServiceTest;
import org.aludratest.service.separatedfile.SeparatedFileReader;
import org.aludratest.service.separatedfile.SeparatedFileService;
import org.aludratest.service.separatedfile.SeparatedFileWriter;
import org.aludratest.util.data.StringData;
import org.databene.commons.IOUtil;
import org.databene.commons.ReaderLineIterator;
import org.junit.Test;

/**
 * Integration test of the {@link SeparatedFileService}. 
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public class SeparatedFileServiceTest extends AbstractAludraServiceTest {
    
    /** Tests the SeparatedFileWriter with SeparatedFileBeans. */
    @Test
    public void testAnnotationBeanWriter() throws Exception {
        // set up the SeparatedFileService
        SeparatedFileService service = getLoggingService(SeparatedFileService.class, "annotest");

        // GIVEN a SeparatedFileWriter
        String fileName = "target" + File.separator + getClass().getSimpleName() + ".anno.fcw";
        MySeparatedFileWriter writer = new MySeparatedFileWriter(fileName, service, true);

        // WHEN persisting 2 FFPerson and 1 FFAddress beans
        writer.writeRow(new SepPersonData("Alice", "23"));
        writer.writeRow(new SepPersonData("Bob", "34"));
        writer.close();

        // THEN the data shall be formatted properly according to the individual formats.
        ReaderLineIterator iterator = new ReaderLineIterator(IOUtil.getReaderForURI(fileName));
        assertTrue(iterator.hasNext());
        assertEquals("VER1234", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("Alice\t23", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("Bob\t34", iterator.next());
        assertFalse(iterator.hasNext());

        // tear down the SeparatedFileService
        IOUtil.close(service);
        assertNotFailed();
    }

    /** Tests the BeanSeparatedFileReader with SeparatedFileBeans */
    @Test
    public void testAnnotationBeanReader() throws Exception {
        // set up the SeparatedFileService
        SeparatedFileService service = getLoggingService(SeparatedFileService.class, "annotest");

        String filePath = getClass().getPackage().getName().replace('.', File.separatorChar) + File.separator + "persons.csv";
        MySeparatedFileReader reader = new MySeparatedFileReader(filePath, service);
        assertNotFailed();

        StringData header = new StringData();
        reader.readHeader(header);
        assertEquals("VER1234", header.getValue());
        
        WrappedSeparatedData<SepPersonData> row = new WrappedSeparatedData<SepPersonData>();
        reader.readRow(row);
        assertEquals(new SepPersonData("Alice", "23"), row.getValue());
        reader.readRow(row);
        assertEquals(new SepPersonData("Bob", "34"), row.getValue());
        reader.readRow(row);
        assertNull(row.getValue());
        reader.close();

        // tear down the SeparatedFileService
        IOUtil.close(service);
        assertNotFailed();
    }
    
    public static final class MySeparatedFileWriter extends SeparatedFileWriter<SepPersonData, MySeparatedFileWriter> {
        public MySeparatedFileWriter(String filePath, SeparatedFileService service, boolean overwrite) {
            super(filePath, service, overwrite, SepPersonData.class, '\t', "VER1234");
        }
    }
    
    public static final class MySeparatedFileReader extends SeparatedFileReader<SepPersonData, MySeparatedFileReader> {
        public MySeparatedFileReader(String filePath, SeparatedFileService service) {
            super(filePath, service, SepPersonData.class, '\t');
        }
    }
    
}