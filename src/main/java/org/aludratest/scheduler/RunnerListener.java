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
package org.aludratest.scheduler;

import org.aludratest.scheduler.node.RunnerGroup;
import org.aludratest.scheduler.node.RunnerLeaf;
import org.aludratest.testcase.event.TestStepInfo;

/** Interface for listeners wanting to be notified about the progress of a testing run. Please always keep in mind that test cases
 * could be executed in parallel, so the listeners should not cause any locks or keep an object state. <br>
 * <br>
 * To register a RunnerListener, retrieve the <code>RunnerListenerRegistry</code> e.g. from the AludraTest ServiceManager
 * instance, and add it to this registry:
 * 
 * <pre>
 * AludraTest myAludra = AludraTest.startFramework(MyTestSuite.class);
 * RunnerListenerRegistry registry = myAludra.getServiceManager().newImplementorInstance(RunnerListenerRegistry.class);
 * registry.addRunnerListener(myRunnerListener);
 * </pre>
 * 
 * @author falbrech */
public interface RunnerListener {

    /** Called when the overall test process is about to be started, i.e. the first test case is about to run.
     * 
     * @param runnerTree RunnerTree which contains all test cases for execution. */
    void startingTestProcess(RunnerTree runnerTree);

    /** Called when the given test group is about to be started, i.e. the first test case within the group is about to run.
     * 
     * @param runnerGroup RunnerGroup which contains test cases for execution. */
    void startingTestGroup(RunnerGroup runnerGroup);

    /** Called when the given test case is about to be run.
     * 
     * @param runnerLeaf RunnerLeaf which encapsulates the single test case which is about to be run. */
    void startingTestLeaf(RunnerLeaf runnerLeaf);

    /** Calles when the given test case has finished execution. This is also called if some or all steps of the test case failed,
     * or the test case is ignored.
     * 
     * @param runnerLeaf RunnerLeaf which encapsulates the single test case which has finished execution. */
    void finishedTestLeaf(RunnerLeaf runnerLeaf);

    /** Called when all test cases of the given runner group have finished execution.
     * 
     * @param runnerGroup RunnerGroup of which all test cases have finished execution. */
    void finishedTestGroup(RunnerGroup runnerGroup);

    /** Called when all test cases have finished execution.
     * 
     * @param runnerTree RunnerTree which contains all test cases. */
    void finishedTestProcess(RunnerTree runnerTree);

    /** Called when a new test step group within the given test case node starts. Note that there is no matching
     * "endTestStepGroup"; every following test step is intended to be assigned to the currently open test step group for the
     * given test case.
     * 
     * @param runnerLeaf RunnerLeaf encapsulating the test step for which a new test step group shall be opened.
     * @param groupName Name of the group to start. */
    void newTestStepGroup(RunnerLeaf runnerLeaf, String groupName);

    /** Called when a single test step within a test case has been performed. This is the smallest traceable unit within
     * AludraTest. The step should be assigned to the last test step group started for the given RunnerLeaf. If there is no test
     * step group active, it is OK to throw an IllegalStateException.
     * 
     * @param runnerLeaf RunnerLeaf to which to assign the given test step.
     * @param testStepInfo Information about the test step. */
    void newTestStep(RunnerLeaf runnerLeaf, TestStepInfo testStepInfo);

}
