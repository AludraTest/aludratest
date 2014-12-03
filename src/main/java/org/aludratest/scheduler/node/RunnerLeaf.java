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
package org.aludratest.scheduler.node;

import org.aludratest.invoker.TestInvoker;
import org.aludratest.scheduler.RunStatus;

/**
 * {@link RunnerNode} implementation which represents a leaf,
 * meaning an atomic executable test case, represented by an
 * object implementing the {@link Runnable} interface.
 * @author Volker Bergmann
 */
public final class RunnerLeaf extends RunnerNode {

    private final TestInvoker testInvoker;

    private RunStatus runStatus;

    private int id;

    /** Constructor requiring the tree path and the runnable to execute.
     * @param id The ID of the test case, assigned by the tree builder.
     * @param path
     * @param parent
     * @param testInvoker */
    public RunnerLeaf(int id, String path, RunnerGroup parent, TestInvoker testInvoker) {
        super(path, parent);
        this.id = id;
        this.testInvoker = testInvoker;
        this.runStatus = RunStatus.WAITING;
    }

    public TestInvoker getTestInvoker() {
        return testInvoker;
    }

    /** Creates a string representation of the leaf. */
    @Override
    public String toString() {
        return "leaf:[" + testInvoker + "], path:" + name;
    }

    @Override
    public RunStatus getRunStatus() {
        return runStatus;
    }

    public void setRunStatus(RunStatus runStatus) {
        this.runStatus = runStatus;
    }

    public int getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }

        RunnerLeaf leaf = (RunnerLeaf) obj;
        return leaf.id == id;
    }
}
