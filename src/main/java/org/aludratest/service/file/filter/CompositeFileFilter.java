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
package org.aludratest.service.file.filter;

import java.util.Arrays;

import org.aludratest.service.file.FileFilter;

/**
 * Abstract {@link FileFilter} implementation which manages other filter components and 
 * serves as parent for filter classes which combine the component results individually.
 * @author Volker Bergmann
 */
public abstract class CompositeFileFilter implements FileFilter {

    /** The components. */
    protected FileFilter[] components;

    /** Constructor 
     *  @param components the components to combine */
    public CompositeFileFilter(FileFilter... components) {
        this.components = components;
    }

    /** Creates a string representation of the filter. */
    @Override
    public String toString() {
        return getClass().getSimpleName() + '[' + Arrays.toString(components) + ']';
    }

}
