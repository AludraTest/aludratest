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
package org.aludratest.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.aludratest.impl.log4testing.data.TestLogger;
import org.aludratest.junit.JUnitWrapperFactory;
import org.aludratest.scheduler.node.RunnerLeaf;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runner.notification.StoppedByUserException;

/**
 * Tests the {@link JUnitWrapperFactory}.
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public class JUnitWrapperFactoryTest {

    @Test
    public void test() {
        RunNotifierMock notifier = new RunNotifierMock();
        JUnitWrapperFactory factory = new JUnitWrapperFactory(notifier, getClass());
        RunnerLeafMock leaf = new RunnerLeafMock();
        RunnerLeaf wrapper = factory.wrap(leaf);
        wrapper.run();
        assertTrue(leaf.invoked);
        assertTrue(notifier.started);
        assertTrue(notifier.finished);
    }

    /** Mocks a RunnerLeaf and provides a flag ({@link #invoked}) 
     *  that tells if it has been invoked */
    static class RunnerLeafMock extends RunnerLeaf {

        public boolean invoked;

        public RunnerLeafMock() {
            super("path", null, null, TestLogger.getTestCase(JUnitWrapperFactoryTest.class));
            this.invoked = false;
        }

        @Override
        public void run() {
            this.invoked = true;
        }

    }

    /** Mocks a JUnit {@link RunNotifier} and stores the information if 
     *  #fireTestStarted and #fireTestFinished have been called. */
    static class RunNotifierMock extends RunNotifier {

        public boolean started = false;
        public boolean finished = false;

        @Override
        public void fireTestStarted(Description description) throws StoppedByUserException {
            assertEquals("path(" + JUnitWrapperFactoryTest.class.getName() + ")", description.getDisplayName());
            started = true;
        }

        @Override
        public void fireTestFinished(Description description) {
            finished = true;
        }

    }

}
