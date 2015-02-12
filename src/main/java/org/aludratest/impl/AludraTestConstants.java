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
package org.aludratest.impl;

/** Provides constants applicable to all AludraTest modules.
 * @author Volker Bergmann */
public interface AludraTestConstants {

    /** OS return code for normal execution */
    public static final int EXIT_NORMAL = 0;

    /** OS return code for program errors */
    public static final int EXIT_EXECUTION_ERROR = 1;

    /** OS return code for invocations with an illegal argument */
    public static final int EXIT_ILLEGAL_ARGUMENT = 3;

    /** OS return code for signaling some kind of failure (not an error!) */
    public static final int EXIT_EXECUTION_FAILURE = 4;

}
