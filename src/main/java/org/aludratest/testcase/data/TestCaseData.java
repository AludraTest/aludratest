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
package org.aludratest.testcase.data;

import org.aludratest.dict.Data;
import org.databene.commons.ArrayUtil;

/** Represents the data for a single invocation of a method of a test class. Conceptually spoken, a concrete instance of this class
 * <b>is</b> the "test case". More formally, the combination of a test method <i>M</i> and a <code>TestCaseData</code> object
 * <i>D</i> makes up the test case <i>T</i>: <code>T = (M, D)</code> or <code>T = M(D)</code>.
 * 
 * @author falbrech */
public final class TestCaseData {

    private String id;

    private String description;

    private Data[] data;

    private boolean ignored;

    private Throwable exception;

    /** Constructs a new TestCaseData object.
     * 
     * @param id ID for the test case data. Must be not <code>null</code> and unique within all datasets for the same test method
     *            (this will be checked by the framework before test invocation).
     * @param description Description for the test case data. May be <code>null</code>.
     * @param data The array of parameters for the test method to use for a single test invocation. May be <code>null</code> if
     *            the method does not require any parameters. Otherwise, it must have as many entries as the method has
     *            parameters. */
    public TestCaseData(String id, String description, Data[] data) {
        this(id, description, data, false);
    }

    /** Constructs a new TestCaseData object.
     * 
     * @param id ID for the test case data. Must be not <code>null</code> and unique within all datasets for the same test method
     *            (this will be checked by the framework before test invocation).
     * @param description Description for the test case data. May be <code>null</code>.
     * @param data The array of parameters for the test method to use for a single test invocation. May be <code>null</code> if
     *            the method does not require any parameters. Otherwise, it must have as many entries as the method has
     *            parameters.
     * @param ignored If <code>true</code>, marks this data set as ignored, i.e. this test case shall not be invoked (this
     *            behaviour can be disabled by Framework settings). */
    public TestCaseData(String id, String description, Data[] data, boolean ignored) {
        this.id = id;
        this.description = description;
        this.data = data == null ? new Data[0] : ArrayUtil.copyOfRange(data, 0, data.length);
        this.ignored = ignored;
    }

    /** Constructs a new TestCaseData object, for a dataset which could not be loaded for a given reason.
     * 
     * @param id ID for the test case data. Must be not <code>null</code> and unique within all datasets for the same test method
     *            (this will be checked by the framework before test invocation).
     * @param exception The cause why the data for this dataset could not be loaded. */
    public TestCaseData(String id, Throwable exception) {
        this.id = id;
        this.exception = exception;
    }

    /** Returns a unique ID for this test case dataset (unique within the parent test suite, represented by the test class). Can
     * also contain a short description, e.g. <code>A3456_max_length_for_street</code>. Should not contain space characters!
     * 
     * @return A unique ID for this test case dataset, never <code>null</code>. */
    public String getId() {
        return id;
    }

    /** Returns a textual description for this test case dataset. May be <code>null</code> if no description is available.
     * 
     * @return A textual description for this test case dataset, or <code>null</code>. */
    public String getDescription() {
        return description;
    }

    /** Returns the data elements for this test case, in the order of the parameter list of the test case method. May be an empty
     * array if the test case method does not require any parameters. Otherwise, it must have as many entries as the method's
     * parameter list.
     * 
     * @return The data elements for this test case, maybe empty, but never <code>null</code>. */
    public Data[] getData() {
        return data;
    }

    /** Returns <code>true</code> if this dataset should not be tested, but skipped.
     * 
     * @return <code>true</code> if this dataset should not be tested, but skipped, <code>false</code> otherwise. */
    public boolean isIgnored() {
        return ignored;
    }

    /** Returns an exception that describes why this dataset could not be loaded. If <code>null</code> is returned, the dataset
     * could be loaded successfully.
     * 
     * @return <code>null</code>, or an exception that describes why this dataset could not be loaded. */
    public Throwable getException() {
        return exception;
    }

    @Override
    public String toString() {
        return id + (ignored ? " [IGNORED]" : "");
    }

}
