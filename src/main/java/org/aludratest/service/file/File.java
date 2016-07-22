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
import org.aludratest.exception.AutomationException;
import org.aludratest.exception.TechnicalException;
import org.aludratest.service.file.data.FileData;
import org.aludratest.service.file.data.TargetFileData;
import org.aludratest.util.DataUtil;
import org.aludratest.util.data.StringData;
import org.databene.commons.StringUtil;

/**
 * Represents a file in a file system, provided by a {@link FileService}.
 * @author Volker Bergmann
 */
public class File implements ActionWordLibrary<File> {

    /** The file path of the related file. */
    private final String filePath;

    /** The file service that provides access to the related file. */
    private final FileService service;

    private String elementName;

    /**
     * Constructor
     * @param filePath the absolute path of the file in the related file service
     * @param service the {@link FileService} that is responsible for handling the file
     */
    public File(String filePath, FileService service) {
        verifyFilePath(filePath);
        this.filePath = filePath;
        this.service = service;
        this.elementName = "File";
    }

    @Override
    public File verifyState() {
        // nothing to verify
        return this;
    }

    /** Creates a new directory
     *  @param name the name of the directory to create
     *  @return a reference to the created child directory */
    public File createSubDirectory(StringData name) {
        assertDirectory();
        if (StringUtil.isEmpty(name.getValue())) {
            throw new TechnicalException("Directory name is empty");
        }
        String childPath = filePath + "/" + name.getValue();
        service.perform().createDirectory(childPath);
        File child = new File(childPath, service);
        child.assertPresence();
        return child;
    }

    /**
     * Moves the file to a new location and/or renames it.
     *  @param targetData the target data
     *  @return a File object that represents the new File location
     */
    public File moveTo(TargetFileData targetData) {
        String targetPath = targetData.getFilePath();
        boolean overwrite = DataUtil.parseBoolean(targetData.getOverwrite());
        service.perform().move(this.filePath, targetPath, overwrite);
        File targetFile = new File(targetPath, service);
        targetFile.assertPresence();
        return targetFile;
    }

    /**
     * Copies the file to a new location.
     * @param targetData the target data
     * @return a File object that represents the new File location
     */
    public File copyTo(TargetFileData targetData) {
        String targetPath = targetData.getFilePath();
        boolean overwrite = DataUtil.parseBoolean(targetData.getOverwrite());
        service.perform().copy(this.filePath, targetPath, overwrite);
        File targetFile = new File(targetPath, service);
        targetFile.assertPresence();
        return targetFile;
    }

    /**
     * Creates a text file with the given content.
     * @param content the file content to write
     * @return A reference to the File instance
     */
    public File writeTextContent(StringData content) {
        this.service.perform().writeTextFile(this.filePath, content.getValue(), true);
        return this;
    }

    /**
     * Reads the content of a text file.
     * @param result a mutable string that receives the file content
     * @return A reference to the File instance
     */
    public File readTextContent(StringData result) {
        String content = this.service.perform().readTextFile(this.filePath);
        result.setValue(content);
        return this;
    }

    /* Features postponed
     * 
     * Reads the content of a binary file.
     * @param result a mutable array that receives the file content
     * @return A reference to the File instance
     *
    public File readBinaryContent(MutableData<byte[]> result) {
    	byte[] content = this.service.perform().readBinaryFile(this.filePath);
    	result.setValue(content);
    	return this;
    }

     ** Tells if the file exists
     *  @param result a mutable boolean that receives the operation result
     *  @return a reference to the File object itself
     *
    public File exists(MutableData<Boolean> result) {
    	result.setValue(service.check().exists(filePath));
    	return this;
    }
     */

    /** Deletes the file.
     *  @return a reference to the File object itself
     */
    public File delete() {
        service.perform().delete(filePath);
        return this;
    }

    /** Waits until the file exists or a timeout occurs.
     *  @return a reference to the File object itself
     */
    public File waitUntilExists() {
        service.perform().waitUntilExists(elementName, filePath);
        return this;
    }

    /** Waits until a child file exists or a timeout occurs.
     * @param chooser the {@link FileChooser} that chooses the file from the directory
     * @param resultFile returns a reference to the chosen file
     * @return a reference to the File object itself */
    public File waitUntilChildExists(FileChooser chooser, FileData resultFile) {
        if (resultFile == null) {
            throw new AutomationException("No resultFile data object provided");
        }
        String chosenPath = service.perform().waitUntilChildExists(filePath, chooser);
        resultFile.setFile(new File(chosenPath, service));
        return this;
    }

    /** Waits until the file does not exist or a timeout occurs.
     *  @return a reference to the File object itself */
    public File waitUntilNotExists() {
        service.perform().waitUntilNotExists(filePath);
        return this;
    }

    /** Expects the file to be absent.
     *  @return a reference to the File object itself */
    public File assertAbsence() {
        service.verify().assertAbsence(filePath);
        return this;
    }

    /** Expects the file to be present.
     *  @return a reference to the File object itself */
    public File assertPresence() {
        service.verify().assertPresence(filePath);
        return this;
    }

    /** Asserts that the file is not a directory.
     * 	@return a self-reference */
    public File assertFile() {
        service.verify().assertFile(this.filePath);
        return this;
    }

    /** Asserts that the file is a directory.
     * 	@return a self-reference */
    public File assertDirectory() {
        service.verify().assertDirectory(this.filePath);
        return this;
    }

    /** Verifies a file path.
     * @param filePath */
    public static void verifyFilePath(String filePath) {
        if (filePath == null || filePath.length() == 0) {
            throw new AutomationException("File path is empty or null");
        }
    }

}
