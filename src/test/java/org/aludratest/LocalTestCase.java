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

package org.aludratest;

import org.aludratest.testcase.AludraTestCase;
import org.databene.commons.IOUtil;
import org.junit.Before;
import org.junit.internal.AssumptionViolatedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Parent class for test cases that shall only be performed when running on a system with good performance characteristics. This
 * is used to disable tests on a CI environment like Jenkins, where execution times can vary heavily and where no Selenium GUI
 * server is available. Execution is enabled on a specific system by putting a file 'performLocalTests' into the classpath OR by
 * setting a VM variable 'performLocalTests' to true.
 * @author Volker Bergmann */
public abstract class LocalTestCase extends AludraTestCase {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String LOCAL_TESTS_ACTIVATION_FILE = "performLocalTests";

    private static final String LOCAL_TESTS_ACTIVATION_PROPERTY = LOCAL_TESTS_ACTIVATION_FILE;

    private boolean performLocalTests = "true".equals(System.getProperty(LOCAL_TESTS_ACTIVATION_PROPERTY))
            || IOUtil.isURIAvailable(LOCAL_TESTS_ACTIVATION_FILE);

    /** Reads the configuration settings and decides whether to execute the class' tests */
    @Before
    public void assumeLocal() {
        if (!shallPerformLocalTests()) {
            String message = "Skipping local tests of " + getClass();
            logger.info(message);
            throw new AssumptionViolatedException(message);
        }
    }

    /** @return true if local tests shall be executed, otherwise false */
    protected boolean shallPerformLocalTests() {
        return performLocalTests;
    }

}
