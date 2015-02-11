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
package org.aludratest.scheduler.sort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.aludratest.scheduler.RunnerTree;
import org.aludratest.scheduler.node.RunnerGroup;
import org.aludratest.scheduler.node.RunnerNode;

/** Helper class for sorting Runner Trees with the help of a Comparator.
 * 
 * @author falbrech */
public final class RunnerTreeSorter {

    private RunnerTreeSorter() {
    }

    /** Sorts the given RunnerTree's nodes according to the given comparator. Starting on the lowest levels, every group's children
     * list containing more than one child node is sorted according the comparator.
     * 
     * @param tree Tree to sort.
     * @param comparator Comparator to use for sorting. */
    public static void sortTree(RunnerTree tree, Comparator<RunnerNode> comparator) {
        sortTree(tree.getRoot(), comparator);
    }

    private static void sortTree(RunnerNode node, Comparator<RunnerNode> comparator) {
        if (!(node instanceof RunnerGroup)) {
            return;
        }

        RunnerGroup group = (RunnerGroup) node;

        // sort children first, then this node
        for (RunnerNode child : group.getChildren()) {
            sortTree(child, comparator);
        }

        List<RunnerNode> nodes = new ArrayList<RunnerNode>(group.getChildren());
        if (!nodes.isEmpty()) {
            Collections.sort(nodes, comparator);
            group.reorderChildren(nodes);
        }
    }

}
