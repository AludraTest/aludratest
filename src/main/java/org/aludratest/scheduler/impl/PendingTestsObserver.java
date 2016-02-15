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
package org.aludratest.scheduler.impl;

import java.util.concurrent.atomic.AtomicInteger;

import org.aludratest.scheduler.RunnerListener;
import org.aludratest.scheduler.RunnerTree;
import org.aludratest.scheduler.node.RunnerGroup;
import org.aludratest.scheduler.node.RunnerLeaf;
import org.aludratest.scheduler.node.RunnerNode;
import org.aludratest.testcase.event.TestStepInfo;
import org.codehaus.plexus.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logs how many test cases are pending in the test process.
 * @author Volker Bergmann
 */
@Component(role = RunnerListener.class, hint = "pending-tests")
public class PendingTestsObserver implements RunnerListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PendingTestsObserver.class);

    private AtomicInteger pendingTests;

    /** Instantiates a new observer object. */
    public PendingTestsObserver() {
        this.pendingTests = new AtomicInteger();
    }

    @Override
    public void startingTestProcess(RunnerTree runnerTree) {
        int n = getNumberOfTestCases(runnerTree.getRoot());
        this.pendingTests.set(n);
        LOGGER.info("Starting root suite {}", runnerTree.getRoot().getName());
        LOGGER.info("Planned tests: {}", n);
    }

    @Override
    public void startingTestGroup(RunnerGroup runnerGroup) {
        // nothing to do
    }

    @Override
    public void startingTestLeaf(RunnerLeaf runnerLeaf) {
        LOGGER.info("Starting test {}", runnerLeaf.getName());
    }

    @Override
    public void finishedTestLeaf(RunnerLeaf runnerLeaf) {
        LOGGER.info("Finished test {}", runnerLeaf.getName());
        LOGGER.info("Pending tests: {}", pendingTests.decrementAndGet());
    }

    @Override
    public void finishedTestGroup(RunnerGroup runnerGroup) {
        // nothing to do
    }

    @Override
    public void finishedTestProcess(RunnerTree runnerTree) {
        LOGGER.info("Finished root suite {}", runnerTree.getRoot().getName());
    }

    @Override
    public void newTestStepGroup(RunnerLeaf runnerLeaf, String groupName) {
        // nothing to do
    }

    @Override
    public void newTestStep(RunnerLeaf runnerLeaf, TestStepInfo testStepInfo) {
        // nothing to do
    }

    private int getNumberOfTestCases(RunnerGroup root) {
        int count = 0;
        for (RunnerNode node : root.getChildren()) {
            if (node instanceof RunnerLeaf) {
                count++;
            }
            else if (node instanceof RunnerGroup) {
                count += getNumberOfTestCases((RunnerGroup) node);
            }
        }

        return count;
    }

}
