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

import org.aludratest.invoker.TestInvoker;
import org.aludratest.scheduler.node.ExecutionMode;
import org.aludratest.scheduler.node.RunnerGroup;
import org.aludratest.scheduler.node.RunnerLeaf;
import org.aludratest.scheduler.node.RunnerNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** <p>
 * Assembles nodes in a tree structure and provides the feature of executing each node's sub nodes either concurrently or
 * sequentially. Each leaf node is executed by a thread of the parallel executor service, so its size limits the number of
 * concurrent leaf executions.
 * </p>
 * <p>
 * Leaf nodes are formed by objects which implement the {@link Runnable} interface.
 * </p>
 * @see RunnerNode
 * @author Volker Bergmann */
public class RunnerTree {

    /** The {@link Logger} of the class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(RunnerTree.class);

    /** The root of the runner tree structure. */
    private RunnerGroup root;

    /**
     * @return the {@link #root} attribute
     */
    public RunnerGroup getRoot() {
        return root;
    }

    /** Creates the tree root.
     *  @param rootName the name of the root node
     *  @param parallel default {@link ExecutionMode} for the root node
     *  @return A {@link RunnerGroup} that has been set as new {@link #root}. */
    public RunnerGroup createRoot(String rootName, boolean parallel) {
        if (root == null) {
            createGroup(rootName, (parallel ? ExecutionMode.PARALLEL : ExecutionMode.SEQUENTIAL), null);
        } else {
            throw new IllegalStateException("Root has already been defined");
        }
        return root;
    }

    /**
     * Creates a {@link RunnerGroup} and adds it as new child to a parent.
     * @param groupName the name of the group to create
     * @param mode the {@link ExecutionMode} of the new group
     * @param parentGroup the parent group
     * @return a {@link RunnerGroup} representing a tree node.
     * 		Missing parent nodes are created on demand.
     */
    public RunnerGroup createGroup(String groupName, ExecutionMode mode, RunnerGroup parentGroup) {
        RunnerGroup group;
        group = new RunnerGroup(groupName, mode, parentGroup);
        if (parentGroup != null) {
            parentGroup.addChild(group);
        } else {
            this.root = group;
        }
        return group;
    }

    /** Adds a {@link TestInvoker} object as leaf to the tree structure.
     * @param id Unique ID for the leaf.
     * @param testInvoker The Test Invoker to execute
     * @param nodeName the name of the node to add
     * @param parentGroup the parent group to which to add the new node
     * @return The newly created and added Runner Leaf. */
    public RunnerLeaf addLeaf(int id, TestInvoker testInvoker, String nodeName, RunnerGroup parentGroup) {
        LOGGER.debug("Adding leaf {} to group {}", new Object[] { testInvoker, parentGroup });
        if (parentGroup == null) {
            throw new IllegalArgumentException("parentGroup is null");
        }
        //        TestCaseLog logCase = TestLogger.getTestCase(nodeName);
        RunnerLeaf leaf = new RunnerLeaf(id, nodeName, parentGroup, testInvoker);
        parentGroup.addChild(leaf);
        return leaf;
    }

    /** Creates a string representation of the object. */
    @Override
    public String toString() {
        return getClass().getSimpleName() + ": " + (root == null ? "(empty)" : root.getName());
    }

}
