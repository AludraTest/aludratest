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
package org.aludratest.util.data.helper;

import org.aludratest.config.AludraTestConfig;
import org.aludratest.data.DataConfiguration;
import org.aludratest.service.AludraServiceManager;

public class DataMarkerCheck {

    private static AludraTestConfig aludraConfiguration;

    private static DataConfiguration dataConfiguration;

    /** Private constructor of utility class preventing instantiation by other classes */
    private DataMarkerCheck() {
    }

    public static void init(AludraServiceManager serviceManager) {
        dataConfiguration = serviceManager.newImplementorInstance(DataConfiguration.class);
        aludraConfiguration = serviceManager.newImplementorInstance(AludraTestConfig.class);
    }

    private static DataConfiguration getDataConfiguration() {
        if (dataConfiguration == null) {
            throw new IllegalStateException("DataMarkerCheck class has not been initialized yet");
        }
        return dataConfiguration;
    }

    public static boolean isNull(String string) {
        if ((string == null) || (string.length() == 0) || (string.equals(getDataConfiguration().getNullMarker()))) {
            return true;
        }
        return false;
    }

    public static String convertIfEmpty(String string) {
        if (string == null) {
            return string;
        }
        if (string.equals(getDataConfiguration().getEmptyMarker())) {
            return "";
        }
        return string;
    }

    public static String[] convertIfEmpty(String[] strings) {
        if (strings == null) {
            return strings;
        }
        for (int i = 0; i < strings.length; i++) {
            if (strings[i].equals(getDataConfiguration().getEmptyMarker())) {
                strings[i] = "";
            }
        }
        return strings;
    }

    public static double getNumericTolerance() {
        getDataConfiguration(); // to check init state
        return aludraConfiguration.getNumericTolerance();
    }

}
