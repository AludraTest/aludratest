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

/**
 * Provides elementary file information which might serve as filter criteria.
 * @author Volker Bergmann
 */
public interface FileInfo {

    /** @return the name of the file */
    public String getName();

    /** @return the path of the file relative to the root folder of the related {@link FileService} */
    public String getPath();

    /** @return true if the file is a directory, otherwise false */
    public boolean isDirectory();

    /** @return the size of the file */
    public long getSize();

    /** @return the time at which the file was modified the last time */
    long getLastModifiedTime();

}