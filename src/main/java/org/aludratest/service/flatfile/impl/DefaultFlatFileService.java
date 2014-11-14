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
package org.aludratest.service.flatfile.impl;

import java.util.Locale;

import org.aludratest.config.Preferences;
import org.aludratest.content.flat.FlatContent;
import org.aludratest.exception.TechnicalException;
import org.aludratest.service.AbstractConfigurableAludraService;
import org.aludratest.service.Implementation;
import org.aludratest.service.file.FileService;
import org.aludratest.service.flatfile.FlatFileCondition;
import org.aludratest.service.flatfile.FlatFileInteraction;
import org.aludratest.service.flatfile.FlatFileService;
import org.aludratest.service.flatfile.FlatFileVerification;

/**
 * Default {@link FlatFileService} implementation.
 * @author Volker Bergmann
 */
@Implementation({ FlatFileService.class })
public class DefaultFlatFileService extends AbstractConfigurableAludraService implements FlatFileService {


    /** Implementor of all action interfaces of the FlatFileService. */
    private DefaultFlatFileAction action;

    private FileService fileService;

    private FlatFileConfig configuration;

    /** Public default constructor. */
    public DefaultFlatFileService() {
    }

    @Override
    public FileService getFileService() {
        return fileService;
    }

    /** Provides an object that implements the {@link FlatFileInteraction} interface. */
    @Override
    public FlatFileInteraction perform() {
        return action;
    }

    /** Provides an object that implements the {@link FlatFileVerification} interface. */
    @Override
    public FlatFileVerification verify() {
        return action;
    }

    /** Provides an object that implements the {@link FlatFileCondition} interface. */
    @Override
    public FlatFileCondition check() {
        return action;
    }

    @Override
    public void configure(Preferences preferences) {
        configuration = new FlatFileConfig(preferences);
    }

    @Override
    public String getPropertiesBaseName() {
        return "flatfile";
    }

    /** Callback method that is called by the AludraTest framework
     * and initializes the service. */
    @Override
    public void initService() {
        try {
            Locale locale = configuration.getLocale();
            fileService = aludraServiceContext.getNonLoggingService(FileService.class);
            FlatContent contentHandler = aludraServiceContext.newComponentInstance(FlatContent.class);
            contentHandler.setLocale(locale);
            this.action = new DefaultFlatFileAction(contentHandler, fileService);
        } catch (Exception e) {
            throw new TechnicalException("Error initializing " + this, e);
        }
    }

    /** Closes the service. */
    @Override
    public void close() {
        // nothing to do
    }

    @Override
    public String getDescription() {
        return toString();
    }

}
