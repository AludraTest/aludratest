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

import java.util.HashMap;
import java.util.Map;

import org.aludratest.config.MutablePreferences;
import org.aludratest.config.Preferences;

/**
 * Default implementation of the {@link MutablePreferences} interface.
 * 
 * @author falbrech
 * 
 */
public class SimplePreferences extends AbstractPreferences implements MutablePreferences {

    private Map<String, String> values = new HashMap<String, String>();

    // lazy initialization as most modules will not have children
    private Map<String, SimplePreferences> children;

    @Override
    public String internalGetStringValue(String key) {
        return values.get(key);
    }

    @Override
    public String[] getKeyNames() {
        return values.keySet().toArray(new String[0]);
    }

    @Override
    public Preferences getChildNode(String name) {
        if (children == null) {
            return null;
        }

        return children.get(name);
    }

    @Override
    public String[] getChildNodeNames() {
        if (children == null) {
            return new String[0];
        }

        return children.keySet().toArray(new String[0]);
    }

    @Override
    public void setValue(String key, String value) {
        if (key.contains("/")) {
            String childName = key.substring(0, key.indexOf('/'));
            MutablePreferences child = createChildNode(childName);
            child.setValue(key.substring(key.indexOf('/') + 1), value);
        }
        else {
            values.put(key, value);
        }
    }

    @Override
    public void setValue(String key, boolean value) {
        setValue(key, "" + value);
    }

    @Override
    public void setValue(String key, int value) {
        setValue(key, "" + value);
    }

    @Override
    public void setValue(String key, double value) {
        setValue(key, "" + value);
    }

    @Override
    public void setValue(String key, float value) {
        setValue(key, "" + value);
    }

    @Override
    public void setValue(String key, char value) {
        setValue(key, "" + value);
    }

    @Override
    public MutablePreferences createChildNode(String name) {
        if (name == null || "".equals(name)) {
            throw new IllegalArgumentException("Name must be a non-empty string");
        }

        if (children == null) {
            children = new HashMap<String, SimplePreferences>();
        }

        if (children.containsKey(name)) {
            return children.get(name);
        }

        SimplePreferences prefs = new SimplePreferences();
        children.put(name, prefs);

        return prefs;
    }

    @Override
    public void removeChildNode(String name) {
        if (children != null) {
            children.remove(name);
        }
    }

    @Override
    public void removeKey(String key) {
        values.remove(key);
    }

}
