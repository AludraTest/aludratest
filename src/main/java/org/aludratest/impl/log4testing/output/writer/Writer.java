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
package org.aludratest.impl.log4testing.output.writer;

import java.io.File;

import org.aludratest.impl.log4testing.configuration.Validatable;
import org.aludratest.impl.log4testing.data.TestObject;

/**
 * Parent interface for classes to be used for writing logging reports. 
 * @param <T> a test object like a test case or a test suite
 * @author Marcel Malitz
 * @author Volker Bergmann
 */
public interface Writer<T extends TestObject> extends Validatable {

    /**
     * This method gets called by log4testing when the implementing writer class
     * is registered and when log4testing is going to write the logging reports.
     * @param logObject to be written
     * @return a reference to the created file
     */
    public File write(T logObject, String path);

}
