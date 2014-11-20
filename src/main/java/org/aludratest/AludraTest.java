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

import static org.aludratest.impl.AludraTestConstants.EXIT_EXECUTION_ERROR;
import static org.aludratest.impl.AludraTestConstants.EXIT_EXECUTION_FAILURE;
import static org.aludratest.impl.AludraTestConstants.EXIT_ILLEGAL_ARGUMENT;
import static org.aludratest.impl.AludraTestConstants.EXIT_NORMAL;

import org.aludratest.config.AludraTestConfig;
import org.aludratest.config.impl.DefaultConfigurator;
import org.aludratest.impl.log4testing.data.TestLogger;
import org.aludratest.impl.log4testing.data.TestSuiteLog;
import org.aludratest.impl.plexus.AludraTestClosePhase;
import org.aludratest.impl.plexus.AludraTestComponentDiscoverer;
import org.aludratest.impl.plexus.AludraTestConfigurationPhase;
import org.aludratest.scheduler.AludraTestRunner;
import org.aludratest.scheduler.RunnerTree;
import org.aludratest.scheduler.RunnerTreeBuilder;
import org.aludratest.service.AludraServiceManager;
import org.aludratest.util.data.helper.DataMarkerCheck;
import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.lifecycle.UndefinedLifecycleHandlerException;
import org.databene.commons.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AludraTest framework class. This is the main entry point to AludraTest. Clients can either invoke the class via its main method
 * directly, or construct an instance of this class and call one of its <code>run</code> methods.
 * 
 * @author Volker Bergmann
 * @author falbrech
 */
public final class AludraTest {

    /**
     * The name of the System Property which is checked for the environment name to use.
     */
    public static final String ENVIRONMENT_NAME_PROPERTY = "aludraTest.environment";

    private static final Logger LOGGER = LoggerFactory.getLogger(AludraTest.class);

    /** The component bootstrap of AludraTest. */
    private final AludraServiceManager serviceManager;

    /** The IoC container for AludraTest. */
    private final PlexusContainer iocContainer;

    private AludraTest() {
        // start IoC and instantiate service manager
        this.iocContainer = createIoCContainer();
        try {
            serviceManager = iocContainer.lookup(AludraServiceManager.class);
        }
        catch (ComponentLookupException e) {
            throw new RuntimeException("Could not create AludraServiceManager instance", e);
        }
    }

    public static AludraTest startFramework() {
        AludraTest framework = new AludraTest();
        LOGGER.info("Starting AludraTest {}", framework.getAludraTestConfig().getVersion());

        // static accessors to "dynamic" content are initialized here.
        DataMarkerCheck.init(framework.getServiceManager());

        return framework;
    }

    public void stopFramework() {
        iocContainer.dispose();
    }

    private static PlexusContainer createIoCContainer() {
        try {
            // register AludraTest configuration phase
            DefaultContainerConfiguration config = new DefaultContainerConfiguration();
            config.getLifecycleHandlerManager().getLifecycleHandler("basic").addBeginSegment(new AludraTestConfigurationPhase());
            config.getLifecycleHandlerManager().getLifecycleHandler("plexus").addBeginSegment(new AludraTestConfigurationPhase());
            config.getLifecycleHandlerManager().getLifecycleHandler("basic").addEndSegment(new AludraTestClosePhase());
            config.getLifecycleHandlerManager().getLifecycleHandler("plexus").addEndSegment(new AludraTestClosePhase());

            // register AludraTest component lookup
            config.addComponentDiscoverer(new AludraTestComponentDiscoverer(new DefaultConfigurator()));

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
     * Parses the test class/suite, executes it and waits until all tests are finished.
     * 
     * @param testClass
     *            The AludraTest class or test suite to run
     */
    public void run(Class<?> testClass) {

        RunnerTreeBuilder builder = serviceManager.newImplementorInstance(RunnerTreeBuilder.class);
        RunnerTree runnerTree = builder.buildRunnerTree(testClass);

        // This would SORT the tree
        // RunnerTreeSorter.sortTree(runnerTree, new Alphabetic());

        AludraTestRunner runner = serviceManager.newImplementorInstance(AludraTestRunner.class);
        runner.runAludraTests(runnerTree);
    }

    // private helpers ---------------------------------------------------------------

    private AludraTestConfig getAludraTestConfig() {
        return serviceManager.newImplementorInstance(AludraTestConfig.class);
    }

    private static int exitCode(Class<?> testClass) {
        TestSuiteLog suite = TestLogger.getTestSuite(testClass);
        int failures = suite.getNumberOfFailedTestCases();
        int exitCode = (failures > 0 ? EXIT_EXECUTION_FAILURE : EXIT_NORMAL);
        return exitCode;
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
            AludraTest aludra = startFramework();
            try {
                Class<?> testClass = Class.forName(args[0]);
                aludra.run(testClass);
                System.exit(exitCode(testClass));
            } catch (Throwable t) {
                t.printStackTrace(System.err);
                System.exit(EXIT_EXECUTION_ERROR);
            } finally {
                aludra.stopFramework();
            }
        }
    }

}
