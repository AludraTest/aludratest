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

/** Data class for wrapping an integer.
 * @author Volker Bergmann */
public class IntData extends Data {

    private int value;

    /** Public default constructor. */
    public IntData() {
        this(0);
    }

    /** Full constructor.
     * @param value the {@link #value} to set */
    public IntData(int value) {
        this.value = value;
    }

    /** @return the {@link #value} */
    public int getValue() {
        return value;
    }

}
