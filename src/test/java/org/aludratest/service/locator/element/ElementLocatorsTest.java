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
package org.aludratest.service.locator.element;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.aludratest.service.locator.element.ElementLocators.ElementLocatorsGUI;
import org.junit.Test;

/**
 * Tests the {@link ElementLocators}.
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public class ElementLocatorsTest {

    @Test
    public void testToString() {
        IdLocator opt1 = new IdLocator("id1");
        IdLocator opt2 = new IdLocator("id2");
        ElementLocatorsGUI loc = (ElementLocatorsGUI) new ElementLocators(opt1, opt2).newMutableInstance();
        assertEquals("ElementLocatorsGUI[options=[#0: id1, #1: id2], usedLocator=none]", loc.toString());
        loc.setUsedOption(null);
        assertEquals("ElementLocatorsGUI[options=[#0: id1, #1: id2], usedLocator=none]", loc.toString());
        loc.setUsedOption(opt1);
        assertEquals("ElementLocatorsGUI[options=[#0: id1, #1: id2], usedLocator=id1]", loc.toString());
        loc.setUsedOption(opt2);
        assertEquals("ElementLocatorsGUI[options=[#0: id1, #1: id2], usedLocator=id2]", loc.toString());
    }

    @Test
    public void testUsedOption() {
        IdLocator opt1 = new IdLocator("id1");
        IdLocator opt2 = new IdLocator("id2");
        ElementLocatorsGUI loc = (ElementLocatorsGUI) new ElementLocators(opt1, opt2).newMutableInstance();
        assertNull(loc.getUsedOption());
        loc.setUsedOption(opt1);
        assertTrue(opt1 == loc.getUsedOption());
        loc.setUsedOption(opt2);
        assertTrue(opt2 == loc.getUsedOption());
        loc.setUsedOption(null);
        assertNull(loc.getUsedOption());
    }

}
