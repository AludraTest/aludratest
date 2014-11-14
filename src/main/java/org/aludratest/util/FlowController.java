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
package org.aludratest.util;

import java.util.HashSet;
import java.util.Set;

import org.aludratest.impl.log4testing.data.TestCaseLog;

/**
 * Tracks stopped test cases.
 * @author Volker Bergmann
 */
public class FlowController {

    /** Singleton instance attribute */
    private static final FlowController INSTANCE = new FlowController();

    /** Accessor method for the singleton instance 
     *  @return the singleton instance */
    public static FlowController getInstance() {
        return INSTANCE;
    }

    /** {@link Set} which stores the stopped test cases */
    private Set<TestCaseLog> stoppedTestCases;

    /** Default constructor. */
    private FlowController() {
        stoppedTestCases = new HashSet<TestCaseLog>();
    }

    /** Tells if the given test case was stopped. 
     *  @param testCase 
     *  @return true if test case execution was stopped for the test, otherwise false */
    public boolean isStopped(TestCaseLog testCase) {
        return stoppedTestCases.contains(testCase);
    }

    /** Stores the information that the given test case is stopped. 
     *  @param testCase the test case for which to stop test case execution */
    public void stopTestCaseExecution(TestCaseLog testCase) {
        stoppedTestCases.add(testCase);
    }

    /** Clears the list of stopped test cases, 
     *  making each one executable again. */
    public void reset() {
        this.stoppedTestCases.clear();
    }

}
