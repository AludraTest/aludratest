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
package org.aludratest.service.edifactfile.data;

import org.aludratest.dict.Data;

/**
 * Defines a difference in one or more locations 
 * which are represented by an XPath expression.
 * @author Volker Bergmann
 */
public class LocalDiffTypeData extends Data {

    private String type;
    private String xpath;

    /** Refers to any change art any location */
    public LocalDiffTypeData() {
        this(null, null);
    }

    /**
     * Refers to a certain type of difference at (a) given location(s)
     * @param type
     * @param xpath
     */
    public LocalDiffTypeData(String type, String xpath) {
        this.type = type;
        this.xpath = xpath;
    }

    /** @return the type of the difference */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of the difference.
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /** @return an XPath expression of the related location(s) */
    public String getXpath() {
        return xpath;
    }

    /**
     * Sets an XPath expression of the related location(s)
     * @param xpath an XPath expression of the related location(s)
     */
    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

}
