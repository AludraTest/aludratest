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

import java.util.regex.Pattern;

import org.aludratest.service.file.FileFilter;
import org.aludratest.service.file.FileInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Matches the file path against a regular expression.
 * @author Volker Bergmann
 */
public class RegexFilePathFilter implements FileFilter {

    /** The logger of the class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(RegexFilePathFilter.class);

    /** The regex which must be matched. */
    private String regex;

    /** Constructor.
     *  @param regex The regular expression to apply for filtering */
    public RegexFilePathFilter(String regex) {
        this.regex = regex;
    }

    /** Accepts files that match the {@link #regex} */
    @Override
    public boolean accept(FileInfo file) {
        boolean result = Pattern.matches(regex, file.getPath());
        if (result) {
            LOGGER.debug("Regex '{}' matches {}", regex, file);
        } else {
            LOGGER.debug("Regex '{}' does not match {}", regex, file);
        }
        return result;
    }

    @Override
    public String toString() {
        return "RegEx File Path Filter with Pattern " + regex;
    }

}
