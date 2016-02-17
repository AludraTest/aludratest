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

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.databene.commons.ParseUtil;
import org.databene.commons.StringUtil;

/**
 * Provides data related utility methods.
 * @author Volker Bergmann
 */
public class DataUtil {

    /** {@link Charset} instance for the UTF-8 character encoding */
    public static final Charset UTF_8 = Charset.forName("UTF-8");

    /** Private constructor for preventing instantiation of utility class. */
    private DataUtil() {
    }

    /** Parses a boolean value in a string ("true" or "false").
     * @param text the text to parse
     * @return <code>true</code> or <code>false</code> */
    public static boolean parseBoolean(String text) {
        return ParseUtil.parseBoolean(text);
    }

    /**
     * Compares two string arrays
     * @param expectedStrings an array of the expected strings
     * @param actualStrings an array of the actual strings
     * @return a message of the differences or an empty string if the arrays are equal
     */
    public static String expectEqualArrays(String[] expectedStrings, String[] actualStrings) {
        // ENHANCE apply ArrayComparator to recognize missing or added elements
        if (actualStrings == null || expectedStrings == null) {
            return "No Values found for comparison. ";
        }
        if (!Arrays.equals(expectedStrings, actualStrings)) {
            return "Arrays do not match. Expected " + Arrays.toString(expectedStrings) + ", " +
                    "but found " + Arrays.toString(actualStrings) + ". ";
        }
        return "";
    }

    /** Compares two string arrays, ignoring the order of elements.
     * @param expectedStrings an array of the expected strings
     * @param actualStrings an array of the actual strings
     * @return a message of the differences or an empty string if the arrays are equal */
    public static String expectEqualArraysIgnoreOrder(String[] expectedStrings, String[] actualStrings) {
        if (actualStrings == null || expectedStrings == null) {
            return "No Values found for comparison. ";
        }

        List<String> lsExpected = Arrays.asList(expectedStrings);
        List<String> lsActual = Arrays.asList(actualStrings);

        Set<String> missing = new HashSet<String>(lsExpected);
        missing.removeAll(lsActual);
        Set<String> sflous = new HashSet<String>(lsActual);
        sflous.removeAll(lsExpected);

        StringBuilder sb = new StringBuilder();
        if (!missing.isEmpty()) {
            sb.append("Following items were missing: " + missing);
        }
        if (!sflous.isEmpty()) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append("Following items were found, but not expected: " + sflous);
        }

        return sb.toString();
    }

    /** Verifies that each expected string is found in the array of actual strings.
     * @param expectedStrings
     * @param actualStrings
     * @return a String listing all strings which were NOT found in the array of actual strings. */
    public static String containsStrings(String[] expectedStrings, String[] actualStrings) {
        Set<String> mismatches = new HashSet<String>(Arrays.asList(expectedStrings));
        mismatches.removeAll(Arrays.asList(actualStrings));
        return StringUtil.concat(' ', mismatches.toArray(new String[0]));
    }

    /** Verifies that the expected string is found in the array of actual strings.
     * @param expectedString
     * @param actualStrings
     * @return The expected String, if not found in the array, or an empty string, if found. */
    public static String containsString(String expectedString, String[] actualStrings) {
        // elements were converted to uppercase before, but it is nowhere stated that this would be expected,
        // and containsStrings() also did not convert to uppercase.
        return Arrays.asList(actualStrings).contains(expectedString) ? "" : expectedString;
    }

}
