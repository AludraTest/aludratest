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
 * Filters files by (optional) minumum and (optional) maximum time stamps 
 * of their lastModifiedTime.
 * @author Volker Bergmann
 */
public class LastModifiedTimeFileFilter implements FileFilter {

    /** The logger of the class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(LastModifiedTimeFileFilter.class);

    /** Minimum time stamp accepted (optional) as lastModifiedTime. */
    private Long minTimestamp;

    /** Maximum time stamp accepted (optional) as lastModifiedTime. */
    private Long maxTimestamp;

    /** Constructor. 
     *  @param minTimestamp the minimum time stamp accepted 
     *  @param maxTimestamp the maximum time stamp accepted*/
    public LastModifiedTimeFileFilter(Long minTimestamp, Long maxTimestamp) {
        this.minTimestamp = minTimestamp;
        this.maxTimestamp = maxTimestamp;
    }

    /** Accepts a file if timestamp constraints are matched. */
    @Override
    public boolean accept(FileInfo file) {
        if (minTimestamp != null && file.getLastModifiedTime() < minTimestamp) {
            LOGGER.debug("File is too old: {}", file);
            return false;
        } else if (maxTimestamp != null && maxTimestamp < file.getLastModifiedTime()) {
            LOGGER.debug("File is too new: {}", file);
            return false;
        } else {
            LOGGER.debug("File {} in expected time range", file);
            return true;
        }
    }

}
