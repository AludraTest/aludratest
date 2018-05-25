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
package org.aludratest.config;

import static org.junit.Assert.assertNull;

import org.aludratest.service.AbstractAludraIntegrationTest;
import org.aludratest.service.ComponentId;
import org.aludratest.service.file.FileService;
import org.aludratest.testcase.AludraTestCase;
import org.junit.Test;

/** @author vdorai */
public class LoggingDisabledTest extends AbstractAludraIntegrationTest {


    /** logging disabled test */
    @Test
    public void testLoggingDisabled() {
        config.setLoggingDisabled(Boolean.TRUE);
        aludra.run(SimpleTest.class);

        // no log should have been written to memory at all
        assertNull(getTestLog().getRootSuite());
    }

    /** @author vdorai */
    public static class SimpleTest extends AludraTestCase {

        /** simple test */
        @org.aludratest.testcase.Test
        public void testSimple() {
            FileService svc = getService(ComponentId.create(FileService.class));
            svc.verify().assertAbsence("ifduisdhfirfre.txt");
        }

    }
}
