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

import java.util.Arrays;

import org.aludratest.config.MutablePreferences;
import org.aludratest.service.AbstractAludraServiceTest;
import org.junit.Assert;
import org.junit.Test;

public class PropertyPriorityPreferencesTest extends AbstractAludraServiceTest {

    @Test
    public void testSystemProperties() {

        // build complex preferences tree
        SimplePreferences prefs = new SimplePreferences();
        prefs.setValue("Test1", false);

        MutablePreferences mp = prefs.createChildNode("testNode1");
        mp.setValue("Test2", 2);

        mp = mp.createChildNode("testNode2");
        mp.setValue("Test3", 3.0f);

        // wrap as framework does
        PropertyPriorityPreferences ppp = new PropertyPriorityPreferences("myComponent", prefs);

        // set system properties according to config documentation
        System.setProperty("ALUDRATEST_CONFIG/myComponent/testNode1/testNode2/Test3", "" + 4.0f);
        System.setProperty("ALUDRATEST_CONFIG/myComponent/testNode1/Test5", "NewProp");

        // expect effects
        Assert.assertEquals(ppp.getChildNode("testNode1").getChildNode("testNode2").getFloatValue("Test3"), 4.0f, 0.01);
        Assert.assertTrue(Arrays.asList(ppp.getChildNode("testNode1").getKeyNames()).contains("Test5"));

        // Reset properties
        System.getProperties().remove("ALUDRATEST_CONFIG/myComponent/testNode1/testNode2/Test3");
        System.getProperties().remove("ALUDRATEST_CONFIG/myComponent/testNode1/Test5");
    }

    @Test
    public void testInstanceSystemProperties() {
        SimplePreferences prefs = new SimplePreferences();
        prefs.setValue("Test1", false);

        MutablePreferences mp = prefs.createChildNode("testNode1");
        mp.setValue("Test2", 2);

        mp = mp.createChildNode("testNode2");
        mp.setValue("Test3", 3.0f);

        PropertyPriorityPreferences ppp = new PropertyPriorityPreferences("myComponent", "myCompInstance", prefs);

        // set system properties according to config documentation
        System.setProperty("ALUDRATEST_CONFIG/myComponent/_myCompInstance/testNode1/testNode2/Test3", "" + 4.0f);
        System.setProperty("ALUDRATEST_CONFIG/myComponent/_myCompInstance/testNode1/Test5", "NewProp");

        // expect effects
        Assert.assertEquals(ppp.getChildNode("testNode1").getChildNode("testNode2").getFloatValue("Test3"), 4.0f, 0.01);
        Assert.assertTrue(Arrays.asList(ppp.getChildNode("testNode1").getKeyNames()).contains("Test5"));

        // reset properties
        System.getProperties().remove("ALUDRATEST_CONFIG/myComponent/_myCompInstance/testNode1/testNode2/Test3");
        System.getProperties().remove("ALUDRATEST_CONFIG/myComponent/_myCompInstance/testNode1/Test5");
    }

}
