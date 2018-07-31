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
package org.aludratest.service.gitclient.data;

import java.util.ArrayList;
import java.util.List;

import org.aludratest.service.gitclient.GitClient;

/** Wraps data for the invocation of the {@link GitClient}'s log method.
 * @see GitClient#log(LogData)
 * @author Volker Bergmann */
public class LogData extends AbstractGitData {

    private String maxCount;
    private List<LogItemData> items;

    /** Public default constructor. */
    public LogData() {
        setMaxCount(null);
        setItems(new ArrayList<LogItemData>());
    }

    /** Creates a LogData object with the given maxCount.
     * @param maxCount the maxCount to set
     * @return a new LogData object set to the provided maxCount */
    public static LogData createWithMaxCount(int maxCount) {
        LogData result = new LogData();
        result.setMaxCount(String.valueOf(maxCount));
        return result;
    }

    /** Returns the maxCount
     * @return the {@link #maxCount} */
    public final String getMaxCount() {
        return maxCount;
    }

    /** Sets the {@link #maxCount}.
     * @param maxCount the maxCount */
    public final void setMaxCount(String maxCount) {
        this.maxCount = maxCount;
    }

    /** Returns the log items returned be the git invocation.
     * @return the log items returned be the git invocation */
    public final List<LogItemData> getItems() {
        return items;
    }

    /** Sets the log items returned be the git invocation.
     * @param items the log items returned be the git invocation */
    public final void setItems(List<LogItemData> items) {
        this.items = items;
    }

}
