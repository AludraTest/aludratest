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

import org.aludratest.service.file.FileFilter;
import org.aludratest.service.file.FileInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link FileFilter} implementation which combines several other filters 
 * and requires each one to match a given file for accepting it.
 * @author Volker Bergmann
 */
public class AndFileFilter extends CompositeFileFilter {

    /** The logger of the class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AndFileFilter.class);

    /** Constructor 
     *  @param components the components to combine */
    public AndFileFilter(FileFilter... components) {
        super(components);
    }

    /** Accepts a file if each of the filter components does. */
    @Override
    public boolean accept(FileInfo file) {
        for (FileFilter component : components) {
            if (!component.accept(file)) {
                LOGGER.debug("{} does not match {}, so the file is rejected", component, file);
                return false;
            }
        }
        LOGGER.debug("Accepting {}", file);
        return true;
    }

}
