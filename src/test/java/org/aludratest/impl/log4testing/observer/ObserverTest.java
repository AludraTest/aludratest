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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.aludratest.impl.log4testing.configuration.ConfigurationError;
import org.aludratest.impl.log4testing.data.TestCaseLog;
import org.aludratest.impl.log4testing.data.TestLogger;
import org.aludratest.impl.log4testing.data.TestSuiteLog;
import org.aludratest.impl.log4testing.observer.TestObserver;
import org.aludratest.impl.log4testing.observer.TestObserverManager;
import org.junit.Test;

/**
 * Component test which verifies Log4Testing's overall observer support 
 * from the {@link TestSuiteLog} structure over the {@link TestObserverManager} 
 * to the {@link TestObserver}s themselves.
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public class ObserverTest {

    @Test
    public void test() {
        // GIVEN a simple test suite structure...
        TestSuiteLog rootSuite = TestLogger.getTestSuite("rootSuite");
        TestCaseLog rootCase = TestLogger.getTestCase("rootCase");
        rootSuite.add(rootCase);
        TestSuiteLog childSuite = TestLogger.getTestSuite("childSuite");
        rootSuite.add(childSuite);
        TestCaseLog childCase = TestLogger.getTestCase("childCase");
        childSuite.add(childCase);
        // ...with a ListObserver
        ListObserver observer = new ListObserver();
        TestObserverManager.getInstance().addObserver(observer);

        // WHEN the first test was started
        rootCase.newTestStepGroup("g1");
        rootCase.newTestStep();

        // THEN test process, rooSuite and rootCase must have been observed as started
        assertEquals(3, observer.invocations.size());
        assertEquals("startingTestProcess(rootSuite)", observer.invocations.get(0));
        assertEquals("startingTestSuite(rootSuite)", observer.invocations.get(1));
        assertEquals("startingTestCase(rootCase)", observer.invocations.get(2));

        // WHEN the root test is finished
        rootCase.finish();

        // THEN the rootCase must have been observed as finished
        assertEquals(4, observer.invocations.size());
        assertEquals("finishedTestCase(rootCase)", observer.invocations.get(3));

        // WHEN the test of the child suite was started
        childCase.newTestStepGroup("g1");
        childCase.newTestStep();

        // THEN childSuite and childCase must have been observed as started
        assertEquals(6, observer.invocations.size());
        assertEquals("startingTestSuite(childSuite)", observer.invocations.get(4));
        assertEquals("startingTestCase(childCase)", observer.invocations.get(5));

        // WHEN the child test is finished
        childCase.finish();

        // THEN the complete test suite hierarchy and ten test process must be finished
        assertEquals(10, observer.invocations.size());
        assertEquals("finishedTestCase(childCase)", observer.invocations.get(6));
        assertEquals("finishedTestSuite(childSuite)", observer.invocations.get(7));
        assertEquals("finishedTestSuite(rootSuite)", observer.invocations.get(8));
        assertEquals("finishedTestProcess(rootSuite)", observer.invocations.get(9));
    }

    static class ListObserver implements TestObserver {

        List<String> invocations = new ArrayList<String>();

        public void validate() throws ConfigurationError {
            // nothing to validate here
        }

        public void startingTestProcess(TestSuiteLog rootSuite) {
            this.invocations.add("startingTestProcess(" + rootSuite.getName() + ")");
        }

        public void startingTestSuite(TestSuiteLog suite) {
            this.invocations.add("startingTestSuite(" + suite.getName() + ")");
        }

        public void startingTestCase(TestCaseLog testCase) {
            this.invocations.add("startingTestCase(" + testCase.getName() + ")");
        }

        public void finishedTestCase(TestCaseLog testCase) {
            this.invocations.add("finishedTestCase(" + testCase.getName() + ")");
        }

        public void finishedTestSuite(TestSuiteLog suite) {
            this.invocations.add("finishedTestSuite(" + suite.getName() + ")");
        }

        public void finishedTestProcess(TestSuiteLog rootSuite) {
            this.invocations.add("finishedTestProcess(" + rootSuite.getName() + ")");
        }

    }
}
