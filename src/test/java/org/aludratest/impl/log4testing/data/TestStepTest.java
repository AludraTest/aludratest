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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.aludratest.impl.log4testing.data.TestStepLog;
import org.aludratest.impl.log4testing.data.attachment.Attachment;
import org.aludratest.impl.log4testing.data.attachment.StringAttachment;
import org.aludratest.testcase.TestStatus;
import org.joda.time.DateTime;
import org.junit.Test;

/**
 * Tests the {@link TestStepLog} class
 */
public class TestStepTest {

    /**
     * Tests the function getStartingTime() with a created time and 100 ms delay
     */
    @Test
    public void testGetStartingTime() {
        DateTime startingTime = new DateTime();
        TestStepLog newTestStep = new TestStepLog(null);

        if (!((newTestStep.getStartingTime() == startingTime) || (newTestStep.getStartingTime().isBefore(startingTime.plusMillis(100))))) {
            fail("Starting time not Equal");
        }

    }

    /**
     * Tests the function getFinishingTime() with a created time and 100 ms
     * delay
     */
    @Test
    public void testGetFinishingTime() {
        DateTime finishingTime = new DateTime();
        TestStepLog newTestStep = new TestStepLog(null);
        newTestStep.finish();
        if (!((newTestStep.getFinishingTime() == finishingTime) || (newTestStep.getFinishingTime().isBefore(finishingTime.plusMillis(100))))) {
            fail("Finishing time not Equal");
        }
    }

    /**
     * Tests the function IsFailed() with a Error TestStep
     */
    @Test
    public void testIsFailed() {
        TestStepLog failed = new TestStepLog(null);
        failed.setStatus(TestStatus.FAILED);

        if (!(failed.isFailed()))
            fail("TestStep with Status ERROR is not isFailed()");
    }

    /**
     * Tests the functions SetCommand(), getCommand() and clearComment()
     */
    @Test
    public void testSetGetCommand() {
        String command = "the command";
        TestStepLog testStep = new TestStepLog(null);
        assertNull(testStep.getCommand());
        testStep.setCommand(command);
        assertEquals(command, testStep.getCommand());
    }

    /**
     * Tests the function setStatus(), getStatus() and clearStatus()
     */
    @Test
    public void testSetGetClearStatus() {
        TestStepLog testStep = new TestStepLog(null);
        testStep.setStatus(TestStatus.INCONCLUSIVE);
        assertEquals(testStep.getStatus(), TestStatus.INCONCLUSIVE);
    }

    /**
     * Tests the function getId()
     */
    @Test
    public void testGetId() {
        TestStepLog testStep = new TestStepLog(null);
        assertNotNull(testStep.getId());
    }

    /**
     * Tests the functions addAttachement(), getAttachements() and
     * clearAttachements()
     */
    @Test
    public void testAddGetAttachment() {
        ArrayList<Attachment> attachments = new ArrayList<Attachment>();
        final Attachment attachment = new StringAttachment("My Attachment", "Hello World!", "txt");
        TestStepLog testStep = new TestStepLog(null);
        assertEquals(testStep.getAttachments(), attachments);
        testStep.addAttachment(attachment);
        assertEquals(testStep.getAttachments().iterator().next(), attachment);
    }

    /**
     * Test the function start(), which is called when a TestStep is created
     */
    @Test
    public void testStart() {
        // When the Constructor is called, the constructor calls start()
        TestStepLog testStep = new TestStepLog(null);
        assertNotNull(testStep.getStartingTime());

    }

    /**
     * Tests the function finish() which sets the finishingTime
     */
    @Test
    public void testFinish() {
        TestStepLog testStep = new TestStepLog(null);
        testStep.finish();
        assertNotNull(testStep.getFinishingTime());
    }

    /**
     * Tests the function getDuration()
     */
    @Test
    public void testGetDuration() {
        TestStepLog testStep = new TestStepLog(null);
        assertEquals(testStep.getDuration().toPeriod().getSeconds(), 0);
        testStep.finish();
        if (testStep.getDuration().toPeriod().getSeconds() >= 1)
            fail("Error in GetDuration");
    }

    /**
     * Tests the functions setComment() and getComment()
     */
    @Test
    public void testSetGetComment() {
        String comment = "the comment";
        TestStepLog testStep = new TestStepLog(null);
        assertNull(testStep.getComment());
        testStep.setComment(comment);
        assertEquals(comment, testStep.getComment());
    }
    
}
