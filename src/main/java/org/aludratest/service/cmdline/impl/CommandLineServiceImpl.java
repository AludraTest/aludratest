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

import org.aludratest.service.AbstractAludraService;
import org.aludratest.service.cmdline.CommandLineCondition;
import org.aludratest.service.cmdline.CommandLineInteraction;
import org.aludratest.service.cmdline.CommandLineService;
import org.aludratest.service.cmdline.CommandLineVerification;

/** Default implementation of the {@link CommandLineService}. It uses a single {@link CommandLineActionImpl} instance.
 * @author Volker Bergmann */
public class CommandLineServiceImpl extends AbstractAludraService implements CommandLineService {

    private CommandLineActionImpl action;

    // properties --------------------------------------------------------------

    @Override
    public String getDescription() {
        return getClass().getSimpleName();
    }

    // life cycle methods ------------------------------------------------------

    @Override
    public void initService() {
        this.action = new CommandLineActionImpl();
    }

    /** Closes the service */
    @Override
    public void close() {
        // nothing to do
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

}
