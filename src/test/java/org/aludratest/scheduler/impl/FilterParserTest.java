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
package org.aludratest.scheduler.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.ParseException;

import org.aludratest.scheduler.TestClassFilter;
import org.aludratest.scheduler.impl.AndTestClassFilter;
import org.aludratest.scheduler.impl.AttributeBasedTestClassFilter;
import org.aludratest.scheduler.impl.OrTestClassFilter;
import org.aludratest.scheduler.util.FilterParser;
import org.junit.Test;

/** Tests FilterParser class.
 * 
 * @author falbrech */
public class FilterParserTest {

    /** Tests the parser with a valid filter.
     * 
     * @throws Exception */
    @Test
    public void testValidFilter() throws Exception {
        FilterParser parser = new FilterParser();

        TestClassFilter filter = parser.parse("author = ( jdoe, mmiller, []); status != (Draft,InWork,[]);testgroup=UAT|author=falbrech|status=ForceExecution  ");

        OrTestClassFilter orFilter = (OrTestClassFilter) filter;
        assertEquals(3, orFilter.getFilters().size());

        AndTestClassFilter andFilter = (AndTestClassFilter) orFilter.getFilters().get(0);
        assertEquals(3, andFilter.getFilters().size());

        AttributeBasedTestClassFilter attrFilter = (AttributeBasedTestClassFilter) andFilter.getFilters().get(0);
        assertEquals("author", attrFilter.getAttributeName());
        assertEquals(3, attrFilter.getValues().size());
        assertFalse(attrFilter.isInvert());
        assertEquals("jdoe", attrFilter.getValues().get(0));
        assertEquals("mmiller", attrFilter.getValues().get(1));
        assertEquals("[]", attrFilter.getValues().get(2));

        attrFilter = (AttributeBasedTestClassFilter) andFilter.getFilters().get(1);
        assertEquals("status", attrFilter.getAttributeName());
        assertEquals(3, attrFilter.getValues().size());
        assertTrue(attrFilter.isInvert());
        assertEquals("Draft", attrFilter.getValues().get(0));
        assertEquals("InWork", attrFilter.getValues().get(1));
        assertEquals("[]", attrFilter.getValues().get(2));

        attrFilter = (AttributeBasedTestClassFilter) andFilter.getFilters().get(2);
        assertEquals("testgroup", attrFilter.getAttributeName());
        assertEquals(1, attrFilter.getValues().size());
        assertFalse(attrFilter.isInvert());
        assertEquals("UAT", attrFilter.getValues().get(0));

        andFilter = (AndTestClassFilter) orFilter.getFilters().get(1);
        assertEquals(1, andFilter.getFilters().size());
        attrFilter = (AttributeBasedTestClassFilter) andFilter.getFilters().get(0);
        assertEquals("author", attrFilter.getAttributeName());
        assertEquals(1, attrFilter.getValues().size());
        assertFalse(attrFilter.isInvert());
        assertEquals("falbrech", attrFilter.getValues().get(0));

        andFilter = (AndTestClassFilter) orFilter.getFilters().get(2);
        assertEquals(1, andFilter.getFilters().size());
        attrFilter = (AttributeBasedTestClassFilter) andFilter.getFilters().get(0);
        assertEquals("status", attrFilter.getAttributeName());
        assertEquals(1, attrFilter.getValues().size());
        assertFalse(attrFilter.isInvert());
        assertEquals("ForceExecution", attrFilter.getValues().get(0));
    }

    /** Tests the parser with invalid filters.
     * 
     * @throws Exception */
    @Test
    public void testInvalidFilter() throws Exception {
        try {
            FilterParser parser = new FilterParser();
            parser.parse("author = ( jdoe, mmiller, []); status !!= (Draft,InWork,[]);testgroup=UAT|author=falbrech|status=ForceExecution  ");
            fail("ParseException expected");
        }
        catch (ParseException pe) {
            assertEquals(38, pe.getErrorOffset());
        }

        try {
            FilterParser parser = new FilterParser();
            parser.parse("author=falbrech|");
            fail("ParseException expected");
        }
        catch (ParseException pe) {
            // OK
        }

        try {
            FilterParser parser = new FilterParser();
            parser.parse("|author=falbrech");
            fail("ParseException expected");
        }
        catch (ParseException pe) {
            assertEquals(0, pe.getErrorOffset());
        }
    }

}
