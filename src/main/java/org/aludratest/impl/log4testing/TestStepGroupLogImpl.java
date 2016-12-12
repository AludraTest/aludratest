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
package org.aludratest.impl.log4testing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.aludratest.log4testing.TestCaseLog;
import org.aludratest.log4testing.TestStatus;
import org.aludratest.log4testing.TestStepGroupLog;
import org.joda.time.Duration;

/** Implementation of the TestStepGroupLog interface. This class is Thread-safe.
 * 
 * @author falbrech */
public final class TestStepGroupLogImpl extends AbstractNamedTestLogElementImpl implements TestStepGroupLog {

    private TestCaseLogImpl parent;

    private List<TestStepLogImpl> steps = new ArrayList<TestStepLogImpl>();

    /** Constructs a new TestStepGroupLogImpl object with the given name and parent.
     * @param name Name of the test step group.
     * @param parent Test Case containing this test step group. This log is added to the parent automatically. */
    public TestStepGroupLogImpl(String name, TestCaseLogImpl parent) {
        super(name);
        this.parent = parent;
    }

    /** Adds a test step log to this test step group log.
     * 
     * @param step Log of a test step to add. Must not be <code>null</code>. */
    public synchronized void addTestStep(TestStepLogImpl step) {
        if (step == null) {
            throw new IllegalArgumentException("step must not be null");
        }
        steps.add(step);
    }

    @Override
    public Duration getWork() {
        return getDuration();
    }

    @Override
    public TestStatus getStatus() {
        return getStatus(getTestSteps());
    }

    @Override
    public synchronized List<TestStepLogImpl> getTestSteps() {
        return Collections.unmodifiableList(new ArrayList<TestStepLogImpl>(steps));
    }

    @Override
    public TestCaseLog getParent() {
        return parent;
    }

}
