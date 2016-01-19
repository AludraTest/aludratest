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

import static org.aludratest.testcase.event.impl.AludraTestUtil.nullOrPrimitiveDefault;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;

import org.aludratest.testcase.event.impl.AludraTestUtil;
import org.junit.Test;

/**
 * Tests the {@link AludraTestUtil}.
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public class AludraTestUtilTest {

    @Test
    public void testGetStackTraceElement() {
        StackTraceElement actualCaller = AludraTestUtil.getStackTraceElement(1);
        String expectedDescription = "org.aludratest.util.AludraTestUtilTest.testGetStackTraceElement(AludraTestUtilTest.java:36)";
        assertEquals(expectedDescription, actualCaller.toString());
    }

    @Test
    public void testNullOrPrimitiveDefault() {
        assertEquals(null, nullOrPrimitiveDefault(Object.class));
        assertEquals(0, nullOrPrimitiveDefault(int.class));
        assertEquals(0, nullOrPrimitiveDefault(Integer.class));
        assertEquals((byte) 0, nullOrPrimitiveDefault(byte.class));
        assertEquals((byte) 0, nullOrPrimitiveDefault(Byte.class));
        assertEquals((short) 0, nullOrPrimitiveDefault(short.class));
        assertEquals((short) 0, nullOrPrimitiveDefault(Short.class));
        assertEquals((long) 0, nullOrPrimitiveDefault(long.class));
        assertEquals((long) 0, nullOrPrimitiveDefault(Long.class));
        assertEquals(Boolean.FALSE, nullOrPrimitiveDefault(boolean.class));
        assertEquals(Boolean.FALSE, nullOrPrimitiveDefault(Boolean.class));
        assertEquals((char) 0, nullOrPrimitiveDefault(char.class));
        assertEquals((char) 0, nullOrPrimitiveDefault(Character.class));
        assertEquals("", nullOrPrimitiveDefault(String.class));
        assertEquals(0f, nullOrPrimitiveDefault(float.class));
        assertEquals(0f, nullOrPrimitiveDefault(Float.class));
        assertEquals(0., nullOrPrimitiveDefault(double.class));
        assertEquals(0., nullOrPrimitiveDefault(Double.class));
    }

    @Test
    public void testUnwrapInvocationTargetExceptionWithCause() {
        // if an InvocationTargetException has a cause, the cause shall be returned
        RuntimeException rte = new RuntimeException();
        InvocationTargetException iteWithCause = new InvocationTargetException(rte);
        assertTrue(rte == ExceptionUtil.unwrapInvocationTargetException(iteWithCause));
    }

    @Test
    public void testUnwrapInvocationTargetExceptionWithOtherException() {
        // any exception that is no InvocationTargetException shall be returned itself
        RuntimeException rte = new RuntimeException();
        assertTrue(rte == ExceptionUtil.unwrapInvocationTargetException(rte));
    }

    @Test
    public void testUnwrapInvocationTargetExceptionWithoutCause() {
        // if an InvocationTargetException has no cause, it should be returned itself
        InvocationTargetException iteWithoutCause = new InvocationTargetException(null);
        assertTrue(iteWithoutCause == ExceptionUtil.unwrapInvocationTargetException(iteWithoutCause));
    }

}
