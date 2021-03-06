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

/** Wraps data for the invocation of the {@link GitClient}'s checkout method.
 * @see GitClient#checkout(CheckoutData)
 * @author Volker Bergmann */
public class CheckoutData {

    private String branchName;

    /** Public default constructor. */
    public CheckoutData() {
        this(null);
    }

    /** Fully parameterized constructor.
     * @param branchName the name of the branch to check out */
    public CheckoutData(String branchName) {
        setBranchName(branchName);
    }

    /** @return the name of the branch to check out Returns the name of the branch to check out */
    public final String getBranchName() {
        return branchName;
    }

    /** Sets the name of the branch to check out.
     * @param branchName the name of the branch to check out */
    public final void setBranchName(String branchName) {
        this.branchName = branchName;
    }

}
