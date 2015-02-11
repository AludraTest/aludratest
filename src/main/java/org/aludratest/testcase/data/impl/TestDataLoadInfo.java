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
package org.aludratest.testcase.data.impl;

/**
 * Provides additional information for a test data record and consequential test data set.
 * @author Volker Bergmann
 */
public class TestDataLoadInfo {

    /** The info, either a {@link String} or a {@link Throwable}. */
    private Object info;
    private boolean ignored;

    /** Good case constructor porting a test message.
     *  @param infoText the text to store as info
     *  @param ignored a flag that indicates whether the related test shall be ignored */
    public TestDataLoadInfo(String infoText, boolean ignored) {
        this.info = infoText;
        this.ignored = ignored;
    }

    /**
     * Bad case constructor porting a Throwable for reporting an initialization error.
     * @param error {@link Throwable} that represents the occurred error
     */
    public TestDataLoadInfo(Throwable error) {
        this.info = error;
        this.ignored = false;
    }

    /** @return The {@link #info}*/
    public Object getInfo() {
        return info;
    }

    /** @return the value of the {@link #ignored} attribute. */
    public boolean isIgnored() {
        return ignored;
    }

}
