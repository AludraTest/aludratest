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
 * Tests the {@link NotEqualsValidator}.
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public class NotEqualsValidatorTest extends AbstractValidatorTest {

    @Test
    public void testPositive() {
        NotEqualsValidator validator = new NotEqualsValidator("TEST");
        assertTrue(validator.valid("es"));
        assertTrue(validator.valid("AlphaBetaGamma"));
        assertTrue(validator.valid("test"));
        assertTrue(validator.valid("TesT"));
        assertTrue(validator.valid(" test"));
        assertTrue(validator.valid("test "));
        assertTrue(validator.valid(" test "));
        assertTrue(validator.valid("Xtest"));
        assertTrue(validator.valid("testX"));
        assertTrue(validator.valid("XtestX"));
    }

    @Test
    public void testNegative() {
        NotEqualsValidator validator = new NotEqualsValidator("TEST");
        assertFalse(validator.valid("TEST"));
    }

    @Test
    public void testNullArgument() {
        NotEqualsValidator validator = new NotEqualsValidator("TEST");
        assertFalse(validator.valid(null));
    }

    @Test
    public void testNullValidator() {
        NotEqualsValidator validator = new NotEqualsValidator(null);
        assertTrue(validator.valid("TEST"));
    }

    @Test
    public void testNullMarkedValidator() {
        NotEqualsValidator validator = new NotEqualsValidator(NULL_MARKER);
        assertTrue(validator.valid("TEST"));
    }

}
