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
package org.aludratest.util;

import java.lang.reflect.InvocationTargetException;

/** Provides exception related utility methods.
 * @author Volker Bergmann */
public class ExceptionUtil {

    private ExceptionUtil() {
        // prevent instantiation of static class
    }

    /** Unwraps the root cause of an exception.
     * @param t the Throwable to examine
     * @return If the argument is an {@link InvocationTargetException} with a non-null cause, the method returns that cause
     *         exception, otherwise the {@link Throwable} object itself */
    public static Throwable unwrapInvocationTargetException(Throwable t) {
        while (t instanceof InvocationTargetException && t.getCause() != null) {
            t = t.getCause();
        }
        return t;
    }

}
