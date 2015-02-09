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
package org.aludratest.service.gui.web.selenium;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.aludratest.config.AludraTestConfig;
import org.aludratest.config.ConfigProperties;
import org.aludratest.config.ConfigProperty;
import org.aludratest.config.Configurable;
import org.aludratest.config.MutablePreferences;
import org.aludratest.config.Preferences;
import org.aludratest.exception.AccessFailure;
import org.aludratest.exception.AutomationException;
import org.aludratest.service.Implementation;
import org.aludratest.util.ObjectPool;
import org.codehaus.plexus.component.annotations.Requirement;
import org.databene.commons.StringUtil;

/**
 * Creates and manages a pool of Selenium hosts by their URL
 * using an {@link ObjectPool}.
 * @author Volker Bergmann
 */
@Implementation({ SeleniumResourceService.class })
@ConfigProperties({
    @ConfigProperty(name = "execution.hosts", type = String.class, description = "Comma-separated list of host names running Selenium. For each Thread configured for AludraTest, one entry must be present. Host names can be appended with :port to specify a different than the default selenium port, e.g. localhost:4445.", defaultValue = "localhost"),
    @ConfigProperty(name = "default.selenium.port", type = String.class, description = "The default port for Selenium hosts to use, when no port is specified for a host.", defaultValue = "4444") })
public class LocalSeleniumResourceService implements SeleniumResourceService, Configurable {

    private static final String EXECUTION_HOSTS_PROP = "execution.hosts";

    /** The list of host names */
    private List<String> executionHosts;

    /** The {@link ObjectPool} which does the real URL management */
    private ObjectPool<String> hosts;

    /** The AludraTest configuration. Will be injected by IoC container. */
    @Requirement
    private AludraTestConfig aludraConfig;

    private int defaultPort;

    @Override
    public String getPropertiesBaseName() {
        return "seleniumResourceService";
    }

    @Override
    public void fillDefaults(MutablePreferences preferences) {
    }

    @Override
    public void configure(Preferences preferences) throws AutomationException {
        String executionHostsSpec = preferences.getStringValue(EXECUTION_HOSTS_PROP);
        if (StringUtil.isEmpty(executionHostsSpec)) {
            executionHosts = Collections.emptyList();
        }
        else {
            executionHosts = Arrays.asList(executionHostsSpec.replace(" ", "").split(","));
        }

        // assert that AludraTest number of threads matches executionHosts size
        if (aludraConfig.getNumberOfThreads() != executionHosts.size()) {
            throw new AutomationException("Execution hosts size (" + executionHosts.size()
                    + ") is not equal to number of threads of AludraTest (" + aludraConfig.getNumberOfThreads() + ")");
        }

        defaultPort = preferences.getIntValue("default.selenium.port", 4444);

        getHosts();
    }

    @Override
    public int getHostCount() {
        return executionHosts.size();
    }

    /** acquires a proxy from the pool for exclusive use by a single client. */
    @Override
    public String acquire() {
        try {
            return getHosts().acquire();
        } catch (InterruptedException e) {
            throw new AccessFailure("Failed to acquire a host", e);
        }
    }

    /** Puts back a used host URL into the pool.
     *  After having finished server access, the client must call this method
     *  to make the server available to other clients again. */
    @Override
    public void release(String host) {
        hosts.release(host);
    }

    private ObjectPool<String> getHosts() {
        if (this.hosts == null) {
            this.hosts = new ObjectPool<String>(executionHosts.size(), false);
            for (String realHost : executionHosts) {
                if (!realHost.contains(":")) {
                    realHost += ":" + defaultPort;
                }
                this.hosts.add("http://" + realHost + "/");
            }
        }
        return this.hosts;
    }
}
