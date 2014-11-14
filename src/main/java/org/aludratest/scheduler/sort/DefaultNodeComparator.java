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
 * Default {@link Comparator} for {@link RunnerNode}s which delegates 
 * to the {@link Alphabetic} comparator.
 * @author Volker Bergmann
 */
public class DefaultNodeComparator implements Comparator<RunnerNode>, Serializable {

    private static final long serialVersionUID = 1L;

    /** The {@link Alphabetic} comparator to delegate evaluation to. */
    private Alphabetic realComparator = new Alphabetic();

    /** Calls the equally named method of the {@link #realComparator} 
     *  and forwards its result. */
    public int compare(RunnerNode node1, RunnerNode node2) {
        return realComparator.compare(node1, node2);
    }

}
