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

import org.aludratest.content.flat.data.FlatFileBeanData;
import org.aludratest.service.flatfile.FlatFileService;

/**
 * Data class for testing the {@link FlatFileService}
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public class FFPerson extends FlatFileBeanData {

    /** public name attribute */
    public String name;

    /** public age attribute */
    public int age;

    /** public birthDate attribute */
    public Date birthDate;

    /** public pet attribute */
    public FFPet pet;

    /** public default constructor */
    public FFPerson() {
        this(null, 0, null, null);
    }

    /** Constructor with complete attribute initialization. */
    public FFPerson(String name, int age, Date birthDate, FFPet pet) {
        this.name = name;
        this.age = age;
        this.birthDate = birthDate;
        this.pet = pet;
    }

}
