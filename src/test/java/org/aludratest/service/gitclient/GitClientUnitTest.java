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
package org.aludratest.service.gitclient;

import static org.junit.Assert.assertEquals;

import org.aludratest.service.gitclient.GitClient;
import org.junit.Test;

/** Tests the {@link GitClient}.
 * @author Volker Bergmann */
@SuppressWarnings("javadoc")
public class GitClientUnitTest {

    @Test
    public void testExtractVersionNumber() {
        String versionNumber = GitClient.extractVersionNumber("git version 1.7.9.6 (Apple Git-31.1)");
        assertEquals("1.7.9.6", versionNumber);
    }

}
