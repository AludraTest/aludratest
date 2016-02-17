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
package org.aludratest.impl.log4testing;

import org.aludratest.log4testing.TestCaseLog;
import org.aludratest.log4testing.TestSuiteLog;
import org.aludratest.log4testing.engine.AbstractTestFramework;

/** Very straightforward TestFramework implementation which allows the Log4TestingRunnerListener to fire the events directly when
 * necessary.
 * 
 * @author falbrech */
class Log4TestingAludraTestFramework extends AbstractTestFramework {

    private boolean hooked;

    @Override
    protected void hook() {
        hooked = true;
    }

    @Override
    protected void unhook() {
        hooked = false;
    }

    @Override
    public void fireStartingTestProcess(TestSuiteLog rootSuite) {
        if (hooked) {
            super.fireStartingTestProcess(rootSuite);
        }
    }

    @Override
    public void fireStartingTestSuite(TestSuiteLog suite) {
        if (hooked) {
            super.fireStartingTestSuite(suite);
        }
    }

    @Override
    public void fireStartingTestCase(TestCaseLog testCase) {
        if (hooked) {
            super.fireStartingTestCase(testCase);
        }
    }

    @Override
    public void fireFinishedTestCase(TestCaseLog testCase) {
        if (hooked) {
            super.fireFinishedTestCase(testCase);
        }
    }

    @Override
    public void fireFinishedTestSuite(TestSuiteLog suite) {
        if (hooked) {
            super.fireFinishedTestSuite(suite);
        }
    }

    @Override
    protected void fireFinishedTestProcess(TestSuiteLog rootSuite) {
        if (hooked) {
            super.fireFinishedTestProcess(rootSuite);
        }
    }

}
