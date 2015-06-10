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
package org.aludratest.service.cmdline;

import org.aludratest.config.ConfigProperties;
import org.aludratest.config.ConfigProperty;
import org.aludratest.service.AludraService;
import org.aludratest.service.ServiceInterface;

/** Provides command line operations, like executíng a program or script and check the results. The service provides an interface
 * with streams for sending data to the executable and reading results back.
 * @author Volker Bergmann */
@ServiceInterface(name = "Command Line Service", description = "Provides invocation and I/O of command line processes.")
@ConfigProperties({ @ConfigProperty(name = "base.directory", type = String.class, description = "The base URL of the service. On a local filesystem, this is a folder path.", defaultValue = "${user.home}", required = true) })
public interface CommandLineService extends AludraService {

    /** Exhibits the service's Interaction implementor */
    @Override
    CommandLineInteraction perform();

    /** Exhibits the service's Verification implementor */
    @Override
    CommandLineVerification verify();

    /** Exhibits the service's Condition implementor */
    @Override
    CommandLineCondition check();

    /**
     * @return the configured base.directory
     */
    String getBaseDirectory();

}
