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

/** Common parent class for all Data classes that refer to a refspec in a repository.
 * @author Volker Bergmann */
public abstract class AbstractRefspecData extends AbstractGitData {

    private String repository;
    private String refspec;

    /** Fully parameterized constructor.
     * @param repository the repository
     * @param refspec the refspec */
    public AbstractRefspecData(String repository, String refspec) {
        setRepository(repository);
        setRefspec(refspec);
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

    /** Returns the refspec.
     * @return the refspec */
    public final String getRefspec() {
        return refspec;
    }

    /** Sets the refspec.
     * @param refspec the refspec */
    public final void setRefspec(String refspec) {
        this.refspec = refspec;
    }

}
