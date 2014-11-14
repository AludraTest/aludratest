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
package org.aludratest.service.file;

import org.aludratest.config.ConfigProperties;
import org.aludratest.config.ConfigProperty;
import org.aludratest.service.AludraService;
import org.aludratest.service.ServiceInterface;

/** An AludraTest service which provides access to and file operations on local and network file systems as well as on (S)FTP and
 * HTTP(S) servers.
 * @author Volker Bergmann */
@ServiceInterface(name = "File Service", description = "Offers file related access and verifaction methods.")
@ConfigProperties({
    @ConfigProperty(name = "protocol", type = String.class, description = "The name of the used protocol: file, ftp, sftp, http or https.", defaultValue = "file", required = true),
    @ConfigProperty(name = "base.url", type = String.class, description = "The base URL of the service. On a local filesystem, this is a folder path.", defaultValue = "${user.home}", required = true),
    @ConfigProperty(name = "encoding", type = String.class, description = "The encoding to use when reading / writing text files.", defaultValue = "UTF-8", required = true),
    @ConfigProperty(name = "linefeed", type = String.class, description = "The line feed to use when reading / writing text files. Can be WINDOWS or UNIX", defaultValue = "UNIX", required = true),
    @ConfigProperty(name = "writing.permitted", type = boolean.class, description = "Activates a \"write lock\" if set to false, so all modification operations on this file system will fail.", defaultValue = "false", required = true),
    @ConfigProperty(name = "user", type = String.class, description = "The user name to use for login purposes (unused for file protocol)", required = false),
    @ConfigProperty(name = "password", type = String.class, description = "The password to use for login purposes (unused for file protocol)", required = false) })
public interface FileService extends AludraService {

    /** @see AludraService#perform() */
    @Override
    FileInteraction perform();

    /** @see AludraService#verify() */
    @Override
    FileVerification verify();

    /** @see AludraService#check() */
    @Override
    FileCondition check();

}
