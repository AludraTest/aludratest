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

import java.util.ArrayList;
import java.util.List;

import org.aludratest.impl.log4testing.data.TestCaseLog;
import org.aludratest.impl.log4testing.data.TestSuiteLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Singleton class that manages {@link TestObserver}s.
 * 
 * @author Volker Bergmann
 */
public class TestObserverManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestObserverManager.class);

    private static final TestObserverManager INSTANCE = new TestObserverManager();

    public static TestObserverManager getInstance() {
        return INSTANCE;
    }

    private List<TestObserver> observers;

    private TestObserverManager() {
        this.observers = new ArrayList<TestObserver>();
    }

    public void addObserver(TestObserver observer) {
        this.observers.add(observer);
    }

    public void removeObserver(TestObserver observer) {
        this.observers.remove(observer);
    }

    void removeAllObservers() {
        this.observers.clear();
    }

    public void notifyStartingTestProcess(TestSuiteLog rootSuite) {
        LOGGER.debug("sendig notification that test process started " + "with root suite {}", rootSuite);
        for (int i = observers.size() - 1; i >= 0; i--) {
            TestObserver observer = observers.get(i);
            try {
                observer.startingTestProcess(rootSuite);
            }
            catch (Exception e) { // NOSONAR
                LOGGER.error("Error calling notifyStartingTestProcess() on " + observer, e);
            }
        }
    }

    public void notifyStartingTestSuite(TestSuiteLog suite) {
        LOGGER.debug("sendig notification that test suite started: {}", suite);
        for (int i = observers.size() - 1; i >= 0; i--) {
            TestObserver observer = observers.get(i);
            try {
                observer.startingTestSuite(suite);
            }
            catch (Exception e) { // NOSONAR
                LOGGER.error("Error calling notifyStartingTestSuite() on " + observer, e);
            }
        }
    }

    public void notifyStartingTestCase(TestCaseLog testCase) {
        LOGGER.debug("sendig notification that test case started: {}", testCase);
        for (int i = observers.size() - 1; i >= 0; i--) {
            TestObserver observer = observers.get(i);
            try {
                observer.startingTestCase(testCase);
            }
            catch (Exception e) { // NOSONAR
                LOGGER.error("Error calling notifyStartingTestCase() on " + observer, e);
            }
        }
    }

    public void notifyFinishedTestCase(TestCaseLog testCase) {
        LOGGER.debug("sendig notification that test case finished: {}", testCase);
        for (int i = observers.size() - 1; i >= 0; i--) {
            TestObserver observer = observers.get(i);
            try {
                observer.finishedTestCase(testCase);
            }
            catch (Exception e) { // NOSONAR
                LOGGER.error("Error calling notifyFinishedTestCase() on " + observer, e);
            }
        }
    }

    public void notifyFinishedTestSuite(TestSuiteLog suite) {
        LOGGER.debug("sendig notification that test suite finished: {}", suite);
        for (int i = observers.size() - 1; i >= 0; i--) {
            TestObserver observer = observers.get(i);
            try {
                observer.finishedTestSuite(suite);
            }
            catch (Exception e) { // NOSONAR
                LOGGER.error("Error calling notifyFinishedTestSuite() on " + observer, e);
            }
        }
    }

    public void notifyFinishedTestProcess(TestSuiteLog rootSuite) {
        LOGGER.debug("sendig notification that test process finished " + "with root suite {}", rootSuite);
        for (int i = observers.size() - 1; i >= 0; i--) {
            TestObserver observer = observers.get(i);
            try {
                observer.finishedTestProcess(rootSuite);
            }
            catch (Exception e) { // NOSONAR
                LOGGER.error("Error calling notifyFinishedTestProcess() on " + observer, e);
            }
        }
    }

}
