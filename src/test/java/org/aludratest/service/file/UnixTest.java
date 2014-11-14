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
package org.aludratest.service.file;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.aludratest.service.file.FileService;
import org.databene.commons.IOUtil;
import org.databene.commons.SystemInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the conversion of character encodings and linefeeds 
 * for Unix-formatted files. 
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public class UnixTest extends AbstractLocalFileServiceTest {

    /** The {@link FileService} instance to be used for testing; 
     *  it is opened before each test and closed afterwards. */
    private FileService service;

    /** Opens the {@link #service} */
    @Before
    public void openService() {
        service = getService(FileService.class, "unixtest");
    }

    /** Closes the {@link #service} */
    @After
    public void closeService() {
        IOUtil.close(service);
    }

    // tests -------------------------------------------------------------------

    @Test
    public void testReadUTF8() {
        String content = service.perform().readTextFile("encoding/utf8.txt");
        assertEquals("AÄÖÜÉÓÚÈÒÙ", content);
    }

    @Test
    public void testWriteUTF8() throws Exception {
        String filePath = "encoding/utf8-written.txt";
        service.perform().writeTextFile(filePath, "AÄÖÜÉÓÚÈÒÙ", true);
        byte[] content = service.perform().readBinaryFile(filePath);
        assertArrayEquals("AÄÖÜÉÓÚÈÒÙ".getBytes("utf-8"), content);
    }

    @Test
    public void testReadWindowsLinefeed() {
        checkReadLF("encoding/linefeed-rn.txt");
    }

    @Test
    public void testReadUnixLinefeed() {
        checkReadLF("encoding/linefeed-n.txt");
    }

    @Test
    public void testWriteWindowsLinefeed() throws Exception {
        checkWriteLF("encoding/linefeed-windows.txt", "\r\n");
    }

    @Test
    public void testWriteUnixLinefeed() throws Exception {
        checkWriteLF("encoding/linefeed-unix.txt", "\r\n");
    }

    // private helper methods --------------------------------------------------

    private void checkReadLF(String filePath) {
        String content = service.perform().readTextFile(filePath);
        assertEquals("Alpha" + SystemInfo.getLineSeparator() + "Beta", content);
    }

    private void checkWriteLF(String filePath, String separator) throws Exception {
        String sourceFormat = "Alpha" + separator + "Beta";
        service.perform().writeTextFile(filePath, sourceFormat, true);
        String expectedContent = "Alpha\nBeta";
        String actualContent = new String(service.perform().readBinaryFile(filePath), "utf8");
        assertEquals(expectedContent, actualContent);
    }

}
