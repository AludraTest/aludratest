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
package org.aludratest.content.separated.data;

import org.aludratest.dict.Data;

/**
 * Wraps a {@link SeparatedFileBeanData} object for separated file parsing.
 * @author Volker Bergmann
 * @param <E> 
 */
public class WrappedSeparatedData<E extends SeparatedFileBeanData> extends Data {

    private E value;

    /** Default constructor that sets the {@link #value} to null */
    public WrappedSeparatedData() {
        this(null);
    }

    /** Constructor that initializes the {@link #value} with the provided argument. 
     *  @param value the initialization value for the {@link #value} property */
    public WrappedSeparatedData(E value) {
        this.value = value;
    }

    /** @return the {@link #value} */
    public E getValue() {
        return value;
    }

    /** Sets the {@link #value} to the provided argument
     *  @param value the new value */
    public void setValue(E value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return (this.value != null ? this.value.toString() : "null");
    }
}
