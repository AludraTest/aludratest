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

import org.aludratest.impl.log4testing.util.LogUtil;
import org.aludratest.invoker.TestInvoker;
import org.aludratest.testcase.AludraTestContext;

/** Adapter class which wraps an AludraTest {@link TestInvoker} with a {@link Runnable} interface.
 * @author Volker Bergmann */
public class AludraTestRunner implements Runnable {

    /** The wrapped {@link TestInvoker} */
    private TestInvoker invoker;

    private AludraTestContext context;

    /** Constructor
     *  @param invoker the TestInvoker to wrap.
     */
    public AludraTestRunner(TestInvoker invoker) {
        this.invoker = invoker;
        this.context = null;
    }

    /** Calls the {@link #invoker}'s invoke() method
     *  and logs invocation to log4testing. */
    @Override
    public void run() {
        openTestCaseLog();
        try {
            invoker.invoke();
        } catch (Throwable e) { //NOSONAR
            // SonarQube does not like catch(Throwable), but Runnable.run()
            // does not declare 'throws Throwable'. One solution would be to
            // migrate to the Callable interface but is not worth the effort.
            handleException(e);
        }
    }

    // private helper methods --------------------------------------------------

    /** Sets the invoker's context.
     *  @param context */
    public void setContext(AludraTestContext context) {
        this.context = context;
        this.invoker.setContext(context);
    }

    /** initializes the test log */
    private void openTestCaseLog() {
    }

    /** Logs exceptions in the test log. */
    private void handleException(Throwable e) {
        LogUtil.appendErrorInfoToLastStep(null, e, null, null, context.getTestCaseLog());
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    /** Creates a String representation of the object */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + invoker + ")";
    }

}
