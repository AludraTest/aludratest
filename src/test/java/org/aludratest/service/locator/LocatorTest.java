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
package org.aludratest.service.locator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.aludratest.exception.TechnicalException;
import org.aludratest.service.locator.element.LabelLocator;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class LocatorTest {

    @Test(expected = TechnicalException.class)
    public void testLocatorConstructor() {
        new DummyTestLocator(null);
    }

    @Test
    public void testToString() {
        Locator l = new DummyTestLocator("TEST!");
        assertEquals("TEST!", l.toString());
    }

    @Test
    public void testEquals() {
        DummyTestLocator loc1 = new DummyTestLocator("L");
        DummyTestLocator loc2 = new DummyTestLocator("L");
        DummyTestLocator loc3 = new DummyTestLocator("X");

        LabelLocator lloc = new LabelLocator("L");

        assertEquals(loc1, loc2);
        assertEquals(loc1.hashCode(), loc2.hashCode());
        assertNotEquals(loc2, loc3);
        assertNotEquals(loc1, lloc); // because different classes!
    }

    private static class DummyTestLocator extends Locator {

        protected DummyTestLocator(String locator) {
            super(locator);
        }

    }

}
