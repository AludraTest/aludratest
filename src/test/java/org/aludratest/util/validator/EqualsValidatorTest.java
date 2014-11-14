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
 * Tests the {@link EqualsValidator}.
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public class EqualsValidatorTest extends AbstractValidatorTest {

    @Test
    public void testPositive() {
        EqualsValidator validator = new EqualsValidator("TEST");
        assertTrue(validator.valid("TEST"));
    }

    @Test
    public void testNegative() {
        EqualsValidator validator = new EqualsValidator("TEST");
        assertFalse(validator.valid("es"));
        assertFalse(validator.valid("AlphaBetaGamma"));
        assertFalse(validator.valid("test"));
        assertFalse(validator.valid("TesT"));
        assertFalse(validator.valid(" test"));
        assertFalse(validator.valid("test "));
        assertFalse(validator.valid(" test "));
        assertFalse(validator.valid("Xtest"));
        assertFalse(validator.valid("testX"));
        assertFalse(validator.valid("XtestX"));
    }

    @Test
    public void testNullArgument() {
        EqualsValidator validator = new EqualsValidator("TEST");
        assertFalse(validator.valid(null));
    }

    @Test
    public void testNullValidator() {
        EqualsValidator validator = new EqualsValidator(null);
        assertTrue(validator.valid("TEST"));
    }

    @Test
    public void testNullMarkedValidator() {
        EqualsValidator validator = new EqualsValidator(NULL_MARKER);
        assertTrue(validator.valid("TEST"));
    }

}
