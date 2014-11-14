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
import org.databene.commons.NullSafeComparator;

/**
 * FlatFileBean for testing the {@link FlatFileService}
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public class FFAddressBean {

    /** 1-character row type flag */
    @FlatFileColumn(startIndex = 1, format = "1")
    private char rowType;

    /** street attribute */
    @FlatFileColumn(startIndex = 2, format = "20")
    private String street;

    /** city attribute */
    @FlatFileColumn(startIndex = 22, format = "20")
    private String city;

    /** num attribute */
    @FlatFileColumn(startIndex = 42, format = "N0000.00")
    private double num;

    /** Public default constructor. */
    public FFAddressBean() {
        this(null, null, 0);
    }

    /** Constructor requiring a value for each attribute */
    public FFAddressBean(String street, String city, double num) {
        this.rowType = 'A';
        this.street = street;
        this.city = city;
        this.num = num;
    }

    /** Returns the {@link #rowType} */
    public char getRowType() {
        return rowType;
    }

    /** Sets the {@link #rowType} */
    public void setRowType(char rowType) {
        this.rowType = rowType;
    }

    /** Returns the {@link #street} */
    public String getStreet() {
        return street;
    }

    /** Sets the {@link #street} */
    public void setStreet(String street) {
        this.street = street;
    }

    /** Returns the {@link #city} */
    public String getCity() {
        return city;
    }

    /** Sets the {@link #city} */
    public void setCity(String city) {
        this.city = city;
    }

    /** Returns the {@link #num} */
    public double getNum() {
        return num;
    }

    /** Sets the {@link #num} */
    public void setNum(double num) {
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
        FFAddressBean that = (FFAddressBean) obj;
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
