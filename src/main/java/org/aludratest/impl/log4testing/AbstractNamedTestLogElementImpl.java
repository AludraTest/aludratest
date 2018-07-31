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
package org.aludratest.impl.log4testing;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.aludratest.log4testing.NamedTestLogElement;
import org.aludratest.log4testing.TestLogElement;
import org.aludratest.log4testing.TestStatus;
import org.joda.time.DateTime;
import org.joda.time.Duration;

/** Abstract base class for log element implementation classes. Provides setters for all mutable fields. This implementation is
 * Thread-safe.
 * 
 * @author falbrech */
public abstract class AbstractNamedTestLogElementImpl implements NamedTestLogElement {

    private static AtomicInteger objectId = new AtomicInteger();

    private int id;

    private String name;

    private DateTime startTime;

    private DateTime endTime;

    protected AbstractNamedTestLogElementImpl(String name) {
        id = objectId.incrementAndGet();
        this.name = name;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public synchronized DateTime getStartTime() {
        return startTime;
    }

    /** Sets the timestamp when this element started.
     * 
     * @param startTime Timestamp when this element started. */
    public synchronized void setStartTime(DateTime startTime) {
        this.startTime = startTime;
    }

    @Override
    public synchronized DateTime getEndTime() {
        return endTime;
    }

    /** Sets the timestamp when this element finished.
     * 
     * @param endTime Timestamp when this element finished. */
    public synchronized void setEndTime(DateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public synchronized Duration getDuration() {
        if (startTime == null) {
            return null;
        }

        return new Duration(startTime, endTime == null ? new DateTime() : endTime);
    }

    /** Calculates the status of this element, based on the status of all child elements. The most "severe" status is returned; if
     * childElements is empty, PASSED is returned. If any child is currently RUNNING, this is returned as status.
     * 
     * @param childElements Child elements to use for status check.
     * 
     * @return The status of this element, as calculated from child elements. */
    protected final TestStatus getStatus(List<? extends TestLogElement> childElements) {
        TestStatus status = TestStatus.PASSED;
        for (TestLogElement element : childElements) {
            if (element.getStatus() == TestStatus.RUNNING) {
                return TestStatus.RUNNING;
            }
            if (element.getStatus().ordinal() < status.ordinal()) {
                status = element.getStatus();
            }
        }

        return status;
    }

}
