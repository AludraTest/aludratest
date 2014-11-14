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
package org.aludratest.service.flatfile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Locale;

import org.aludratest.content.flat.FlatFileColumn;
import org.aludratest.content.flat.PrefixRowType;
import org.aludratest.content.flat.webdecs.RowParser;
import org.aludratest.exception.AutomationException;
import org.databene.commons.TimeUtil;
import org.junit.Test;

/**
 * Tests the {@link RowParser}.
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public class RowParserTest {

    /** Tests the mapping of different classes with direct access 
     *  to public attributes of the most important simple types. */
    @Test
    public void testAttributesAndBeanClasses() {
        RowParser parser = new RowParser(Locale.US);
        parser.addRowType(new PrefixRowType(FFFlatPerson.class, "P"));
        parser.addRowType(new PrefixRowType(FFAddress.class, "A"));
        assertEquals(new FFFlatPerson("Alice", 23, TimeUtil.date(1991, 0, 6), "Miez"), parser.parseRow("PAlice               02319910106Miez    "));
        assertEquals(new FFFlatPerson("Bob", 34, TimeUtil.date(1980, 1, 3), "Hasso"), parser.parseRow("PBob                 03419800203Hasso   "));
        assertEquals(new FFAddress("MAIN STREET 321", "NEW YORK", 123.45), parser.parseRow("AMAIN STREET 321     NEW YORK            0123.45"));
    }

    /** Tests access to JavaBean properties with the format specification 
     *  attached to the property's underlying attribute */
    @Test
    public void testProperties() {
        RowParser parser = new RowParser(Locale.US);
        parser.addRowType(new PrefixRowType(FFAddressBean.class, "A"));
        assertEquals(new FFAddressBean("MAIN STREET 321", "NEW YORK", 123.45), parser.parseRow("AMAIN STREET 321     NEW YORK            0123.45"));
        assertEquals(new FFAddressBean("2ND STREET 23", "NEW JERSEY", 0.5), parser.parseRow("A2ND STREET 23       NEW JERSEY          0000.50"));
    }

    /** Tests a flat file attribute configured to be at column 0. */
    @Test
    public void testFirstRowAtIndex0() {
        try {
            RowParser parser = new RowParser(Locale.US);
            parser.addRowType(new FixedRowType(StartIndex0.class));
            parser.parseRow("");
            fail("Expected " + AutomationException.class.getName());
        } catch (AutomationException e) {
            assertEquals("Flat file column indices must start at index 1, " + "but the first column 'org.aludratest.service." + "flatfile.RowParserTest$StartIndex0.s' starts at index 0",
                    e.getMessage());
        }
    }

    /** FlatFileBean class with a startIndex of 0 */
    public static class StartIndex0 {
        @FlatFileColumn(startIndex = 0, format = "10")
        public String s;
    }

    /** Tests a flat file attribute configured to be at column 2. */
    @Test
    public void testFirstRowAtIndex2() {
        try {
            RowParser parser = new RowParser(Locale.US);
            parser.addRowType(new FixedRowType(StartIndex2.class));
            parser.parseRow("");
            fail("Expected " + AutomationException.class.getName());
        } catch (AutomationException e) {
            assertEquals("Flat file column indices must start at index 1, " + "but the first column 'org.aludratest.service." + "flatfile.RowParserTest$StartIndex2.s' starts at index 2",
                    e.getMessage());
        }
    }

    /** FlatFileBean class with a startIndex of 2. */
    public static class StartIndex2 {
        @FlatFileColumn(startIndex = 2, format = "10")
        public String s;
    }

    /** Tests a FlatFileBean with a gap between its attributes' startIndices. */
    @Test
    public void testIndexGap() {
        try {
            RowParser parser = new RowParser(Locale.US);
            parser.addRowType(new FixedRowType(IndexGap.class));
            parser.parseRow("");
            fail("Expected " + AutomationException.class.getName());
        } catch (AutomationException e) {
            assertEquals("Flat file column 'org.aludratest." + "service.flatfile.RowParserTest$IndexGap.s2' " + "is expected at index 11, but was found at index 12", e.getMessage());
        }
    }

    /** FlatFileBean class with a gap between its attributes' startIndices. */
    public static class IndexGap {
        @FlatFileColumn(startIndex = 1, format = "10")
        public String s1;
        @FlatFileColumn(startIndex = 12, format = "10")
        public String s2;
    }

    /** Tests a FlatFileBean class with attributes that have overlapping column ranges. */
    @Test
    public void testIndexOverlap() {
        try {
            RowParser parser = new RowParser(Locale.US);
            parser.addRowType(new FixedRowType(IndexOverlap.class));
            parser.parseRow("");
            fail("Expected " + AutomationException.class.getName());
        } catch (AutomationException e) {
            assertEquals("Flat file column 'org.aludratest.service.flatfile.RowParserTest$IndexOverlap.s2' is expected at index 11, " + "but was found at index 10", e.getMessage());
        }
    }

    /** FlatFileBean class with attributes that have overlapping column ranges. */
    public static class IndexOverlap {
        @FlatFileColumn(startIndex = 1, format = "10")
        public String s1;
        @FlatFileColumn(startIndex = 10, format = "10")
        public String s2;
    }

    /** Tests a FlatFileBean class with different attributes of the same startIndex */
    @Test
    public void testDuplicateIndex() {
        try {
            RowParser parser = new RowParser(Locale.US);
            parser.addRowType(new FixedRowType(DuplicateIndex.class));
            parser.parseRow("");
            fail("Expected " + AutomationException.class.getName());
        } catch (AutomationException e) {
            assertEquals("Multiple column definitions at index 1: " + "'s1', and 's2'", e.getMessage());
        }
    }

    /** FlatFileBean class with different attributes of the same startIndex. */
    public static class DuplicateIndex {
        @FlatFileColumn(startIndex = 1, format = "10")
        public String s1;
        @FlatFileColumn(startIndex = 1, format = "8")
        public String s2;
    }

}
