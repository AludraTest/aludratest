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

import org.aludratest.dict.ActionWordLibrary;

/** Parent class for file reader/writer classes that support the action word library approach.
 * @param <E> Generic parameter to be set by final child classes pointing to the child class itself
 * @author Volker Bergmann */
public abstract class FileStream<E extends FileStream<E>> implements ActionWordLibrary<E> {

    protected final String filePath;
    protected final FileService fileService;
    protected final String elementType;

    protected FileStream(String filePath, FileService fileService) {
        this.filePath = filePath;
        this.fileService = fileService;
        this.elementType = getClass().getSimpleName();
    }

    /** Deletes the file.
     *  @return a reference to the FileStream object itself */
    @SuppressWarnings("unchecked")
    public E delete() {
        fileService.perform().delete(filePath);
        return (E) this;
    }

    /** Polls the file system until a file at the given path is found
     *  or a timeout occurs.
     *  @return a reference to the FileStream object itself */
    @SuppressWarnings("unchecked")
    public E waitUntilExists() {
        fileService.perform().waitUntilExists(elementType, filePath);
        return (E) this;
    }

    /** Polls the file system until no file is found at the given path.
     *  @return a reference to the FileStream object itself */
    @SuppressWarnings("unchecked")
    public E waitUntilNotExists() {
        fileService.perform().waitUntilNotExists(filePath);
        return (E) this;
    }

}
