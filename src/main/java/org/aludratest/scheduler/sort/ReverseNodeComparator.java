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

import org.aludratest.scheduler.node.RunnerNode;

/**
 * {@link RunnerNode} {@link Comparator} inverts the order
 * of another (base) {@link Comparator}.
 * @author Volker Bergmann
 */
public abstract class ReverseNodeComparator implements Comparator<RunnerNode>, Serializable {

    private static final long serialVersionUID = 1L;

    /** The base comparator to use and of which to inverse the output. */
    private Comparator<RunnerNode> base;

    /** Constructor requiring the base comparator.
     *  @param base the underlying comparator */
    public ReverseNodeComparator(Comparator<RunnerNode> base) {
        this.base = base;
    }

    /** Compares the given nodes using the base comparator
     *  and inverting its output. */
    @Override
    public int compare(RunnerNode node1, RunnerNode node2) {
        return base.compare(node2, node1);
    }

}
