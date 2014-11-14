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
package org.aludratest.service.file.impl;

/** 
 * Provides line feed information for different operating systems. 
 * @author Volker Bergmann
 */
public enum Linefeed {

    /** The Windows line feed: &#92;r&#92;n */
    WINDOWS("\r\n"),

    /** The UNIX line feed: &#92;n */
    UNIX("\n");

    /** The characters used as line feed */
    private String chars;

    /** Constructor receiving the characters 
     *  used as line feed by the related OS. */
    private Linefeed(String chars) {
        this.chars = chars;
    }

    /** Returns the characters used as line feed 
     *  @return the characters used as line feed*/
    public String getChars() {
        return chars;
    }

}
