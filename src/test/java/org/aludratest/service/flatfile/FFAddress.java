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
package org.aludratest.service.flatfile;

import org.aludratest.content.flat.FlatFileColumn;
import org.aludratest.content.flat.data.FlatFileBeanData;
import org.aludratest.service.flatfile.FlatFileService;

/**
 * FlatFileBean for testing the {@link FlatFileService}
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public class FFAddress extends FlatFileBeanData {

    /** 1-character row type flag. */
    @FlatFileColumn(startIndex = 1, format = "1")
    public char rowType;

    /** street attribute. */
    @FlatFileColumn(startIndex = 2, format = "20")
    public String street;

    /** city attribute. */
    @FlatFileColumn(startIndex = 22, format = "20")
    public String city;

    /** num attribute. */
    @FlatFileColumn(startIndex = 42, format = "N0000.00")
    public double num;

    // constructors ----------------------------------------------------------

    /** Public default constructor. */
    public FFAddress() {
        this(null, null, 0);
    }

    /** Constructor initializing each attribute. */
    public FFAddress(String street, String city, double num) {
        this.rowType = 'A';
        this.street = street;
        this.city = city;
        this.num = num;
    }

    // java.lang.Object overrides ----------------------------------------------

    /** @see Object#equals(Object) */
    @Override
    public boolean equals(Object obj) {
        if (this == obj || obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FFAddress other = (FFAddress) obj;
        if (city == null) {
            if (other.city != null)
                return false;
        } else if (!city.equals(other.city))
            return false;
        if (Double.doubleToLongBits(num) != Double.doubleToLongBits(other.num))
            return false;
        if (rowType != other.rowType)
            return false;
        if (street == null) {
            if (other.street != null)
                return false;
        } else if (!street.equals(other.street))
            return false;
        return true;
    }

    /** Creates a String representation of the object. */
    @Override
    public String toString() {
        return street + ", " + city + ", " + num;
    }

}
