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

/**
 * Helper class for Preferences consumers which can be used to have getter methods which validate the existence of a configuration
 * property when trying to retrieve it. Clients can wrap an existing Preferences object with this class and use its methods. <br>
 * Queried child nodes are automatically wrapped and returned as ValidatingPreferencesWrapper.
 * 
 * @author falbrech
 * 
 */
public final class ValidatingPreferencesWrapper implements Preferences {

    private Preferences delegate;

    /**
     * Creates a new ValidatingPreferencesWrapper object which wraps the given Preferences object.
     * 
     * @param delegate
     *            Preferences node to wrap.
     */
    public ValidatingPreferencesWrapper(Preferences delegate) {
        this.delegate = delegate;
    }

    /** Returns the String representation of the given configuration key's value. If the configuration key does not exist on this
     * Preferences node, or if its value is <code>null</code>, an {@link org.aludratest.exception.AutomationException} is thrown.
     * 
     * @param key Configuration key to retrieve the value of.
     * 
     * @return The value of the configuration key, as a String. */
    public String getRequiredStringValue(String key) {
        return assertNotNull(key, getStringValue(key));
    }

    /** Returns the int representation of the given configuration key's value. If the configuration key does not exist on this
     * Preferences node, or if its stored String value is <code>null</code>, an
     * {@link org.aludratest.exception.AutomationException} is thrown.
     * 
     * @param key Configuration key to retrieve the value of.
     * 
     * @return The value of the configuration key, as an int. */
    public int getRequiredIntValue(String key) {
        return assertNotNull(key, getIntValue(key));
    }

    /** Returns the double representation of the given configuration key's value. If the configuration key does not exist on this
     * Preferences node, or if its stored String value is <code>null</code>, an
     * {@link org.aludratest.exception.AutomationException} is thrown.
     * 
     * @param key Configuration key to retrieve the value of.
     * 
     * @return The value of the configuration key, as a double. */
    public double getRequiredDoubleValue(String key) {
        return assertNotNull(key, getDoubleValue(key));
    }

    /** Returns the float representation of the given configuration key's value. If the configuration key does not exist on this
     * Preferences node, or if its stored String value is <code>null</code>, an
     * {@link org.aludratest.exception.AutomationException} is thrown.
     * 
     * @param key Configuration key to retrieve the value of.
     * 
     * @return The value of the configuration key, as a float. */
    public float getRequiredFloatValue(String key) {
        return assertNotNull(key, getFloatValue(key));
    }

    /** Returns the boolean representation of the given configuration key's value. If the configuration key does not exist on this
     * Preferences node, or if its stored String value is <code>null</code>, an
     * {@link org.aludratest.exception.AutomationException} is thrown.
     * 
     * @param key Configuration key to retrieve the value of.
     * 
     * @return The value of the configuration key, as a boolean. */
    public boolean getRequiredBooleanValue(String key) {
        return assertNotNull(key, getBooleanValue(key));
    }

    /** Returns the char representation of the given configuration key's value. If the configuration key does not exist on this
     * Preferences node, or if its stored String value is <code>null</code>, an
     * {@link org.aludratest.exception.AutomationException} is thrown.
     * 
     * @param key Configuration key to retrieve the value of.
     * 
     * @return The value of the configuration key, as a char. */
    public char getRequiredCharValue(String key) {
        return assertNotNull(key, getCharValue(key));
    }

    @Override
    public String getStringValue(String key) {
        return delegate.getStringValue(key);
    }

    @Override
    public int getIntValue(String key) {
        return delegate.getIntValue(key);
    }

    @Override
    public boolean getBooleanValue(String key) {
        return delegate.getBooleanValue(key);
    }

    @Override
    public float getFloatValue(String key) {
        return delegate.getFloatValue(key);
    }

    @Override
    public double getDoubleValue(String key) {
        return delegate.getDoubleValue(key);
    }

    @Override
    public char getCharValue(String key) {
        return delegate.getCharValue(key);
    }

    @Override
    public String getStringValue(String key, String defaultValue) {
        return delegate.getStringValue(key, defaultValue);
    }

    @Override
    public int getIntValue(String key, int defaultValue) {
        return delegate.getIntValue(key, defaultValue);
    }

    @Override
    public boolean getBooleanValue(String key, boolean defaultValue) {
        return delegate.getBooleanValue(key, defaultValue);
    }

    @Override
    public float getFloatValue(String key, float defaultValue) {
        return delegate.getFloatValue(key, defaultValue);
    }

    @Override
    public double getDoubleValue(String key, double defaultValue) {
        return delegate.getDoubleValue(key, defaultValue);
    }

    @Override
    public char getCharValue(String key, char defaultValue) {
        return delegate.getCharValue(key, defaultValue);
    }

    @Override
    public String[] getKeyNames() {
        return delegate.getKeyNames();
    }

    @Override
    public ValidatingPreferencesWrapper getChildNode(String name) {
        return new ValidatingPreferencesWrapper(delegate.getChildNode(name));
    }

    @Override
    public String[] getChildNodeNames() {
        return delegate.getChildNodeNames();
    }

    /** Helper method which asserts that a property value is not null and not empty.
     * 
     * @param key the name of the property to check
     * @param value the property value to check
     * @return the value specified as parameter */
    private static <T> T assertNotNull(String key, T value) {
        if (value == null || "".equals(value)) {
            throw new ConfigurationException("Missing configuration property: " + key);
        }
        return value;
    }
}
