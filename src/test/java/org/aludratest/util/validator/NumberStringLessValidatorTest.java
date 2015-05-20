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
package org.aludratest.util.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests the {@link NumberStringLessValidator}.
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public class NumberStringLessValidatorTest extends AbstractValidatorTest {

    @Test
    public void testPositive() {
        NumberStringLessValidator validator = new NumberStringLessValidator("5", 1);
        assertTrue(validator.valid("-1000"));
        assertTrue(validator.valid("-1"));
        assertTrue(validator.valid("0"));
        assertTrue(validator.valid("1"));
        assertTrue(validator.valid("4"));
        assertTrue(validator.valid("5"));
        assertTrue(validator.valid("5.99"));
    }

    @Test
    public void testNegative() {
        NumberStringLessValidator validator = new NumberStringLessValidator("5", 1);
        assertFalse(validator.valid("6.0"));
        assertFalse(validator.valid("6.1"));
        assertFalse(validator.valid("100000"));
    }

    @Test
    public void testNullArgument() {
        NumberStringLessValidator validator = new NumberStringLessValidator("5", 1);
        assertFalse(validator.valid(null));
    }

    @Test
    public void testNullValidator() {
        NumberStringLessValidator validator = new NumberStringLessValidator(null, 1);
        assertTrue(validator.valid("TEST"));
    }

    @Test
    public void testNullMarkedValidator() {
        NumberStringLessValidator validator = new NumberStringLessValidator(NULL_MARKER, 1);
        assertTrue(validator.valid("TEST"));
        assertTrue(validator.valid("X"));
    }

}
