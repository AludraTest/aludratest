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
package org.aludratest.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.aludratest.AludraTest;
import org.junit.Test;
import org.test.testclasses.service.MoreComplexComponent;
import org.test.testclasses.service.MoreComplexComponentImpl;

public class ComponentConfiguratorTest {

    @Test
    public void testAnnotationLoad() {
        String propName = "ALUDRATEST_CONFIG/aludraservice/" + MoreComplexComponent.class.getName();
        System.setProperty(propName, MoreComplexComponentImpl.class.getName());

        // init AludraTest framework
        AludraTest aludraTest = AludraTest.startFramework();

        // retrieve an MCC
        MoreComplexComponent comp = aludraTest.getServiceManager().newImplementorInstance(MoreComplexComponent.class);
        assertTrue(comp instanceof MoreComplexComponentImpl);
        MoreComplexComponentImpl confComp = (MoreComplexComponentImpl) comp;
        assertTrue(confComp.configured);

        // assertions against Preferences object
        MutablePreferences prefs = confComp.getDefaultPreferences();

        assertEquals(1, prefs.getIntValue("testvalue1"));
        assertEquals(true, prefs.getBooleanValue("testvalue2"));
        assertFalse(Arrays.asList(prefs.getKeyNames()).contains("testvalue3"));
        assertEquals("TEST", prefs.getStringValue("atestvalue"));
        assertEquals(2.0, prefs.getFloatValue("implProp"), 0.01);
        assertEquals("!", prefs.getStringValue("dynamicValue"));

        System.getProperties().remove(propName);
        aludraTest.stopFramework();
    }

}
