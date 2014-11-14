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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.aludratest.impl.log4testing.data.TestSuiteLog;

/**
 * {@link RunnerNode} implementation which forms a tree node that can have
 * sub nodes which may be executed sequentially or concurrently.
 * @author Volker Bergmann
 */
public class RunnerGroup extends RunnerNode {

    /** The parent group. */
    private final RunnerGroup parent;

    /** The child nodes. */
    private final List<RunnerNode> children;

    /** The log4testing log suite to log test results to */
    private final TestSuiteLog logSuite;

    /** Flag indicating if the child nodes may be executed concurrently. */
    private ExecutionMode mode;

    /** Comparator object for ordering the {@link #children}. */
    private final Comparator<RunnerNode> comparator;

    /** Flag indicating if the comparator has already been applied. */
    private boolean sorted;

    /**
     * Constructs an instance without connection to log4testing.
     * This is mainly for backwards compatibility of existing code
     * which used this constructor and managed log4testing logs
     * outside itself.
     * @param path The path of the node.
     * @param mode See {@link #mode}.
     * @param parent See {@link #parent}.
     * @param comparator See {@link #comparator}.
     */
    public RunnerGroup(String path, ExecutionMode mode, RunnerGroup parent, Comparator<RunnerNode> comparator) {
        this(path, mode, parent, comparator, null);
    }

    /**
     * Constructor
     * @param path The path of the node.
     * @param mode See {@link #mode}.
     * @param parent See {@link #parent}.
     * @param comparator See {@link #comparator}.
     * @param logSuite See {@link #logSuite}
     */
    public RunnerGroup(String path, ExecutionMode mode, RunnerGroup parent, Comparator<RunnerNode> comparator, TestSuiteLog logSuite) {
        super(path, parent);
        this.parent = parent;
        this.mode = mode;
        this.comparator = comparator;
        this.children = new ArrayList<RunnerNode>();
        this.sorted = true;
        this.logSuite = logSuite;
    }

    /** Tells if the child nodes may be executed concurrently. */
    public boolean isParallel() {
        if (mode == ExecutionMode.PARALLEL) {
            return true;
        } else if (mode == ExecutionMode.SEQUENTIAL) {
            return false;
        } else if (parent == null) {
            return true;
        } else {
            return parent.isParallel();
        }
    }

    public TestSuiteLog getLogSuite() {
        return logSuite;
    }

    /** Returns the {@link #children},
     *  ordered with respect to the {@link #comparator}. */
    public List<RunnerNode> getChildren() {
        if (!sorted && comparator != null) {
            Collections.sort(children, comparator);
            sorted = true;
        }
        return children;
    }

    /** Add a child node to the {@link #children}. */
    public void addChild(RunnerNode childNode) {
        children.add(childNode);
        sorted = false;
        if (childNode instanceof RunnerGroup) {
            logSuite.addTestSuite(((RunnerGroup) childNode).getLogSuite());
        } else {
            logSuite.addTestCase(((RunnerLeaf) childNode).getLogCase());
        }
    }

    // private helpers -------------------------------------------------------------------------------------------------

    void childFinished(RunnerNode child) { // default visibility to be accessible only from classes of the same package
        if (logSuite != null && hasFinished()) {
            if (parent != null) {
                parent.childFinished(this);
            }
        }
    }

    @Override
    public synchronized boolean hasFinished() {
        for (RunnerNode child : children) {
            if (!child.hasFinished()) {
                return false;
            }
        }
        return true;
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    /** Creates a string representation of this group. */
    @Override
    public String toString() {
        return (isParallel() ? "parallel" : "serial") + " group: " + (name.length() == 0 ? "<root>" : name);
    }

}
