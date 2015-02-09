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

import java.io.File;
import java.io.FileNotFoundException;

import org.aludratest.service.AbstractAludraServiceTest;
import org.aludratest.service.file.FileService;
import org.databene.commons.FileUtil;
import org.junit.Before;

/**
 * Abstract parent class for {@link FileService} tests 
 * that access the local file system.
 * @author Volker Bergmann
 */
public abstract class AbstractLocalFileServiceTest extends AbstractAludraServiceTest {

    /** Source path of the files and directories that on which tests are based. */
    protected static final File TEMPLATE = new File("src/test/resources/fileServiceTest");

    /** Temporary path of the files and directories on which the tests are executed. */
    protected static final File ROOT = new File("target/fileServiceTest");

    /**
     * Invoked before each test method invocation, 
     * copies the directory template from src/test/resources to target/ 
     * @throws Exception
     */
    @Before
    public void prepareFiles() throws Exception {
        // expect a "FileNotFoundException" when access is denied because tests
        // are executed too fast
        // sleep a while, and try again
        try {
            FileUtil.deleteDirectoryIfExists(ROOT);
            FileUtil.copy(TEMPLATE, ROOT, true, null);
        }
        catch (FileNotFoundException e) {
            try {
                Thread.sleep(200);
            }
            catch (InterruptedException ie) {
                // ignore
            }
            FileUtil.deleteDirectoryIfExists(ROOT);
            FileUtil.copy(TEMPLATE, ROOT, true, null);
        }
    }

}
