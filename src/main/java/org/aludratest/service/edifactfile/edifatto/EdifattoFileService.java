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
package org.aludratest.service.edifactfile.edifatto;

import org.aludratest.content.edifact.edifatto.EdifattoContent;
import org.aludratest.service.AbstractAludraService;
import org.aludratest.service.edifactfile.EdifactFileCondition;
import org.aludratest.service.edifactfile.EdifactFileInteraction;
import org.aludratest.service.edifactfile.EdifactFileService;
import org.aludratest.service.edifactfile.EdifactFileVerification;
import org.aludratest.service.file.FileService;

/** 
 * Default implementation of the EdifactService 
 * which employs Edifatto for EDIFACR/X12 processing.
 * @author Volker Bergmann
 */
public class EdifattoFileService extends AbstractAludraService implements EdifactFileService {
    
    private FileService fileService;
    
    /** The action object that implements all EdifactService action interfaces */
    private EdifattoFileAction action;
    
    /** Initializes the service */
    @Override
    public void initService() {
        this.fileService = aludraServiceContext.getNonLoggingService(FileService.class);
        this.action = new EdifattoFileAction(new EdifattoContent(), fileService);
    }
    
    
    // AludraService interface implementation ----------------------------------
    
    /** Provides a description of the service */
    @Override
    public String getDescription() {
        return getClass().getSimpleName();
    }
    
    /** Provides an object to parses and save EDIFACT and X12 documents from and to streams. */
    @Override
    public EdifactFileInteraction perform() {
        return action;
    }
    
    /** Provides an object to verify EDIFACT or X12 documents. */
    @Override
    public EdifactFileVerification verify() {
        return action;
    }
    
    /** Provides an object for performing queries on EDIFACT or X12 interchanges 
     *  and analyze their differences. */
    @Override
    public EdifactFileCondition check() {
        return action;
    }
    
    @Override
    public FileService getFileService() {
        return fileService;
    }
    
    /** Closes the service */
    @Override
    public void close() {
        // nothing to do
    }

}
