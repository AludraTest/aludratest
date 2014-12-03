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
package org.aludratest.service.gui.web;

import org.aludratest.config.ConfigProperties;
import org.aludratest.config.ConfigProperty;
import org.aludratest.service.ServiceInterface;
import org.aludratest.service.gui.AludraGUI;

/**
 * {@link AludraGUI} specialization for Web GUIs.
 * @author Volker Bergmann
 */
@ServiceInterface(name = "Aludra Web GUI", description = "Allows testing of a web based GUI, e.g. a web application.")
@ConfigProperties({
    @ConfigProperty(name = "url.of.aut", type = String.class, description = "URL of the application under test.", defaultValue = "http://localhost/", required = true),
    @ConfigProperty(name = "close.testapp.after.execution", type = boolean.class, description = "Indicates if the application under test shall be closed after test execution.", defaultValue = "true") })
public interface AludraWebGUI extends AludraGUI {

    @Override
    public WebGUIInteraction perform();

    @Override
    public WebGUIVerification verify();

    @Override
    public WebGUICondition check();
}
