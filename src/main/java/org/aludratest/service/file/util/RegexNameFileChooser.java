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
package org.aludratest.service.file.util;

import java.util.List;
import java.util.regex.Pattern;

import org.aludratest.service.file.FileChooser;
import org.aludratest.service.file.FileInfo;

/** Chooses the first file whose name matches a regular expression.
 * @author Volker Bergmann */
public class RegexNameFileChooser implements FileChooser {

    private Pattern pattern;

    /** @param regex the regular expression to use */
    public RegexNameFileChooser(String regex) {
        setRegex(regex);
    }

    private void setRegex(String regex) {
        this.pattern = Pattern.compile(regex);
    }

    @Override
    public FileInfo chooseFrom(List<FileInfo> fileInfos) {
        for (FileInfo file : fileInfos) {
            if (this.pattern.matcher(file.getName()).matches()) {
                return file;
            }
        }
        return null;
    }

}
