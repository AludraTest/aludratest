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

/** Wraps data for the invocation of the {@link GitClient}'s mv method.
 * @see GitClient#mv(MvData)
 * @author Volker Bergmann */
public class MvData extends AbstractGitData {

    private String source;
    private String destination;

    /** Public default constructor. */
    public MvData() {
        this(null, null);
    }

    /** Fully parameterized constructor.
     * @param source
     * @param destination */
    public MvData(String source, String destination) {
        this.source = source;
        this.destination = destination;
    }

    /** Returns the source path.
     * @return the source path */
    public String getSource() {
        return source;
    }

    /** Sets the source path.
     * @param source the source path to set */
    public void setSource(String source) {
        this.source = source;
    }

    /** Returns the destination.
     * @return the destination */
    public String getDestination() {
        return destination;
    }

    /** Sets the destination.
     * @param destination the destination to set */
    public void setDestination(String destination) {
        this.destination = destination;
    }

}
