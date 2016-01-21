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
 * {@link RunnerNode} {@link Comparator} which evaluates the 
 * alphabetical order of the nodes' path names.
 * @author Volker Bergmann
 */
public class Alphabetic implements Comparator<RunnerNode>, Serializable {

    private static final long serialVersionUID = 1L;

    /** Compares the paths of the given nodes alphabetically. */
    @Override
    public int compare(RunnerNode node1, RunnerNode node2) {
        String thisPath = node1.getName();
        String thatPath = node2.getName();
        if (thisPath == null) {
            return (thatPath == null ? 0 : -1);
        } else if (thatPath == null) {
            return 1;
        } else {
            return thisPath.compareTo(thatPath);
        }
    }

}
