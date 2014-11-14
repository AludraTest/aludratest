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
 * Combines several other {@link FileFilter} components 
 * and accepts a file if any of the components does.
 * @author Volker Bergmann
 */
public class OrFileFilter extends CompositeFileFilter {

    /** The logger of the class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(OrFileFilter.class);

    /** Constructor 
     *  @param components the components to combine */
    public OrFileFilter(FileFilter... components) {
        super(components);
    }

    /** Accepts a file if any of the filter components does. */
    public boolean accept(FileInfo file) {
        for (FileFilter component : components) {
            if (component.accept(file)) {
                LOGGER.debug("{} matches {}", component, file);
                return true;
            }
        }
        LOGGER.debug("No match found for file {}", file);
        return false;
    }

}
