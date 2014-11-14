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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.aludratest.exception.AutomationException;
import org.aludratest.exception.FunctionalFailure;
import org.aludratest.exception.TechnicalException;
import org.aludratest.service.file.data.TargetFileData;
import org.aludratest.util.data.StringData;
import org.databene.commons.FileUtil;
import org.databene.commons.IOUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the {@link File} class.
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public class FileTest extends AbstractLocalFileServiceTest {

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

    @Test(expected = AutomationException.class)
    public void testEmptyPath() {
        new File("", service);
    }

    @Test(expected = AutomationException.class)
    public void testNullPath() {
        new File(null, service);
    }

    // delete tests ------------------------------------------------------------

    @Test
    public void testDelete_file() {
        File file = new File("sub/subfile.txt", service);
        file.delete();
        file.assertAbsence();
    }

    @Test
    public void testDelete_folder() {
        File file = new File("sub", service);
        file.delete();
        file.assertAbsence();
    }

    @Test(expected = AutomationException.class)
    public void testDelete_noPath() {
        File file = new File("", service);
        file.delete();
    }

    // write tests -------------------------------------------------------------

    @Test
    public void testWriteTextFile() throws Exception {
        File file = new File("out.txt", service);
        file.writeTextContent(new StringData("Demo"));
        String content = IOUtil.getContentOfURI(ROOT + "/out.txt");
        assertEquals("Demo", content);
    }

    @Test
    public void testWriteTextFile_emptyFile() throws Exception {
        File file = new File("empty.txt", service);
        file.writeTextContent(new StringData(""));
        String content = IOUtil.getContentOfURI(ROOT + "/empty.txt");
        assertEquals(0, content.length());
    }

    @Test(expected = AutomationException.class)
    public void testWriteTextFile_noPath() {
        File file = new File("", service);
        file.writeTextContent(new StringData("s"));
    }

    /* feature postponed
    @Test
    public void testWriteBinaryFile() throws IOException {
    	File file = new File("out.dat", service);
    	file.writeBinaryContent("binary".getBytes());
    	byte[] bytes = IOUtil.getBinaryContentOfUri(ROOT + "/out.dat");
    	assertArrayEquals("binary".getBytes(), bytes);
    }
    
    @Test(expected = IllegalFilePathException.class)
    public void testWriteBinaryFile_noPath() {
    	File file = new File("", service);
    	file.writeBinaryContent("binary".getBytes());
    }
    */

    // read tests --------------------------------------------------------------

    @Test
    public void testReadTextFile() {
        File file = new File("sub/subfile.txt", service);
        StringData content = new StringData();
        file.readTextContent(content);
        assertEquals("File in sub folder", content.getValue());
    }

    @Test
    public void testReadTextFile_utf8() {
        File file = new File("encoding/utf8.txt", service);
        StringData content = new StringData();
        file.readTextContent(content);
        assertEquals("AÄÖÜÉÓÚÈÒÙ", content.getValue());
    }

    @Test(expected = AutomationException.class)
    public void testReadTextFile_noPath() {
        File file = new File("", service);
        file.readTextContent(new StringData());
    }

    /* feature postponed
    @Test
    public void testReadBinaryFile() {
    	File file = new File("sub/subfile.txt", service);
    	MutableData<byte[]> content = new MutableData<byte[]>();
    	file.readBinaryContent(content);
    	assertArrayEquals("File in sub folder".getBytes(), content.getValue());
    }
    
    @Test(expected = IllegalFilePathException.class)
    public void testReadBinaryFile_noPath() {
    	File file = new File("", service);
    	MutableData<byte[]> content = new MutableData<byte[]>();
    	file.readBinaryContent(content);
    }
    */

    // directory creation tests ------------------------------------------------

    @Test
    public void testCreateSubDirectory_root() {
        // GIVEN a root directory without a sub directory
        java.io.File createdDir = new java.io.File(ROOT, "sub2");
        assertFalse(createdDir.exists());
        // WHEN calling the service to create a sub directory
        new File(".", service).createSubDirectory(new StringData("sub2"));
        // THEN the sub directory must exist
        assertTrue(createdDir.exists());
        assertTrue(createdDir.isDirectory());
    }

    @Test
    public void testCreateSubDirectory_subfolder() {
        // GIVEN a sub directory without a sub directory
        java.io.File createdDir = new java.io.File(new java.io.File(ROOT, "sub"), "sub2");
        assertFalse(createdDir.exists());
        // WHEN calling the service to create a sub directory
        new File("sub", service).createSubDirectory(new StringData("sub2"));
        // THEN the sub directory must exist
        assertTrue(createdDir.exists());
        assertTrue(createdDir.isDirectory());
    }

    @Test(expected = TechnicalException.class)
    public void testCreateDirectory_noPath() {
        new File(".", service).createSubDirectory(new StringData(""));
    }

    // move tests --------------------------------------------------------------

    @Test
    public void testMove_file_noOverwrite_noPreviousFile() {
        new File("sub/subfile.txt", service).moveTo(new TargetFileData("newsub/moved-subfile.txt", "false"));
        assertAbsence("sub/subfile.txt");
        assertPresence("newsub/moved-subfile.txt");
    }

    @Test
    public void testMove_file_overwrite_noPreviousFile() {
        new File("sub/subfile.txt", service).moveTo(new TargetFileData("newsub/moved-subfile.txt", "true"));
        assertAbsence("sub/subfile.txt");
        assertPresence("newsub/moved-subfile.txt");
    }

    @Test
    public void testMove_file_overwrite_previousFile() throws Exception {
        haveFile("newsub/moved-subfile.txt");
        new File("sub/subfile.txt", service).moveTo(new TargetFileData("newsub/moved-subfile.txt", "true"));
        assertAbsence("sub/subfile.txt");
        assertPresence("newsub/moved-subfile.txt");
    }

    @Test(expected = FunctionalFailure.class)
    public void testMove_file_noOverwrite_previousFile() throws Exception {
        haveFile("newsub/moved-subfile.txt");
        new File("sub/subfile.txt", service).moveTo(new TargetFileData("newsub/moved-subfile.txt", "false"));
    }

    @Test
    public void testMove_folder() {
        new File("sub", service).moveTo(new TargetFileData("sub2", "false"));
        assertAbsence("sub");
        assertPresence("sub2");
    }

    @Test(expected = AutomationException.class)
    public void testMove_noTargetPath() {
        new File("x", service).moveTo(new TargetFileData("", "true"));
    }

    // copy tests --------------------------------------------------------------

    @Test
    public void testCopy_file_noOverwrite_noPreviousFile() {
        new File("sub/subfile.txt", service).copyTo(new TargetFileData("newsub/moved-subfile.txt", "false"));
        assertPresence("sub/subfile.txt");
        assertPresence("newsub/moved-subfile.txt");
    }

    @Test
    public void testCopy_file_overwrite_noPreviousFile() {
        new File("sub/subfile.txt", service).copyTo(new TargetFileData("newsub/moved-subfile.txt", "true"));
        assertPresence("sub/subfile.txt");
        assertPresence("newsub/moved-subfile.txt");
    }

    @Test
    public void testCopy_file_overwrite_previousFile() throws Exception {
        haveFile("newsub/moved-subfile.txt");
        new File("sub/subfile.txt", service).copyTo(new TargetFileData("newsub/moved-subfile.txt", "true"));
        assertPresence("sub/subfile.txt");
        assertPresence("newsub/moved-subfile.txt");
    }

    @Test(expected = FunctionalFailure.class)
    public void testCopy_file_noOverwrite_previousFile() throws Exception {
        haveFile("newsub/moved-subfile.txt");
        new File("sub/subfile.txt", service).copyTo(new TargetFileData("newsub/moved-subfile.txt", "false"));
    }

    @Test
    public void testCopy_folder() {
        new File("sub", service).copyTo(new TargetFileData("sub2", "false"));
        assertPresence("sub");
        assertPresence("sub2");
    }

    @Test(expected = AutomationException.class)
    public void testCopy_noTargetPath() {
        new File("sub/subfile.txt", service).copyTo(new TargetFileData("", "true"));
    }

    // testing waitUntilExists() -----------------------------------------------

    @Test
    public void testWaitUntilExists_preexisting() {
        File file = new File("file.txt", service);
        file.waitUntilExists();
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
        File file = new File("no-file.txt", service);
        file.waitUntilExists();
    }

    @Test(expected = AutomationException.class)
    public void testWaitUntilExists_noPath() {
        File file = new File("", service);
        file.waitUntilExists();
    }

    // testing waitUntilNotExists() --------------------------------------------

    @Test
    public void testWaitUntilNotExists_nonexisting() {
        File file = new File("__nonexisting.file__", service);
        file.waitUntilNotExists();
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
        File file = new File("file.txt", service);
        file.waitUntilNotExists();
    }

    @Test(expected = AutomationException.class)
    public void testWaitUntilNotExists_noPath() {
        File file = new File("", service);
        file.waitUntilNotExists();
    }

    // existence verification tests --------------------------------------------

    @Test
    public void testAssertPresence_success() {
        new File("file.txt", service);
        new File("sub/subfile.txt", service);
    }

    @Test(expected = AutomationException.class)
    public void testAssertPresence_failure() {
        new File("no-file.txt", service).assertPresence();
    }

    @Test(expected = AutomationException.class)
    public void testAssertPresence_noPath() {
        new File("", service).assertPresence();
    }

    @Test
    public void testAssertAbsence_success() {
        new File("no-file.txt", service).assertAbsence();
    }

    @Test(expected = AutomationException.class)
    public void testAssertAbsence_failure() {
        new File("file.txt", service).assertAbsence();
    }

    @Test(expected = AutomationException.class)
    public void testAssertAbsense_noPath() {
        new File("", service).assertAbsence();
    }

    // file type assertions ----------------------------------------------------

    @Test
    public void testAssertFile_positive() {
        new File("file.txt", service).assertFile();
    }

    @Test(expected = FunctionalFailure.class)
    public void testAssertFile_negative() {
        new File("sub", service).assertFile();
    }

    @Test
    public void testAssertDirectory_positive() {
        new File("sub", service).assertDirectory();
    }

    @Test(expected = FunctionalFailure.class)
    public void testAssertDirectory_negative() {
        new File("file.txt", service).assertDirectory();
    }

    // private helper methods --------------------------------------------------

    private void haveFile(String filePath) throws IOException {
        java.io.File file = new java.io.File(ROOT, filePath);
        FileUtil.ensureDirectoryExists(file.getParentFile());
        IOUtil.writeTextFile(file.getAbsolutePath(), "xxx");
    }

    private void assertPresence(String filePath) {
        assertTrue("File not found: " + filePath, new java.io.File(ROOT, filePath).exists());
    }

    private void assertAbsence(String filePath) {
        assertFalse("File is assumed to be absent: " + filePath, new java.io.File(ROOT, filePath).exists());
    }

}
