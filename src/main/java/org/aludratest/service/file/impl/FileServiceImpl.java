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
package org.aludratest.service.file.impl;

import org.aludratest.config.ConfigProperties;
import org.aludratest.config.ConfigProperty;
import org.aludratest.config.Preferences;
import org.aludratest.exception.AutomationException;
import org.aludratest.service.AbstractConfigurableAludraService;
import org.aludratest.service.Implementation;
import org.aludratest.service.file.FileCondition;
import org.aludratest.service.file.FileInteraction;
import org.aludratest.service.file.FileService;
import org.aludratest.service.file.FileVerification;
import org.apache.commons.vfs2.FileSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements the {@link FileService} interface.
 * @author Volker Bergmann
 */
@Implementation({ FileService.class })
@ConfigProperties({
        @ConfigProperty(name = "wait.max.retries", type = int.class, description = "The maximum number of retries when polling files.", defaultValue = ""
                + FileServiceConfiguration.DEFAULT_WAIT_MAX_RETRIES),
        @ConfigProperty(name = "wait.timeout", type = int.class, description = "The maximum time to wait when polling, in milliseconds. The retries are distributed over this time", defaultValue = ""
                + FileServiceConfiguration.DEFAULT_WAIT_TIMEOUT) })
public class FileServiceImpl extends AbstractConfigurableAludraService implements FileService {

    /** The logger of the class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(FileServiceImpl.class);

    private FileServiceConfiguration configuration;

    /** The implementor of all action interfaces. */
    private FileActionImpl action;

    /** Default constructor. */
    public FileServiceImpl() {
        // nothing to do here
    }

    // Configurable interface implementation ----------------------------------

    @Override
    public String getPropertiesBaseName() {
        return "fileService";
    }

    @Override
    public void configure(Preferences preferences) throws AutomationException {
        try {
            configuration = new FileServiceConfiguration(preferences);
        }
        catch (FileSystemException e) {
            LOGGER.error("Error configuring FileService", e);
            throw new AutomationException("Error initializing " + this, e);
        }
    }

    // AludraService interface implementation ----------------------------------
    @Override
    public void initService() {
        this.action = new FileActionImpl(configuration);
    }

    /** Closes the configuration (and with it, Commons VFS' StandardFileSystemManager). */
    @Override
    public void close() {
        this.configuration.close();
    }

    /** Provides the service's interaction operations. */
    @Override
    public FileInteraction perform() {
        return this.action;
    }

    /** Provides the service's verification operations. */
    @Override
    public FileVerification verify() {
        return this.action;
    }

    /** Provides the service's checking operations. */
    @Override
    public FileCondition check() {
        return this.action;
    }

    @Override
    public String getDescription() {
        String description = "Accessing " + configuration.getBaseUrl();
        if (configuration.getUser() != null) {
            description += " as user " + configuration.getUser();
        }
        return description;
    }

}
