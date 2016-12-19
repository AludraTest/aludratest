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

/** Abstract base implementation of the RunnerListener interface, with all methods implemented empty. Use this as the base for your
 * RunnerListener implementation if you only want to override some methods.
 * 
 * @author falbrech */
public abstract class AbstractRunnerListener implements RunnerListener {

    @Override
    public void startingTestProcess(RunnerTree runnerTree) { // NOSONAR: empty default implementation
    }

    @Override
    public void startingTestGroup(RunnerGroup runnerGroup) { // NOSONAR: empty default implementation
    }

    @Override
    public void startingTestLeaf(RunnerLeaf runnerLeaf) { // NOSONAR: empty default implementation
    }

    @Override
    public void finishedTestLeaf(RunnerLeaf runnerLeaf) { // NOSONAR: empty default implementation
    }

    @Override
    public void finishedTestGroup(RunnerGroup runnerGroup) { // NOSONAR: empty default implementation
    }

    @Override
    public void finishedTestProcess(RunnerTree runnerTree) { // NOSONAR: empty default implementation
    }

    @Override
    public void newTestStepGroup(RunnerLeaf runnerLeaf, String groupName) { // NOSONAR: empty default implementation
    }

    @Override
    public void newTestStep(RunnerLeaf runnerLeaf, TestStepInfo testStepInfo) { // NOSONAR: empty default implementation
    }

}
