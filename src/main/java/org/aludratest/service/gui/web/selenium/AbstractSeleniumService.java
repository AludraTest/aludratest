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

import org.aludratest.config.ConfigProperties;
import org.aludratest.config.ConfigProperty;
import org.aludratest.config.Preferences;
import org.aludratest.service.AbstractConfigurableAludraService;
import org.aludratest.service.gui.web.AludraWebGUI;

/** Common base class for Selenium based implementations of the AludraWebGUI interface.
 * 
 * @author falbrech */
@ConfigProperties({
    @ConfigProperty(name = "timeout", type = int.class, description="Timeout in milliseconds after which a test step stops retrying doing a action.", defaultValue = "15000"),
    @ConfigProperty(name = "speed", type = int.class, description="Speed in milliseconds. What means that between each Selenium command Selenium waits x milliseconds where x is the speed.", defaultValue="50"),
    @ConfigProperty(name = "browser.log.level", type = String.class, description = "The browser log level. One of debug, info, warn, error.", defaultValue = "error"),
    @ConfigProperty(name = "highlight.elements", type = boolean.class, description = "Activates or deactivates highlighting of web GUI elements currently being used.", defaultValue = "true"),
    @ConfigProperty(name = "pause.between.retries", type = int.class, description = "If execution of an action fails, Selenium has to pause until it retries to execute this action again. This value specifies how long the program will pause, in milliseconds.", defaultValue = "100"),
    @ConfigProperty(name = "locator.prefix", type = String.class, description = "This value specifies the default prefix of an ID locator. With the help of this prefix, Selenium can be influenced how it locates elements. Selenium provides several mechanisms for that like regular expressions etc.", defaultValue = "css=[id$=\""),
    @ConfigProperty(name = "locator.suffix", type = String.class, description = "The matching suffix to the ID locator prefix.", defaultValue = "\"]"),
    @ConfigProperty(name = "screenshot.attachment.extension", type = String.class, description = "The file extension to use for screenshot attachments.", defaultValue = "png"),
    @ConfigProperty(name = "page.source.attachment.extension", type = String.class, description = "The file extension to use for HTML page source attachments.", defaultValue = "html"),
    @ConfigProperty(name = "task.start.timeout", type = int.class, description = "The time the Selenium service waits for an activity to start, in milliseconds.", defaultValue = "2000"),
    @ConfigProperty(name = "task.completion.timeout", type = int.class, description = "The time the Selenium service waits for an activity to finish, in milliseconds.", defaultValue = "45000"),
    @ConfigProperty(name = "task.polling.interval", type = int.class, description = "The polling interval for checking task states, in milliseconds.", defaultValue = "1000")
})
public abstract class AbstractSeleniumService extends AbstractConfigurableAludraService implements AludraWebGUI {

    protected SeleniumWrapperConfiguration configuration;

    @Override
    public final String getPropertiesBaseName() {
        return "seleniumWrapper";
    }

    @Override
    public final void configure(Preferences preferences) {
        configuration = new SeleniumWrapperConfiguration(preferences);
    }

}
