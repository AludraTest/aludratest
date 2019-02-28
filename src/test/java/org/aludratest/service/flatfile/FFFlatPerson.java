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

import java.util.Date;

import org.aludratest.content.flat.FlatFileColumn;
import org.aludratest.content.flat.data.FlatFileBeanData;
import org.databene.commons.Formatter;

/**
 * FlatFileBean for testing the {@link FlatFileService}
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public class FFFlatPerson extends FlatFileBeanData {

    /** 1-character row type flag */
    @FlatFileColumn(startIndex = 1, format = "1")
    public char rowType;

    /** name column: 20 characters */
    @FlatFileColumn(startIndex = 2, format = "20")
    public String name;

    /** age column: 3 digits, right-aligned, padded with blanks */
    @FlatFileColumn(startIndex = 22, format = "3r0")
    public int age;

    /** birthDate column: Date in format 'yyyyMMdd' */
    @FlatFileColumn(startIndex = 25, format = "DyyyyMMdd")
    public Date birthDate;

    /** petName column: 8 characters, left-aligned */
    @FlatFileColumn(startIndex = 33, format = "8")
    public String petName;

    // constructors ------------------------------------------------------------

    /** Public default constructor. */
    public FFFlatPerson() {
        this(null, 0, null, null);
    }

    /** Constructor which initializes all attributes.
     * @param name String
     * @param age integer
     * @param birthDate Date
     * @param petName String */
    public FFFlatPerson(String name, int age, Date birthDate, String petName) {
        this.rowType = 'P';
        this.name = name;
        this.age = age;
        this.birthDate = birthDate;
        this.petName = petName;
    }

    // java.lang.Object overrides ----------------------------------------------

    /** Compares this object with another for equality. */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        FFFlatPerson other = (FFFlatPerson) obj;
        if (age != other.age)
            return false;
        if (birthDate == null) {
            if (other.birthDate != null)
                return false;
        } else if (!birthDate.equals(other.birthDate))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (petName == null) {
            if (other.petName != null)
                return false;
        } else if (!petName.equals(other.petName))
            return false;
        if (rowType != other.rowType)
            return false;
        return true;
    }

    /** Creates a String representation of the instance. */
    @Override
    public String toString() {
        return name + ", " + age + ", " + Formatter.format(birthDate) + ", " + petName;
    }

}
