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
package org.test.testclasses.service;

import org.aludratest.config.ConfigProperties;
import org.aludratest.config.ConfigProperty;

@ConfigProperties({ @ConfigProperty(name = "testvalue1", description = "A test value", type = int.class, defaultValue = "1"),
    @ConfigProperty(name = "testvalue2", description = "Another test value", type = boolean.class, defaultValue = "true"),
    @ConfigProperty(name = "testvalue3", description = "Another test value", type = String.class) })
public interface ComplexComponent {

}
