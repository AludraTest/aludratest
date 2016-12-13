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

/** Provides data structure for the result of the {@link GitClient}'s status method.
 * @see GitClient#status(StatusData)
 * @author Volker Bergmann */
public class RenamedStatusData extends AbstractGitData {

    private String fromPath;
    private String toPath;

    /** Public default constructor. */
    public RenamedStatusData() {
        this(null, null);
    }

    /** Full constructor.
     * @param fromPath
     * @param toPath */
    public RenamedStatusData(String fromPath, String toPath) {
        setFromPath(fromPath);
        setToPath(toPath);
    }

    /** Returns the {@link #fromPath}
     * @return the {@link #fromPath} */
    public final String getFromPath() {
        return fromPath;
    }

    /** Sets the {@link #fromPath}.
     * @param fromPath sets the {@link #fromPath} */
    public final void setFromPath(String fromPath) {
        this.fromPath = fromPath;
    }

    /** Returns the {@link #toPath}
     * @return the {@link #toPath} */
    public final String getToPath() {
        return toPath;
    }

    /** Sets the {@link #toPath}
     * @param toPath the {@link #toPath} to set */
    public final void setToPath(String toPath) {
        this.toPath = toPath;
    }

    @Override
    public int hashCode() {
        int result = 0;
        if (fromPath != null) {
            result = result * 31 + fromPath.hashCode();
        }
        if (toPath != null) {
            result = result * 31 + toPath.hashCode();
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        RenamedStatusData that = (RenamedStatusData) obj;
        return NullSafeComparator.equals(this.fromPath, that.fromPath) && NullSafeComparator.equals(this.toPath, that.toPath);
    }

    @Override
    public String toString() {
        return fromPath + " -> " + toPath;
    }

}
