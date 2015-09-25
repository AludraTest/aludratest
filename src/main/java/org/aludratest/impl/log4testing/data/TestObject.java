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
package org.aludratest.impl.log4testing.data;

import java.util.HashMap;
import java.util.Map;

import org.aludratest.testcase.TestStatus;
import org.joda.time.DateTime;
import org.joda.time.Duration;

/**
 * Every test object even if it is a simple test step or a complex test suite
 * with several test cases and test suites as its children must provide the
 * general information of this object.</br> A test object of log4testing is an
 * object which stores logging information about the execution of test commands.
 * Test commands can be technical or more domain based ones what depends on the
 * context in which log4testing is used.
 */
public abstract class TestObject {

    /**
     * Some explanations about the test object and what it should do. The text
     * should be human readable and written in a way which can be understood by
     * people who will read test results.
     */
    private String comment = null;

    private Map<String, String> tags = new HashMap<String, String>();

    /**
     * Each TestObject has a unique id. On the next creation of a TestObject the
     * next TestObject gets the id of this attribute. For obtaining a new id, use
     * {@link #getNextId()}.
     */
    private static int nextId = 0;

    /**
     * Unique id of an TestObject. This id is unique in the context of the virtual
     * machine in which this code is running. The id is not unique across
     * several java virtual machine instances and different executions.
     */
    private String id = getNextId();

    // Creation of multiple threads which try to create new test steps.
    // When this method wouldn't be synchronized, some test objects will have
    // the same id.
    /**
     * Returns the next unique id which is used for the identification of this
     * {@link TestObject}.
     * 
     * @return the next unique id
     * @see TestObject#id
     */
    private static synchronized String getNextId() {
        int id = nextId;
        ++nextId;
        return Integer.toString(id);
    }

    /**
     * @return the unique id of this test object
     */
    public String getId() {
        return id;
    }

    /** @return the {@link TestStatus} of the test object */
    public abstract TestStatus getStatus();

    /**
     * @return time when the test object has been started
     */
    public abstract DateTime getStartingTime();

    /**
     * @return time when the test object has finished
     */
    public abstract DateTime getFinishingTime();

    /**
     * @return <code>true</code> if a test represented by this test object has
     *         failed; <code>false</code> otherwise
     */
    public abstract boolean isFailed();

    /**
     * Calculates and returns the duration of how long it took to execute the
     * test operations whose test results are represented by this test object
     * 
     * @return duration duration between starting and finishing time
     */
    public Duration getDuration() {
        DateTime startingTime = getStartingTime();
        DateTime finishingTime = getFinishingTime();
        Duration duration = new Duration(0);
        if (startingTime != null) {
            if (finishingTime == null) {
                finishingTime = new DateTime(); // if the object is still running, provide the currently elapsed time
            }
            duration = new Duration(startingTime, finishingTime);
        }
        return duration;
    }

    /**
     * Is used to set a comment which explains what this test object shall do. A
     * test writer can provide information like a textual explanation are some
     * details about settings, parameters or other data used for test actions
     * represented by this test object.
     * 
     * @param comment
     *            with some detailed information about the test object
     * @return the test object itself
     */
    public TestObject setComment(String comment) {
        this.comment = comment;
        return this;
    }

    /**
     * @return the comment with some detailed information about the test object
     */
    public String getComment() {
        return comment;
    }

    /** Returns the value of a tag
     *  @param key the key of the requested tag
     *  @return the value of the tag or null if it has not been defined */
    public String getTag(String key) {
        return this.tags.get(key);
    }

    /** Sets a tag
     *  @param key
     *  @param value */
    public void setTag(String key, String value) {
        this.tags.put(key, value);
    }

}
