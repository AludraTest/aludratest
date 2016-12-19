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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aludratest.scheduler.RunStatus;

/**
 * {@link RunnerNode} implementation which forms a tree node that can have
 * sub nodes which may be executed sequentially or concurrently.
 * @author Volker Bergmann
 */
public class RunnerGroup extends RunnerNode {

    /** The child nodes. */
    private final List<RunnerNode> children;

    /** Flag indicating if the child nodes may be executed concurrently. */
    private ExecutionMode mode;

    /** Constructor
     * @param path The path of the node.
     * @param mode See {@link #mode}.
     * @param parent See {@link #parent}. */
    public RunnerGroup(String path, ExecutionMode mode, RunnerGroup parent) {
        super(path, parent);
        this.mode = mode;
        this.children = new ArrayList<RunnerNode>();
    }

    /** Tells if the child nodes may be executed concurrently. */
    public boolean isParallel() {
        if (mode == ExecutionMode.PARALLEL) {
            return true;
        } else if (mode == ExecutionMode.SEQUENTIAL) {
            return false;
        }
        else if (parent == null) { // NOSONAR
            return true;
        } else {
            return parent.isParallel();
        }
    }

    /** @return true if the group has no child elements, otherwise false */
    public boolean isEmpty() {
        return this.children.isEmpty();
    }

    /** Returns the children of this runner group.
     * @return The children of this runner group, as an unmodifiable list. */
    public List<RunnerNode> getChildren() {
        return Collections.unmodifiableList(children);
    }

    /** Add a child node to the {@link #children}. */
    public void addChild(RunnerNode childNode) {
        children.add(childNode);
    }

    /** Reorders the children of this group, so they are in the order of the passed list.
     * 
     * @param children List which must contain all children of this group, but in the desired new order. */
    public void reorderChildren(List<RunnerNode> children) {
        List<RunnerNode> checks = new ArrayList<RunnerNode>(this.children);
        checks.removeAll(children);
        if (!checks.isEmpty()) {
            throw new IllegalArgumentException("Passed list does not contain all children of this group");
        }
        checks.addAll(children);
        checks.removeAll(this.children);
        if (!checks.isEmpty()) {
            throw new IllegalArgumentException("Passed list contains more than the children of this group");
        }
        this.children.clear();
        this.children.addAll(children);
    }

    @Override
    public RunStatus getRunStatus() {
        List<RunnerNode> checkChildren;
        synchronized (children) {
            if (children.isEmpty()) {
                return RunStatus.EMPTY;
            }
            checkChildren = new ArrayList<RunnerNode>(children);
        }
        Set<RunStatus> allStates = new HashSet<RunStatus>();
        for (RunnerNode child : checkChildren) {
            allStates.add(child.getRunStatus());
        }

        // ignore empty ones for status calculation
        if (allStates.size() > 1) {
            allStates.remove(RunStatus.EMPTY);
        }

        if (allStates.size() == 1) {
            return allStates.iterator().next();
        }

        // now, it contains RUNNING, or it's FINISHED + WAITING, so RUNNING...
        return RunStatus.RUNNING;
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    /** Creates a string representation of this group. */
    @Override
    public String toString() {
        return (isParallel() ? "parallel" : "serial") + " group: " + (name.length() == 0 ? "<root>" : name);
    }

}
