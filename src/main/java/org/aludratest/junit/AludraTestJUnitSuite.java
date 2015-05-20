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

import java.util.HashMap;
import java.util.Map;

import org.aludratest.AludraTest;
import org.aludratest.exception.FunctionalFailure;
import org.aludratest.scheduler.AludraTestRunner;
import org.aludratest.scheduler.RunnerListener;
import org.aludratest.scheduler.RunnerListenerRegistry;
import org.aludratest.scheduler.RunnerTree;
import org.aludratest.scheduler.RunnerTreeBuilder;
import org.aludratest.scheduler.node.RunnerGroup;
import org.aludratest.scheduler.node.RunnerLeaf;
import org.aludratest.scheduler.util.CommonRunnerLeafAttributes;
import org.aludratest.testcase.TestStatus;
import org.aludratest.testcase.event.TestStepInfo;
import org.databene.commons.BeanUtil;
import org.databene.commons.StringUtil;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** JUnit {@link Runner} which creates a JUnit test suite structure based on AludraTest files. The related AludraTest base suite is
 * specified using a virtual machine parameter 'suite' with a fully qualified class name, e.g. -Dsuite=com.foo.MyTest
 * @author Volker Bergmann */
public class AludraTestJUnitSuite extends Runner implements RunnerListener {

    public static final String SUITE_SYSPROP = "suite";

    /** The technical {@link Logger} to track debugging information. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AludraTestJUnitSuite.class);

    /** The Java class which triggered JUnit invocation
     *  (the one with a {@link RunWith}(AludraTestJUnitSuite.class) annotation.*/
    private Class<?> testClass;

    /** The JUnit {@link Description} of the AludraTest test suite. */
    private Description description;

    /** The scheduler which coordinates test execution. */
    private RunnerTree tree;

    private AludraTest aludraTest;

    private RunNotifier notifier;

    private Map<RunnerLeaf, TestStatus> leafStatus = new HashMap<RunnerLeaf, TestStatus>();

    private Map<RunnerLeaf, Throwable> leafErrors = new HashMap<RunnerLeaf, Throwable>();

    /** Standard JUnit {@link Runner} constructor which takes the JUnit test class as argument.
     * @param testClass JUnit test class. */
    public AludraTestJUnitSuite(Class<?> testClass) throws InitializationError {
        aludraTest = AludraTest.startFramework();
        this.testClass = testClass;
        String suiteName = System.getProperty(SUITE_SYSPROP);

        if (StringUtil.isEmpty(suiteName)) {
            LOGGER.debug("SuiteName:\"" + suiteName + "\"");
            throw new InitializationError("No suite configured");
        }

        RunnerTreeBuilder builder = aludraTest.getServiceManager().newImplementorInstance(RunnerTreeBuilder.class);
        tree = builder.buildRunnerTree(BeanUtil.forName(suiteName));

        // register this class as listener for events
        RunnerListenerRegistry registry = aludraTest.getServiceManager().newImplementorInstance(RunnerListenerRegistry.class);
        registry.addRunnerListener(this);
    }

    // Runner interface implementation ---------------------------------------------------------------------------------

    /** Provides a JUnit {@link Description} of the AludraTest test suite.
     * @see Runner#getDescription() */
    @Override
    public Description getDescription() {
        if (description == null) {
            description = JUnitUtil.createDescription(tree.getRoot(), testClass);
        }
        return description;
    }

    @Override
    public void run(RunNotifier notifier) {
        this.notifier = notifier;
        AludraTestRunner runner = aludraTest.getServiceManager().newImplementorInstance(AludraTestRunner.class);
        try {
            runner.runAludraTests(tree);
        }
        finally {
            aludraTest.stopFramework();
        }
    }

    private boolean isIgnored(RunnerLeaf runnerLeaf) {
        return Boolean.TRUE.equals(runnerLeaf.getAttribute(CommonRunnerLeafAttributes.IGNORE));
    }

    @Override
    public void startingTestProcess(RunnerTree runnerTree) {
        // not reported to JUnit.
    }

    @Override
    public void startingTestGroup(RunnerGroup runnerGroup) {
        // not reported to JUnit.
    }

    @Override
    public void startingTestLeaf(RunnerLeaf runnerLeaf) {
        if (!isIgnored(runnerLeaf)) {
            notifier.fireTestStarted(createDescription(runnerLeaf));
            synchronized (leafStatus) {
                leafStatus.put(runnerLeaf, TestStatus.PASSED);
            }
        }
    }

    @Override
    public void finishedTestLeaf(RunnerLeaf runnerLeaf) {
        TestStatus status;
        Throwable error;
        synchronized (leafStatus) {
            status = leafStatus.get(runnerLeaf);
            error = leafErrors.get(runnerLeaf);

            leafStatus.remove(runnerLeaf);
            leafErrors.remove(runnerLeaf);
        }

        if (isIgnored(runnerLeaf)) {
            notifier.fireTestIgnored(createDescription(runnerLeaf));
        }
        else {
            if (status.isFailure()) {
                if (error == null) {
                    error = new FunctionalFailure("Test Case reported status " + status + ", please refer to log");
                }

                notifier.fireTestFailure(new Failure(createDescription(runnerLeaf), error));
            }
            notifier.fireTestFinished(createDescription(runnerLeaf));
        }
    }

    @Override
    public void finishedTestGroup(RunnerGroup runnerGroup) {
        // not reported to JUnit.
    }

    @Override
    public void finishedTestProcess(RunnerTree runnerTree) {
        // not reported to JUnit.
    }

    @Override
    public void newTestStepGroup(RunnerLeaf runnerLeaf, String groupName) {
        // not reported to JUnit.
    }

    @Override
    public void newTestStep(RunnerLeaf runnerLeaf, TestStepInfo testStepInfo) {
        if (testStepInfo.getTestStatus().isFailure()) {
            synchronized (leafStatus) {
                leafStatus.put(runnerLeaf, testStepInfo.getTestStatus());
                if (testStepInfo.getError() != null) {
                    leafErrors.put(runnerLeaf, testStepInfo.getError());
                }
            }
        }
    }

    private Description createDescription(RunnerLeaf runnerLeaf) {
        return JUnitUtil.createDescription(runnerLeaf, testClass);
    }
}
