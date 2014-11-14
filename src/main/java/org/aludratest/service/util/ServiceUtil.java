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
package org.aludratest.service.util;

import org.aludratest.exception.AutomationException;
import org.aludratest.service.locator.Locator;

/** Provides general utility methods for AludraTest services.
 * @author Volker Bergmann */
public final class ServiceUtil {

    private ServiceUtil() {
    }

    /**
     * Creates an exception that represents an unsupported {@link Locator}
     * @param locator the unsupported {@link Locator}
     * @return an exception that represents the unsupported locator
     */
    public static AutomationException newUnsupportedLocatorException(Object locator) {
        return new AutomationException("Not a supported locator type: " +
                (locator == null ? "null" : locator.getClass().getName()));
    }

}
