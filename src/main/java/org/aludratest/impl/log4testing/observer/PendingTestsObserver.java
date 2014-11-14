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
package org.aludratest.impl.log4testing.observer;

import java.util.concurrent.atomic.AtomicInteger;

import org.aludratest.impl.log4testing.data.TestCaseLog;
import org.aludratest.impl.log4testing.data.TestSuiteLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logs how many test cases are pending in the test process.
 * @author Volker Bergmann
 */
public class PendingTestsObserver extends AbstractTestObserver {

    private static final Logger LOGGER = LoggerFactory.getLogger(PendingTestsObserver.class);

    private AtomicInteger pendingTests;

    public PendingTestsObserver() {
        LOGGER.debug("Created {}", this);
        this.pendingTests = new AtomicInteger();
    }

    @Override
    public void startingTestCase(TestCaseLog testCase) {
        LOGGER.info("Starting test {}", testCase.getName());
    }

    @Override
    public void startingTestProcess(TestSuiteLog rootSuite) {
        int n = rootSuite.getNumberOfTestCases();
        this.pendingTests.set(n);
        LOGGER.info("Starting root suite {}", rootSuite.getName());
        LOGGER.info("Planned tests: {}", n);
    }

    @Override
    public void finishedTestProcess(TestSuiteLog rootSuite) {
        LOGGER.info("Finished root suite {}", rootSuite.getName());
    }

    @Override
    public void finishedTestCase(TestCaseLog testCase) {
        LOGGER.info("Finished test {}", testCase.getName());
        LOGGER.info("Pending tests: {}", pendingTests.decrementAndGet());
    }

}
