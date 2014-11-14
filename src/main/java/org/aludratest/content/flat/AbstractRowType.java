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
package org.aludratest.content.flat;

import org.aludratest.content.flat.data.RowTypeData;
import org.databene.commons.Assert;

/**
 * Abstract implementation of a {@link RowTypeData}, 
 * which holds the reference to a Java class and 
 * leaves the row type recognizion unimplemented.
 * @author Volker Bergmann
 */
public abstract class AbstractRowType extends RowTypeData {

    /** The related Java class */
    protected final Class<?> beanClass;

    /** Constructor requiring the related Java class. 
     *  @param beanClass the FlatFileBean class associated with this row type instance */
    public AbstractRowType(Class<?> beanClass) {
        Assert.notNull(beanClass, "beanClass");
        this.beanClass = beanClass;
    }

}
