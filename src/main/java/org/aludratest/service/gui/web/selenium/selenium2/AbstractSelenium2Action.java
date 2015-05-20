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
package org.aludratest.service.gui.web.selenium.selenium2;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.aludratest.exception.TechnicalException;
import org.aludratest.service.Action;
import org.aludratest.service.SystemConnector;
import org.aludratest.service.gui.web.selenium.SeleniumWrapperConfiguration;
import org.aludratest.testcase.event.attachment.Attachment;
import org.aludratest.testcase.event.attachment.StringAttachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract parent class which provides common features of all Selenium1 action classes.
 * @author Volker Bergmann
 */
public abstract class AbstractSelenium2Action implements Action {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /** The wrapper which provides the actual Selenium server access. */
    protected Selenium2Wrapper wrapper;

    /** Constructor.
     *  @param seleniumWrapper */
    public AbstractSelenium2Action(Selenium2Wrapper seleniumWrapper) {
        this.wrapper = seleniumWrapper;
    }

    @Override
    public void setSystemConnector(SystemConnector systemConnector) {
        this.wrapper.systemConnector = systemConnector;
    }

    /** @return the {@link SeleniumWrapperConfiguration} */
    protected SeleniumWrapperConfiguration getConfiguration() {
        return wrapper.getConfiguration();
    }

    protected long getTimeout() {
        return getConfiguration().getTimeout();
    }

    @Override
    public List<Attachment> createAttachments(Object object, String label) {
        throw new TechnicalException("Not supported");
    }

    /** Takes a screen shot and saves the HTML sources of the current page. */
    @Override
    public List<Attachment> createDebugAttachments() {
        List<Attachment> attachments = new ArrayList<Attachment>(2);
        attachments.add(takeScreenShot());
        attachments.add(saveSource());
        return attachments;
    }


    // helper methods for child classes ----------------------------------------

    /** Takes a screen shot of the current web page. */
    protected Attachment takeScreenShot() {
        try {
            return wrapper.getScreenshotOfThePage();
        }
        catch (Exception e) { // NOSONAR
            throw new TechnicalException("Error taking screenshot. ", e);
        }
    }

    /**
     * Saves the HTML sources of the current web page.
     * @return Attachment which contains the source code
     */
    protected Attachment saveSource() {
        try {
            return wrapper.getPageSource();
        }
        catch (Exception e) { // NOSONAR
            // instead, create an Attachment containing the exception
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            pw.flush();

            return new StringAttachment("Exception on saving source", sw.toString(), "txt");
        }
    }

}
