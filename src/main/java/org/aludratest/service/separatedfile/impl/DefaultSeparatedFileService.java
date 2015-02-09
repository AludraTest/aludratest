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
package org.aludratest.service.separatedfile.impl;

import org.aludratest.content.separated.SeparatedContent;
import org.aludratest.exception.TechnicalException;
import org.aludratest.service.AbstractAludraService;
import org.aludratest.service.Implementation;
import org.aludratest.service.file.FileService;
import org.aludratest.service.separatedfile.SeparatedFileCondition;
import org.aludratest.service.separatedfile.SeparatedFileInteraction;
import org.aludratest.service.separatedfile.SeparatedFileService;
import org.aludratest.service.separatedfile.SeparatedFileVerification;

/**
 * Default implementation of the {@link SeparatedFileService}.
 * @author Volker Bergmann
 */
@Implementation({ SeparatedFileService.class })
public class DefaultSeparatedFileService extends AbstractAludraService implements SeparatedFileService {

    /** Implementor of all action interfaces of the WebdecsSeparatedFileService. */
    private DefaultSeparatedFileAction action;

    private FileService fileService;

    /** Public default constructor. */
    public DefaultSeparatedFileService() {
    }

    @Override
    public FileService getFileService() {
        return fileService;
    }

    /** Provides an object that implements the {@link SeparatedFileInteraction} interface. */
    @Override
    public SeparatedFileInteraction perform() {
        return action;
    }

    /** Provides an object that implements the {@link SeparatedFileVerification} interface. */
    @Override
    public SeparatedFileVerification verify() {
        return action;
    }

    /** Provides an object that implements the {@link SeparatedFileCondition} interface. */
    @Override
    public SeparatedFileCondition check() {
        return action;
    }

    /** Callback method that is called by the AludraTest framework
     * and initializes the service. */
    @Override
    public void initService() {
        try {
            fileService = aludraServiceContext.getNonLoggingService(FileService.class);
            SeparatedContent contentHandler = aludraServiceContext.newComponentInstance(SeparatedContent.class);
            this.action = new DefaultSeparatedFileAction(contentHandler, fileService);
        } catch (Exception e) {
            throw new TechnicalException("Error initializing " + this, e);
        }
    }

    /** Closes the service. */
    @Override
    public void close() {
        // nothing to do
    }

    /** Provides a description of the service instance. */
    @Override
    public String getDescription() {
        return toString();
    }
}
