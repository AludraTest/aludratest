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
package org.aludratest.service.gui.web.selenium.selenium1;

import java.util.ArrayList;
import java.util.List;

import org.aludratest.exception.TechnicalException;
import org.aludratest.service.Action;
import org.aludratest.service.SystemConnector;
import org.aludratest.testcase.event.attachment.Attachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract parent class which provides common features of all Selenium1 action
 * classes.
 * 
 * @author Volker Bergmann
 */
public abstract class AbstractSeleniumAction implements Action {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /** The wrapper which provides the actual Selenium server access. */
    protected final SeleniumWrapper wrapper;

    /**
     * Constructor.
     * 
     * @param seleniumWrapper
     *            The {@link SeleniumWrapper} to use
     */
    public AbstractSeleniumAction(SeleniumWrapper seleniumWrapper) {
        this.wrapper = seleniumWrapper;
    }

    @Override
    public void setSystemConnector(SystemConnector systemConnector) {
        this.wrapper.setSystemConnector(systemConnector);
    }

    /** Takes a screen shot and saves the HTML sources of the current page. */
    @Override
    public List<Attachment> createDebugAttachments() {
        List<Attachment> attachments = new ArrayList<Attachment>(2);
        attachments.add(takeScreenShot());
        Attachment source = saveSource();
        if (source != null) {
            attachments.add(source);
        }
        return attachments;
    }

    @Override
    public List<Attachment> createAttachments(Object object, String label) {
        throw new TechnicalException("Not supported");
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
     * 
     * @return Attachment which contains the source code
     */
    protected Attachment saveSource() {
        try {
            return wrapper.getPageSource();
        }
        catch (Exception e) { // NOSONAR
            // The method is called when formatting error info, so do not raise additional issues
            logger.error("Error saving the source ", e);
            return null;
        }
    }

}
