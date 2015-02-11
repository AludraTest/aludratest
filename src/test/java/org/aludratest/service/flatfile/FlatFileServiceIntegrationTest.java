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

import static org.junit.Assert.*;

import java.io.File;
import java.util.Calendar;

import org.aludratest.content.flat.PrefixRowType;
import org.aludratest.content.flat.data.FlatFileBeanData;
import org.aludratest.content.flat.data.WrappedRowData;
import org.aludratest.service.AbstractAludraServiceTest;
import org.aludratest.service.flatfile.FlatFileReader;
import org.aludratest.service.flatfile.FlatFileService;
import org.aludratest.service.flatfile.FlatFileWriter;
import org.databene.commons.IOUtil;
import org.databene.commons.ReaderLineIterator;
import org.databene.commons.TimeUtil;
import org.junit.Test;

/**
 * Tests the {@link FlatFileService}.
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public class FlatFileServiceIntegrationTest extends AbstractAludraServiceTest {

    /** Tests the BeanFlatFileWriter with FlatFileBeans. */
    @Test
    public void testAnnotationBeanWriter() throws Exception {
        // set up the FlatFileService
        FlatFileService service = getLoggingService(FlatFileService.class, "annotest");

        // GIVEN an ArrayFlatFileWriter
        String fileName = "target" + File.separator + getClass().getSimpleName() + ".anno.fcw";
        MyFlatFileWriter writer = new MyFlatFileWriter(fileName, service, true);

        // WHEN persisting 2 FFPerson and 1 FFAddress beans
        writer.writeRow(new FFFlatPerson("Alice", 23, TimeUtil.date(1991, Calendar.JANUARY, 6), "Miez"));
        writer.writeRow(new FFFlatPerson("Bob", 34, TimeUtil.date(1980, Calendar.FEBRUARY, 3), "Hasso"));
        writer.writeRow(new FFAddress("MAIN STREET 321", "NEW YORK", 123.45));
        writer.close();

        // THEN the data shall be formatted properly according to the individual formats.
        ReaderLineIterator iterator = new ReaderLineIterator(IOUtil.getReaderForURI(fileName));
        assertTrue(iterator.hasNext());
        assertEquals("PAlice               02319910106Miez    ", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("PBob                 03419800203Hasso   ", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("AMAIN STREET 321     NEW YORK            0123.45", iterator.next());
        assertFalse(iterator.hasNext());

        // tear down the FlatFileService
        IOUtil.close(service);
        assertNotFailed();
    }

    /** Tests the BeanFlatFileReader with FlatFileBeans */
    @Test
    public void testAnnotationBeanReader() throws Exception {
        // set up the FlatFileService
        FlatFileService service = getLoggingService(FlatFileService.class, "annotest");

        String filePath = getClass().getPackage().getName().replace('.', File.separatorChar) + File.separator + "reader_test.flat";
        FlatFileReader<FlatFileBeanData> reader = new FlatFileReader<FlatFileBeanData>(filePath, service) {
        };
        reader.addRowType(new PrefixRowType(FFFlatPerson.class, "P"));
        reader.addRowType(new PrefixRowType(FFAddress.class, "A"));
        assertNotFailed();

        WrappedRowData row = new WrappedRowData();
        reader.readRow(row);
        assertEquals(new FFFlatPerson("Alice", 23, TimeUtil.date(1991, 0, 6), "Miez"), row.getValue());
        reader.readRow(row);
        assertEquals(new FFFlatPerson("Bob", 34, TimeUtil.date(1980, 1, 3), "Hasso"), row.getValue());
        reader.readRow(row);
        assertEquals(new FFAddress("MAIN STREET 321", "NEW YORK", 123.45), row.getValue());
        reader.readRow(row);
        assertNull(row.getValue());
        reader.close();

        // tear down the FlatFileService
        IOUtil.close(service);
        assertNotFailed();
    }
    
    public static final class MyFlatFileWriter extends FlatFileWriter<FlatFileBeanData, MyFlatFileWriter> {
        public MyFlatFileWriter(String filePath, FlatFileService service, boolean overwrite) {
            super(filePath, service, overwrite);
        }
    }
    
}
