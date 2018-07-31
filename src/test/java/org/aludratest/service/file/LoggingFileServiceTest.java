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
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.aludratest.testcase.event.attachment.Attachment;
import org.databene.commons.IOUtil;
import org.junit.Test;

/**
 * Instantiates a {@link FileService} instance which performs logging
 * and executes some operations on it.
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public class LoggingFileServiceTest extends AbstractLocalFileServiceTest {

    @Test
    public void test() {
        FileService service = null;
        try {
            service = getLoggingService(FileService.class, "localtest");
            List<String> list = service.perform().getChildren("/");
            assertTrue(list.size() > 0);
            assertTrue(list.contains("file.txt"));
            String content = service.perform().readTextFile("file.txt");
            assertEquals("Simple file", content);
        } finally {
            IOUtil.close(service);
        }
    }

    @Test
    public void testAttachments() throws Exception {
        FileService service = null;
        try {
            service = getLoggingService(FileService.class, "localtest");
            service.perform().writeTextFile("testout.txt", "Some test content", true);
            Attachment att = getLastTestStep().getAttachments().iterator().next();
            assertEquals("Some test content", new String(att.getFileData(), "UTF-8"));
        }
        finally {
            IOUtil.close(service);
        }

    }

}
