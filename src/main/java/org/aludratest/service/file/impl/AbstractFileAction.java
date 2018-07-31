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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.aludratest.exception.AutomationException;
import org.aludratest.exception.FunctionalFailure;
import org.aludratest.exception.TechnicalException;
import org.aludratest.service.Action;
import org.aludratest.service.SystemConnector;
import org.aludratest.service.file.File;
import org.aludratest.service.file.FileFilter;
import org.aludratest.service.file.FileInfo;
import org.aludratest.service.file.FileService;
import org.aludratest.service.file.filter.RegexFilePathFilter;
import org.aludratest.testcase.event.attachment.Attachment;
import org.aludratest.testcase.event.attachment.BinaryAttachment;
import org.aludratest.testcase.event.attachment.StringAttachment;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileType;
import org.databene.commons.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Parent class for all {@link Action} interface implementations of the {@link FileService}.
 * @author Volker Bergmann */
public abstract class AbstractFileAction {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /** The configuration for this service instance. */
    protected FileServiceConfiguration configuration;

    /** Constructor taking the configuration.
     * @param configuration the configuration to apply */
    public AbstractFileAction(FileServiceConfiguration configuration) {
        this.configuration = configuration;
        getChildren(getRootFolder());
    }

    // Action interface implementation -----------------------------------------

    /** Injects a SystemConnector
     * @param systemConnector the SystemConnector to use */
    public void setSystemConnector(SystemConnector systemConnector) {
        // empty implementation
    }

    /** Creates zero, one or more attachments which represent the provided object.
     * @param object the object to save
     * @param label the title to use for the object
     * @return a list of {@link Attachment}s or null */
    public List<Attachment> createAttachments(Object object, String label) {
        if (object instanceof String && "Text file contents".equalsIgnoreCase(label)) {
            return Collections.<Attachment> singletonList(new StringAttachment(label, object.toString(), "txt"));
        }
        if (object instanceof byte[]) {
            return Collections.<Attachment> singletonList(new BinaryAttachment(label, (byte[]) object, "bin"));
        }

        throw new TechnicalException("Unsupported object type for attachment: "
                + (object == null ? "null" : object.getClass().getName()));
    }

    /** Creates attachments containing whichever information the service finds helpful for debugging the current state.
     * @return a list of {@link Attachment}s which contains the debugging information */
    public List<Attachment> createDebugAttachments() {
        return null; // NOSONAR
    }

    // Implementation of FileService interface methods -------------------------
    // which are commonly used by children

    /** Provides the root folder of the service instance.
     * @return the path of the root folder */
    public String getRootFolder() {
        return "/";
    }

    /** Lists all child elements of the given folder.
     * @param filePath the path of the directory to read
     * @return a List of all children */
    public final List<String> getChildren(String filePath) {
        File.verifyFilePath(filePath);
        return getChildren(filePath, (FileFilter) null);
    }

    /** Lists all child elements of the given folder which match the given regular expression.
     * @param filePath the path of the directory to read
     * @param filterRegex a regular expression for filtering file paths
     * @return a List of all children that match the filterRegex */
    public List<String> getChildren(String filePath, String filterRegex) {
        File.verifyFilePath(filePath);
        return getChildren(filePath, new RegexFilePathFilter(filterRegex));
    }

    /** Lists all child elements of the given folder which match the filter.
     * @param filePath the path of the directory to read
     * @param filter a filter for choosing files
     * @return a List of all children that match the filter */
    public List<String> getChildren(String filePath, FileFilter filter) {
        File.verifyFilePath(filePath);
        try {
            FileObject parent = configuration.getFileObject(filePath);
            parent.refresh();
            FileObject[] candidates = parent.getChildren();
            List<String> filePaths = new ArrayList<String>();
            if (candidates != null) {
                for (FileObject candidate : candidates) {
                    String path = configuration.pathFromRoot(candidate);
                    FileInfo info = new FileInfoImpl(candidate, path);
                    if (filter == null || filter.accept(info)) {
                        filePaths.add(pathFromRoot(candidate));
                    }
                }
            }
            if (!filePaths.isEmpty()) {
                logger.debug("Found children: {}", filePaths);
            }
            else {
                logger.debug("No children found for filter {}", filter);
            }
            return filePaths;
        }
        catch (IOException e) {
            throw new TechnicalException("Error retrivieving child objects", e);
        }
    }

    /** Tells if a file or folder with the given path exists.
     * @param filePath filePath the path of the file to check
     * @return true if it exosts, otherwise false */
    public boolean exists(String filePath) {
        File.verifyFilePath(filePath);
        try {
            FileObject file = getFileObject(filePath);
            file.refresh();
            boolean result = file.exists();
            logger.debug("File '{}' {}", filePath, (result ? "exists" : "does not exist"));
            return result;
        }
        catch (IOException e) {
            throw new TechnicalException("Error checking file presence", e);
        }
    }

    /** Tells if the given path represents a directory.
     * @param filePath filePath the path of the file to check
     * @return true if it's a directory, otherwise false */
    public boolean isDirectory(String filePath) {
        File.verifyFilePath(filePath);
        try {
            boolean result = getFileObject(filePath).getType() == FileType.FOLDER;
            logger.debug("{} is {}", filePath, (result ? "a folder" : "not a folder"));
            return result;
        }
        catch (IOException e) {
            throw new TechnicalException("Error accessing file or folder", e);
        }
    }

    /** Reads a text file and provides its content as String.
     * @param filePath filePath the path of the file to read
     * @return the file content as String */
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
            logger.debug("Text file read: {}", filePath);
            return writer.toString();
        }
        catch (IOException e) {
            throw new TechnicalException("Error reading text file", e);
        }
        finally {
            IOUtil.close(reader); // InputStream is closed by the Reader
        }
    }

    /** Creates a {@link Reader} for accessing the content of a text file.
     * @param filePath the path of the file to read
     * @return a BufferedReader for accessing the file content */
    public BufferedReader getReaderForTextFile(String filePath) {
        File.verifyFilePath(filePath);
        String encoding = configuration.getEncoding();
        try {
            logger.debug("Providing reader for text file: {}", filePath);
            return new BufferedReader(new InputStreamReader(getInputStreamForFile(filePath), encoding));
        }
        catch (UnsupportedEncodingException e) {
            throw new TechnicalException("Unsupported Encoding:" + encoding, e);
        }
    }

    /** Creates an {@link InputStream} for accessing the content of a file.
     * @param filePath the path of the file to read
     * @return an InputStream for accessing the file content */
    public InputStream getInputStreamForFile(String filePath) {
        File.verifyFilePath(filePath);
        FileObject file = getFileObject(filePath);
        try {
            logger.debug("Providing InputStream for binary file: {}", filePath);
            return file.getContent().getInputStream();
        }
        catch (IOException e) {
            throw new TechnicalException("Error opening InputStream", e);
        }
    }

    // non-public helpers for child classes ------------------------------------

    protected FileObject getFileObject(String pathFromRoot) {
        return configuration.getFileObject(pathFromRoot);
    }

    private String pathFromRoot(FileObject file) {
        return configuration.pathFromRoot(file);
    }

    protected boolean checkWritable(FileObject file, boolean overwrite) {
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
        }
        catch (IOException e) {
            throw new TechnicalException("Error checking file", e);
        }
    }

    protected void getOrCreateDirectory(FileObject directory) {
        FileObject root = configuration.getRootFolder();
        if (!root.equals(directory)) {
            try {
                getOrCreateDirectory(directory.getParent());
                directory.createFolder();
            }
            catch (IOException e) {
                throw new TechnicalException("Error creating directory", e);
            }
        }
    }

    protected void assertWritingPermitted(String operation) {
        if (!configuration.isWritingPermitted()) {
            throw new AutomationException("Invoked " + operation + " on a write protected file system");
        }
    }

    /** Lists FileInfos for all child elements of the given folder which match the filter.
     * @param dirPath the file path of the directory
     * @param filter the file filter to apply
     * @return a list of the directory's child items that match the filter */
    protected List<FileInfo> getChildInfos(String dirPath, FileFilter filter) {
        File.verifyFilePath(dirPath);
        try {
            FileObject parent = configuration.getFileObject(dirPath);
            parent.refresh();
            FileObject[] candidates = parent.getChildren();
            List<FileInfo> infos = new ArrayList<FileInfo>();
            if (candidates != null) {
                for (FileObject candidate : candidates) {
                    FileInfo fileInfo = new FileInfoImpl(candidate, configuration.pathFromRoot(candidate));
                    if (filter == null || filter.accept(fileInfo)) {
                        infos.add(fileInfo);
                    }
                }
            }
            return infos;
        }
        catch (IOException e) {
            throw new TechnicalException("Error retrivieving child objects", e);
        }
    }

}
