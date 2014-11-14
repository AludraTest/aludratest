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
 * Tests the {@link EqualsIgnoreCaseTrimmedValidator}.
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public class EqualsIgnoreCaseTrimmedValidatorTest extends AbstractValidatorTest {

    @Test
    public void testPositive() {
        EqualsIgnoreCaseTrimmedValidator validator = new EqualsIgnoreCaseTrimmedValidator("TEST");
        assertTrue(validator.valid("test"));
        assertTrue(validator.valid("TEST"));
        assertTrue(validator.valid("TesT"));
        assertTrue(validator.valid("TEST  "));
        assertTrue(validator.valid("  TEST"));
        assertTrue(validator.valid("  TEST  "));
    }

    @Test
    public void testNegative() {
        EqualsIgnoreCaseTrimmedValidator validator = new EqualsIgnoreCaseTrimmedValidator("TEST");
        assertFalse(validator.valid("es"));
        assertFalse(validator.valid("AlphaBetaGamma"));
        assertFalse(validator.valid("XTEST"));
        assertFalse(validator.valid("TESTX"));
        assertFalse(validator.valid("XTESTX"));
    }

    @Test
    public void testNullArgument() {
        EqualsIgnoreCaseTrimmedValidator validator = new EqualsIgnoreCaseTrimmedValidator("TEST");
        assertFalse(validator.valid(null));
    }

    @Test
    public void testNullValidator() {
        EqualsIgnoreCaseTrimmedValidator validator = new EqualsIgnoreCaseTrimmedValidator(null);
        assertTrue(validator.valid("TEST"));
    }

    @Test
    public void testNullMarkedValidator() {
        EqualsIgnoreCaseTrimmedValidator validator = new EqualsIgnoreCaseTrimmedValidator(NULL_MARKER);
        assertTrue(validator.valid("TEST"));
    }

}
