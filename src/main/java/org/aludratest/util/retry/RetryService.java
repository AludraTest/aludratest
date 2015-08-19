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

import java.util.concurrent.Callable;

import org.aludratest.util.ExceptionUtil;
import org.databene.commons.Assert;

/** Invokes a method on an object with the provided arguments. If an exception of a tolerated exception type occurs, the the
 * invocation is repeated until
 * <ul>
 * <li>the invocation is successful</li>
 * <li>an exception of an untolerated type occurs</li>
 * <li>the maximum number of allowed retries is exceeded</li>
 * </ul>
 * @author Volker Bergmann */
public class RetryService {

    /** Performs the invocation an exception handling as described in the class doc.
     * @param target
     * @param toleratedThrowable
     * @param maxRetries
     * @return
     * @throws Throwable */
    public static <T> T call(Callable<T> target, Class<? extends Throwable> toleratedThrowable, int maxRetries)
            throws Throwable { // NOSONAR
        // check preconditions
        Assert.notNull(target, "target");

        // invocation cycle
        int retryCount = 0;
        boolean doRetry;
        Throwable recentException = null;
        do {
            doRetry = false;
            try {
                return target.call();
            }
            catch (Exception e) { // NOSONAR
                // handle exceptions
                Throwable t = ExceptionUtil.unwrapInvocationTargetException(e);
                recentException = t;
                if (toleratedThrowable != null && toleratedThrowable.isAssignableFrom(e.getClass()) && retryCount < maxRetries) {
                    doRetry = true;
                    retryCount++;
                }
            }
        }
        while (doRetry);
        // the loop has finished without successful execution, so throw the most recent exception
        throw recentException;
    }

}
