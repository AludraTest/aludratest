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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.Reader;

import org.aludratest.content.separated.SepPersonData;
import org.databene.commons.IOUtil;
import org.junit.Test;

/**
 * Tests the {@link SeparatedFileReader}.
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public class SeparatedFileReaderTest {

    @Test
    public void testReader() throws Exception {
        Reader in = null;
        try {
            in = IOUtil.getReaderForURI("org/aludratest/content/separated/persons.csv");
            SeparatedFileReader<SepPersonData> reader = null;
            reader = new SeparatedFileReader<SepPersonData>(in, SepPersonData.class, ',');
            assertArrayEquals(new String[] { "VER1234" }, reader.readRaw());
            assertEquals(new SepPersonData("Alice", "23"), reader.readRow());
            assertEquals(new SepPersonData("Bob", "34"), reader.readRow());
            assertNull(reader.readRow());
        } finally {
            IOUtil.close(in);
        }
    }

}
