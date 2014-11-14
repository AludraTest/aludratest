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

import static org.junit.Assert.*;

import java.util.List;

import org.aludratest.service.AbstractAludraServiceTest;
import org.aludratest.service.file.FileService;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests the {@link FileService} with a remote file system.
 * @author Volker Bergmann
 */
@Ignore
@SuppressWarnings("javadoc")
public class RemoteFSTest extends AbstractAludraServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteFSTest.class);

    @Test
    public void testGetRootChildren() {
        FileService service = getService(FileService.class, "rfstest");
        List<String> list = service.perform().getChildren(".");
        System.out.println(list);
        assertTrue(list.size() >= 3);
        assertTrue(list.contains("Austausch_wird_am_Monatsende_geloescht"));
    }

    @Test
    public void testGetSubfolderChildren() {
        FileService service = getService(FileService.class, "rfstest");
        List<String> list = service.perform().getChildren("Austausch_wird_am_Monatsende_geloescht");
        assertTrue(list.size() > 0);
    }

    @Test
    public void testReadBinaryFile() {
        System.out.println(System.getProperty("user.dir"));
        FileService service = getService(FileService.class, "rfstest");
        byte[] content = service.perform().readBinaryFile("Austausch_wird_am_Monatsende_geloescht/Thumbs.db");
        assertNotNull(content);
        assertTrue(content.length > 0);
    }

    @Test
    public void testWrite() {
        FileService service = getService(FileService.class, "rfstest");
        String folderPath = "Austausch_wird_am_Monatsende_geloescht/AludraTest";
        if (!service.check().exists(folderPath)) {
            LOGGER.info("Creating folder {}", folderPath);
            service.perform().createDirectory(folderPath);
        } else {
            LOGGER.info("Folder exists: {}", folderPath);
        }
        String filePath = folderPath + "/" + "aludra-out.txt";
        if (service.check().exists(filePath)) {
            LOGGER.info("Deleting file {}", filePath);
            service.perform().delete(filePath);
        }
        LOGGER.info("Writing file {}", filePath);
        service.perform().writeTextFile(filePath, "Created by AludraTest", true);
        service.verify().assertPresence(filePath);
        String content = service.perform().readTextFile(filePath);
        assertEquals("Created by AludraTest", content);
    }

}
