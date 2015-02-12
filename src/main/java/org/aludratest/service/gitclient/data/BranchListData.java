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

/** Wraps data for the invocation of the {@link GitClient}'s listBranches method.
 * @see GitClient#listBranches(BranchListData)
 * @author Volker Bergmann */
public class BranchListData extends AbstractGitData {

    private String currentBranch;
    private List<StringData> branches;

    /** Public default constructor. */
    public BranchListData() {
        setCurrentBranch(null);
        this.branches = new ArrayList<StringData>();
    }

    /** Returns the name of the current branch.
     * @return the name of the current branch */
    public String getCurrentBranch() {
        return currentBranch;
    }

    /** Sets the name of the current branch.
     * @param currentBranch */
    public void setCurrentBranch(String currentBranch) {
        this.currentBranch = currentBranch;
    }

    /** @return a list with the names of all branches */
    public List<StringData> getBranches() {
        return branches;
    }

    /** Sets the list with the names of all branches.
     * @param branches the list with the branch names to set */
    public void setBranches(List<StringData> branches) {
        this.branches = branches;
    }

}
