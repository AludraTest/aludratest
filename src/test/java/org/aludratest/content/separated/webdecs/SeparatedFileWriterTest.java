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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileReader;
import java.io.FileWriter;

import org.aludratest.content.separated.SepPersonData;
import org.aludratest.content.separated.webdecs.SeparatedFileWriter;
import org.databene.commons.IOUtil;
import org.databene.commons.ReaderLineIterator;
import org.junit.Test;

/**
 * Tests the {@link SeparatedFileWriter}.
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public class SeparatedFileWriterTest {
    
    @Test
    public void test() throws Exception {
        String filePath = "target/sepfwtest.csv";
        FileWriter out = null;
        try {
            out = new FileWriter(filePath);
            SeparatedFileWriter writer = new SeparatedFileWriter(out, SepPersonData.class, '\t', "VER1234");
            writer.writeRow(new SepPersonData("Alice", "23"));
            writer.writeRow(new SepPersonData("Bob", "34"));
            IOUtil.close(writer);
        } finally {
            IOUtil.close(out);
        }
        ReaderLineIterator iterator = new ReaderLineIterator(new FileReader(filePath));
        assertTrue(iterator.hasNext());
        assertEquals("VER1234", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("Alice\t23", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("Bob\t34", iterator.next());
        assertFalse(iterator.hasNext());
    }

}
