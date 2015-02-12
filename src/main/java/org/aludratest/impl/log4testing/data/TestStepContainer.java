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
package org.aludratest.impl.log4testing.data;

/** 
 * Represents the parent class of the TestCase, TestStepGroup and TestSuite
 * @author Marcel Malitz
 * @author Volker Bergmann
 * 
 */
public abstract class TestStepContainer extends TestObject {

    private String name = "";

    /** Calls the constructor of the super Class
     *  @param name Name of the TestStepContainer
     */
    public TestStepContainer(String name) {
        setName(name);
    }

    /**
     *  @param name the name to set
     */
    public final void setName(String name) {
        this.name = name;
    }

    /** @return the name of the test step container */
    public String getName() {
        return name;
    }

    /** @return the number of contained test steps */
    public abstract int getNumberOfTestSteps();

    /** Calculates the number of TestSteps
     * @param iterable the container in which the TestSuite/TestCase/TestStepGroups are stored
     * @return number of TestSteps */
    protected final int getNumberOfTestSteps(Iterable<? extends TestStepContainer> iterable) {
        int numberOfTestSteps = 0;
        for (TestStepContainer testStepContainer : iterable) {
            numberOfTestSteps += testStepContainer.getNumberOfTestSteps();
        }
        return numberOfTestSteps;
    }

    /**
     * Checks if the TestStepContainer is failed. That is the case if at least
     * one test step returns true in {@link TestCaseLog#isFailed()}.
     * @return boolean if the TestStepContainer is failed
     */
    @Override
    public boolean isFailed() {
        return getStatus().isFailure();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '[' + getName() + ']';
    }

}
