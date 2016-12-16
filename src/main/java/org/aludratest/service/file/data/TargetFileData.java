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
package org.aludratest.service.file.data;

import org.aludratest.dict.Data;
import org.databene.commons.ParseUtil;

/**
 * Represents the target data of a copy or move operation.
 * @author Volker Bergmann
 */
public class TargetFileData extends Data {

    private String filePath;
    private String overwrite;

    /** Public default constructor that initializes all properties to null. */
    public TargetFileData() {
        this(null, null);
    }

    /** Constructor that initializes each property.
     * @param filePath the file path
     * @param overwrite a flag indicating whether a pre-existing file at this path may be overwritten */
    public TargetFileData(String filePath, String overwrite) {
        setFilePath(filePath);
        setOverwrite(overwrite);
    }

    /** @return the {@link #filePath} */
    public String getFilePath() {
        return filePath;
    }

    /** Sets the file path.
     *  @param filePath the file path to set */
    public final void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /** @return the {@link #overwrite} flag. */
    public String getOverwrite() {
        return overwrite;
    }

    /** Verifies and sets the {@link #overwrite} flag.
     *  @param overwrite must be 'true', 'false' or null */
    public final void setOverwrite(String overwrite) {
        ParseUtil.parseBoolean(overwrite); // verifies that the flag is true, false or null
        this.overwrite = overwrite;
    }

    @Override
    public String toString() {
        return filePath + "(overwrite: " + overwrite + ")";
    }

}
