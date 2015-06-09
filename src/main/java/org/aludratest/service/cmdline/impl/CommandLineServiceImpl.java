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
package org.aludratest.service.cmdline.impl;

import org.aludratest.config.ConfigProperties;
import org.aludratest.config.Preferences;
import org.aludratest.exception.AutomationException;
import org.aludratest.service.AbstractConfigurableAludraService;
import org.aludratest.service.Implementation;
import org.aludratest.service.cmdline.CommandLineCondition;
import org.aludratest.service.cmdline.CommandLineInteraction;
import org.aludratest.service.cmdline.CommandLineService;
import org.aludratest.service.cmdline.CommandLineVerification;
import org.apache.commons.vfs2.FileSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Default implementation of the {@link CommandLineService}. It uses a single {@link CommandLineActionImpl} instance.
 * @author Volker Bergmann */
@Implementation({ CommandLineService.class })
@ConfigProperties({})
public class CommandLineServiceImpl extends AbstractConfigurableAludraService implements CommandLineService {

    /** The logger of the class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandLineServiceImpl.class);

    private CommandLineActionImpl action;

    private CommandLineServiceConfiguration configuration;

    // properties --------------------------------------------------------------

    @Override
    public String getDescription() {
        return getClass().getSimpleName();
    }

    // Configurable interface implementation ----------------------------------

    @Override
    public String getPropertiesBaseName() {
        return "commandLineService";
    }

    @Override
    public void configure(Preferences preferences) throws AutomationException {
        try {
            configuration = new CommandLineServiceConfiguration(preferences);
        }
        catch (FileSystemException e) {
            LOGGER.error("Error configuring FileService", e);
            throw new AutomationException("Error initializing " + this, e);
        }
    }

    // AludraService interface implementation ----------------------------------

    @Override
    public void initService() {
        this.action = new CommandLineActionImpl(configuration);
    }

    /** Closes the service */
    @Override
    public void close() {
        this.configuration.close();
    }

    // functional interface ----------------------------------------------------

    @Override
    public CommandLineInteraction perform() {
        return action;
    }

    @Override
    public CommandLineVerification verify() {
        return action;
    }

    @Override
    public CommandLineCondition check() {
        return action;
    }

    @Override
    public String getBaseDirectory() {
        return configuration.getBaseDirectory();
    }

}
