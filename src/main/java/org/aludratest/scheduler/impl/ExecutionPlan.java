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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aludratest.scheduler.RunStatus;
import org.aludratest.scheduler.RunnerTree;
import org.aludratest.scheduler.node.RunnerGroup;
import org.aludratest.scheduler.node.RunnerLeaf;
import org.aludratest.scheduler.node.RunnerNode;
import org.aludratest.scheduler.util.CommonRunnerLeafAttributes;

/** Class to calculate an execution plan based on a {@link RunnerTree} and its internal execution dependencies (sequential nodes,
 * or <code>@SequentialGroup</code> annotations). It collects all test cases contained in the tree and calculates the execution
 * dependencies for each. Afterwards, the test cases are ordered by the number of dependent test cases, putting the test cases
 * with most dependent test cases first. This gives the best chance that test cases are executed as early as possible, thus
 * reducing the "Sequential Overhead", the effect that in the end, only one Thread is busy working through a long list of
 * sequential test cases.
 * 
 * @author falbrech */
public class ExecutionPlan {

    private Map<String, List<RunnerNode>> sequentialGroups = new HashMap<String, List<RunnerNode>>();

    private List<ExecutionPlanEntry> entries = new ArrayList<ExecutionPlanEntry>();

    /** Populates this execution plan with the test cases contained in the given RunnerTree structure.
     * 
     * @param tree RunnerTree, as created by a <code>RunnerTreeBuilder</code> component. */
    public void buildExecutionPlan(RunnerTree tree) {
        // find sequential groups first
        findSequentialGroups(tree.getRoot(), sequentialGroups);

        // collect all nodes; for each node, determine dependencies
        collectEntries(tree.getRoot());
        calculateDependencies();
    }

    /** Determines whether there are any more items available in this execution plan, i.e. if a call to
     * {@link #getNextExecutableLeaf()} will at some point in the future return a non-null value.
     * 
     * @return <code>true</code> if you cannot expect this execution plan to provide any more RunnerLeafs, <code>false</code>
     *         otherwise. */
    public boolean isEmpty() {
        synchronized (entries) {
            return entries.isEmpty();
        }
    }

    /** Returns the next RunnerLeaf in this execution plan which is ready for execution, i.e. all of its prerequisites are
     * fulfilled. This may return <code>null</code> if there is <i>currently</i> no such RunnerLeaf, but this could change as soon
     * as a RunnerLeaf is finished which is a prerequisite for another leaf. <br>
     * If {@link #isEmpty()} returns <code>true</code>, this will always return <code>null</code>.
     * 
     * @return The next RunnerLeaf in this execution plan which is ready for execution, or <code>null</code> if there currently is
     *         no such element. */
    public RunnerLeaf getNextExecutableLeaf() {
        // get next runner leaf with all dependencies finished
        synchronized (entries) {
            for (ExecutionPlanEntry entry : entries) {
                if (!entry.started && entry.areDependenciesFinished()) {
                    entry.started = true;
                    return entry.leaf;
                }
            }
        }

        return null;
    }

    /** Removes the entry for a finished RunnerLeaf from this execution plan. This should typically be called as soon as a
     * RunnerLeaf's execution is complete.
     * 
     * @param leaf Leaf for which to remove the entry from this execution plan. */
    public void removeFinishedRunnerLeaf(RunnerLeaf leaf) {
        synchronized (entries) {
            entries.remove(findEntry(leaf));
        }
    }

    private void collectEntries(RunnerGroup group) {
        for (RunnerNode node : group.getChildren()) {
            if (node instanceof RunnerLeaf) {
                entries.add(new ExecutionPlanEntry((RunnerLeaf) node));
            }
            else {
                collectEntries((RunnerGroup) node);
            }
        }
    }

    private void calculateDependencies() {
        for (ExecutionPlanEntry entry : entries) {
            RunnerLeaf leaf = entry.leaf;
            entry.addDependencies(findPreconditions(leaf));
        }
    }

    private void findSequentialGroups(RunnerNode node, Map<String, List<RunnerNode>> buildList) {
        String groupName = (String) node.getAttribute(CommonRunnerLeafAttributes.SEQUENTIAL_GROUP_NAME);
        if (groupName != null) {
            List<RunnerNode> ls = buildList.get(groupName);
            if (ls == null) {
                ls = new ArrayList<RunnerNode>();
                buildList.put(groupName, ls);
            }
            ls.add(node);
            Collections.sort(ls, new Comparator<RunnerNode>() {
                @Override
                public int compare(RunnerNode n1, RunnerNode n2) {
                    Integer i1 = (Integer) n1.getAttribute(CommonRunnerLeafAttributes.SEQUENTIAL_GROUP_INDEX);
                    Integer i2 = (Integer) n2.getAttribute(CommonRunnerLeafAttributes.SEQUENTIAL_GROUP_INDEX);

                    return (i1 == null ? -1 : i1.intValue()) - (i2 == null ? -1 : i2.intValue());
                }
            });
        }

        if (node instanceof RunnerGroup) {
            for (RunnerNode n : ((RunnerGroup) node).getChildren()) {
                findSequentialGroups(n, buildList);
            }
        }
    }

    private List<RunnerNode> findPreconditions(RunnerNode node) {
        List<RunnerNode> result = new ArrayList<RunnerNode>();

        if (node.getAttribute(CommonRunnerLeafAttributes.SEQUENTIAL_GROUP_NAME) != null) {
            String groupName = (String) node.getAttribute(CommonRunnerLeafAttributes.SEQUENTIAL_GROUP_NAME);
            List<RunnerNode> ls = sequentialGroups.get(groupName);
            int index = ls.indexOf(node);
            RunnerNode precNode = findPrecedingNonEmptyNode(ls, index);
            if (precNode != null) {
                result.add(precNode);
            }
        }

        RunnerGroup parent = node.getParent();
        if (parent != null) {
            if (parent.isParallel()) {
                result.addAll(findPreconditions(parent));
            }
            else {
                List<RunnerNode> nodes = parent.getChildren();
                int index = nodes.indexOf(node);

                // finishing the previous (non-empty) sibling is our precondition
                // if we are first, null will be returned
                RunnerNode precNode = findPrecedingNonEmptyNode(nodes, index);
                if (precNode == null) {
                    result.addAll(findPreconditions(parent));
                }
                else {
                    result.add(precNode);
                }
            }
        }

        return result;
    }

    private RunnerNode findPrecedingNonEmptyNode(List<? extends RunnerNode> list, int index) {
        for (int i = index - 1; i >= 0; i--) {
            RunnerNode node = list.get(i);
            if (node instanceof RunnerLeaf) {
                return node;
            }
            else if (containsLeafs((RunnerGroup) node)) {
                return node;
            }
        }

        return null;
    }

    private boolean containsLeafs(RunnerGroup group) {
        for (RunnerNode node : group.getChildren()) {
            if (node instanceof RunnerLeaf) {
                return true;
            }
            if (containsLeafs((RunnerGroup) node)) {
                return true;
            }
        }
        return false;
    }

    private ExecutionPlanEntry findEntry(RunnerLeaf leaf) {
        for (ExecutionPlanEntry entry : entries) {
            if (entry.leaf == leaf) {
                return entry;
            }
        }

        return null;
    }

    private static class ExecutionPlanEntry {

        private RunnerLeaf leaf;

        private List<RunnerNode> dependencies;

        private boolean started;

        private ExecutionPlanEntry(RunnerLeaf leaf) {
            this.leaf = leaf;
        }

        private void addDependency(RunnerNode dependency) {
            if (dependency != null) {
                if (dependencies == null) {
                    dependencies = new ArrayList<RunnerNode>();
                }
                dependencies.add(dependency);
            }
        }

        private void addDependencies(List<RunnerNode> dependencies) {
            if (dependencies != null) {
                for (RunnerNode node : dependencies) {
                    addDependency(node);
                }
            }
        }

        private boolean areDependenciesFinished() {
            if (dependencies == null) {
                return true;
            }

            for (RunnerNode node : dependencies) {
                if (node.getRunStatus() != RunStatus.FINISHED) {
                    return false;
                }
            }

            return true;
        }
    }

}
