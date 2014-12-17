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
package org.aludratest.service.gui.integrationtest.selenium1;

import org.aludratest.service.gui.component.Link;
import org.aludratest.service.gui.component.Window;
import org.aludratest.service.gui.component.base.AbstractLinkAndWindowTest;
import org.junit.BeforeClass;

/**
 * Tests {@link Link} and {@link Window} features with Selenium 1.
 * @author Volker Bergmann
 */
public class Selenium1LinkAndWindowTest extends AbstractLinkAndWindowTest {

    /** Activates Selenium 1 before the tests
     *  (inherited from the parent class) are executed. */
    @BeforeClass
    public static void setUpSelenium1() {
        activateSelenium1();
    }

}
