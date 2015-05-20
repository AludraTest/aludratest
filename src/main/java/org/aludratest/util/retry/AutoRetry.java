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
package org.aludratest.util.retry;

import java.lang.reflect.Method;

import org.aludratest.config.InternalComponent;

/** The AutoRetry component is responsible for a given method invocation which caused a given exception if the operation shall be
 * retried, even after a given already performed retry count. <br>
 * The default implementation can easily be configured using a properties based configuration.
 * 
 * @author falbrech */
@InternalComponent
public interface AutoRetry {

    /** Checks if a retry shall be performed after an invocation of the given method caused the given Throwable.
     * <code>retryCount</code> retries have already been performed for the current method invocation.
     * 
     * @param method Method which caused the given Throwable on invocation.
     * @param t Throwable thrown by method invocation.
     * @param retryCount Already performed retries on the current method invocation (starts with 0).
     * 
     * @return <code>true</code> if a retry of the method invocation should be performed, <code>false</code> otherwise. */
    public boolean matches(Method method, Throwable t, int retryCount);

}
