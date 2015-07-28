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
package org.aludratest.service.gui.web.selenium;

import java.net.URL;
import java.util.Set;

import org.aludratest.service.SystemConnectorInterface;
import org.aludratest.testcase.event.attachment.Attachment;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;

/** Interface for System Connectors able to download a file when given the current Selenium state (current Web Element and current
 * cookies, as well as the URL of the Application Under Test). <br>
 * As Selenium cannot handle file downloads in a consistent and clean way, a System Connector can be provided to perform this
 * task. A common implementation would be to:
 * <ol>
 * <li>Examine the web element's <code>href</code> attribute (e.g. for a link)</li>
 * <li>Set up an HttpClient with the current cookies</li>
 * <li>Construct the full download URL and download it using the HTTP client.</lI>
 * </ol>
 * Note that, for this to work, the server running <b>AludraTest</b> must have the same access to the AUT as the current Selenium
 * client does. Selenium clients could be totally different machines, keep this in mind.
 * 
 * @author falbrech */
public interface SystemDownloadProvider extends SystemConnectorInterface {

    /** Downloads the file associated with the given web element. "associated" depends on the Application Under Test, e.g. could
     * the <code>href</code> attribute contain the download URL, or a JavaScript attribute must be examined. <br>
     * The System Connector can use the given set of cookies to e.g. use any possibly active session. It should return
     * <code>null</code> if no download information can be extracted from the given element, or the download could not be
     * performed. Otherwise, the returned attachment is asked for its contents and its <b>file name</b>, which must explicitly be
     * set via <code>setFileName()</code>. The label and file extension attributes of the Attachment are ignored.
     * 
     * @param autBaseUrl URL of the AUT, <b>not</b> necessarily the currently displayed web page.
     * @param element The web element on which a "download" action should be performed.
     * @param activeCookies All currently active cookies.
     * 
     * @return An attachment object describing file name and contents, or <code>null</code> if no download is possible for the
     *         given web element. */
    public Attachment downloadFile(URL autBaseUrl, WebElement element, Set<Cookie> activeCookies);

}
