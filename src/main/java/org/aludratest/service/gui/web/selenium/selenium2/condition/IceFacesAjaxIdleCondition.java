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
package org.aludratest.service.gui.web.selenium.selenium2.condition;

/** The IceFaces implementation for checking current AJAX status. <br>
 * Notice that this is a more complex piece of JavaScript. It registers a callback which listens for AJAX events and adjusts a
 * counter. For working as expected, this script must be initialized BEFORE the AJAX call to check begins. Recommended is a simple
 * way:
 * 
 * <pre>
 * // inits the IceFaces monitoring JavaScript
 * gui.perform().waitForAjaxOperationEnd("icefaces", 10000);
 * myAjaxButton.click();
 * // does the real wait
 * gui.perform().waitForAjaxOperationEnd("icefaces", 10000);
 * </pre>
 * 
 * @author falbrech */
public class IceFacesAjaxIdleCondition extends AbstractAjaxIdleCondition {

    // as IceFaces does not have something we could check for active AJAX request, we have to hook into AJAX events...
    private static final String ICE_FACES_AJAX_CHECK_SCRIPT = "if (typeof(ajaxActive124) === 'undefined') "
            + "{ var ajaxActive124 = 0; jsf.ajax.addOnEvent(function(data) { switch(data.status) { "
            + "case 'begin': ajaxActive124 += 1; break; case 'success': ajaxActive124 -= 1; break; } }) } "
            + "return ajaxActive124 == 0;";

    @Override
    protected String getBooleanAjaxIdleScript() {
        return ICE_FACES_AJAX_CHECK_SCRIPT;
    }

}
