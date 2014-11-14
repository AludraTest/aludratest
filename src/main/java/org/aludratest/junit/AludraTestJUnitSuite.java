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

import org.aludratest.AludraTest;
import org.aludratest.config.AludraTestConfig;
import org.aludratest.scheduler.AludraSuiteParser;
import org.aludratest.scheduler.RunnerTree;
import org.databene.commons.StringUtil;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** JUnit {@link Runner} which creates a JUnit test suite structure based on AludraTest files. The related AludraTest base suite is
 * specified using a virtual machine parameter 'suite' with a fully qualified class name, e.g. -Dsuite=com.foo.MyTest
 * @author Volker Bergmann */
public class AludraTestJUnitSuite extends Runner {

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

    private int poolSize;

    /** Standard JUnit {@link Runner} constructor which takes the JUnit test class as argument. */
    public AludraTestJUnitSuite(Class<?> testClass) throws InitializationError {
        AludraTest aludraTest = new AludraTest();
        AludraTestConfig config = aludraTest.getServiceManager().newImplementorInstance(AludraTestConfig.class);
        this.testClass = testClass;
        poolSize = config.getNumberOfThreads();
        if (poolSize <= 0) {
            LOGGER.warn("Please set a correct poolSize, which should be >=1. By default it uses poolSize=1.");
            poolSize = 1;
        }
        String suiteName = System.getProperty(SUITE_SYSPROP);

        if (StringUtil.isEmpty(suiteName)) {
            LOGGER.debug("SuiteName:\"" + suiteName + "\"");
            throw new InitializationError("No suite configured");

        }
        tree = new AludraSuiteParser(aludraTest).parse(suiteName);
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

    /** Injects a {@link JUnitWrapperFactory} into the runner {@link #tree}
     * and makes the tree execute the tests.
     *  @see Runner#run(RunNotifier) */
    @Override
    public void run(RunNotifier notifier) {
        tree.setWrapperFactory(new JUnitWrapperFactory(notifier, testClass));
        tree.performAllTestsAndWait(poolSize);
    }

}
