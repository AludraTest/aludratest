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
package org.aludratest.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.aludratest.scheduler.node.ExecutionMode;
import org.aludratest.scheduler.node.RunnerGroup;
import org.aludratest.scheduler.node.RunnerLeaf;
import org.junit.Test;
import org.junit.runner.Description;

/**
 * Tests the {@link JUnitUtil} class.
 * @author Volker Bergmann
 */
public class JUnitUtilTest {

    /** Tests the method {@link JUnitUtil#createDescription(org.aludratest.scheduler.node.RunnerNode, Class)} 
     *  for {@link RunnerLeaf} instances. */
    @Test
    public void testCreateDescriptionForLeaf() {
        RunnerLeaf leaf = new RunnerLeaf(1, "path", null, null);
        Description description = JUnitUtil.createDescription(leaf, getClass());
        assertNotNull(description);
        assertEquals(getClass(), description.getTestClass());
        assertTrue(description.isTest());
        assertEquals("path(" + JUnitUtilTest.class.getName() + ")", description.getDisplayName());
        System.out.println(description);
    }

    /** Tests the method {@link JUnitUtil#createDescription(org.aludratest.scheduler.node.RunnerNode, Class)} 
     *  for {@link RunnerGroup} instances. */
    @Test
    public void testCreateDescriptionForGroup() {
        RunnerGroup group = new RunnerGroup("path", ExecutionMode.SEQUENTIAL, null);
        RunnerLeaf leaf = new RunnerLeaf(2, "path/p1", null, null);
        group.addChild(leaf);
        Description description = JUnitUtil.createDescription(group, getClass());
        assertTrue(description.isSuite());
        assertEquals("path", description.getDisplayName());
        System.out.println(description);
    }

}
