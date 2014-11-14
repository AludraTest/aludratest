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
package org.aludratest.impl.log4testing.configuration;

/**
 * Common interface for all classes that can be generically configured and verified.
 * @author Volker Bergmann
 */
public interface Validatable {
    /** This method is called by the log4testing framework after the configuration file 
     *  has been parsed and applied and requests the Writer instance to verify if it 
     *  was configured properly. 
     *  @throws ConfigurationError if the configuration is not alright. */
    void validate();
}
