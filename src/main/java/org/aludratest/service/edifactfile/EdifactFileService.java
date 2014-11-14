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
package org.aludratest.service.edifactfile;

import org.aludratest.service.AludraService;
import org.aludratest.service.ServiceInterface;
import org.aludratest.service.file.FileService;

/**
 * Interface for a service for processing EDIFACT and X12 documents.
 * @author Volker Bergmann
 */
@ServiceInterface(name = "EDIFACT File Service", description = "Offers EDIFACT and X12 related access and verifaction methods. Uses the Edifatto Library to perform operations.")
public interface EdifactFileService extends AludraService {

    /** Provides an object to parses and save EDIFACT and X12 documents from and to streams. */
    @Override
    EdifactFileInteraction perform();

    /** Provides an object to verify EDIFACT or X12 documents. */
    @Override
    EdifactFileVerification verify();

    /** Provides an object for performing queries on EDIFACT or X12 interchanges
     *  and analyze their differences. */
    @Override
    EdifactFileCondition check();

    /** Provides the internally used {@link FileService} instance
     *  @return the internally used {@link FileService} instance */
    FileService getFileService();

}
