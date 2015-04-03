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

/** Wraps data for the invocation of the {@link GitClient}'s commit method.
 * @see GitClient#commit(CommitData)
 * @author Volker Bergmann */
public class CommitData extends AbstractGitData {

    private String message;
    private String allowEmpty;

    /** Public default constructor. */
    public CommitData() {
        this(null);
    }

    /** Convenience constructor.
     * @param message the commit message */
    public CommitData(String message) {
        this(message, null);
    }

    /** Fully parameterized constructor.
     * @param message the commit message
     * @param allowEmpty flag indicating whether git shall accept empty commits */
    public CommitData(String message, Boolean allowEmpty) {
        setMessage(message);
        this.allowEmpty = (allowEmpty != null ? allowEmpty.toString() : null);
    }

    /** Returns the commit message.
     * @return the commit message */
    public String getMessage() {
        return message;
    }

    /** Sets the commit message.
     * @param message the commit message to set */
    public void setMessage(String message) {
        this.message = message;
    }

    /** Tells if the git shall allow empty commits. If not set to "true", git refused empty commits.
     * @return true if git shall allow empty commits, otherwise false. */
    public String getAllowEmpty() {
        return allowEmpty;
    }

    /** Configures git whether to allow empty commits or not. If not set to "true", git refused empty commits.
     * @param allowEmpty the flag value to set */
    public void setAllowEmpty(String allowEmpty) {
        this.allowEmpty = allowEmpty;
    }

}
