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
package org.aludratest.service.gui.component.base;

import static org.junit.Assert.assertEquals;

import org.aludratest.AludraTest;
import org.aludratest.LocalTestCase;
import org.aludratest.impl.log4testing.data.TestCaseLog;
import org.aludratest.impl.log4testing.data.TestLogger;
import org.aludratest.impl.log4testing.data.TestStepLog;
import org.aludratest.service.ComponentId;
import org.aludratest.service.gui.web.AludraWebGUI;
import org.aludratest.service.gui.web.selenium.selenium1.AludraSelenium1;
import org.aludratest.service.gui.web.selenium.selenium2.AludraSelenium2;
import org.aludratest.service.util.DirectLogTestListener;
import org.aludratest.testcase.TestStatus;
import org.aludratest.testcase.impl.AludraTestContextImpl;
import org.databene.commons.StringUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

/** Parent class for GUI tests. <br>
 * To execute a web based GUI test from within eclipse, you must serve the required resources with a Jetty server. Start the Jetty
 * server by calling <code>mvn jetty:run</code> from the command line, then execute the JUnit tests as usual from eclipse.
 * 
 * @author YWANG
 * @author Volker Bergmann */
@SuppressWarnings("javadoc")
public abstract class GUITest extends LocalTestCase {

    private AludraTest aludraTest;

    protected AludraWebGUI aludraWebGUI;
    protected GUITestUIMap guiTestUIMap;
    protected ComponentId<AludraWebGUI> serviceId;
    protected TestCaseLog tCase;
    static int testMethodID = 0;

    protected static final int DEFAULT_TIMEOUT = 1000;

    @Before
    public void setUp() throws Exception {
        if (shallPerformLocalTests()) {
            initializeAludra();
        }
    }

    @After
    public void tearDown() throws Exception {
        tearDownAludra();
    }

    protected void initializeAludra() {
        logger.debug("Initializing AludraTest");
        // reset AludraTest instance, if there is already one created
        aludraTest = AludraTest.startFramework();

        // set test case instance, gives every test method a new ID
        tCase = TestLogger.getTestCase(this.getClass().getName() + testMethodID++);
        tCase.newTestStepGroup("initialization");
        setContext(new AludraTestContextImpl(new DirectLogTestListener(tCase), aludraTest.getServiceManager()));

        // configure aludra service with url.of.aut
        System.setProperty("ALUDRATEST_CONFIG/seleniumWrapper/_testui/url.of.aut", getTestPageUrl());

        // get aludra service
        serviceId = ComponentId.create(AludraWebGUI.class, "testui");
        aludraWebGUI = getService(serviceId);
        // open test page
        openTestPage();
        // get uimap
        guiTestUIMap = new GUITestUIMap(aludraWebGUI);
        logger.debug("AludraTest initialization finished");
    }

    private void tearDownAludra() {
        logger.debug("Closing AludraTest");
        if (aludraWebGUI != null) { // if precondition is violated, the service has not been initialized
            closeService(serviceId);
        }
        if (aludraTest != null) {
            aludraTest.stopFramework();
        }
    }

    protected static void activateSelenium1() {
        overrideImplementor(AludraWebGUI.class, AludraSelenium1.class);
    }

    protected static void activateSelenium2() {
        overrideImplementor(AludraWebGUI.class, AludraSelenium2.class);
    }

    /**
     * check if expected status equals to the last step status
     * @param expectedStatus expected status
     */
    protected void checkLastStepStatus(TestStatus expectedStatus) {
        // get the last test step
        TestStepLog lastStep = tCase.getLastTestStep();
        // format the failure message
        String message = lastStep.getErrorMessage();
        if (StringUtil.isEmpty(message)) {
            message = lastStep.getComment();
        }
        message = "Last test step message: '" + StringUtil.nullToEmpty(message) + "'.";
        // assert
        assertEquals(message, expectedStatus, lastStep.getStatus());
    }

    /**
     * check if expected error message equals to the error message in the last test step
     * @param expectedErrorMessage expected error message
     */
    protected void checkLastStepErrorMessage(String expectedErrorMessage) {
        assertEquals(expectedErrorMessage, getLastStepErrorMessage());
    }

    /**
     * check if error message in the last test step matches the expected error message pattern
     * 
     * @param expectedErrorMessagePattern
     *            expected error message pattern (regular expression)
     */
    protected void checkLastStepErrorMessageMatches(String expectedErrorMessagePattern) {
        Assert.assertTrue(getLastStepErrorMessage().matches(expectedErrorMessagePattern));
    }

    // Open test page
    private void openTestPage() {
        aludraWebGUI.perform().open();
        aludraWebGUI.perform().windowMaximize();
        //		aludraWebGUI.perform().windowFocus();
        aludraWebGUI.perform().refresh();
    }

    // Get error message of the last test step
    private String getLastStepErrorMessage() {
        return tCase.getLastTestStep().getErrorMessage();
    }

    protected String getTestPageUrl() {
        return "http://localhost:8080/index.html";
    }

    protected static void overrideImplementor(Class<?> ifaceClass, Class<?> implClass) {
        System.setProperty("ALUDRATEST_CONFIG/aludraservice/" + ifaceClass.getName(), implClass.getName());
    }

}
