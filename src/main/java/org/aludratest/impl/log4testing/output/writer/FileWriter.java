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
package org.aludratest.impl.log4testing.output.writer;

import java.io.File;

import org.aludratest.impl.log4testing.configuration.ConfigurationError;
import org.aludratest.impl.log4testing.data.TestObject;

/**
 * This class must be extended if you are going to implement a new writer which
 * writes test reports to an output directory.
 * @param <T> a test object type like a test case or a test suite
 */
public abstract class FileWriter<T extends TestObject> implements Writer<T> {

    private String extension;
    private String outputDir;
    private String ignoreableRoot;
    private boolean abbreviating;
    private boolean shortTimeFormat;

    /** Public default constructor. */
    public FileWriter() {
        this.ignoreableRoot = "";
        this.abbreviating = false;
        this.shortTimeFormat = false;
    }

    /** @param extension the extension to set */
    public void setExtension(String extension) {
        this.extension = extension;
    }

    /** @return the extension */
    public String getExtension() {
        return extension;
    }

    /** @return the {@link #outputDir} */
    public String getOutputDir() {
        return outputDir;
    }

    /** @param outputDir the outputdir to set */
    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    /** @return the {@link #ignoreableRoot} */
    public String getIgnoreableRoot() {
        return ignoreableRoot;
    }

    /** Sets the {@link #ignoreableRoot}.
     *  @param ignoreableRoot the value to set as ignoreableRoot */
    public void setIgnoreableRoot(String ignoreableRoot) {
        this.ignoreableRoot = ignoreableRoot;
    }

    /** @return the value of the {@link #abbreviating} flag */
    public boolean isAbbreviating() {
        return abbreviating;
    }

    /** Sets the {@link #abbreviating} flag.
     * @param abbreviating the value to set for the abbreviating flag */
    public void setAbbreviating(boolean abbreviating) {
        this.abbreviating = abbreviating;
    }

    /** @return the {@link #shortTimeFormat} */
    public boolean isShortTimeFormat() {
        return shortTimeFormat;
    }

    /** Sets the {@link #shortTimeFormat}.
     * @param shortTimeFormat the shortTimeFormat to set
     */
    public void setShortTimeFormat(boolean shortTimeFormat) {
        this.shortTimeFormat = shortTimeFormat;
    }

    public void validate() {
        // check extension property
        assertNotEmpty("extension", extension);

        // check outputdir property
        assertNotEmpty("outputdir", outputDir);
        File outputDirectory = new File(outputDir);
        if (outputDirectory.exists() && !outputDirectory.isDirectory()) {
            throw new ConfigurationError("'outputdir' is not a directory: " + outputDir);
        }
    }

    protected static void assertNotEmpty(String propertyName, String propertyValue) {
        if (propertyValue == null || propertyValue.length() == 0) {
            throw new ConfigurationError("Property '" + propertyName + "' was not set");
        }
    }

}
