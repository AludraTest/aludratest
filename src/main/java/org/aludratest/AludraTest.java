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
package org.aludratest;

import static org.aludratest.AludraTestConstants.EXIT_EXECUTION_ERROR;
import static org.aludratest.AludraTestConstants.EXIT_EXECUTION_FAILURE;
import static org.aludratest.AludraTestConstants.EXIT_ILLEGAL_ARGUMENT;
import static org.aludratest.AludraTestConstants.EXIT_NORMAL;

import java.lang.reflect.Method;
import java.util.List;

import org.aludratest.config.AludraTestConfig;
import org.aludratest.impl.log4testing.data.TestLogger;
import org.aludratest.impl.log4testing.data.TestSuiteLog;
import org.aludratest.impl.plexus.AludraTestClosePhase;
import org.aludratest.impl.plexus.AludraTestConfigurationPhase;
import org.aludratest.scheduler.AludraSuiteParser;
import org.aludratest.scheduler.RunnerTree;
import org.aludratest.service.impl.AludraServiceManager;
import org.aludratest.testcase.data.TestCaseData;
import org.aludratest.testcase.data.TestDataProvider;
import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.lifecycle.UndefinedLifecycleHandlerException;
import org.databene.commons.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** AludraTest framework class. This is the main entry point to AludraTest. Clients can either invoke the class via its main method
 * directly, or construct an instance of this class and call one of its <code>run</code> methods. <br>
 * Currently, this class is a "quasi-singleton". Invoke the constructor <code>new AludraTest()</code> once to initialize
 * AludraTest and set the one and only AludraTest instance which can be accessed via <code>AludraTest.getInstance()</code>. This
 * behaviour is somewhat "asymmetric" and is subject to change - either towards a clean singleton structure, or towards a real
 * instance structure.
 * 
 * @author Volker Bergmann
 * @author falbrech */
public final class AludraTest {

    /**
     * The name of the System Property which is checked for the environment name to use.
     */
    public static final String ENVIRONMENT_NAME_PROPERTY = "aludraTest.environment";

    private static final Logger LOGGER = LoggerFactory.getLogger(AludraTest.class);

    protected TestDataProvider dataProvider;

    private static AludraTest instance;

    /** The life cycle state of the instance. */
    private State state;

    /** The component bootstrap of AludraTest. */
    private final AludraServiceManager serviceManager;

    /** The IoC container for AludraTest. */
    private final PlexusContainer iocContainer;

    /** Public default constructor. */
    public AludraTest() {
        if (instance != null && instance.state != State.FINISHED) {
            throw new IllegalStateException("There is already an AludraTest instance which is not yet finished.");
        }

        state = State.CREATED;
        instance = this; // NOSONAR

        // start IoC and instantiate service manager
        this.iocContainer = createIoCContainer();
        serviceManager = new AludraServiceManager(iocContainer);

        LOGGER.info("Starting AludraTest {}", getAludraTestConfig().getVersion());
    }

    private static PlexusContainer createIoCContainer() {
        try {
            // register AludraTest configuration phase
            DefaultContainerConfiguration config = new DefaultContainerConfiguration();
            config.getLifecycleHandlerManager().getLifecycleHandler("basic").addBeginSegment(new AludraTestConfigurationPhase());
            config.getLifecycleHandlerManager().getLifecycleHandler("plexus").addBeginSegment(new AludraTestConfigurationPhase());
            config.getLifecycleHandlerManager().getLifecycleHandler("basic").addEndSegment(new AludraTestClosePhase());
            config.getLifecycleHandlerManager().getLifecycleHandler("plexus").addEndSegment(new AludraTestClosePhase());
            return new DefaultPlexusContainer(config);
        }
        catch (PlexusContainerException e) {
            LOGGER.error("Could not startup IoC container", e);
        }
        catch (UndefinedLifecycleHandlerException e) {
            LOGGER.error("Internal exception when configuring IoC container", e);
        }
        return null;
    }

    /** Returns the one and only AludraTest instance. The instance is set as soon as the constructor is invoked.
     * 
     * @return The one and only AludraTest instance. */
    public static AludraTest getInstance() {
        return instance;
    }

    /**
     * Returns the name of the current environment. This is a central parameter of AludraTest and used to distinguish
     * configuration for different environments. Pass it e.g. via command line System Property:
     * 
     * <pre>
     * -DaludraTest.environment=MYHOST
     * </pre>
     * 
     * @return The name of the current environment. Defaults to <code>LOCAL</code> if none has been set.
     */
    public static String getEnvironmentName() {
        String propValue = System.getProperty(ENVIRONMENT_NAME_PROPERTY);
        if (!StringUtil.isEmpty(propValue)) {
            return propValue;
        }
        return "LOCAL";
    }

    /** Returns the service manager of AludraTest. This can be used for service and component lookups. If you have a context
     * available (an AludraTestContext or an AludraServiceContext), you should prefer to call its methods for these lookups.
     * 
     * @return The service manager of AludraTest, never <code>null</code>. */
    public AludraServiceManager getServiceManager() {
        return serviceManager;
    }

    /**
     * @param method the method for which to receive the test data sets.
     * @return the test data sets configured for the method
     */
    public List<TestCaseData> getTestDataSets(Method method) {
        if (dataProvider == null) {
            dataProvider = serviceManager.newImplementorInstance(TestDataProvider.class);
        }
        return dataProvider.getTestDataSets(method);
    }

    /**
     * Parses the test class/suite, executes it and waits until all tests are finished.
     * 
     * @param testClass
     *            The AludraTest class or test suite to run
     */
    public void run(Class<?> testClass) {
        assertState(State.CREATED);

        int numberOfThreads = getAludraTestConfig().getNumberOfThreads();
        try {
            this.state = State.RUNNING;
            RunnerTree runnerTree = new AludraSuiteParser(this).parse(testClass.getName());
            runnerTree.performAllTestsAndWait(numberOfThreads);
        }
        finally {
            this.state = State.FINISHED;
            setInstance(null);
            instance = null; // NOSONAR
        }
    }

    // private helpers ---------------------------------------------------------------

    private AludraTestConfig getAludraTestConfig() {
        return serviceManager.newImplementorInstance(AludraTestConfig.class);
    }

    /** Requires the instance to be in a given state, if it is not, an {@link IllegalStateException} is thrown. */
    private void assertState(State state) {
        if (this.state != state) {
            throw new IllegalStateException("Expected state '" + state + "' but found state '" + this.state + "'");
        }
    }

    private static int exitCode(Class<?> testClass) {
        TestSuiteLog suite = TestLogger.getTestSuite(testClass);
        int failures = suite.getNumberOfFailedTestCases();
        int exitCode = (failures > 0 ? EXIT_EXECUTION_FAILURE : EXIT_NORMAL);
        return exitCode;
    }

    /** The life cycle states of this class. */
    static enum State {
        CREATED, RUNNING, FINISHED
    }

    // main method -------------------------------------------------------------

    /** Main method for running Aludra tests.
     *  @param args expects a class name as single argument
     *  @throws ClassNotFoundException if the class of the provided name was not found */
    public static void main(String[] args) throws ClassNotFoundException {
        // check preconditions
        if (args.length == 0) {
            // test class/suite name missing
            System.out.println("Please provide a test class name as argument"); //NOSONAR
            System.exit(EXIT_ILLEGAL_ARGUMENT);
        } else {
            // execute
            AludraTest aludra = new AludraTest();
            try {
                Class<?> testClass = Class.forName(args[0]);
                aludra.run(testClass);
                System.exit(exitCode(testClass));
            } catch (Throwable t) {
                t.printStackTrace(System.err);
                System.exit(EXIT_EXECUTION_ERROR);
            } finally {
                aludra.iocContainer.dispose();
            }
        }
    }


    /**
     * Helper method for AludraTest internal unit tests. DO NOT CALL.
     * 
     * @param instance
     *            Instance.
     */
    static void setInstance(AludraTest instance) {
        if (AludraTest.instance != null && AludraTest.instance.iocContainer != null) {
            AludraTest.instance.iocContainer.dispose();
        }

        AludraTest.instance = instance;
    }

}
