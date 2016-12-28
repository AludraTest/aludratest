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

/** Wraps data for the invocation of the {@link GitClient}'s rebase method.
 * @see GitClient#rebase(RebaseData)
 * @author Volker Bergmann */
public class RebaseData {

    private String newbase;
    private String upstream;
    private List<StringData> branches;

    /** Public default constructor. */
    public RebaseData() {
        this(null, null);
    }

    /** Fully parameterized constructor.
     * @param newbase the new base
     * @param upstream the upstream
     * @param branches the branches to rebase */
    public RebaseData(String newbase, String upstream, String... branches) {
        setNewbase(newbase);
        setUpstream(upstream);
        this.branches = new ArrayList<StringData>();
        for (String branch : branches) {
            this.branches.add(new StringData(branch));
        }
    }

    /** Returns the new base.
     * @return the new base */
    public final String getNewbase() {
        return newbase;
    }

    /** Sets the new base.
     * @param newbase the new base */
    public final void setNewbase(String newbase) {
        this.newbase = newbase;
    }

    /** Returns the upstream.
     * @return the upstream */
    public final String getUpstream() {
        return upstream;
    }

    /** Sets the upstream.
     * @param upstream the upstream */
    public final void setUpstream(String upstream) {
        this.upstream = upstream;
    }

    /** Returns the list of branches to rebase.
     * @return the list of branches to rebase */
    public final List<StringData> getBranches() {
        return branches;
    }

    /** Sets the list of branches to rebase.
     * @param branches the list of branches to rebase */
    public final void setBranches(List<StringData> branches) {
        this.branches = branches;
    }

}
