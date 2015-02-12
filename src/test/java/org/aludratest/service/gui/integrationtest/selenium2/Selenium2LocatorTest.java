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
package org.aludratest.service.gui.integrationtest.selenium2;

import org.aludratest.service.gui.component.base.AbstractLocatorTest;
import org.aludratest.service.locator.Locator;
import org.junit.BeforeClass;

/**
 * Tests {@link Locator}s with Selenium 2.
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public class Selenium2LocatorTest extends AbstractLocatorTest {

    @BeforeClass
    public static void setUpSelenium2() {
        activateSelenium2();
    }

}
