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
package org.aludratest.util.validator;

import org.aludratest.AludraTest;
import org.junit.After;
import org.junit.Before;

public abstract class AbstractValidatorTest {

    protected static final String NULL_MARKER = "<NULL>";

    protected static final String EMPTY_MARKER = "<EMPTY>";

    protected static final String ALL_MARKER = "<ALL>";

    private AludraTest aludraTest;

    @Before
    public void setUp() {
        System.setProperty("ALUDRATEST_CONFIG/dataConfiguration/NULL", NULL_MARKER);
        System.setProperty("ALUDRATEST_CONFIG/dataConfiguration/EMPTY", EMPTY_MARKER);
        System.setProperty("ALUDRATEST_CONFIG/dataConfiguration/ALL", ALL_MARKER);
        this.aludraTest = AludraTest.startFramework();
    }

    @After
    public void tearDown() {
        aludraTest.stopFramework();
    }

}
