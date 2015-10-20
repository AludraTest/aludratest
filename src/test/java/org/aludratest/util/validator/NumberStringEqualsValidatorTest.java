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

/** Tests the {@link NumberStringEqualsValidator}.
 * @author falbrech */
@SuppressWarnings("javadoc")
public class NumberStringEqualsValidatorTest extends AbstractValidatorTest {

    @Test
    public void testPositive() {
        NumberStringEqualsValidator validator = new NumberStringEqualsValidator("4.5", 1);
        assertTrue(validator.valid("4.5"));
        assertTrue(validator.valid("4.6"));
        assertTrue(validator.valid("3.51"));
        assertTrue(validator.valid("5.49"));
        validator = new NumberStringEqualsValidator("4.5", 0.001);
        assertTrue(validator.valid("4.5"));
        assertTrue(validator.valid("4.49999"));
        assertTrue(validator.valid("4.50001"));
    }

    @Test
    public void testNegative() {
        NumberStringEqualsValidator validator = new NumberStringEqualsValidator("4.5", 1);
        assertFalse(validator.valid("3.4"));
        assertFalse(validator.valid("5.6"));
        validator = new NumberStringEqualsValidator("4.5", 0.001);
        assertFalse(validator.valid("4.6"));
        assertFalse(validator.valid("4.4"));
        assertFalse(validator.valid("4.51"));
        assertFalse(validator.valid("4.49"));
    }

    @Test
    public void testNullArgument() {
        NumberStringEqualsValidator validator = new NumberStringEqualsValidator("5", 1);
        assertFalse(validator.valid(null));
    }

    @Test
    public void testNullValidator() {
        NumberStringEqualsValidator validator = new NumberStringEqualsValidator(null, 1);
        assertTrue(validator.valid("TEST"));
    }

    @Test
    public void testNullMarkedValidator() {
        NumberStringEqualsValidator validator = new NumberStringEqualsValidator(NULL_MARKER, 1);
        assertTrue(validator.valid("TEST"));
        assertTrue(validator.valid("X"));
    }

}
