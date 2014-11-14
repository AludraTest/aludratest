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

import org.aludratest.impl.log4testing.data.TestCaseLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link RunnerNode} implementation which represents a leaf,
 * meaning an atomic executable test case, represented by an
 * object implementing the {@link Runnable} interface.
 * @author Volker Bergmann
 */
public class RunnerLeaf extends RunnerNode implements Runnable {

    /** The {@link Logger} of the class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(RunnerLeaf.class);

    /** The test {@link Runnable} to execute in {@link #run()}. */
    private final Runnable runnable;

    protected final TestCaseLog logCase;

    private boolean finished;

    /** Constructor requiring the tree path and the runnable to execute.
     *  @param path
     *  @param parent
     *  @param runnable
     *  @param logCase */
    public RunnerLeaf(String path, RunnerGroup parent, Runnable runnable, TestCaseLog logCase) {
        super(path, parent);
        this.runnable = runnable;
        this.logCase = logCase;
        this.finished = false;
    }

    /** Implements {@link Runnable#run()} delegation work
     *  to the wrapped {@link #runnable} */
    @Override
    public void run() {
        LOGGER.debug("Starting " + runnable);

        // rename current Thread for logging purposes
        String oldName = Thread.currentThread().getName();
        Thread.currentThread().setName("RunnerLeaf " + getLogCase().getId());

        try {
            runnable.run();
            this.finished = true;
            LOGGER.debug("Finished " + runnable);
            Thread.currentThread().setName(oldName);
            parent.childFinished(this);
        }
        finally {
            Thread.currentThread().setName(oldName);
        }
    }

    /** Creates a string representation of the leaf. */
    @Override
    public String toString() {
        return "leaf:[" + runnable + "], path:" + name;
    }

    @Override
    public boolean hasFinished() {
        return finished;
    }

    /** @return the {@link #logCase} */
    public TestCaseLog getLogCase() {
        return logCase;
    }

}
