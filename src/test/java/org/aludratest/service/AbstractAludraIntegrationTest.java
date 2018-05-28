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
package org.aludratest.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.aludratest.AludraTest;
import org.aludratest.config.AludraTestConfig;
import org.aludratest.config.impl.AludraTestingTestConfigImpl;
import org.aludratest.impl.log4testing.Log4TestingRunnerListener;
import org.aludratest.log4testing.config.AbbreviatorConfiguration;
import org.aludratest.log4testing.config.Log4TestingConfiguration;
import org.aludratest.log4testing.config.TestLogWriterConfiguration;
import org.aludratest.log4testing.config.TestStepFilterConfiguration;
import org.aludratest.scheduler.MemoryTestLog;
import org.aludratest.scheduler.RunnerListener;
import org.aludratest.scheduler.RunnerListenerRegistry;
import org.aludratest.service.util.TestRunnerListenerRegistryImpl;
import org.codehaus.plexus.util.ReflectionUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

/** Abstract base class for test cases needing a running AludraTest environment, including access to the Log4Testing log. Use
 * <code>aludra.run(<i>className</i>)</code> to run a test or suite class in the AludraTest environment. Use {@link #getTestLog()}
 * to retrieve the log for the last executed test run. <br>
 * If you only want to test a service implementation, consider using {@link AbstractAludraServiceTest} as base class.
 *
 * @author falbrech */
@SuppressWarnings("javadoc")
public abstract class AbstractAludraIntegrationTest {

    protected AludraTest aludra;

    protected AludraTestingTestConfigImpl config;

    @SuppressWarnings("deprecation")
    @Before
    public void prepareTestCase() throws Exception {
        System.setProperty("ALUDRATEST_CONFIG/aludraservice/" + AludraTestConfig.class.getName(),
                AludraTestingTestConfigImpl.class.getName());

        this.aludra = AludraTest.startFramework();

        // hook into execution - remove standard Log4Testing Runner Listener, add our own
        // the components.xml in src/test/resources/META-INF/plexus registers THIS implementation
        TestRunnerListenerRegistryImpl registry = (TestRunnerListenerRegistryImpl) aludra.getServiceManager()
                .newImplementorInstance(RunnerListenerRegistry.class);

        for (RunnerListener listener : registry.getListeners()) {
            if (listener instanceof Log4TestingRunnerListener) {
                registry.removeRunnerListener(listener);
            }
        }

        // inject our own configuration object to be able to override configs for test, and initialize it
        AludraTestConfig config = aludra.getServiceManager().newImplementorInstance(AludraTestConfig.class);
        Assert.assertTrue(config instanceof AludraTestingTestConfigImpl);

        Log4TestingRunnerListener listener = new Log4TestingRunnerListener(new TestLog4TestingConfiguration());
        // as not constructed by IoC container, we must initialize configuration
        ReflectionUtils.setVariableValueInObject(listener, "configuration", config);

        registry.addRunnerListener(listener);

        this.config = (AludraTestingTestConfigImpl) config;
        MemoryTestLog.instance = null;
    }

    @After
    public void closeTestCase() {
        aludra.stopFramework();
    }

    protected void assertNotFailed() {
        Assert.assertFalse("Unexpected failure", getTestLog().getRootSuite().getStatus().isFailure());
    }

    @SuppressWarnings("deprecation")
    protected final MemoryTestLog getTestLog() {
        if (MemoryTestLog.instance == null) {
            MemoryTestLog.instance = new MemoryTestLog();
        }
        return MemoryTestLog.instance;
    }

    private static class TestLog4TestingConfiguration implements Log4TestingConfiguration {

        @Override
        public AbbreviatorConfiguration getAbbreviatorConfiguration() {
            return new AbbreviatorConfiguration() {
                @Override
                public Map<String, String> getAbbreviations() {
                    return Collections.emptyMap();
                }
            };
        }

        @Override
        public List<? extends TestLogWriterConfiguration> getTestLogWriterConfigurations() {
            return Collections.singletonList(new TestLogWriterConfiguration() {
                @Override
                public Properties getWriterProperties() {
                    return new Properties();
                }

                @Override
                public String getWriterClassName() {
                    return MemoryTestLog.class.getName();
                }

                @Override
                public List<? extends TestStepFilterConfiguration> getTestStepFilters() {
                    return Collections.emptyList();
                }
            });
        }

        @Override
        public Properties getGlobalProperties() {
            return new Properties();
        }

    }

}
