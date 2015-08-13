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
package org.aludratest.service.gui.web.selenium.selenium2;

import org.aludratest.config.ConfigProperties;
import org.aludratest.config.ConfigProperty;
import org.aludratest.service.Implementation;
import org.aludratest.service.gui.web.AludraWebGUI;
import org.aludratest.service.gui.web.WebGUICondition;
import org.aludratest.service.gui.web.WebGUIInteraction;
import org.aludratest.service.gui.web.WebGUIVerification;
import org.aludratest.service.gui.web.selenium.AbstractSeleniumService;
import org.aludratest.service.gui.web.selenium.SeleniumResourceService;
import org.slf4j.LoggerFactory;

/** Implements the interface {@link AludraWebGUI} using Selenium 2 functionality to access the web GUI.
 * @author Volker Bergmann */
@Implementation({ AludraWebGUI.class })
@ConfigProperties({
    @ConfigProperty(name = "use.local.proxy", type = boolean.class, description = "If true, a local HTTP proxy will be used to allow adding custom HTTP headers. If set to false, no proxy will be used, but the method addCustomHttpHeaderCommand will have no effect.", defaultValue = "true"),
    @ConfigProperty(name = "proxy.port.min", type = int.class, description = "The lowest port number to use for the authenticating proxy.", defaultValue = "19600"),
    @ConfigProperty(name = "driver", type = String.class, description = "The Selenium 2 driver name. Have a look at the org.aludratest.service.gui.web.selenium.selenium2.Drivers enumeration for potential values", defaultValue = "FIREFOX"),
    @ConfigProperty(name = "use.remotedriver", type = boolean.class, description = "If true, use Selenium Remote Driver (talk to Selenium RC), otherwise, directly use driver class.", defaultValue = "false"),
        @ConfigProperty(name = "browser.arguments", type = String.class, description = "Space-separated list of arguments to pass to the browser. Currently, only the CHROME driver supports additional arguments.", required = false),
        @ConfigProperty(name = "tcp.timeout", type = int.class, description = "The TCP timeout to use. If the Selenium Client does not respond within this period of time, the request is aborted, and a SocketTimeoutException will be raised.", required = false, defaultValue = "5000"),
        @ConfigProperty(name = "screenshot.per.window", type = boolean.class, description = "If true, screenshots (in case of error) will be taken per browser window, instead of a full screenshot. This allows for execution of multiple Selenium clients on the same machine.", defaultValue = "false", required = false) })
public class AludraSelenium2 extends AbstractSeleniumService implements AludraWebGUI {

    private Selenium2Interaction interaction;
    private Selenium2Verification verification;
    private Selenium2Condition condition;

    /** The {@link Selenium2Wrapper} to perform the actual invocations. */
    private Selenium2Wrapper seleniumWrapper;

    /** Used by the framework to configure the service */
    @Override
    public void initService() {
        seleniumWrapper = new Selenium2Wrapper(configuration, getSeleniumResourceService());
        interaction = new Selenium2Interaction(seleniumWrapper);
        verification = new Selenium2Verification(seleniumWrapper);
        condition = new Selenium2Condition(seleniumWrapper);
    }

    private SeleniumResourceService getSeleniumResourceService() {
        return aludraServiceContext.newComponentInstance(SeleniumResourceService.class);
    }

    @Override
    public String getDescription() {
        return "Using Selenium host: " + seleniumWrapper.getUsedSeleniumHost() + ", AUT: " + configuration.getUrlOfAut();
    }

    @Override
    public WebGUIInteraction perform() {
        return this.interaction;
    }

    /** @see AludraWebGUI#verify() */
    @Override
    public WebGUIVerification verify() {
        return this.verification;
    }

    /** @see AludraWebGUI#check() */
    @Override
    public WebGUICondition check() {
        return this.condition;
    }

    @Override
    public void close() {
        try {
            seleniumWrapper.tearDown();
        }
        catch (Exception e) {
            LoggerFactory.getLogger(AludraSelenium2.class).warn("Exception when closing Selenium service", e);
        }
    }

}
