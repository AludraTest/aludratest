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
package org.aludratest;

/**
 * This exception is used when a precondition required for AludraTest to run is not fulfilled.
 * When this exception is thrown, assume that no single test case has been executed.
 * 
 * @author falbrech
 *
 */
public class PreconditionFailedException extends RuntimeException {

    private static final long serialVersionUID = 8247945518493607925L;

    public PreconditionFailedException(String message) {
        super(message);
    }

    public PreconditionFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
