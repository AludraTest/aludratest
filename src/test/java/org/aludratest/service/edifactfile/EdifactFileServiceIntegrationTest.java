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
package org.aludratest.service.edifactfile;

import static org.junit.Assert.assertEquals;

import org.aludratest.service.AbstractAludraServiceTest;
import org.aludratest.service.file.FileFilter;
import org.aludratest.service.file.filter.RegexFilePathFilter;
import org.aludratest.testcase.TestStatus;
import org.aludratest.util.data.StringData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the {@link EdifactFileService}.
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public class EdifactFileServiceIntegrationTest extends AbstractAludraServiceTest {

    private EdifactFileService service;

    @Before
    public void prepare() {
        this.service = getLoggingService(EdifactFileService.class, "localedi");
    }

    @After
    public void tearDown() {
        if (this.service != null) {
            this.service.close();
        }
    }

    @Test
    public void testWaitForFirstMatch() throws Exception {
        MyEdifactFileVerifier verifier = new MyEdifactFileVerifier(null, service);
        FileFilter filter = new RegexFilePathFilter(".*IFTDGN_1\\.edi");
        verifier.waitForFirstMatch("/", filter);
        assertEquals("IFTDGN_1.edi", verifier.filePath);
        assertEquals(TestStatus.PASSED, getLoggedStatus());
        verifier.verifyWith(new StringData("target/test-classes/ediTest/IFTDGN_1.edi"));
    }

    @Test
    public void testVerify_identical() throws Exception {
        MyEdifactFileVerifier verifier = new MyEdifactFileVerifier("IFTDGN_1.edi", service);
        verifier.verifyWith(new StringData("target/test-classes/ediTest/IFTDGN_1.edi"));
        assertEquals(TestStatus.PASSED, getLoggedStatus());
    }

    @Test
    public void testVerify_different_negative() throws Exception {
        MyEdifactFileVerifier verifier = new MyEdifactFileVerifier("IFTDGN_1.edi", service);
        verifier.verifyWith(new StringData("target/test-classes/ediTest/IFTDGN_2.edi"));
        assertEquals(TestStatus.FAILED, getLoggedStatus());
    }

    // ENHANCE write positive test

    class MyEdifactFileVerifier extends EdifactFileVerifier<MyEdifactFileVerifier> {

        public MyEdifactFileVerifier(String filePath, EdifactFileService service) {
            super(filePath, service);
        }

    }

}
