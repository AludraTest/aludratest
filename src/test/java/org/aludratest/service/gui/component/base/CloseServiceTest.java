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
package org.aludratest.service.gui.component.base;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.aludratest.exception.AutomationException;
import org.junit.Test;

/** Closes and reopens a service to ensure closeService works as expected.
 * 
 * @author falbrech */
public class CloseServiceTest extends GUITest {

    @Test
    public void closeAndOpenTest() throws Exception {
        tearDown();
        assertNull(tCase.getLastFailed());
        initializeAludra();
        assertNull(tCase.getLastFailed());
    }

    @Test
    public void doubleCloseTest() throws Exception {
        tearDown();
        tearDown();
        assertNotNull(tCase.getLastFailed());
        assertTrue(tCase.getLastFailed().getError() instanceof AutomationException);
    }

}
