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
package org.aludratest.scheduler.impl;

import java.util.ArrayList;
import java.util.List;

import org.aludratest.scheduler.RunnerListener;
import org.aludratest.scheduler.RunnerListenerRegistry;
import org.aludratest.scheduler.RunnerTree;
import org.aludratest.scheduler.node.RunnerGroup;
import org.aludratest.scheduler.node.RunnerLeaf;
import org.aludratest.testcase.event.TestStepInfo;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

@Component(role = RunnerListenerRegistry.class)
public class RunnerListenerRegistryImpl implements RunnerListenerRegistry {

    @Requirement(role = RunnerListener.class)
    private List<RunnerListener> listeners = new ArrayList<RunnerListener>();

    @Override
    public synchronized void addRunnerListener(RunnerListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    @Override
    public synchronized void removeRunnerListener(RunnerListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void fireStartingTestProcess(RunnerTree runnerTree) {
        for (RunnerListener listener : listeners) {
            listener.startingTestProcess(runnerTree);
        }
    }

    @Override
    public void fireStartingTestGroup(RunnerGroup runnerGroup) {
        for (RunnerListener listener : listeners) {
            listener.startingTestGroup(runnerGroup);
        }
    }

    @Override
    public void fireStartingTestLeaf(RunnerLeaf runnerLeaf) {
        for (RunnerListener listener : listeners) {
            listener.startingTestLeaf(runnerLeaf);
        }
    }

    @Override
    public void fireFinishedTestLeaf(RunnerLeaf runnerLeaf) {
        for (RunnerListener listener : listeners) {
            listener.finishedTestLeaf(runnerLeaf);
        }
    }

    @Override
    public void fireFinishedTestGroup(RunnerGroup runnerGroup) {
        for (RunnerListener listener : listeners) {
            listener.finishedTestGroup(runnerGroup);
        }
    }

    @Override
    public void fireFinishedTestProcess(RunnerTree runnerTree) {
        for (RunnerListener listener : listeners) {
            listener.finishedTestProcess(runnerTree);
        }
    }

    @Override
    public void fireNewTestStepGroup(RunnerLeaf leaf, String groupName) {
        for (RunnerListener listener : listeners) {
            listener.newTestStepGroup(leaf, groupName);
        }
    }

    @Override
    public void fireNewTestStep(RunnerLeaf leaf, TestStepInfo testStep) {
        for (RunnerListener listener : listeners) {
            listener.newTestStep(leaf, testStep);
        }
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return super.toString();
    }
}
