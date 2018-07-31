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

/** Utility class containing frequently-used data marker check related methods. Internally uses AludraTest configuration elements,
 * so must be initialized before use. Framework or caller is responsible for calling {@link #init(AludraServiceManager)} before
 * invoking one of the utility methods.
 * 
 * @author falbrech */
public class DataMarkerCheck {

    private static AludraTestConfig aludraConfiguration;

    private static DataConfiguration dataConfiguration;

    /** Private constructor of utility class preventing instantiation by other classes */
    private DataMarkerCheck() {
    }

    /** Initializes the utility class.
     * 
     * @param serviceManager Service Manager to use to retrieve required AludraTest objects, e.g. configuration. */
    public static synchronized void init(AludraServiceManager serviceManager) {
        dataConfiguration = serviceManager.newImplementorInstance(DataConfiguration.class);
        aludraConfiguration = serviceManager.newImplementorInstance(AludraTestConfig.class);
    }

    private static DataConfiguration getDataConfiguration() {
        if (dataConfiguration == null) {
            throw new IllegalStateException("DataMarkerCheck class has not been initialized yet");
        }
        return dataConfiguration;
    }

    /** Checks if the given string is "logically" <code>null</code>, i.e. one of the following is <code>true</code>:
     * <ul>
     * <li>The string reference is <code>null</code></li>
     * <li>The string is empty</li>
     * <li>The string equals the configured NULL marker (default: <code>&lt;NULL&gt;</code>)</li>
     * </ul>
     * @param string String to check
     * @return <code>true</code> if the string is logically <code>null</code>, <code>false</code> otherwise. */
    public static boolean isNull(String string) {
        if ((string == null) || (string.length() == 0) || (string.equals(getDataConfiguration().getNullMarker()))) {
            return true;
        }
        return false;
    }

    /** Returns an empty string, if the given string is <code>null</code>, empty, or matches the configured EMPTY marker (default:
     * <code>&lt;EMPTY&gt;</code>).
     * @param string String to convert to empty String, or return unchanged if not empty.
     * @return An empty string, or the unchanged string. */
    public static String convertIfEmpty(String string) {
        if (string == null) {
            return string;
        }
        if (string.equals(getDataConfiguration().getEmptyMarker())) {
            return "";
        }
        return string;
    }

    /** Converts each string in the given array to an empty string, if it is <code>null</code>, empty, or matches the configured
     * EMPTY marker (default: <code>&lt;EMPTY&gt;</code>). <br>
     * The original array is modified and returned.
     * 
     * @param strings Array to convert empty elements of.
     * @return The same array object, with "logically" empty strings converted to empty strings. */
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

    /** Returns the configured numeric tolerance for number comparisons (e.g. by Validators).
     * 
     * @return The configured numeric tolerance for number comparisons (e.g. by Validators). */
    public static double getNumericTolerance() {
        getDataConfiguration(); // to check init state
        return aludraConfiguration.getNumericTolerance();
    }

}
