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
 * Tests the {@link ContainsValidator}.
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public class ContainsValidatorTest extends AbstractValidatorTest {

    @Test
    public void testPositive() {
        ContainsValidator validator = new ContainsValidator("TEST");
        assertTrue(validator.valid("TEST"));
        assertTrue(validator.valid("XTEST"));
        assertTrue(validator.valid("TESTX"));
        assertTrue(validator.valid("XTESTX"));
        assertTrue(validator.valid(" TEST"));
        assertTrue(validator.valid("TEST "));
        assertTrue(validator.valid(" TEST "));
    }

    @Test
    public void testNegative() {
        ContainsValidator validator = new ContainsValidator("TEST");
        assertFalse(validator.valid("es"));
        assertFalse(validator.valid("AlphaBetaGamma"));
    }

    @Test
    public void testNullArgument() {
        ContainsValidator validator = new ContainsValidator("TEST");
        assertFalse(validator.valid(null));
    }

    @Test
    public void testNullValidator() {
        ContainsValidator validator = new ContainsValidator(null);
        assertTrue(validator.valid("TEST"));
    }

    @Test
    public void testNullMarkedValidator() {
        ContainsValidator validator = new ContainsValidator(NULL_MARKER);
        assertTrue(validator.valid("TEST"));
        assertTrue(validator.valid("X"));
    }

}
