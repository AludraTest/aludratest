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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;

import org.aludratest.exception.AutomationException;
import org.aludratest.exception.FunctionalFailure;
import org.aludratest.exception.TechnicalException;
import org.aludratest.service.file.File;
import org.aludratest.service.file.FileChooser;
import org.aludratest.service.file.FileFilter;
import org.aludratest.service.file.FileInfo;
import org.aludratest.service.file.FileInteraction;
import org.aludratest.util.poll.PollService;
import org.aludratest.util.poll.PolledTask;
import org.apache.commons.vfs2.AllFileSelector;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSelector;
import org.databene.commons.IOUtil;

/** Implementation of the {@link FileInteraction} interface.
 * @author Volker Bergmann */
public final class FileInteractionImpl extends AbstractFileAction implements FileInteraction {

    /** A polling service to delegate poll operations to. */
    private PollService pollService;

    /** Constructor.
     * @param configuration a {@link FileServiceConfiguration} */
    public FileInteractionImpl(FileServiceConfiguration configuration) {
        super(configuration);
        this.pollService = new PollService(configuration.getTimeout(), configuration.getPollingDelay());
    }

    /** Creates a directory. */
    @Override
    public void createDirectory(String filePath) {
        assertWritingPermitted("createDirectory()");
        File.verifyFilePath(filePath);
        try {
            getFileObject(filePath).createFolder();
            logger.debug("Created directory {}", filePath);
        }
        catch (IOException e) {
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
        try {
            FileObject target = getFileObject(toPath);
            boolean existedBefore = checkWritable(target, overwrite); // NOSONAR
            getOrCreateDirectory(target.getParent());
            getFileObject(fromPath).moveTo(target);
            logger.debug("Moved {} to {}", fromPath, toPath);
            return existedBefore;
        }
        catch (IOException e) {
            throw new TechnicalException("Error moving file" + fromPath + " -> " + toPath, e);
        }
    }

    /** Copies a file or folder.
     * @param fromPath the file/folder to copy
     * @param toPath the name/location of the copy
     * @param overwrite flag which indicates if an existing file may be overwritten by the operation
     * @return true if a formerly existing file was overwritten.
     * @throws FilePresentException if a file was already present and overwriting was disabled. */
    @Override
    public boolean copy(String fromPath, String toPath, boolean overwrite) {
        assertWritingPermitted("copy()");
        File.verifyFilePath(fromPath);
        File.verifyFilePath(toPath);
        try {
            FileObject target = getFileObject(toPath);
            boolean existedBefore = checkWritable(target, overwrite);
            FileObject source = getFileObject(fromPath);
            FileSelector sourceSelector = new FilePathSelector(source.getName().getPath());
            target.copyFrom(source, sourceSelector);
            logger.debug("Copied {} to {}", fromPath, toPath);
            return existedBefore;
        }
        catch (IOException e) {
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
            logger.debug("Deleted {}", filePath);
        }
        catch (IOException e) {
            throw new TechnicalException("Error deleting file", e);
        }
    }

    /** Creates a text file with the provided content.
     * @param filePath the path of the file to save
     * @param content the text to save as file content
     * @param overwrite flag which indicates if an existing file may be overwritten by the operation
     * @return true if a formerly existing file was overwritten.
     * @throws FilePresentException if a file was already present and overwriting was disabled. */
    @Override
    public boolean writeTextFile(String filePath, String content, boolean overwrite) {
        assertWritingPermitted("writeTextFile()");
        File.verifyFilePath(filePath);
        return writeTextFile(filePath, new StringReader(content), overwrite);
    }

    /** Creates a text file and writes to it all content provided by the source Reader.
     * @param filePath the path of the file to save
     * @param source a {@link Reader} which provides the file content
     * @param overwrite flag which indicates if an existing file may be overwritten by the operation
     * @return true if a formerly existing file was overwritten.
     * @throws FilePresentException if a file was already present and overwriting was disabled. */
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
            boolean existedBefore = checkWritable(target, overwrite); // NOSONAR
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
            logger.debug("Wrote text file {}", filePath);
            return existedBefore;
        }
        catch (UnsupportedEncodingException e) {
            throw new TechnicalException("Unsupported Encoding:" + encoding, e);
        }
        catch (IOException e) {
            throw new TechnicalException("Error writing text file", e);
        }
        finally {
            IOUtil.close(writer);
            IOUtil.close(reader);
        }
    }

    /** Creates a binary file with the provided content.
     * @param filePath the path of the file to save
     * @param bytes the file content to write
     * @param overwrite flag which indicates if an existing file may be overwritten by the operation
     * @return true if a formerly existing file was overwritten.
     * @throws FilePresentException if a file was already present and overwriting was disabled. */
    @Override
    public boolean writeBinaryFile(String filePath, byte[] bytes, boolean overwrite) {
        assertWritingPermitted("writeBinaryFile()");
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        return writeBinaryFile(filePath, in, overwrite);
    }

    /** Creates a binary file and writes to it all content provided by the source {@link InputStream}.
     * @param filePath the path of the file to save
     * @param source an {@link InputStream} which provides the content to write to the file
     * @param overwrite flag which indicates if an existing file may be overwritten by the operation
     * @return true if a formerly existing file was overwritten.
     * @throws FilePresentException if a file was already present and overwriting was disabled. */
    @Override
    public boolean writeBinaryFile(String filePath, InputStream source, boolean overwrite) {
        assertWritingPermitted("writeBinaryFile()");
        File.verifyFilePath(filePath);
        OutputStream out = null;
        try {
            FileObject target = getFileObject(filePath);
            boolean existedBefore = checkWritable(target, overwrite); // NOSONAR
            out = target.getContent().getOutputStream();
            IOUtil.transfer(source, out);
            logger.debug("Wrote binary file {}", filePath);
            return existedBefore;
        }
        catch (IOException e) {
            throw new TechnicalException("Error writing text file", e);
        }
        finally {
            IOUtil.close(out);
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
            logger.debug("Binary file read: {}", filePath);
            return out.toByteArray();
        }
        catch (IOException e) {
            throw new TechnicalException("Error reading binary file", e);
        }
        finally {
            IOUtil.close(in);
        }
    }

    /** Polls the file system for a given file until it is found or a timeout is exceeded. Timeout and the maximum number of polls
     * are retrieved from the {@link FileServiceConfiguration}.
     * @throws FunctionalFailure if the file was not found within the timeout */
    @Override
    public void waitUntilExists(String elementType, String filePath) {
        File.verifyFilePath(filePath);
        pollService.poll(new WaitForFileTask(filePath, true));
    }

    /** Polls the file system for searching a file until it is found or a timeout is exceeded. Timeout and the maximum number of
     * polls are retrieved from the {@link FileServiceConfiguration}.
     * @throws FunctionalFailure if the file was not found within the timeout */
    @Override
    public String waitUntilChildExists(String dirPath, FileChooser chooser) {
        File.verifyFilePath(dirPath);
        if (chooser == null) {
            throw new AutomationException("No FileChooser provided");
        }
        return pollService.poll(new WaitForFileChoosingTask(dirPath, chooser, true));
    }

    /** Polls the file system for a given file until it has disappeared or a timeout is exceeded. Timeout and the maximum number of
     * polls are retrieved from the {@link FileServiceConfiguration}.
     * @throws FunctionalFailure if the file was not found within the timeout */
    @Override
    public void waitUntilNotExists(String filePath) {
        File.verifyFilePath(filePath);
        pollService.poll(new WaitForFileTask(filePath, false));
    }

    /** Polls the given directory until the filter finds a match or a timeout is exceeded. Timeout and the maximum number of polls
     * are retrieved from the {@link FileServiceConfiguration}.
     * @throws AutomationException if the file was not found within the timeout */
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
                    logger.debug("File found: {}", filePath);
                    return (awaitExistence ? filePath : null);
                }
                else {
                    logger.debug("File not found: {}", filePath);
                    return (awaitExistence ? null : filePath);
                }
            }
            catch (IOException e) {
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

    private class WaitForFileChoosingTask implements PolledTask<String> {

        private String dirPath;
        private FileChooser chooser;
        private boolean awaitExistence;

        public WaitForFileChoosingTask(String dirPath, FileChooser chooser, boolean awaitExistence) {
            this.dirPath = dirPath;
            this.chooser = chooser;
            this.awaitExistence = awaitExistence;
        }

        @Override
        public String run() {
            FileObject dir = getFileObject(dirPath);
            try {
                if (!awaitExistence) {
                    // This is a workaround for VFS 2.0's flaws in the
                    // handling of attached/detached state and caching:
                    FileObject parent = dir.getParent();
                    parent.getType(); // assure that parent folder is attached
                    parent.refresh(); // detach parent folder and clear child object cache
                    // (works only if attached before)
                    // ...end of workaround
                }
                dir.refresh();

                List<FileInfo> children = getChildInfos(dirPath, null);
                FileInfo chosenFile = chooser.chooseFrom(children);
                if (chosenFile != null) {
                    String chosenPath = chosenFile.getPath();
                    logger.debug("File chosen: {}", chosenFile);
                    return (awaitExistence ? chosenPath : null);
                }
                else {
                    logger.debug("No appropriate file found in: {}", dirPath);
                    return (awaitExistence ? null : dirPath);
                }
            }
            catch (IOException e) {
                throw new TechnicalException("Error checking directory", e);
            }
        }

        @Override
        public String timedOut() {
            String expectedState = (awaitExistence ? "found" : "removed");
            throw new FunctionalFailure("File not " + expectedState + " within timeout: " + dirPath);
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + "(" + dirPath + ")";
        }

    }

    /** {@link PolledTask} that waits for the first occurrence of a file that matches the filter.
     * @author Volker Bergmann */
    public class WaitForFirstMatchTask implements PolledTask<String> {

        private String parentPath;
        private FileFilter filter;

        /** Constructor
         * @param parentPath the folder in which to search
         * @param filter the filter to apply in search */
        public WaitForFirstMatchTask(String parentPath, FileFilter filter) {
            this.parentPath = parentPath;
            this.filter = filter;
        }

        @Override
        public String run() {
            List<String> children = getChildren(parentPath, filter);
            if (!children.isEmpty()) {
                String result = children.get(0);
                logger.debug("File found: {}", result);
                return result;
            }
            else {
                logger.debug("No match found for {} in {}", filter, parentPath);
                return null;
            }
        }

        @Override
        public String timedOut() {
            throw new FunctionalFailure("No match found for filter " + filter + " in " + parentPath);
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + "(" + filter.toString() + ")";
        }

    }

}
