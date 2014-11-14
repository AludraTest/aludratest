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

import org.aludratest.impl.log4testing.TechnicalLocator;
import org.aludratest.service.Condition;

/**
 * {@link Condition} interface of the {@link FileService}.
 * @author Volker Bergmann
 */
public interface FileCondition extends Condition {

    /** Tells if a file or folder with the given path exists. 
     *  @param filePath the path of the file of which to check existence 
     *  @return true if the file exists, otherwise false. 
     */
    boolean exists(@TechnicalLocator String filePath);

    /** Tells if the given path represents a directory. 
     *  @param filePath the path of the file of which to check if it is a directory
     *  @return true if the file is a directory, otherwise false. 
     */
    boolean isDirectory(@TechnicalLocator String filePath);

}
