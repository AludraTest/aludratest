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
import org.databene.commons.NullSafeComparator;

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
        FFAddress that = (FFAddress) obj;
        return (NullSafeComparator.equals(this.city, that.city)
                && Double.doubleToLongBits(this.num) == Double.doubleToLongBits(that.num) && this.rowType == that.rowType && NullSafeComparator
                .equals(this.street, that.street));
    }

    /** Creates a String representation of the object. */
    @Override
    public String toString() {
        return street + ", " + city + ", " + num;
    }

}
