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
 * Interface for configuration Preferences objects which can be modified. Instances of classes implementing this interface are
 * passed to {@link Configurable#fillDefaults(MutablePreferences)} to create the default configuration before overwriting it with
 * configuration from the various documented configuration locations.
 * 
 * @author falbrech
 * 
 */
public interface MutablePreferences extends Preferences {

    /**
     * Sets the given configuration key's value to the given String value.
     * 
     * @param key
     *            Configuration key to set the value of.
     * 
     * @param value
     *            Value to set the configuration key's value to. <code>null</code> is allowed and does <b>not</b> remove the key
     *            from this configuration node.
     */
    public void setValue(String key, String value);

    /**
     * Sets the given configuration key's value to the given boolean value.
     * 
     * @param key
     *            Configuration key to set the value of.
     * 
     * @param value
     *            Value to set the configuration key's value to.
     */
    public void setValue(String key, boolean value);

    /**
     * Sets the given configuration key's value to the given int value.
     * 
     * @param key
     *            Configuration key to set the value of.
     * 
     * @param value
     *            Value to set the configuration key's value to.
     */
    public void setValue(String key, int value);

    /**
     * Sets the given configuration key's value to the given double value.
     * 
     * @param key
     *            Configuration key to set the value of.
     * 
     * @param value
     *            Value to set the configuration key's value to.
     */
    public void setValue(String key, double value);

    /**
     * Sets the given configuration key's value to the given float value.
     * 
     * @param key
     *            Configuration key to set the value of.
     * 
     * @param value
     *            Value to set the configuration key's value to.
     */
    public void setValue(String key, float value);

    /**
     * Sets the given configuration key's value to the given char value.
     * 
     * @param key
     *            Configuration key to set the value of.
     * 
     * @param value
     *            Value to set the configuration key's value to.
     */
    public void setValue(String key, char value);

    /**
     * Creates a child configuration node with the given name. If such a node already exists, it is returned.
     * 
     * @param name
     *            Name of the configuration node.
     * 
     * @return The newly created child configuration node, or the already existing child configuration node of the same name.
     */
    public MutablePreferences createChildNode(String name);

    /**
     * Removes the child configuration node with the given name. If no such node exists, no action is performed.
     * 
     * @param name
     *            Name of the configuration node to remove.
     */
    public void removeChildNode(String name);

    /**
     * Removes the configuration key-value pair with the given key from this configuration node. If no such key exists, no action
     * is performed.
     * 
     * @param key
     *            Key of the key-value pair to remove from this configuration node.
     */
    public void removeKey(String key);

}
