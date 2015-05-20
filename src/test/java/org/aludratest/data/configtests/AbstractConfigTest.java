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
package org.aludratest.data.configtests;

import static org.junit.Assert.assertEquals;

import org.aludratest.data.PersonBean;
import org.aludratest.testcase.AludraTestCase;

/** Abstract parent test class for testing the default Excel test data parser.
 * @author Volker Bergmann */
public abstract class AbstractConfigTest extends AludraTestCase {

    protected static void verifyPerson(PersonBean person) throws AssertionError {
        String name = person.getName();
        if ("Alice".equals(name)) {
            assertEquals("Alice is expected to be 23, but was found to be " + person.getAge(), 23, person.getAge());
        } else if ("Bob".equals(name)) {
            assertEquals("Bob is expected to be 34, but was found to be " + person.getAge(), 34, person.getAge());
        } else {
            throw new AssertionError("Unexpected name: " + name);
        }
    }

}
