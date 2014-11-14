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
 * The basic interface for configuration data. Instances of implementing classes are passed to {@link Configurable} objects for
 * configuration purposes.<br>
 * The Configurable can invoke any methods declared in this interface to retrieve its required configuration, without having to
 * deal with its storage location, overwrite issues etc. <br>
 * Preferences based configuration is stored in a tree form, with nodes containing configuration key-value pairs as well as named
 * subnodes. Imagine a configuration file like this:
 * 
 * <pre>
 * mykey=valueXY
 * myfont/name=Arial
 * myfont/size=12
 * myfont/color=#ff0000
 * </pre>
 * 
 * Then you could access e.g. the <code>myfont/size</code> property using
 * <code>prefs.<b>getChildNode("myfont")</b>.getFloatValue("size");</code> <br>
 * You could even pass the <code>prefs.getChildNode("myfont")</code> node to e.g. an internal configuration method. <br>
 * <br>
 * 
 * One Preferences instance is <b>always</b> only used for one Configurable type, so you will not be able to retrieve the
 * configuration of other Configurable classes via this interface. <br>
 * <br>
 * 
 * <b>This interface is not intended to be implemented outside AludraTest framework.</b>
 * 
 * @author falbrech
 * 
 */
public interface Preferences {

    /**
     * Returns the String representation of the given configuration key's value.
     * 
     * @param key
     *            Configuration key to retrieve the value of.
     * 
     * @return The value of the configuration key, as a String. <code>null</code> is returned if no value is stored.
     */
    public String getStringValue(String key);

    /**
     * Returns the String representation of the given configuration key's value, or the given default value when no value is
     * stored for this key, or the stored value is <code>null</code>.
     * 
     * @param key
     *            Configuration key to retrieve the value of.
     * @param defaultValue
     *            The default value to return if no value is stored for the given configuration key, or the stored value is
     *            <code>null</code>.
     * @return The value of the configuration key, as a String, or the default value.
     */
    public String getStringValue(String key, String defaultValue);

    /**
     * Returns the int representation of the given configuration key's value.
     * 
     * @param key
     *            Configuration key to retrieve the value of.
     * 
     * @return The value of the configuration key, as an int. <code>0</code> is returned if no value is stored, or the stored
     *         String is <code>null</code>.
     */
    public int getIntValue(String key);

    /**
     * Returns the int representation of the given configuration key's value, or the given default value when no value is stored
     * for this key, or the stored String form of the value is <code>null</code>.
     * 
     * @param key
     *            Configuration key to retrieve the value of.
     * @param defaultValue
     *            The default value to return if no value is stored for the given configuration key, or if the stored String form
     *            of the value is <code>null</code>.
     * @return The value of the configuration key, as an int, or the default value.
     */
    public int getIntValue(String key, int defaultValue);

    /**
     * Returns the boolean representation of the given configuration key's value.
     * 
     * @param key
     *            Configuration key to retrieve the value of.
     * 
     * @return The value of the configuration key, as a boolean. <code>false</code> is returned if no value is stored, or the
     *         stored String is <code>null</code>.
     */
    public boolean getBooleanValue(String key);

    /**
     * Returns the boolean representation of the given configuration key's value, or the given default value when no value is
     * stored for this key, or the stored String form of the value is <code>null</code>.
     * 
     * @param key
     *            Configuration key to retrieve the value of.
     * @param defaultValue
     *            The default value to return if no value is stored for the given configuration key, or if the stored String form
     *            of the value is <code>null</code>.
     * @return The value of the configuration key, as a boolean, or the default value.
     */
    public boolean getBooleanValue(String key, boolean defaultValue);

    /**
     * Returns the float representation of the given configuration key's value.
     * 
     * @param key
     *            Configuration key to retrieve the value of.
     * 
     * @return The value of the configuration key, as a float. <code>0</code> is returned if no value is stored, or the stored
     *         String is <code>null</code>.
     */
    public float getFloatValue(String key);

    /**
     * Returns the float representation of the given configuration key's value, or the given default value when no value is stored
     * for this key, or the stored String form of the value is <code>null</code>.
     * 
     * @param key
     *            Configuration key to retrieve the value of.
     * @param defaultValue
     *            The default value to return if no value is stored for the given configuration key, or if the stored String form
     *            of the value is <code>null</code>.
     * @return The value of the configuration key, as a float, or the default value.
     */
    public float getFloatValue(String key, float defaultValue);

    /**
     * Returns the double representation of the given configuration key's value.
     * 
     * @param key
     *            Configuration key to retrieve the value of.
     * 
     * @return The value of the configuration key, as a double. <code>0</code> is returned if no value is stored, or the stored
     *         String is <code>null</code>.
     */
    public double getDoubleValue(String key);

    /**
     * Returns the double representation of the given configuration key's value, or the given default value when no value is
     * stored for this key, or the stored String form of the value is <code>null</code>.
     * 
     * @param key
     *            Configuration key to retrieve the value of.
     * @param defaultValue
     *            The default value to return if no value is stored for the given configuration key, or if the stored String form
     *            of the value is <code>null</code>.
     * @return The value of the configuration key, as a double, or the default value.
     */
    public double getDoubleValue(String key, double defaultValue);

    /**
     * Returns the char representation of the given configuration key's value.
     * 
     * @param key
     *            Configuration key to retrieve the value of.
     * 
     * @return The value of the configuration key, as a char. <code>'\0'</code> is returned if no value is stored, or the stored
     *         String is <code>null</code>. If a longer String is stored, only the first character is returned.
     */
    public char getCharValue(String key);

    /**
     * Returns the char representation of the given configuration key's value, or the given default value when no value is stored
     * for this key, or the stored String form of the value is <code>null</code>.
     * 
     * @param key
     *            Configuration key to retrieve the value of.
     * @param defaultValue
     *            The default value to return if no value is stored for the given configuration key, or if the stored String form
     *            of the value is <code>null</code>.
     * @return The value of the configuration key, as a char, or the default value.
     */
    public char getCharValue(String key, char defaultValue);

    /**
     * Returns the available configuration key names on this Preferences node.
     * 
     * @return The available configuration key names on this Preferences node, possibly an empty array, but never
     *         <code>null</code>.
     */
    public String[] getKeyNames();

    /**
     * Returns the configuration child node with the given name, or <code>null</code> if no such node exists on this configuration
     * node.
     * 
     * @param name
     *            Name of the configuration child node
     * 
     * @return The configuration child node with the given name, or <code>null</code> if no such node exists on this configuration
     *         node.
     */
    public Preferences getChildNode(String name);

    /**
     * Returns the names of the available configuration child nodes on this configuration node.
     * 
     * @return The names of the available configuration child nodes on this configuration node, possibly an empty Array, but never
     *         <code>null</code>.
     */
    public String[] getChildNodeNames();


}
