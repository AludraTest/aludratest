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
package org.aludratest.content.separated;

import org.aludratest.content.separated.SeparatedColumn;
import org.aludratest.content.separated.SeparatedContent;
import org.aludratest.content.separated.data.SeparatedFileBeanData;
import org.databene.commons.NullSafeComparator;

/**
 * Sample {@link SeparatedFileBeanData} class for testing 
 * the {@link SeparatedContent} functionality.
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public class SepPersonData extends SeparatedFileBeanData {

    @SeparatedColumn(columnIndex = 1)
    public String name;

    @SeparatedColumn(columnIndex = 2)
    public String age;

    public SepPersonData() {
        this(null, null);
    }

    public SepPersonData(String name, String age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public int hashCode() {
        return ((age == null) ? 0 : age.hashCode()) * 31 + ((name == null) ? 0 : name.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        SepPersonData that = (SepPersonData) obj;
        return NullSafeComparator.equals(this.name, that.name) && 
                NullSafeComparator.equals(this.age, that.age);
    }

    @Override
    public String toString() {
        return name + "(age=" + age + ")";
    }

}
