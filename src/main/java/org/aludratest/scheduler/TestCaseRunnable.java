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
package org.aludratest.scheduler;

import org.aludratest.impl.log4testing.data.TestCaseLog;

/**
 * Wraps a {@link Runnable} that represents a test case
 * @author Volker Bergmann
 */
public class TestCaseRunnable implements Runnable {

    private Runnable realRunnable;

    private TestCaseLog logCase;

    /**  Constructor
     *  @param realRunnable the real {@link Runnable} to execute
     *  @param logCase the log to write to */
    public TestCaseRunnable(Runnable realRunnable, TestCaseLog logCase) {
        this.realRunnable = realRunnable;
        this.logCase = logCase;
    }

    public void run() {
        logCase.start();
        realRunnable.run();
        logCase.finish();
    }

}
