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

/** Wraps data for the invocation of the {@link GitClient}'s reset method.
 * @see GitClient#resetSoft(ResetData)
 * @see GitClient#resetMixed(ResetData)
 * @see GitClient#resetHard(ResetData)
 * @author Volker Bergmann */
public class ResetData extends AbstractGitData {

    private String commit;

    /** Public default constructor. */
    public ResetData() {
        this(null);
    }

    /** Fully parameterized constructor.
     * @param commit the name of the commit */
    public ResetData(String commit) {
        setCommit(commit);
    }

    /** Returns the name of the commit
     * @return the name of the commit */
    public String getCommit() {
        return commit;
    }

    /** Sets the name of the commit.
     * @param commit the name of the commit */
    public void setCommit(String commit) {
        this.commit = commit;
    }

}
