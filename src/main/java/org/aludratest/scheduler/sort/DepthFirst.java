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

import java.io.Serializable;
import java.util.Comparator;

import org.aludratest.scheduler.node.RunnerGroup;
import org.aludratest.scheduler.node.RunnerNode;

/**
 * {@link RunnerNode} {@link Comparator} which puts test groups 
 * to the head of the list.
 * @author Volker Bergmann
 */
public class DepthFirst implements Comparator<RunnerNode>, Serializable {

    private static final long serialVersionUID = 1L;

    /** Compares two {@link RunnerNode}, 
     *  judging group nodes as 'less' than leaf ones. 
     *  Comparison of group nodes with each other yields 'equal' 
     *  as well as comparison of leaf nodes with each other does. */
    public int compare(RunnerNode node1, RunnerNode node2) {
        boolean isGroup1 = (node1 instanceof RunnerGroup);
        boolean isGroup2 = (node2 instanceof RunnerGroup);
        if (isGroup1) {
            return (isGroup2 ? 0 : -1);
        } else {
            return (isGroup2 ? 1 : 0);
        }
    }

}
