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
package org.aludratest.service.file.impl;

import org.apache.commons.vfs2.FileSelectInfo;
import org.apache.commons.vfs2.FileSelector;

/**
 * Implementation of the commons-vfs {@link FileSelector} interface.
 * for selecting a file with a given name.
 * @author Volker Bergmann
 */
public class FilePathSelector implements FileSelector {

    /** The path of the desired file. */
    private String absolutePath;

    /** Constructor. 
     *  @param absolutePath the absolute file path */
    public FilePathSelector(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    /** Includes only the file with the expected {@link #absolutePath}. */
    public boolean includeFile(FileSelectInfo fileInfo) {
        return absolutePath.equals(fileInfo.getFile().getName().getPath());
    }

    /** Refuses recursion. */
    public boolean traverseDescendents(FileSelectInfo fileInfo) {
        return true;
    }

}
