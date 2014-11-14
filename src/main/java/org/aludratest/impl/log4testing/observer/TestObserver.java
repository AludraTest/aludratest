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

import org.aludratest.impl.log4testing.configuration.Validatable;
import org.aludratest.impl.log4testing.data.TestCaseLog;
import org.aludratest.impl.log4testing.data.TestSuiteLog;

/** 
 * Interface for classes that want to be notified of testing progress.
 * @author Volker Bergmann
 */
public interface TestObserver extends Validatable {
    void startingTestProcess(TestSuiteLog rootSuite);

    void startingTestSuite(TestSuiteLog suite);

    void startingTestCase(TestCaseLog testCase);

    void finishedTestCase(TestCaseLog testCase);

    void finishedTestSuite(TestSuiteLog suite);

    void finishedTestProcess(TestSuiteLog rootSuite);
}
