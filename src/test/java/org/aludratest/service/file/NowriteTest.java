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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.aludratest.exception.AutomationException;
import org.aludratest.service.AbstractAludraServiceTest;
import org.databene.commons.FileUtil;
import org.databene.commons.IOUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the FileService on a system without write permission.
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public class NowriteTest extends AbstractAludraServiceTest {

    private static final File TEMPLATE = new File("src/test/resources/fileServiceTest");
    private static final File ROOT = new File("target/fileServiceTest");

    private FileService service;

    /** Invoked before each test method invocation, copies the directory template from src/test/resources to target/ and
     * initializes the {@link #service} attribute.
     * @throws Exception File related exception. */
    @Before
    public void prepareFilesAndService() throws Exception {
        FileUtil.deleteDirectoryIfExists(ROOT);
        FileUtil.copy(TEMPLATE, ROOT, true, null);
        service = getService(FileService.class, "nowritetest");
    }

    /** Closes the {@link #service} after each test method execution. */
    @After
    public void closeService() {
        IOUtil.close(service);
    }

    // tests -------------------------------------------------------------------

    @Test
    public void testGetChildren_root() {
        List<String> list = service.perform().getChildren("/");
        assertTrue(list.size() >= 2);
        assertTrue(list.contains("sub"));
        assertTrue(list.contains("file.txt"));
    }

    @Test
    public void testGetChildren_subfolder() {
        List<String> list = service.perform().getChildren("sub");
        assertTrue(list.size() == 1);
        assertEquals("sub/subfile.txt", list.get(0));
    }

    @Test
    public void testGetChildren_regex() {
        List<String> list = service.perform().getChildren("sub", ".*\\.txt");
        assertEquals(1, list.size());
        assertEquals("sub/subfile.txt", list.get(0));
    }

    @Test(expected = AutomationException.class)
    public void testCreateDirectory_root() {
        service.perform().createDirectory("sub2");
    }

    @Test(expected = AutomationException.class)
    public void testCreateDirectory_subfolder() {
        service.perform().createDirectory("sub/sub2");
    }

    @Test(expected = AutomationException.class)
    public void testMove() {
        service.perform().move("sub/subfile.txt", "newsub/moved-subfile.txt", false);
    }

    @Test(expected = AutomationException.class)
    public void testCopy() {
        service.perform().copy("sub/subfile.txt", "newsub/moved-subfile.txt", false);
    }

    @Test(expected = AutomationException.class)
    public void testDelete_file() {
        service.perform().delete("sub/subfile.txt");
    }

    // write tests -------------------------------------------------------------

    @Test(expected = AutomationException.class)
    public void testWriteTextFile() throws Exception {
        service.perform().writeTextFile("out.txt", "Demo", true);
    }

    @Test(expected = AutomationException.class)
    public void testWriteBinaryFile() throws IOException {
        service.perform().writeBinaryFile("out.dat", "binary".getBytes(), true);
    }

    // read tests --------------------------------------------------------------

    @Test
    public void testReadTextFile() {
        String content = service.perform().readTextFile("sub/subfile.txt");
        assertEquals("File in sub folder", content);
    }

    @Test
    public void testReadBinaryFile() {
        byte[] bytes = service.perform().readBinaryFile("sub/subfile.txt");
        assertArrayEquals("File in sub folder".getBytes(), bytes);
    }

    // polling tests -----------------------------------------------------------

    @Test
    public void testWaitUntilExists_preexisting() {
        service.perform().waitUntilExists("File", "file.txt");
    }

    @Test
    public void testWaitForFirstMatch_preexistent() {
        service.perform().waitForFirstMatch("/", new FileFilter() {
            @Override
            public boolean accept(FileInfo file) {
                return true;
            }
        });
    }

    // existence verification tests --------------------------------------------

    @Test
    public void testAssertPresence_success() {
        service.verify().assertPresence("file.txt");
        service.verify().assertPresence("sub/subfile.txt");
    }

    @Test(expected = AutomationException.class)
    public void testAssertPresence_failure() {
        service.verify().assertPresence("no-file.txt");
    }

    @Test
    public void testAssertAbsence_success() {
        service.verify().assertAbsence("no-file.txt");
    }

    @Test(expected = AutomationException.class)
    public void testAssertAbsence_failure() {
        service.verify().assertAbsence("file.txt");
    }

    // FileCondition tests -----------------------------------------------------

    @Test
    public void testExists_true() {
        assertTrue(service.check().exists("/"));
        assertTrue(service.check().exists("sub"));
        assertTrue(service.check().exists("file.txt"));
        assertTrue(service.check().exists("sub/subfile.txt"));
    }

    @Test
    public void testExists_false() {
        assertFalse(service.check().exists("no-file.txt"));
        assertFalse(service.check().exists("sub/no-file.txt"));
    }

    @Test(expected = AutomationException.class)
    public void testExists_noPath() {
        service.check().exists("");
    }

    @Test
    public void testIsDirectory_true() {
        assertTrue(service.check().isDirectory("/"));
        assertTrue(service.check().isDirectory("sub"));
    }

    @Test
    public void testIsDirectory_false() {
        assertFalse(service.check().isDirectory("file.txt"));
        assertFalse(service.check().isDirectory("sub/subfile.txt"));
    }

}
