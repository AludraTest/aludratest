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
    public void testVerify_identical() throws Exception {
        EdifactFileVerifier verifier = new EdifactFileVerifier("IFTDGN_1.edi", service) {
        };
        verifier.verifyWith(new StringData("IFTDGN_1.edi"));
        assertEquals(TestStatus.PASSED, testCase.getLastTestStep().getStatus());
    }

    @Test
    public void testVerify_different_negative() throws Exception {
        EdifactFileVerifier verifier = new EdifactFileVerifier("IFTDGN_1.edi", service) {
        };
        verifier.verifyWith(new StringData("IFTDGN_2.edi"));
        assertEquals(TestStatus.FAILED, testCase.getLastTestStep().getStatus());
    }

    // ENHANCE write positive test
}
