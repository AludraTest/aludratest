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

import org.aludratest.exception.AutomationException;
import org.aludratest.exception.FunctionalFailure;
import org.aludratest.service.file.File;
import org.aludratest.service.file.FileVerification;
import org.databene.commons.Validator;

/** Implementation of the {@link FileVerification} interface.
 * @author Volker Bergmann */
public final class FileVerificationImpl extends AbstractFileAction implements FileVerification {

    /** Constructor.
     * @param configuration a {@link FileServiceConfiguration} */
    public FileVerificationImpl(FileServiceConfiguration configuration) {
        super(configuration);
    }

    /** Expects a file to exist.
     * @throws AutomationException if the file was not found. */
    @Override
    public void assertPresence(String filePath) {
        File.verifyFilePath(filePath);
        if (!exists(filePath)) {
            throw new AutomationException("Expected file not present: " + filePath);
        }
        else {
            logger.debug("File exists as expected: {}", filePath);
        }
    }

    /** Expects a file to be absent.
     * @throws AutomationException if the file was encountered. */
    @Override
    public void assertAbsence(String filePath) {
        File.verifyFilePath(filePath);
        if (exists(filePath)) {
            throw new AutomationException("File expected to be absent: " + filePath);
        }
        else {
            logger.debug("File is absent as expected: {}", filePath);
        }
    }

    @Override
    public void assertTextContentMatches(String filePath, Validator<String> validator) {
        String contents = readTextFile(filePath);
        if (!validator.valid(contents)) {
            throw new FunctionalFailure("The text contents of the file do not match the given validator.");
        }
    }

    @Override
    public void assertFile(String filePath) {
        File.verifyFilePath(filePath);
        assertPresence(filePath);
        if (isDirectory(filePath)) {
            throw new FunctionalFailure("Not a file: " + filePath);
        }
        else {
            logger.debug("File is not a directory: {}", filePath);
        }
    }

    @Override
    public void assertDirectory(String filePath) {
        File.verifyFilePath(filePath);
        assertPresence(filePath);
        if (!isDirectory(filePath)) {
            throw new FunctionalFailure("Not a directory: " + filePath);
        }
        else {
            logger.debug("File is not directory: {}", filePath);
        }
    }

}
