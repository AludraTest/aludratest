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
package org.aludratest.service.file;

import org.aludratest.exception.TechnicalException;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;

/**
 * Provides elementary file information which might serve as filter criteria.
 * @author Volker Bergmann
 */
public class FileInfo {

    /** The commons-vfs {@link FileObject} which provides the real data. */
    // ENHANCE there should not be dependencies to VFS in this package!
    private FileObject file;

    /** Constructor 
     *  @param file the underlying {@link FileObject}. */
    public FileInfo(FileObject file) {
        this.file = file;
    }

    /** Provides the file name without path elements. 
     *  @return the file name */
    public String getName() {
        return file.getName().getBaseName();
    }

    /** Provides the path relative from the {@link FileService}'s root directory. 
     *  @return the file path */
    public String getPath() {
        return file.getName().getPath();
    }

    /** Tells if the file is a directory. 
     *  @return true if a directory is referred, otherwise false */
    public boolean isDirectory() {
        try {
            return (file.getType() == FileType.FOLDER);
        } catch (FileSystemException e) {
            throw new TechnicalException("Error checking file type", e);
        }
    }

    /** Provides the size of the file. 
     *  @return the size of the file */
    public long getSize() {
        try {
            return file.getContent().getSize();
        } catch (FileSystemException e) {
            throw new TechnicalException("Error retrieving file size", e);
        }
    }

    /** Provides the time stamp of the last file modification. 
     *  @return the time stamp of the last file modification */
    public long getLastModifiedTime() {
        try {
            return file.getContent().getLastModifiedTime();
        } catch (FileSystemException e) {
            throw new TechnicalException("Error retrieving lastModifiedTime", e);
        }
    }

    /** Provides the file name. */
    @Override
    public String toString() {
        return getName();
    }

}
