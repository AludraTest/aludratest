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

import static org.junit.Assert.assertFalse;

import org.aludratest.config.impl.AludraTestingTestConfigImpl;
import org.aludratest.service.AbstractAludraIntegrationTest;
import org.databene.commons.StringUtil;
import org.junit.Test;

/**
 * Tests the {@link AludraTestConfig} class.
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public class AludraTestConfigTest extends AbstractAludraIntegrationTest {

    @Test
    public void testVersion() {
        assertFalse(StringUtil.isEmpty(AludraTestingTestConfigImpl.getTestInstance().getVersion()));
    }

}
