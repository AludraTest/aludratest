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
package org.aludratest.util;

import org.databene.commons.StringUtil;

/** Central class to access the "global variable" containing the AludraTest Environment name. Clients can as well call
 * {@link org.aludratest.AludraTest#getEnvironmentName()}, which delegates to this class.
 *
 * @author falbrech */
public final class EnvUtil {

    /** The name of the System Property which is checked for the environment name to use. */
    public static final String ENVIRONMENT_NAME_PROPERTY = "aludraTest.environment";

    private EnvUtil() {
    }

    /** Returns the name of the current environment. This is a central parameter of AludraTest and used to distinguish
     * configuration for different environments. Pass it e.g. via command line System Property:
     *
     * <pre>
     * -DaludraTest.environment=MYHOST
     * </pre>
     *
     * @return The name of the current environment. Defaults to <code>LOCAL</code> if none has been set. */
    public static String getEnvironmentName() {
        String propValue = System.getProperty(ENVIRONMENT_NAME_PROPERTY);
        if (!StringUtil.isEmpty(propValue)) {
            return propValue;
        }
        return "LOCAL";
    }

}
