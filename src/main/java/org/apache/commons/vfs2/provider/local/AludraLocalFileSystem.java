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
package org.apache.commons.vfs2.provider.local;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.provider.AbstractFileName;

/** Helper class for fixing a VFS bug. It is needed to inject the {@link AludraLocalFile} class, which provides the bug fix.
 * @author Volker Bergmann */
public class AludraLocalFileSystem extends LocalFileSystem {

    private final String rootFile;

    /** @param rootName
     * @param rootFile
     * @param fileSystemOptions */
    public AludraLocalFileSystem(LocalFileName rootName, String rootFile, FileSystemOptions fileSystemOptions) {
        super(rootName, rootFile, fileSystemOptions);
        this.rootFile = rootFile;
    }

    @Override
    protected FileObject createFile(AbstractFileName name) throws FileSystemException {
        return new AludraLocalFile(this, rootFile, name);
    }

}
