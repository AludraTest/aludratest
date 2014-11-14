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
package org.aludratest.scheduler;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class which logs activities with time stamps 
 * for verifying test execution and scheduling.
 * @author Volker Bergmann
 */
public class Log {

    /** The list of log entries. */
    private static final List<Entry> entries = new ArrayList<Log.Entry>();

    /** The log's start time. */
    private static long startTime;

    // interface ---------------------------------------------------------------

    /** Resets the {@link #startTime} and clears the {@link #entries}. */
    public static synchronized void reset() {
        entries.clear();
        startTime = System.currentTimeMillis();
    }

    /** Logs a message with the current time stamp. 
     *  @param action the action string to log */
    public static synchronized void log(String action) {
        Entry entry = new Entry(action, System.currentTimeMillis() - startTime);
        System.out.println(entry);
        entries.add(entry);
    }

    /** Provides all log entries. 
     *  @return a list of the logged entries */
    public static synchronized List<Entry> getEntries() {
        return entries;
    }

    /** Retrieves the first entry that has the specified action. 
     *  @param action the action string to search
     *  @return the first entry that matches the search term */
    public static Entry getEntry(String action) {
        for (Entry entry : entries)
            if (entry.action.equals(action))
                return entry;
        throw new RuntimeException("No entry found for: " + action);
    }

    /** Wraps a log message with the time stamp at which it occurred. */
    public static class Entry {

        /** The logged action. */
        public final String action;

        /** The time point at which the message was logged. */
        public final long millis;

        /** Constructor receiving all attribute values. 
         * @param action The action string to log
         * @param millis The time point at which the action occurred */
        public Entry(String action, long millis) {
            this.action = action;
            this.millis = millis;
        }

        /** Provides a string representation of the Entry in the format 
         *  &lt;timestamp&gt;: &lt;action&gt; */
        @Override
        public String toString() {
            return millis + ": " + action;
        }
    }

}
