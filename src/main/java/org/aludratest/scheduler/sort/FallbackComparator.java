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
import java.util.List;

import org.aludratest.scheduler.node.RunnerNode;

/**
 * Multi-Step {@link RunnerNode} {@link Comparator} which
 * uses a list of RunnerNode Comparators.
 * This allows a fallback-style for ordering test for execution:
 * Any two tests are first compared by the first comparator
 * in the list. If the comparator signals 'greater' (positive
 * value) or less (negative value), that result is used for
 * ordering. For any zero results, the next comparator in the
 * list is used until one returns a non-zero value or the
 * last comparator has been queried. The default setting uses
 * the {@link NoComparator} which is just a place holder for
 * signaling that no deterministic ordering shall be applied.
 * @author Volker Bergmann
 */
public class FallbackComparator implements Comparator<RunnerNode>, Serializable {

    private static final long serialVersionUID = 1L;

    /** The list of fallback-comparators. */
    private List<Comparator<RunnerNode>> steps;

    /** Constructor requiring the list of fallback-comparators to use.
     * @param steps the comparison steps to apply */
    public FallbackComparator(List<Comparator<RunnerNode>> steps) {
        this.steps = steps;
    }

    /** Returns the list of fallback-comparators.
     *  @return the {@link #steps} */
    public List<Comparator<RunnerNode>> getSteps() {
        return steps;
    }

    /** Compares two {@link RunnerNode}s as described in the class Javadoc. */
    @Override
    public int compare(RunnerNode node1, RunnerNode node2) {
        int result = 0;
        for (Comparator<RunnerNode> step : steps) {
            result = step.compare(node1, node2);
            if (result != 0) {
                return result;
            }
        }
        return result;
    }

}
