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
import org.aludratest.exception.FunctionalFailure;
import org.aludratest.util.validator.EqualsValidator;
import org.databene.commons.FileUtil;
import org.databene.commons.IOUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the FileService on the local file system.
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public class LocalFileServiceTest extends AbstractLocalFileServiceTest {

    protected FileService service;

    /**
     * Invoked before each test method invocation
     * in order to initialize the {@link #service} attribute.
     * @throws Exception
     */
    @Before
    public void prepareService() throws Exception {
        service = getService(FileService.class, "localtest");
    }

    /** Closes the {@link #service} after each test method execution. */
    @After
    public void closeService() {
        IOUtil.close(service);
    }

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

    @Test(expected = AutomationException.class)
    public void testGetChildren_noPath() {
        service.perform().getChildren("");
    }

    @Test
    public void testGetChildren_regex() {
        List<String> list = service.perform().getChildren("sub", ".*\\.txt");
        assertEquals(1, list.size());
        assertEquals("sub/subfile.txt", list.get(0));
    }

    @Test(expected = AutomationException.class)
    public void testGetChildren_regex_noPath() {
        service.perform().getChildren("", "x");
    }

    @Test(expected = AutomationException.class)
    public void testGetChildren_filter_noPath() {
        service.perform().getChildren("", (FileFilter) null);
    }

    @Test
    public void testCreateDirectory_root() {
        // GIVEN a root directory without a sub directory
        File createdDir = new File(ROOT, "sub2");
        assertFalse(createdDir.exists());
        // WHEN calling the service to create a sub directory
        service.perform().createDirectory("sub2");
        // THEN the sub directory must exist
        assertTrue(createdDir.exists());
        assertTrue(createdDir.isDirectory());
    }

    @Test
    public void testCreateDirectory_subfolder() {
        // GIVEN a sub directory without a sub directory
        File createdDir = new File(new File(ROOT, "sub"), "sub2");
        assertFalse(createdDir.exists());
        // WHEN calling the service to create a sub directory
        service.perform().createDirectory("sub/sub2");
        // THEN the sub directory must exist
        assertTrue(createdDir.exists());
        assertTrue(createdDir.isDirectory());
    }

    @Test(expected = AutomationException.class)
    public void testCreateDirectory_noPath() {
        service.perform().createDirectory("");
    }

    // move tests --------------------------------------------------------------

    @Test
    public void testMove_file_noOverwrite_noPreviousFile() {
        boolean overwritten = service.perform().move("sub/subfile.txt", "newsub/moved-subfile.txt", false);
        assertFalse(overwritten);
        assertAbsence("sub/subfile.txt");
        assertPresence("newsub/moved-subfile.txt");
    }

    @Test
    public void testMove_file_overwrite_noPreviousFile() {
        boolean overwritten = service.perform().move("sub/subfile.txt", "newsub/moved-subfile.txt", true);
        assertFalse(overwritten);
        assertAbsence("sub/subfile.txt");
        assertPresence("newsub/moved-subfile.txt");
    }

    @Test
    public void testMove_file_overwrite_previousFile() throws Exception {
        haveFile("newsub/moved-subfile.txt");
        boolean overwritten = service.perform().move("sub/subfile.txt", "newsub/moved-subfile.txt", true);
        assertTrue(overwritten);
        assertAbsence("sub/subfile.txt");
        assertPresence("newsub/moved-subfile.txt");
    }

    @Test(expected = FunctionalFailure.class)
    public void testMove_file_noOverwrite_previousFile() throws Exception {
        haveFile("newsub/moved-subfile.txt");
        service.perform().move("sub/subfile.txt", "newsub/moved-subfile.txt", false);
    }

    @Test
    public void testMove_folder() {
        boolean overwritten = service.perform().move("sub", "sub2", false);
        assertFalse(overwritten);
    }

    @Test(expected = AutomationException.class)
    public void testMove_noSourcePath() {
        service.perform().move("", "x", true);
    }

    @Test(expected = AutomationException.class)
    public void testMove_noTargetPath() {
        service.perform().move("x", "", true);
    }

    // copy tests --------------------------------------------------------------

    @Test
    public void testCopy_file_noOverwrite_noPreviousFile() {
        boolean overwritten = service.perform().copy("sub/subfile.txt", "newsub/moved-subfile.txt", false);
        assertFalse(overwritten);
        assertPresence("sub/subfile.txt");
        assertPresence("newsub/moved-subfile.txt");
    }

    @Test
    public void testCopy_file_overwrite_noPreviousFile() {
        boolean overwritten = service.perform().copy("sub/subfile.txt", "newsub/moved-subfile.txt", true);
        assertFalse(overwritten);
        assertPresence("sub/subfile.txt");
        assertPresence("newsub/moved-subfile.txt");
    }

    @Test
    public void testCopy_file_overwrite_previousFile() throws Exception {
        haveFile("newsub/moved-subfile.txt");
        boolean overwritten = service.perform().copy("sub/subfile.txt", "newsub/moved-subfile.txt", true);
        assertTrue(overwritten);
        assertPresence("sub/subfile.txt");
        assertPresence("newsub/moved-subfile.txt");
    }

    @Test(expected = FunctionalFailure.class)
    public void testCopy_file_noOverwrite_previousFile() throws Exception {
        haveFile("newsub/moved-subfile.txt");
        service.perform().copy("sub/subfile.txt", "newsub/moved-subfile.txt", false);
    }

    @Test
    public void testCopy_folder() {
        boolean overwritten = service.perform().copy("sub", "sub2", false);
        assertFalse(overwritten);
    }

    @Test(expected = AutomationException.class)
    public void testCopy_noSourcePath() {
        service.perform().copy("", "x", true);
    }

    @Test(expected = AutomationException.class)
    public void testCopy_noTargetPath() {
        service.perform().copy("x", "", true);
    }

    // delete tests ------------------------------------------------------------

    @Test
    public void testDelete_file() {
        service.perform().delete("sub/subfile.txt");
        assertAbsence("sub/subfile.txt");
    }

    @Test
    public void testDelete_folder() {
        service.perform().delete("sub");
        assertAbsence("sub");
    }

    @Test(expected = AutomationException.class)
    public void testDelete_noPath() {
        service.perform().delete("");
    }

    // write tests -------------------------------------------------------------

    @Test
    public void testWriteTextFile() throws Exception {
        service.perform().writeTextFile("out.txt", "Demo", true);
        String content = IOUtil.getContentOfURI(ROOT + "/out.txt");
        assertEquals("Demo", content);
    }

    @Test
    public void testWriteTextFile_emptyFile() throws Exception {
        service.perform().writeTextFile("empty.txt", "", true);
        String content = IOUtil.getContentOfURI(ROOT + "/empty.txt");
        assertEquals(0, content.length());
    }

    @Test(expected = AutomationException.class)
    public void testWriteTextFile_noPath() {
        service.perform().writeTextFile("", "s", true);
    }

    @Test
    public void testWriteBinaryFile() throws IOException {
        service.perform().writeBinaryFile("out.dat", "binary".getBytes(), true);
        byte[] bytes = IOUtil.getBinaryContentOfUri(ROOT + "/out.dat");
        assertArrayEquals("binary".getBytes(), bytes);
    }

    @Test(expected = AutomationException.class)
    public void testWriteBinaryFile_noPath() {
        service.perform().writeBinaryFile("", "binary".getBytes(), true);
    }

    // read tests --------------------------------------------------------------

    @Test
    public void testReadTextFile() {
        String content = service.perform().readTextFile("sub/subfile.txt");
        assertEquals("File in sub folder", content);
    }

    @Test
    public void testReadTextFile_utf8() {
        String content = service.perform().readTextFile("encoding/utf8.txt");
        assertEquals("AÄÖÜÉÓÚÈÒÙ", content);
    }

    @Test(expected = AutomationException.class)
    public void testReadTextFile_noPath() {
        service.perform().readTextFile("");
    }

    @Test
    public void testReadBinaryFile() {
        byte[] bytes = service.perform().readBinaryFile("sub/subfile.txt");
        assertArrayEquals("File in sub folder".getBytes(), bytes);
    }

    @Test(expected = AutomationException.class)
    public void testReadBinaryFile_noPath() {
        service.perform().readBinaryFile("");
    }

    // testing waitUntilExists() -----------------------------------------------

    @Test
    public void testWaitUntilExists_preexisting() {
        service.perform().waitUntilExists("File", "file.txt");
    }

    @Test
    public void testWaitUntilExists_delayed() {
        (new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    IOUtil.writeTextFile(ROOT + "/delayed-file.txt", "x");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        service.perform().waitUntilExists("File", "delayed-file.txt");
    }

    @Test(expected = FunctionalFailure.class)
    public void testWaitUntilExists_failure() {
        service.perform().waitUntilExists("File", "no-file.txt");
    }

    @Test(expected = AutomationException.class)
    public void testWaitUntilExists_noPath() {
        service.perform().waitUntilExists("File", "");
    }

    // testing waitUntilNotExists() --------------------------------------------

    @Test
    public void testWaitUntilNotExists_nonexisting() {
        service.perform().waitUntilNotExists("__nonexisting.file__");
    }

    @Test
    public void testWaitUntilNotExists_delayed() {
        (new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    service.perform().delete("file.txt");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        service.perform().waitUntilNotExists("file.txt");
    }

    @Test(expected = FunctionalFailure.class)
    public void testWaitUntilNotExists_failure() {
        service.perform().waitUntilNotExists("file.txt");
    }

    @Test(expected = AutomationException.class)
    public void testWaitUntilNotExists_noPath() {
        service.perform().waitUntilNotExists("");
    }

    // testing waitForFirstMatch() ---------------------------------------------

    @Test
    public void testWaitForFirstMatch_preexistent() {
        service.perform().waitForFirstMatch("/", new FileFilter() {
            @Override
            public boolean accept(FileInfo file) {
                return true;
            }
        });
    }

    @Test(expected = FunctionalFailure.class)
    public void testWaitForFirstMatch_failure() {
        service.perform().waitForFirstMatch("/", new FileFilter() {
            @Override
            public boolean accept(FileInfo file) {
                return false;
            }
        });
    }

    @Test
    public void testWaitForFirstMatch_delayed() {
        (new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    IOUtil.writeTextFile(ROOT + "/delayed-file2.txt", "x");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(FileInfo file) {
                return file.getName().endsWith("delayed-file2.txt");
            }
        };
        service.perform().waitForFirstMatch("/", filter);
    }

    @Test(expected = AutomationException.class)
    public void testWaitForFirstMatch_noPath() {
        service.perform().waitForFirstMatch("", new FileFilter() {
            @Override
            public boolean accept(FileInfo file) {
                return true;
            }
        });
    }

    @Test(expected = AutomationException.class)
    public void testWaitForFirstMatch_noFilter() {
        service.perform().waitForFirstMatch("/", null);
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

    @Test(expected = AutomationException.class)
    public void testAssertPresence_noPath() {
        service.verify().assertPresence("");
    }

    @Test
    public void testAssertAbsence_success() {
        service.verify().assertAbsence("no-file.txt");
    }

    @Test(expected = AutomationException.class)
    public void testAssertAbsence_failure() {
        service.verify().assertAbsence("file.txt");
    }

    @Test(expected = AutomationException.class)
    public void testAssertAbsense_noPath() {
        service.verify().assertAbsence("");
    }

    // content verification tests ----------------------------------------------
    @Test
    public void testAssertContentMatches_success() {
        service.verify().assertTextContentMatches("file.txt", new EqualsValidator("Simple file"));
    }

    @Test(expected = FunctionalFailure.class)
    public void testAssertContentMatches_functionalFailure() {
        service.verify().assertTextContentMatches("file.txt", new EqualsValidator("Complex file"));
    }

    @Test(expected = AutomationException.class)
    public void testAssertContentMatches_automationException() {
        service.verify().assertTextContentMatches("no-such-file.txt", new EqualsValidator("Simple file"));
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

    @Test(expected = AutomationException.class)
    public void testIsDirectory_noPath() {
        service.check().isDirectory("");
    }

    // private helper methods --------------------------------------------------

    private void assertPresence(String filePath) {
        assertTrue("File not found: " + filePath, new File(ROOT, filePath).exists());
    }

    private void assertAbsence(String filePath) {
        assertFalse("File is assumed to be absent: " + filePath, new File(ROOT, filePath).exists());
    }

    private void haveFile(String filePath) throws IOException {
        File file = new File(ROOT, filePath);
        FileUtil.ensureDirectoryExists(file.getParentFile());
        IOUtil.writeTextFile(file.getAbsolutePath(), "xxx");
    }

}
