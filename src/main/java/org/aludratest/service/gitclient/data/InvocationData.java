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

import java.util.List;

import org.aludratest.exception.TechnicalException;
import org.aludratest.service.gitclient.GitClient;
import org.databene.commons.CollectionUtil;

/** Wraps data for the invocation of the {@link GitClient}'s invokeGenerically method.
 * @see GitClient#invokeGenerically(InvocationData)
 * @author Volker Bergmann */
public class InvocationData extends AbstractGitData {

    private String processName;
    private List<String> args;
    private String stdOut;
    private String failOnErrOut;

    /** Public default constructor. */
    public InvocationData() {
        this("<generic>");
    }

    /** Constructor with full definition of process name and arguments.
     * @param processName
     * @param args */
    public InvocationData(String processName, String... args) {
        setProcessName(processName);
        setArgs(CollectionUtil.toList(args));
        setFailOnErrOut(null);
        setStdOut(null);
    }

    /** Returns the name of the process.
     * @return the name of the process */
    public String getProcessName() {
        return processName;
    }

    /** Sets the name of the process.
     * @param processName the name of the process to set */
    public void setProcessName(String processName) {
        this.processName = processName;
    }

    /** Returns the invocation arguments.
     * @return the invocation arguments */
    public List<String> getArgs() {
        return args;
    }

    /** Sets the invocation arguments.
     * @param args the invocation arguments to set */
    public void setArgs(List<String> args) {
        this.args = args;
    }

    /** Returns the std out of the process.
     * @return the std out of the process */
    public String getStdOut() {
        return stdOut;
    }

    /** Sets the std out of the process.
     * @param stdOut the std out of the process */
    public void setStdOut(String stdOut) {
        this.stdOut = stdOut;
    }

    /** @return the {@link #failOnErrOut} */
    public String getFailOnErrOut() {
        return failOnErrOut;
    }

    /** Sets the {@link #failOnErrOut} flag. If set to true, the occurrence of content in the process' error out stream is
     * considered to indicate a failure and causes the framework to emit a {@link TechnicalException}.
     * @param failOnErrOut the {@link #failOnErrOut} to set */
    public void setFailOnErrOut(String failOnErrOut) {
        this.failOnErrOut = failOnErrOut;
    }

}
