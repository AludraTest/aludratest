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

import org.aludratest.content.flat.data.RowTypeData;

/**
 * Simple row type for testing: It maps any flat file row to the same Java class.
 * @author Volker Bergmann
 */
public class FixedRowType extends RowTypeData {

    /** The class to map each flat file row to. */
    private Class<?> beanClass;

    /** Constructor expecting the {@link #beanClass} attribute. 
     *  @param beanClass the class to map each row to */
    public FixedRowType(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    /** @see RowTypeData#beanClassFor(String) */
    public Class<?> beanClassFor(String line) {
        return beanClass;
    }

}
