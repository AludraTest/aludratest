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
import org.aludratest.util.data.StringData;

/** Wraps data for the invocation of the {@link GitClient}'s merge method.
 * @see GitClient#merge(MergeData)
 * @author Volker Bergmann */
public class MergeData extends AbstractGitData {

    String message;
    List<StringData> branches;

    /** Public default constructor. */
    public MergeData() {
        this(null);
    }

    /** Fully parameterized constructor.
     * @param message
     * @param branches */
    public MergeData(String message, String... branches) {
        setMessage(message);
        this.branches = new ArrayList<StringData>();
        for (String branch : branches) {
            this.branches.add(new StringData(branch));
        }
    }

    /** Returns the message.
     * @return the message */
    public final String getMessage() {
        return message;
    }

    /** Sets the message.
     * @param message the message to set */
    public final void setMessage(String message) {
        this.message = message;
    }

    /** Returns a list of the branches.
     * @return the branches */
    public final List<StringData> getBranches() {
        return branches;
    }

    /** Sets the branches.
     * @param branches the branches to set */
    public final void setBranches(List<StringData> branches) {
        this.branches = branches;
    }

}
