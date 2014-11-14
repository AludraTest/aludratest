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

/**
 * Associates a flat file row prefix with a bean class.
 * @author Volker Bergmann
 */
public class PrefixRowType extends AbstractRowType {

    /** The prefix */
    private String prefix;

    /** Constructor 
     *  @param beanClass the associated FlatFileBean class 
     *  @param prefix the line prefix associated with the FlatFileBean */
    public PrefixRowType(Class<?> beanClass, String prefix) {
        super(beanClass);
        this.prefix = prefix;
    }

    /** Returns the associated bean class if the prefix matches, otherwise null. */
    public Class<?> beanClassFor(String line) {
        return (line.startsWith(prefix) ? beanClass : null);
    }

}
