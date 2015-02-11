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
package org.aludratest.service.flatfile;

import org.aludratest.config.ConfigProperty;
import org.aludratest.service.AludraService;
import org.aludratest.service.ServiceInterface;
import org.aludratest.service.file.FileService;

/**
 * AludraTest service interface for flat files support.
 * @author Volker Bergmann
 */
@ServiceInterface(name = "Flatfile Service", description = "Offers flatfile related access and verifaction methods.")
@ConfigProperty(name = "locale", type = String.class, description = "The locale to use for type conversions, as a Java Locale String representation.", defaultValue = "en_US")
public interface FlatFileService extends AludraService {

    @Override
    FlatFileInteraction perform();

    @Override
    FlatFileVerification verify();

    @Override
    FlatFileCondition check();

    /** @return the underlying {@link FileService} */
    FileService getFileService();

}
