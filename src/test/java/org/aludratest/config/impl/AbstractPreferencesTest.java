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
package org.aludratest.config.impl;

import junit.framework.TestCase;

import org.aludratest.config.Preferences;
import org.junit.Test;

public class AbstractPreferencesTest extends TestCase {

    @Test
    public void testPropertyResolution() {
        TestPreferencesImpl prefs = new TestPreferencesImpl();

        System.setProperty("my.special.testprop", "TEST");
        assertEquals("TEST", prefs.getStringValue("userHome"));
    }

    private static class TestPreferencesImpl extends AbstractPreferences {

        @Override
        public String[] getKeyNames() {
            return new String[] { "userHome" };
        }

        @Override
        public Preferences getChildNode(String name) {
            return null;
        }

        @Override
        public String[] getChildNodeNames() {
            return new String[0];
        }

        @Override
        protected String internalGetStringValue(String key) {
            return "${my.special.testprop}";
        }

    }

}
