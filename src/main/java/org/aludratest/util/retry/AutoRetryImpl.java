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
import java.util.ArrayList;
import java.util.List;

import org.aludratest.config.ConfigNodeProperty;
import org.aludratest.config.ConfigProperties;
import org.aludratest.config.ConfigProperty;
import org.aludratest.config.Configurable;
import org.aludratest.config.MutablePreferences;
import org.aludratest.config.Preferences;
import org.aludratest.config.ValidatingPreferencesWrapper;
import org.aludratest.service.Implementation;
import org.aludratest.util.retry.AutoRetryImpl.AutoRetryEntry;
import org.databene.commons.BeanUtil;

/**
 * Configurable mechanism for automatically retrying a certain invocation
 * after a certain errors for a given number of times.
 * If each of these invocations throws an exception, the last one is returned to the caller.
 * @author Volker Bergmann
 */
@Implementation({ AutoRetry.class })
@ConfigNodeProperty(name = "retry", appendCounterPattern = true, elementType = AutoRetryEntry.class, description = "A list of retry entries. Each entry configures a circumstance which allows a configured number of retries of the failed operation.")
public class AutoRetryImpl implements AutoRetry, Configurable {

    /** List of all AutoRetry entries. */
    private List<AutoRetryEntry> entries;

    @Override
    public String getPropertiesBaseName() {
        return "retry";
    }

    @Override
    public void fillDefaults(MutablePreferences preferences) {
        // nothing to do
    }

    @Override
    public void configure(Preferences preferences) {
        entries = new ArrayList<AutoRetryEntry>();

        for (String nodeName : preferences.getChildNodeNames()) {
            if (nodeName.matches("retry[0-9]+")) {
                AutoRetryEntry entry = new AutoRetryEntry();
                ValidatingPreferencesWrapper values = new ValidatingPreferencesWrapper(preferences.getChildNode(nodeName));
                entry.maxCount = values.getRequiredIntValue("maxCount");
                entry.methodName = values.getRequiredStringValue("method");
                entry.exceptionClass = BeanUtil.forName(values.getRequiredStringValue("exception"));
                entry.interfaceClass = BeanUtil.forName(values.getRequiredStringValue("interface"));
                entries.add(entry);
            }
        }
    }

    @Override
    public boolean matches(Method method, Throwable t, int retryCount) {
        for (AutoRetryEntry entry : entries) {
            if (entry.matches(method, t, retryCount)) {
                return true;
            }
        }
        return false;
    }

    /** An entry in the internal list of retry elements.
     * 
     * @author falbrech */
    @ConfigProperties({
        @ConfigProperty(name = "maxCount", type = int.class, description = "The maximum retry count before no more retries are performed.", required = true),
        @ConfigProperty(name = "method", type = String.class, description = "The name of the invoked method for which to perform the retries in case of an exception.", required = true),
        @ConfigProperty(name = "interface", type = String.class, description = "The name of a service interface for which this retry entry shall be applied.", required = true),
        @ConfigProperty(name = "exception", type = String.class, description = "The name of the exception class which forces a retry when raised on the given method in a service of the given type.", required = true) })
    public static class AutoRetryEntry {

        private int maxCount;

        private Class<?> exceptionClass;

        private Class<?> interfaceClass;

        private String methodName;

        private AutoRetryEntry() {
        }

        /**
         * Tells if the conditions under which an exception occurred, match the configuration of this AutoRetry instance.
         */
        private boolean matches(Method method, Throwable t, int retryCount) {
            return (exceptionClass.isAssignableFrom(t.getClass()) && method.getName().equals(methodName)
                    && interfaceClass.isAssignableFrom(method.getDeclaringClass()) && retryCount < maxCount);
        }
    }

}
