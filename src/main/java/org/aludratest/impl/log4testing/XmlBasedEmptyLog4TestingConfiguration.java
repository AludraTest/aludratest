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
package org.aludratest.impl.log4testing;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.aludratest.log4testing.config.AbbreviatorConfiguration;
import org.aludratest.log4testing.config.Log4TestingConfiguration;
import org.aludratest.log4testing.config.TestLogWriterConfiguration;

/** Configuration implementation which provides empty <code>log4testing.xml</code> configuration with empty objects. */
public class XmlBasedEmptyLog4TestingConfiguration implements Log4TestingConfiguration {

    @Override
    public AbbreviatorConfiguration getAbbreviatorConfiguration() {
        return new AbbreviatorConfiguration() {
            @Override
            public Map<String, String> getAbbreviations() {
                return Collections.emptyMap();
            }
        };
    }

    @Override
    public List<? extends TestLogWriterConfiguration> getTestLogWriterConfigurations() {
        return Collections.emptyList();
    }

    @Override
    public Properties getGlobalProperties() {
        return new Properties();
    }

}
