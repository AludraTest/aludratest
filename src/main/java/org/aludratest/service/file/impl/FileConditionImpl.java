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

import org.aludratest.service.file.FileCondition;

/** Implementation of the {@link FileCondition} interface.
 * @author Volker Bergmann */
public final class FileConditionImpl extends AbstractFileAction implements FileCondition {

    /** Constructor.
     * @param configuration a {@link FileServiceConfiguration} */
    public FileConditionImpl(FileServiceConfiguration configuration) {
        super(configuration);
    }

    // all methods of the interface FileCondition are already implemented and used in the parent class
}
