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
 * {@link RunnerNode} {@link Comparator} which is meant to 
 * indicate that no sorting should be applied.
 * @author Volker Bergmann
 */
public class NoComparator implements Comparator<RunnerNode>, Serializable {

    private static final long serialVersionUID = 1L;

    /** implementation of {@link Comparable#compareTo(Object)} which 
     *  is not supposed to be called, since this is a marker class. 
     *  If called though, the method raises an 
     *  {@link UnsupportedOperationException}. */
    public int compare(RunnerNode node1, RunnerNode node2) {
        throw new UnsupportedOperationException("The Class is intended to serve as marker and not to be invoked");
    }

}
