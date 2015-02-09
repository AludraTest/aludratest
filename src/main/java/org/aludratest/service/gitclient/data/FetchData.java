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

/** Wraps data for the invocation of the {@link GitClient}'s fetch method.
 * @see GitClient#fetch(FetchData)
 * @author Volker Bergmann */
public class FetchData {

    private String repository;

    /** Public default constructor. */
    public FetchData() {
        this(null);
    }

    /** Fully parameterized constructor.
     * @param repository */
    public FetchData(String repository) {
        setRepository(repository);
    }

    /** Returns the repository.
     * @return the repository */
    public String getRepository() {
        return repository;
    }

    /** Sets the repository.
     * @param repository the repository to set */
    public void setRepository(String repository) {
        this.repository = repository;
    }

}
