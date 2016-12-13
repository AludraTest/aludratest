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
import org.databene.commons.NullSafeComparator;

/** Wraps data for the invocation of the {@link GitClient}'s version method.
 * @see GitClient#version(VersionData)
 * @author Volker Bergmann */
public class VersionData extends AbstractGitData {

    private String versionNumber;

    // constructors ------------------------------------------------------------

    /** Public default constructor. */
    public VersionData() {
        setVersionNumber(null);
    }

    // properties --------------------------------------------------------------

    /** @return the version number returned by git. */
    public final String getVersionNumber() {
        return versionNumber;
    }

    /** Sets the version number information.
     * @param versionNumber */
    public final void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }

    // java-lang.Object overrides ----------------------------------------------

    @Override
    public int hashCode() {
        return (versionNumber != null ? versionNumber.hashCode() : 0);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        VersionData that = (VersionData) obj;
        return NullSafeComparator.equals(this.versionNumber, that.versionNumber);
    }

    @Override
    public String toString() {
        return "git version " + versionNumber;
    }

}
