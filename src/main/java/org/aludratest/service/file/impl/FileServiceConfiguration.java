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

import org.aludratest.config.Preferences;
import org.aludratest.config.ValidatingPreferencesWrapper;
import org.aludratest.exception.AutomationException;
import org.aludratest.service.AludraCloseable;
import org.aludratest.service.file.FileService;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.UserAuthenticator;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.ftp.FtpFileSystemConfigBuilder;

/**
 * Provides configuration for the {@link FileService}.
 * @author Volker Bergmann
 */
public class FileServiceConfiguration implements AludraCloseable {

    static final int DEFAULT_WAIT_MAX_RETRIES = 15;

    static final int DEFAULT_WAIT_TIMEOUT = 30000;

    private ValidatingPreferencesWrapper configuration;

    /** Common-VFS' {@link StandardFileSystemManager}. */
    private StandardFileSystemManager manager;

    /** The root folder used by this service instance. */
    private FileObject rootFolder;

    /** Creates a new FileServiceConfiguration object which wraps the given Preferences object.
     * 
     * @param configuration Preferences configuration object to wrap.
     * 
     * @throws FileSystemException If an exception occurs when applying the configuration to the VFS Config Builders. */
    public FileServiceConfiguration(Preferences configuration) throws FileSystemException {
        this.configuration = new ValidatingPreferencesWrapper(configuration);

        // Configure secured access
        FileSystemOptions fileSystemOptions = new FileSystemOptions();
        String protocol = getProtocol();
        String baseUrl = getBaseUrl();
        String user = getUser();
        String password = getPassword();

        if (user != null || password != null) {
            UserAuthenticator authenticator = new StaticUserAuthenticator(protocol, user, password);
            DefaultFileSystemConfigBuilder builder = DefaultFileSystemConfigBuilder.getInstance();
            builder.setUserAuthenticator(fileSystemOptions, authenticator);
        }
        if ("ftp".equals(protocol)) {
            FtpFileSystemConfigBuilder builder = FtpFileSystemConfigBuilder.getInstance();
            builder.setUserDirIsRoot(fileSystemOptions, false);
            builder.setDataTimeout(fileSystemOptions, getTimeout());
            builder.setSoTimeout(fileSystemOptions, getTimeout());
            builder.setPassiveMode(fileSystemOptions, true);
        }

        // configure FileObject for root folder
        this.manager = new StandardFileSystemManager();
        // the setConfiguration call causes the vfs-providers.xml to be read twice
        // but is a trick to prevent parsing of VFS-2.0's internal default configuration file
        this.manager.setConfiguration(getClass().getResource("/META-INF/vfs-providers.xml"));
        this.manager.init();
        this.rootFolder = manager.resolveFile(protocol + "://" + baseUrl, fileSystemOptions);

        // access all configuration element in order to verify a complete configuration
        getEncoding();
        getLinefeed();
        getPollingDelay();
        getWaitMaxRetries();
        getTimeout();
        getRootFolder();
        getFileObject("/");
        getHost();
        isWritingPermitted();

    }

    /** @return the name of the used protocol: file, ftp, sftp, http or https. */
    public final String getProtocol() {
        return configuration.getRequiredStringValue("protocol");
    }

    /** @return the base URL of the service. */
    public final String getBaseUrl() {
        return configuration.getRequiredStringValue("base.url").replace('\\', '/');
    }

    /** @return the file encoding used on the file system. */
    public final String getEncoding() {
        return configuration.getRequiredStringValue("encoding");
    }

    /** @return the linefeed character(s) used on the file system. */
    public final String getLinefeed() {
        return Linefeed.valueOf(configuration.getRequiredStringValue("linefeed")).getChars();
    }

    /** @return the user name for login. */
    public final String getUser() {
        return configuration.getStringValue("user");
    }

    /** @return the password for login. */
    public final String getPassword() {
        return configuration.getStringValue("password");
    }

    /** @return the delay to use between polls. */
    public final int getPollingDelay() {
        return getTimeout() / getWaitMaxRetries();
    }

    /** @return the maximum number of polls to try. */
    private final int getWaitMaxRetries() {
        return configuration.getIntValue("wait.max.retries", DEFAULT_WAIT_MAX_RETRIES);
    }

    /** @return the maximum time to wait when polling. */
    public final int getTimeout() {
        return configuration.getIntValue("wait.timeout", DEFAULT_WAIT_TIMEOUT);
    }

    /** @return the root folder of the service. */
    public final FileObject getRootFolder() {
        return rootFolder;
    }

    /** @return true if writing is permitted on the file system, otherwise false */
    public final boolean isWritingPermitted() {
        return configuration.getBooleanValue("writing.permitted", false);
    }

    /** @return the host */
    public final String getHost() {
        String baseUrl = getBaseUrl();
        int sep = baseUrl.indexOf('/');
        String host = sep > 0 ? baseUrl.substring(0, sep) : "localhost";
        return host;
    }

    /** @return a commons-vfs {@link FileObject} for the given path.
     *  @param filePath the path of the requested file
     */
    public final FileObject getFileObject(String filePath) {
        try {
            String path = (filePath.charAt(0) == '/' ? (filePath.length() == 1 ? "." : filePath.substring(1)) : filePath);
            return manager.resolveFile(rootFolder, path);
        } catch (FileSystemException e) {
            throw new AutomationException("Error accessing file", e);
        }
    }

    /**
     * @param file the file
     * @return the full path
     */
    public final String pathFromRoot(FileObject file) {
        FileName rootFolderName = rootFolder.getName();
        FileName fileName = file.getName();
        try {
            return rootFolderName.getRelativeName(fileName);
        } catch (FileSystemException e) {
            throw new AutomationException("Error resolving relative path " +
                    rootFolderName.getPath() + " -> " + fileName.getPath(), e);
        }
    }

    @Override
    public void close() {
        this.manager.close();
    }

}
