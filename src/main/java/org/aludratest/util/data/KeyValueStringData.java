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
package org.aludratest.util.data;

import org.aludratest.dict.Data;

/** Data class that wraps a pair of key and value of type string.
 * @author Volker Bergmann */
public class KeyValueStringData extends Data {

    private String key;
    private String value;

    /** Default constructor. */
    public KeyValueStringData() {
        this(null, null);
    }

    /** Full constructor.
     * @param key the key
     * @param value the value */
    public KeyValueStringData(String key, String value) {
        this.key = key;
        this.value = value;
    }

    /** Returns the key.
     * @return the {@link #key} */
    public String getKey() {
        return key;
    }

    /** Sets the {@link #key}.
     * @param key the key to set */
    public void setKey(String key) {
        this.key = key;
    }

    /** Returns the value.
     * @return the {@link #value} */
    public String getValue() {
        return value;
    }

    /** Sets the value.
     * @param value the {@link #value} to set */
    public void setValue(String value) {
        this.value = value;
    }

}
