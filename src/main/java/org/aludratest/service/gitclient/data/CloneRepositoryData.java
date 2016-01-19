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

/** Wraps data for the invocation of the {@link GitClient}'s clone method.
 * @see GitClient#clone(CloneRepositoryData)
 * @author Volker Bergmann */
public class CloneRepositoryData extends AbstractGitData {

    private String repository;
    private String directory;

    /** Public default constructor. */
    public CloneRepositoryData() {
        this(null);
    }

    /** Convenience constructor.
     * @param repository */
    public CloneRepositoryData(String repository) {
        this(repository, null);
    }

    /** Fully parameterized constructor.
     * @param repository the source repository to clone
     * @param directory the target directory in which to store the clone */
    public CloneRepositoryData(String repository, String directory) {
        setRepository(repository);
        setDirectory(directory);
    }

    /** Returns the repository.
     * @return the repository */
    public final String getRepository() {
        return repository;
    }

    /** Sets the repository.
     * @param repository the repository to set */
    public final void setRepository(String repository) {
        this.repository = repository;
    }

    /** Returns the target directory in which to store the clone.
     * @return the target directory in which to store the clone */
    public final String getDirectory() {
        return directory;
    }

    /** Sets the target directory in which to store the clone.
     * @param directory the target directory in which to store the clone */
    public final void setDirectory(String directory) {
        this.directory = directory;
    }

}
