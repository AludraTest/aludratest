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

import java.util.HashMap;
import java.util.Map;

import org.aludratest.scheduler.RunStatus;

/**
 * Abstract parent class for all (leaf and group) nodes of the 
 * test (runner) tree.
 * @author Volker Bergmann
 */
public abstract class RunnerNode {

    /** The tree path of the node */
    protected final String name;

    /** The parent group of the node. For the root node this is null */
    protected final RunnerGroup parent;

    /** Arbitrary attributes stored on this node which can be used by listeners etc. to add information for later use. */
    protected Map<String, Object> nodeAttributes;

    /** Constructor requiring the tree {@link #name}. */
    public RunnerNode(String name, RunnerGroup parent) {
        this.name = name;
        this.parent = parent;
    }

    public void setAttribute(String name, Object value) {
        if (nodeAttributes == null) {
            nodeAttributes = new HashMap<String, Object>();
        }
        nodeAttributes.put(name, value);
    }

    public Object getAttribute(String name) {
        if (nodeAttributes == null) {
            return null;
        }

        return nodeAttributes.get(name);
    }

    public RunnerGroup getParent() {
        return parent;
    }

    /** Provides the tree {@link #name} of the node. */
    public String getName() {
        return name;
    }

    public abstract RunStatus getRunStatus();

}
