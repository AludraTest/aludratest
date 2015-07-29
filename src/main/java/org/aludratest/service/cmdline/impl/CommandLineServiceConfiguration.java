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

import org.aludratest.config.Preferences;
import org.aludratest.config.ValidatingPreferencesWrapper;
import org.aludratest.service.AludraCloseable;
import org.aludratest.service.cmdline.CommandLineService;
import org.apache.commons.vfs2.FileSystemException;

/** Provides the configuration for the {@link CommandLineService}.
 * @author Volker Bergmann */
public class CommandLineServiceConfiguration implements AludraCloseable {

    /** property name of the base directory for the CommandLineService */
    public static final String BASE_DIRECTORY = "base.directory";

    private ValidatingPreferencesWrapper configuration;

    /** Creates a new FileServiceConfiguration object which wraps the given Preferences object.
     * @param configuration Preferences configuration object to wrap.
     * @throws FileSystemException If an exception occurs when applying the configuration to the VFS Config Builders. */
    public CommandLineServiceConfiguration(Preferences configuration) throws FileSystemException {
        this.configuration = new ValidatingPreferencesWrapper(configuration);
    }

    /** @return the root folder of the service. */
    public final String getBaseDirectory() {
        return configuration.getRequiredStringValue(BASE_DIRECTORY);
    }

    @Override
    public void close() {
        // nothing to do
    }

}
