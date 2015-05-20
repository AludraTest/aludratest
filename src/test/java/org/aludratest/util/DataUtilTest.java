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
package org.aludratest.util;

import static org.junit.Assert.assertEquals;

import org.aludratest.util.DataUtil;
import org.junit.Test;

/**
 * Tests the {@link DataUtil} class.
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public class DataUtilTest {

    @Test
    public void testExpectEqualArrays_null() {
        assertEquals("No Values found for comparison. ", DataUtil.expectEqualArrays(null, null));
        assertEquals("No Values found for comparison. ", DataUtil.expectEqualArrays(new String[] { "x" }, null));
        assertEquals("No Values found for comparison. ", DataUtil.expectEqualArrays(null, new String[] { "x" }));
    }

    @Test
    public void testExpectEqualArrays_match() {
        assertEquals("", DataUtil.expectEqualArrays(new String[] { "x" }, new String[] { "x" }));
        assertEquals("", DataUtil.expectEqualArrays(new String[] { "x", "y" }, new String[] { "x", "y" }));
    }

    @Test
    public void testExpectEqualArrays_mismatch() {
        assertEquals("Arrays do not match. Expected [x], but found [x, y]. ", DataUtil.expectEqualArrays(new String[] { "x" }, new String[] { "x", "y" }));
        assertEquals("Arrays do not match. Expected [x, y], but found [x]. ", DataUtil.expectEqualArrays(new String[] { "x", "y" }, new String[] { "x" }));
        assertEquals("Arrays do not match. Expected [x], but found [z]. ", DataUtil.expectEqualArrays(new String[] { "x" }, new String[] { "z" }));
    }

}
