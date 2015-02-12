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
package org.aludratest.service.separatedfile;

import org.aludratest.service.AludraService;
import org.aludratest.service.ServiceInterface;
import org.aludratest.service.file.FileService;

/**
 * AludraTest service interface for separated file support.
 * @author Volker Bergmann
 */
@ServiceInterface(name = "Separated File Service", description = "Offers separated-value file (e.g. CSV) related access and verifaction methods.")
public interface SeparatedFileService extends AludraService {

    /** @see AludraService#perform() */
    @Override
    SeparatedFileInteraction perform();

    /** @see AludraService#verify() */
    @Override
    SeparatedFileVerification verify();

    /** @see AludraService#check() */
    @Override
    SeparatedFileCondition check();

    /** @return the underlying {@link FileService} */
    FileService getFileService();

}
