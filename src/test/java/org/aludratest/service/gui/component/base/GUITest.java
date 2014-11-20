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

import java.io.File;

import org.aludratest.AludraTest;
import org.aludratest.LocalTestCase;
import org.aludratest.impl.log4testing.data.TestCaseLog;
import org.aludratest.impl.log4testing.data.TestLogger;
import org.aludratest.service.ComponentId;
import org.aludratest.service.gui.web.AludraWebGUI;
import org.aludratest.service.gui.web.selenium.selenium1.AludraSelenium1;
import org.aludratest.service.gui.web.selenium.selenium2.AludraSelenium2;
import org.aludratest.service.util.DirectLogTestListener;
import org.aludratest.testcase.TestStatus;
import org.aludratest.testcase.impl.AludraTestContextImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

/**
 * Parent class for GUI tests.
 * @author YWANG
 */
@SuppressWarnings("javadoc")
public abstract class GUITest extends LocalTestCase {

    private AludraTest aludraTest;

    protected AludraWebGUI aludraWebGUI;
    protected GUITestUIMap guiTestUIMap;
    protected ComponentId<AludraWebGUI> serviceId;
    protected TestCaseLog tCase;
    static int testMethodID = 0;

    protected static final int DEFAULT_TIMEOUT = 1000;
    private static final String TEST_PAGE_FOLDER = "src/test/resources/testPages/";

    // Initialization
    @Before
    public void initializeAludra() {
        if (shallPerformLocalTests()) {
            // reset AludraTest instance, if there is already one created
            aludraTest = AludraTest.startFramework();

            // set test case instance, gives every test method a new ID
            tCase = TestLogger.getTestCase(this.getClass().getName() + testMethodID++);
            tCase.newTestStepGroup("initialization");
            setContext(new AludraTestContextImpl(new DirectLogTestListener(tCase), aludraTest.getServiceManager()));

            // configure aludra service with url.of.aut
            System.setProperty("ALUDRATEST_CONFIG/seleniumWrapper/_testui/url.of.aut", getSeleniumLinkForTestPage());

            // get aludra service
            serviceId = ComponentId.create(AludraWebGUI.class, "testui");
            aludraWebGUI = getService(serviceId);
            // open test page
            openTestPage();
            // get uimap
            guiTestUIMap = new GUITestUIMap(aludraWebGUI);
        }
    }

    // Close test page
    @After
    public void close() {
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
        assertEquals(expectedStatus, getLastStepStatus());
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
        aludraWebGUI.open();
        aludraWebGUI.perform().windowMaximize();
        //		aludraWebGUI.perform().windowFocus();
        aludraWebGUI.perform().refresh();
    }

    // Get error message of the last test step
    private String getLastStepErrorMessage() {
        return tCase.getLastTestStep().getErrorMessage();
    }

    // Get status of the last test step
    private TestStatus getLastStepStatus() {
        return tCase.getLastTestStep().getStatus();
    }

    // Get the selenium compatible link for the test page
    private String getSeleniumLinkForTestPage() {
        return "file:///" + normalizedPathToFolder(TEST_PAGE_FOLDER) + '/' + getTestPage();
    }

    private String normalizedPathToFolder(String folder) {
        return new File(folder).getAbsolutePath().replace(File.separatorChar, '/');
    }

    protected String getTestPage() {
        return "index.html";
    }

    protected static void overrideImplementor(Class<?> ifaceClass, Class<?> implClass) {
        System.setProperty("ALUDRATEST_CONFIG/aludraservice/" + ifaceClass.getName(), implClass.getName());
    }

}
