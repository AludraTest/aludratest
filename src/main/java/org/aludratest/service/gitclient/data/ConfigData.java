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
package org.aludratest.service.gitclient.data;

import org.aludratest.service.gitclient.GitClient;

/** Wraps data for the invocation of the {@link GitClient}'s version method.
 * @see GitClient#version(ConfigData)
 * @author Volker Bergmann */
public class ConfigData extends AbstractGitData {

    private String key;
    private String value;
    private String valueRegex;

    // constructors ------------------------------------------------------------

    /** Public default constructor. */
    public ConfigData() {
        this(null, null);
    }

    /** Key-value constructor.
     * @param key the key to set
     * @param value the value to assign to the key */
    public ConfigData(String key, String value) {
        this(key, value, null);
    }

    /** Full constructor.
     * @param key the key to set
     * @param value the value to assign to the key
     * @param valueRegex the value regex to set */
    public ConfigData(String key, String value, String valueRegex) {
        this.key = key;
        this.value = value;
        this.valueRegex = valueRegex;
    }

    // properties --------------------------------------------------------------

    /** Returns the key.
     * @return the {@link #key} */
    public String getKey() {
        return key;
    }

    /** Sets the key.
     * @param key the key to set */
    public void setKey(String key) {
        this.key = key;
    }

    /** Returns the value.
     * @return the value */
    public String getValue() {
        return value;
    }

    /** Sets the value.
     * @param value the value to set */
    public void setValue(String value) {
        this.value = value;
    }

    /** Returns the value regex.
     * @return the value regex */
    public String getValueRegex() {
        return valueRegex;
    }

    /** Sets the value regex.
     * @param valueRegex the value regex to set */
    public void setValueRegex(String valueRegex) {
        this.valueRegex = valueRegex;
    }

    // java-lang.Object overrides ----------------------------------------------

    @Override
    public String toString() {
        return "git config " + key + (value != null ? " " + value : "");
    }

}
