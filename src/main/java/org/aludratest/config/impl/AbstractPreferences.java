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
package org.aludratest.config.impl;

import org.aludratest.config.Preferences;

/**
 * Abstract base implementation of the {@link Preferences} interface. All getters are implemented and perform conversions from
 * String to required type. The String getter deals with complex key names (containing slashes for subnode access), with variable
 * references, and eventually redirects to an abstract method {@link #internalGetStringValue(String)}, which only has to deal with
 * a given configuration key simple name.
 * 
 * @author falbrech
 * 
 */
public abstract class AbstractPreferences implements Preferences {

    /**
     * Performs the actual task of retrieving the plain stored value for a given key on this Preferences node.
     * 
     * @param key
     *            Configuration key to retrieve the value of.
     * 
     * @return The plain stored String for the given configuration key.
     */
    protected abstract String internalGetStringValue(String key);

    @Override
    public final String getStringValue(String key) {
        if (key.contains("/")) {
            String subnode = key.substring(0, key.indexOf('/'));
            String remainder = key.substring(key.indexOf('/') + 1);
            if ("".equals(remainder)) {
                return null;
            }
            Preferences child = getChildNode(subnode);
            if (child == null) {
                return null;
            }
            return child.getStringValue(remainder);
        }

        String value = internalGetStringValue(key);
        if (value != null) {
            value = resolveVariables(value);
        }
        return value;
    }

    @Override
    public final int getIntValue(String key, int defaultValue) {
        String val = getStringValue(key);
        if (val == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(val);
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @Override
    public final boolean getBooleanValue(String key, boolean defaultValue) {
        String val = getStringValue(key);
        if (val == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(val);
    }

    @Override
    public final float getFloatValue(String key, float defaultValue) {
        String val = getStringValue(key);
        if (val == null) {
            return defaultValue;
        }
        try {
            return Float.parseFloat(val);
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @Override
    public final double getDoubleValue(String key, double defaultValue) {
        String val = getStringValue(key);
        if (val == null) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(val);
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @Override
    public final char getCharValue(String key, char defaultValue) {
        String val = getStringValue(key);
        if (val == null || val.length() == 0) {
            return defaultValue;
        }
        return val.charAt(0);
    }

    @Override
    public String getStringValue(String key, String defaultValue) {
        String val = getStringValue(key);
        return val == null ? defaultValue : val;
    }

    @Override
    public int getIntValue(String key) {
        return getIntValue(key, 0);
    }

    @Override
    public float getFloatValue(String key) {
        return getFloatValue(key, 0);
    }

    @Override
    public boolean getBooleanValue(String key) {
        return getBooleanValue(key, false);
    }

    @Override
    public char getCharValue(String key) {
        return getCharValue(key, '\0');
    }

    @Override
    public double getDoubleValue(String key) {
        return getDoubleValue(key, 0);
    }

    private static String resolveVariables(String template) {
        if (template == null) {
            return null;
        }
        String result = template;
        int varStartIndex;
        while ((varStartIndex = result.indexOf("${")) >= 0) {
            int endIndex = result.indexOf('}', varStartIndex + 2);
            String propKey = result.substring(varStartIndex + 2, endIndex);
            result = result.substring(0, varStartIndex) + System.getProperty(propKey) + result.substring(endIndex + 1);
        }
        return result;
    }

}
