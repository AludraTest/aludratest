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
package org.aludratest.service.gui.web.selenium.selenium1;

import org.aludratest.config.AludraTestConfig;
import org.aludratest.config.ConfigProperty;
import org.aludratest.exception.AutomationException;
import org.aludratest.exception.TechnicalException;
import org.aludratest.service.Implementation;
import org.aludratest.service.gui.web.AludraWebGUI;
import org.aludratest.service.gui.web.WebGUICondition;
import org.aludratest.service.gui.web.WebGUIInteraction;
import org.aludratest.service.gui.web.WebGUIVerification;
import org.aludratest.service.gui.web.selenium.AbstractSeleniumService;
import org.aludratest.service.gui.web.selenium.SeleniumResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements the interface {@link AludraWebGUI} using Selenium 1 functionality
 * to access the web GUI.
 * 
 * @author Marcel Malitz
 * @author Volker Bergmann
 * @author Joerg Langnickel
 */
@Implementation({ AludraWebGUI.class })
@ConfigProperty(name = "browser", type = String.class, description = "The Selenium 1 browser name to use.", defaultValue = "*firefoxproxy")
public class AludraSelenium1 extends AbstractSeleniumService implements AludraWebGUI {

    /** The class' Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AludraSelenium1.class);

    /** The {@link SeleniumWrapper} to perform the actual invocations. */
    private SeleniumWrapper seleniumWrapper;

    private Selenium1Interaction interaction;
    private Selenium1Verification verification;
    private Selenium1Condition condition;

    private State state;

    /** Default constructor as required by the framework */
    public AludraSelenium1() {
        this.state = State.CREATED;
    }

    /** Used by the framework to configure the service */
    @Override
    public void initService() {
        assertState(State.CREATED, "init()");
        try {
            this.seleniumWrapper = new SeleniumWrapper(aludraServiceContext.getInstanceName(),
                    aludraServiceContext.newComponentInstance(SeleniumResourceService.class), configuration);

            int hostCount = seleniumWrapper.getHostCount();
            int threadCount = aludraServiceContext.newComponentInstance(AludraTestConfig.class).getNumberOfThreads();
            if (hostCount != threadCount) {
                throw new AutomationException("Number of execution hosts (" + hostCount + ") "
                        + "does not match the AludraTest number of threads (" + threadCount + ")");
            }

            this.interaction = new Selenium1Interaction(seleniumWrapper);
            this.verification = new Selenium1Verification(seleniumWrapper);
            this.condition = new Selenium1Condition(seleniumWrapper);
            this.state = State.INITIALIZED;
            LOGGER.info("Opened " + getDescription());
        }
        catch (Exception e) { // NOSONAR
            this.state = State.CLOSED;
            String message = "Failed to initialize " + getClass() + "(" + getDescription() + ")";
            throw new TechnicalException(message, e);
        }
    }

    @Override
    public String getDescription() {
        if (seleniumWrapper != null && seleniumWrapper.getConfiguration() != null) {
            return "Using Selenium host: " + seleniumWrapper.getUsedSeleniumHost() + ", AUT: "
                    + seleniumWrapper.getConfiguration().getUrlOfAut();
        }
        else {
            return "[not available]";
        }
    }

    @Override
    public void open() {
        assertState(State.INITIALIZED, "open()");
        try {
            interaction.open(configuration.getUrlOfAut());
        }
        finally {
            // also in case of error, treat as OPEN to be able to perform additional steps (which will fail)
            state = State.OPEN;
        }
    }

    @Override
    public WebGUIInteraction perform() {
        // workaround to allow calls to "addCustomHttpHeaderCommand" before open().
        // TODO clean location for addCustomHttpHeaderCommand has to be found!
        if (state != State.OPEN && state != State.INITIALIZED) {
            assertState(State.OPEN, "perform()");
        }
        return this.interaction;
    }

    @Override
    public WebGUIVerification verify() {
        assertState(State.OPEN, "verify()");
        return this.verification;
    }

    @Override
    public WebGUICondition check() {
        assertState(State.OPEN, "check()");
        return this.condition;
    }

    @Override
    public void close() {
        if (this.state == State.INITIALIZED || this.state == State.OPEN) {
            this.state = State.CLOSED;
            LOGGER.info("Closed " + getDescription());
            try {
                seleniumWrapper.tearDown();
            }
            catch (Exception e) { // NOSONAR
                LOGGER.error("Error closing " + getClass().getName(), e);
            }
        }
    }

    // private helpers ---------------------------------------------------------
    private void assertState(State expectedState, String operation) {
        if (this.state != expectedState) {
            throw new TechnicalException("Operation '" + operation + "' " +
                    "expects state '" + expectedState + "', but found " + this.state);
        }
    }

    static enum State {
        CREATED, INITIALIZED, OPEN, CLOSED
    }

}
