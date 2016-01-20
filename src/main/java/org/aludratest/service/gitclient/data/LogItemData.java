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

import org.aludratest.service.gitclient.GitClient;
import org.databene.commons.SystemInfo;

/** Wraps result data of the {@link GitClient}'s log method.
 * @see GitClient#log(LogData)
 * @author Volker Bergmann */
public class LogItemData extends AbstractGitData {

    private static final String LF = SystemInfo.getLineSeparator();

    private String commit;
    private String merge;
    private String author;
    private String date;
    private String message;

    /** Public default constructor. */
    public LogItemData() {
        this(null, null, null, null, null);
    }

    /** Full constructor.
     * @param commit the commit message
     * @param merge the merge info
     * @param author the author
     * @param date the date
     * @param message the message */
    public LogItemData(String commit, String merge, String author, String date, String message) {
        setCommit(commit);
        setMerge(merge);
        setAuthor(author);
        setDate(date);
        setMessage(message);
    }

    /** Returns the commit message.
     * @return the commit message */
    public final String getCommit() {
        return commit;
    }

    /** Sets the commit message.
     * @param commit the commit message */
    public final void setCommit(String commit) {
        this.commit = commit;
    }

    /** Returns the merge info.
     * @return the merge info */
    public final String getMerge() {
        return merge;
    }

    /** Sets the merge info.
     * @param merge the merge info to set */
    public final void setMerge(String merge) {
        this.merge = merge;
    }

    /** Returns the author.
     * @return the author */
    public final String getAuthor() {
        return author;
    }

    /** Sets the author.
     * @param author the author */
    public final void setAuthor(String author) {
        this.author = author;
    }

    /** Returns the date.
     * @return the date */
    public final String getDate() {
        return date;
    }

    /** Sets the date.
     * @param date the date */
    public final void setDate(String date) {
        this.date = date;
    }

    /** Returns the message.
     * @return the message */
    public final String getMessage() {
        return message;
    }

    /** Sets the message.
     * @param message the message */
    public final void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "commit " + commit + LF + "Author: " + author + LF + "Date:   " + date + LF + LF + "    " + message;
    }

}
