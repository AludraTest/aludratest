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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.aludratest.exception.AutomationException;
import org.aludratest.exception.FunctionalFailure;
import org.aludratest.exception.TechnicalException;
import org.aludratest.service.SystemConnector;
import org.aludratest.service.file.File;
import org.aludratest.service.file.FileCondition;
import org.aludratest.service.file.FileFilter;
import org.aludratest.service.file.FileInfo;
import org.aludratest.service.file.FileInteraction;
import org.aludratest.service.file.FileService;
import org.aludratest.service.file.FileVerification;
import org.aludratest.service.file.filter.RegexFilePathFilter;
import org.aludratest.testcase.event.attachment.Attachment;
import org.aludratest.util.poll.PollService;
import org.aludratest.util.poll.PolledTask;
import org.apache.commons.vfs2.AllFileSelector;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSelector;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.databene.commons.IOUtil;
import org.databene.commons.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements all Action interfaces of the {@link FileService} in a single class:
 * {@link FileInteraction}, {@link FileVerification} and {@link FileCondition}.
 * @author Volker Bergmann
 */
public final class FileActionImpl implements FileInteraction, FileVerification, FileCondition {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileActionImpl.class);

    /** The configuration for this service instance. */
    private FileServiceConfiguration configuration;

    /** A polling service to delegate poll operations to. */
    private PollService pollService;

    /** Constructor taking the configuration.
     *  @param configuration */
    public FileActionImpl(FileServiceConfiguration configuration) {
        this.configuration = configuration;
        this.pollService = new PollService(configuration.getTimeout(), configuration.getPollingDelay());
        getChildren(getRootFolder());
    }

    @Override
    public void setSystemConnector(SystemConnector systemConnector) {
        // empty implementation
    }

    /** Provides the root folder of the service instance. */
    @Override
    public String getRootFolder() {
        return "/";
    }

    /** Lists all child elements of the given folder. */
    @Override
    public final List<String> getChildren(String filePath) {
        File.verifyFilePath(filePath);
        return getChildren(filePath, (FileFilter) null);
    }

    /** Lists all child elements of the given folder which match the given regular expression. */
    @Override
    public List<String> getChildren(String filePath, String filterRegex) {
        File.verifyFilePath(filePath);
        return getChildren(filePath, new RegexFilePathFilter(filterRegex));
    }

    /** Lists all child elements of the given folder which match the filter. */
    @Override
    public List<String> getChildren(String filePath, FileFilter filter) {
        File.verifyFilePath(filePath);
        try {
            FileObject parent = configuration.getFileObject(filePath);
            parent.refresh();
            FileObject[] candidates = parent.getChildren();
            List<String> filePaths = new ArrayList<String>();
            if (candidates != null) {
                for (FileObject candidate : candidates) {
                    if (filter == null || filter.accept(new FileInfo(candidate))) {
                        filePaths.add(pathFromRoot(candidate));
                    }
                }
            }
            if (filePaths.size() > 0) {
                LOGGER.debug("Found children: {}", filePaths);
            } else {
                LOGGER.debug("No children found for filter {}", filter);
            }
            return filePaths;
        } catch (FileSystemException e) {
            throw new TechnicalException("Error retrivieving child objects", e);
        }
    }

    /** Creates a directory. */
    @Override
    public void createDirectory(String filePath) {
        assertWritingPermitted("createDirectory()");
        File.verifyFilePath(filePath);
        try {
            getFileObject(filePath).createFolder();
            LOGGER.debug("Created directory {}", filePath);
        } catch (FileSystemException e) {
            throw new TechnicalException("Directory creation failed", e);
        }
    }

    /** Renames or moves a file or folder.
     * @param fromPath the file/folder to rename/move
     * @param toPath the new name/location of the file/folder
     * @param overwrite flag which indicates if an existing file may be overwritten by the operation
     * @return true if a formerly existing file was overwritten.
     * @throws FilePresentException if a file was already present and overwriting was disabled. */
    @Override
    public boolean move(String fromPath, String toPath, boolean overwrite) {
        assertWritingPermitted("move()");
        File.verifyFilePath(fromPath);
        File.verifyFilePath(toPath);
        FileObject target = getFileObject(toPath);
        boolean existedBefore = checkWritable(target, overwrite);
        try {
            getOrCreateDirectory(target.getParent());
            getFileObject(fromPath).moveTo(target);
            LOGGER.debug("Moved {} to {}", fromPath, toPath);
            return existedBefore;
        } catch (FileSystemException e) {
            throw new TechnicalException("Error moving file" + fromPath + " -> " + toPath, e);
        }
    }

    /** Copies a file or folder.
     *  @param fromPath the file/folder to copy
     *  @param toPath the name/location of the copy
     *  @param overwrite flag which indicates if an existing file may be overwritten by the operation
     *  @return true if a formerly existing file was overwritten.
     *  @throws FilePresentException if a file was already present and overwriting was disabled. */
    @Override
    public boolean copy(String fromPath, String toPath, boolean overwrite) {
        assertWritingPermitted("copy()");
        File.verifyFilePath(fromPath);
        File.verifyFilePath(toPath);
        FileObject target = getFileObject(toPath);
        boolean existedBefore = checkWritable(target, overwrite);
        try {
            FileObject source = getFileObject(fromPath);
            FileSelector sourceSelector = new FilePathSelector(source.getName().getPath());
            target.copyFrom(source, sourceSelector);
            LOGGER.debug("Copied {} to {}", fromPath, toPath);
            return existedBefore;
        } catch (FileSystemException e) {
            throw new TechnicalException("Error copying file " + fromPath + " -> " + toPath, e);
        }
    }

    /** Deletes a file or folder. */
    @Override
    public void delete(String filePath) {
        assertWritingPermitted("delete()");
        File.verifyFilePath(filePath);
        try {
            getFileObject(filePath).delete(new AllFileSelector());
            LOGGER.debug("Deleted {}", filePath);
        } catch (FileSystemException e) {
            throw new TechnicalException("Error deleting file", e);
        }
    }

    /** Creates a text file with the provided content.
     *  @param filePath the path of the file to save
     *  @param content the text to save as file content
     *  @param overwrite flag which indicates if an existing file may be overwritten by the operation
     *  @return true if a formerly existing file was overwritten.
     *  @throws FilePresentException if a file was already present and overwriting was disabled. */
    @Override
    public boolean writeTextFile(String filePath, String content, boolean overwrite) {
        assertWritingPermitted("writeTextFile()");
        File.verifyFilePath(filePath);
        return writeTextFile(filePath, new StringReader(content), overwrite);
    }

    /** Creates a text file and writes to it all content provided by the source Reader.
     *  @param filePath the path of the file to save
     *  @param source a {@link Reader} which provides the file content
     *  @param overwrite flag which indicates if an existing file may be overwritten by the operation
     *  @return true if a formerly existing file was overwritten.
     *  @throws FilePresentException if a file was already present and overwriting was disabled. */
    @Override
    public boolean writeTextFile(String filePath, Reader source, boolean overwrite) {
        assertWritingPermitted("writeTextFile()");
        File.verifyFilePath(filePath);
        String encoding = configuration.getEncoding();
        Writer writer = null;
        BufferedReader reader = null;
        try {
            String linefeed = configuration.getLinefeed();
            FileObject target = getFileObject(filePath);
            boolean existedBefore = checkWritable(target, overwrite);
            writer = new OutputStreamWriter(target.getContent().getOutputStream(), encoding);
            reader = new BufferedReader(source);
            boolean firstLine = true;
            String line;
            while ((line = reader.readLine()) != null) {
                if (!firstLine) {
                    writer.write(linefeed);
                }
                writer.write(line);
                firstLine = false;
            }
            LOGGER.debug("Wrote text file {}", filePath);
            return existedBefore;
        } catch (UnsupportedEncodingException e) {
            throw new TechnicalException("Unsupported Encoding:" + encoding, e);
        } catch (Exception e) {
            throw new TechnicalException("Error writing text file", e);
        } finally {
            IOUtil.close(writer);
            IOUtil.close(reader);
        }
    }

    /** Creates a binary file with the provided content.
     *  @param filePath the path of the file to save
     *  @param bytes the file content to write
     *  @param overwrite flag which indicates if an existing file may be overwritten by the operation
     *  @return true if a formerly existing file was overwritten.
     *  @throws FilePresentException if a file was already present and overwriting was disabled. */
    @Override
    public boolean writeBinaryFile(String filePath, byte[] bytes, boolean overwrite) {
        assertWritingPermitted("writeBinaryFile()");
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        return writeBinaryFile(filePath, in, overwrite);
    }

    /** Creates a binary file and writes to it all content provided by the source {@link InputStream}.
     *  @param filePath the path of the file to save
     *  @param source an {@link InputStream} which provides the content to write to the file
     *  @param overwrite flag which indicates if an existing file may be overwritten by the operation
     *  @return true if a formerly existing file was overwritten.
     *  @throws FilePresentException if a file was already present and overwriting was disabled. */
    @Override
    public boolean writeBinaryFile(String filePath, InputStream source, boolean overwrite) {
        assertWritingPermitted("writeBinaryFile()");
        File.verifyFilePath(filePath);
        OutputStream out = null;
        try {
            FileObject target = getFileObject(filePath);
            boolean existedBefore = checkWritable(target, overwrite);
            out = target.getContent().getOutputStream();
            IOUtil.transfer(source, out);
            LOGGER.debug("Wrote binary file {}", filePath);
            return existedBefore;
        } catch (Exception e) {
            throw new TechnicalException("Error writing text file", e);
        } finally {
            IOUtil.close(out);
        }
    }

    /** Reads a text file and provides its content as String. */
    @Override
    public String readTextFile(String filePath) {
        File.verifyFilePath(filePath);
        if (!exists(filePath) || isDirectory(filePath)) {
            throw new AutomationException("No file exists at the given file path");
        }

        BufferedReader reader = null;
        try {
            StringWriter writer = new StringWriter();
            PrintWriter printer = new PrintWriter(writer);
            reader = getReaderForTextFile(filePath);
            boolean firstLine = true;
            String line;
            while ((line = reader.readLine()) != null) {
                if (!firstLine) {
                    printer.println();
                }
                printer.write(line);
                firstLine = false;
            }
            LOGGER.debug("Text file read: {}", filePath);
            return writer.toString();
        } catch (IOException e) {
            throw new TechnicalException("Error reading text file", e);
        } finally {
            IOUtil.close(reader); // InputStream is closed by the Reader
        }
    }

    /** Creates a {@link Reader} for accessing the content of a text file. */
    @Override
    public BufferedReader getReaderForTextFile(String filePath) {
        File.verifyFilePath(filePath);
        String encoding = configuration.getEncoding();
        try {
            LOGGER.debug("Providing reader for text file: {}", filePath);
            return new BufferedReader(new InputStreamReader(getInputStreamForFile(filePath), encoding));
        } catch (UnsupportedEncodingException e) {
            throw new TechnicalException("Unsupported Encoding:" + encoding, e);
        }
    }

    /** Reads a binary file and provides its content as an array of bytes. */
    @Override
    public byte[] readBinaryFile(String filePath) {
        File.verifyFilePath(filePath);
        if (!exists(filePath) || isDirectory(filePath)) {
            throw new AutomationException("No file exists at the given file path");
        }

        InputStream in = null;
        try {
            in = getInputStreamForFile(filePath);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            IOUtil.transfer(in, out);
            LOGGER.debug("Binary file read: {}", filePath);
            return out.toByteArray();
        } catch (IOException e) {
            throw new TechnicalException("Error reading binary file", e);
        } finally {
            IOUtil.close(in);
        }
    }

    /** Creates an {@link InputStream} for accessing the content of a file. */
    @Override
    public InputStream getInputStreamForFile(String filePath) {
        File.verifyFilePath(filePath);
        FileObject file = getFileObject(filePath);
        try {
            LOGGER.debug("Providing InputStream for binary file: {}", filePath);
            return file.getContent().getInputStream();
        } catch (FileSystemException e) {
            throw new TechnicalException("Error opening InputStream", e);
        }
    }

    /** Polls the file system for a given file until it is found or a timeout is exceeded.
     *  Timeout and the maximum number of polls are retrieved from the
     *  {@link FileServiceConfiguration}.
     *  @throws FunctionalFailure if the file was not found within the timeout */
    @Override
    public void waitUntilExists(String elementType, String filePath) {
        File.verifyFilePath(filePath);
        pollService.poll(new WaitForFileTask(filePath, true));
    }

    /** Polls the file system for a given file until it has disappeared or a timeout is exceeded.
     *  Timeout and the maximum number of polls are retrieved from the
     *  {@link FileServiceConfiguration}.
     *  @throws FunctionalFailure if the file was not found within the timeout */
    @Override
    public void waitUntilNotExists(String filePath) {
        File.verifyFilePath(filePath);
        pollService.poll(new WaitForFileTask(filePath, false));
    }

    /** Polls the given directory until the filter finds a match or a timeout is exceeded.
     *  Timeout and the maximum number of polls are retrieved from the
     *  {@link FileServiceConfiguration}.
     *  @throws AutomationException if the file was not found within the timeout */
    @Override
    public String waitForFirstMatch(String parentPath, FileFilter filter) {
        File.verifyFilePath(parentPath);
        if (!exists(parentPath)) {
            throw new AutomationException("Directory not found: " + parentPath);
        }
        if (!isDirectory(parentPath)) {
            throw new AutomationException("Not a directory: " + parentPath);
        }
        if (filter == null) {
            throw new AutomationException("Filter is null");
        }
        return pollService.poll(new WaitForFirstMatchTask(parentPath, filter));
    }

    /** Expects a file to exist.
     *  @throws AutomationException if the file was not found. */
    @Override
    public void assertPresence(String filePath) {
        File.verifyFilePath(filePath);
        if (!exists(filePath)) {
            throw new AutomationException("Expected file not present: " + filePath);
        } else {
            LOGGER.debug("File exists as expected: {}", filePath);
        }
    }

    /** Expects a file to be absent.
     *  @throws AutomationException if the file was encountered. */
    @Override
    public void assertAbsence(String filePath) {
        File.verifyFilePath(filePath);
        if (exists(filePath)) {
            throw new AutomationException("File expected to be absent: " + filePath);
        } else {
            LOGGER.debug("File is absent as expected: {}", filePath);
        }
    }

    @Override
    public void assertTextContentMatches(String filePath, Validator<String> validator) {
        String contents = readTextFile(filePath);
        if (!validator.valid(contents)) {
            throw new FunctionalFailure("The text contents of the file do not match the given validator.");
        }
    }

    /** Tells if a file or folder with the given path exists. */
    @Override
    public boolean exists(String filePath) {
        File.verifyFilePath(filePath);
        try {
            FileObject file = getFileObject(filePath);
            file.refresh();
            boolean result = file.exists();
            LOGGER.debug("File '{}' {}", filePath, (result ? "exists" : "does not exist"));
            return result;
        } catch (FileSystemException e) {
            throw new TechnicalException("Error checking file presence", e);
        }
    }

    @Override
    public void assertFile(String filePath) {
        File.verifyFilePath(filePath);
        assertPresence(filePath);
        if (isDirectory(filePath)) {
            throw new FunctionalFailure("Not a file: " + filePath);
        } else {
            LOGGER.debug("File is not a directory: {}", filePath);
        }
    }

    @Override
    public void assertDirectory(String filePath) {
        File.verifyFilePath(filePath);
        assertPresence(filePath);
        if (!isDirectory(filePath)) {
            throw new FunctionalFailure("Not a directory: " + filePath);
        } else {
            LOGGER.debug("File is not directory: {}", filePath);
        }
    }

    /** Tells if the given path represents a directory. */
    @Override
    public boolean isDirectory(String filePath) {
        File.verifyFilePath(filePath);
        try {
            boolean result = getFileObject(filePath).getType() == FileType.FOLDER;
            LOGGER.debug("{} is {}", filePath, (result ? "a folder" : "not a folder"));
            return result;
        } catch (FileSystemException e) {
            throw new TechnicalException("Error accessing file or folder", e);
        }
    }

    @Override
    public List<Attachment> createAttachments(Object object, String label) {
        throw new TechnicalException("Not supported");
    }

    @Override
    public List<Attachment> createDebugAttachments() {
        return null;
    }

    // private helpers ---------------------------------------------------------

    private FileObject getFileObject(String pathFromRoot) {
        return configuration.getFileObject(pathFromRoot);
    }

    private String pathFromRoot(FileObject file) {
        return configuration.pathFromRoot(file);
    }

    private boolean checkWritable(FileObject file, boolean overwrite) {
        try {
            if (file.exists()) {
                if (overwrite) {
                    file.delete();
                } else {
                    throw new FunctionalFailure("File expected to be absent: " + pathFromRoot(file));
                }
                return true;
            } else {
                return false;
            }
        } catch (FileSystemException e) {
            throw new TechnicalException("Error checking file", e);
        }
    }

    private void getOrCreateDirectory(FileObject directory) {
        FileObject root = configuration.getRootFolder();
        if (!root.equals(directory)) {
            try {
                getOrCreateDirectory(directory.getParent());
                directory.createFolder();
            } catch (FileSystemException e) {
                throw new TechnicalException("Error creating directory", e);
            }
        }
    }

    private void assertWritingPermitted(String operation) {
        if (!configuration.isWritingPermitted()) {
            throw new AutomationException("Invoked " + operation + " on a write protected file system");
        }
    }

    private class WaitForFileTask implements PolledTask<String> {

        private String filePath;
        private boolean awaitExistence;

        public WaitForFileTask(String filePath, boolean awaitExistence) {
            this.filePath = filePath;
            this.awaitExistence = awaitExistence;
        }

        @Override
        public String run() {
            FileObject file = getFileObject(filePath);
            try {
                if (!awaitExistence) {
                    // This is a workaround for VFS 2.0's flaws in the
                    // handling of attached/detached state and caching:
                    FileObject parent = file.getParent();
                    parent.getType(); // assure that parent folder is attached
                    parent.refresh(); // detach parent folder and clear child object cache
                    // (works only if attached before)
                    // ...end of workaround
                }
                file.refresh();
                if (file.exists()) {
                    LOGGER.debug("File found: {}", filePath);
                    return (awaitExistence ? filePath : null);
                } else {
                    LOGGER.debug("File not found: {}", filePath);
                    return (awaitExistence ? null : filePath);
                }
            } catch (FileSystemException e) {
                throw new TechnicalException("Error checking presence of file ", e);
            }
        }

        @Override
        public String timedOut() {
            String expectedState = (awaitExistence ? "found" : "removed");
            throw new FunctionalFailure("File not " + expectedState + " within timeout: " + filePath);
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + "(" + filePath + ")";
        }

    }

    /**
     * {@link PolledTask} that waits for the first occurrence of a file that matches the filter.
     * @author Volker Bergmann
     */
    public class WaitForFirstMatchTask implements PolledTask<String> {

        private String parentPath;
        private FileFilter filter;

        /** Constructor
         *  @param parentPath the folder in which to search
         *  @param filter the filter to apply in search */
        public WaitForFirstMatchTask(String parentPath, FileFilter filter) {
            this.parentPath = parentPath;
            this.filter = filter;
        }

        @Override
        public String run() {
            List<String> children = getChildren(parentPath, filter);
            if (children.size() > 0) {
                String result = children.get(0);
                LOGGER.debug("File found: {}", result);
                return result;
            } else {
                LOGGER.debug("No match found for {}", filter);
                return null;
            }
        }

        @Override
        public String timedOut() {
            throw new FunctionalFailure("No match found for filter " + filter);
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + "(" + filter.toString() + ")";
        }

    }

}
