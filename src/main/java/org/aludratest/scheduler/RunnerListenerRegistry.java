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

public interface RunnerListenerRegistry {

    public static final String ROLE = RunnerListenerRegistry.class.getName();

    void addRunnerListener(RunnerListener listener);

    void removeRunnerListener(RunnerListener listener);

    void fireStartingTestProcess(RunnerTree runnerTree);

    void fireStartingTestGroup(RunnerGroup runnerGroup);

    void fireStartingTestLeaf(RunnerLeaf runnerLeaf);

    void fireFinishedTestLeaf(RunnerLeaf runnerLeaf);

    void fireFinishedTestGroup(RunnerGroup runnerGroup);

    void fireFinishedTestProcess(RunnerTree runnerTree);

    void fireNewTestStepGroup(RunnerLeaf leaf, String groupName);

    void fireNewTestStep(RunnerLeaf leaf, TestStepInfo testStep);

}
