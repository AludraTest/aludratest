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

import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileSystem;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;

/** Helper class for fixing a VFS bug. It is needed to inject the {@link AludraLocalFileSystem} class, which is used to inject the
 * {@link AludraLocalFile} class, which provides the bug fix.
 * @author Volker Bergmann */
public class AludraLocalFileProvider extends DefaultLocalFileProvider {

    @Override
    protected FileSystem doCreateFileSystem(FileName name, FileSystemOptions fileSystemOptions) throws FileSystemException {
        final LocalFileName rootName = (LocalFileName) name;
        return new AludraLocalFileSystem(rootName, rootName.getRootFile(), fileSystemOptions);
    }

}
