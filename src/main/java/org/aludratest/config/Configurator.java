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
package org.aludratest.config;

/** Interface for objects being able to configure Configurable objects. As this has to work without AludraTest Service Manager
 * running, it is <b>not</b> a service or component interface. Currently, treat it as an AludraTest internal interface. <br>
 * <b>Clients must not implement this interface.</b>
 * 
 * @author falbrech */
public interface Configurator {

    public static final String ROLE = Configurator.class.getName();

    public void configure(Configurable configurable);

    public void configure(String instanceName, Configurable configurable);

}
