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
package org.aludratest.service.cmdline;

import org.aludratest.util.data.StringData;
import org.aludratest.util.validator.ContainsValidator;
import org.aludratest.util.validator.EqualsValidator;
import org.databene.commons.Validator;
import org.databene.commons.validator.PrefixValidator;
import org.databene.commons.validator.SuffixValidator;

/** Class for verifying process output line-wise.
 * @author Volker Bergmann */
public class Line {

    private final Out out;

    Line(Out out) {
        this.out = out;
    }

    /** Asserts that a text line is identical to the provided string.
     * @param text the expected line content
     * @return this */
    public Line assertEquals(StringData text) {
        return assertMatch(new EqualsValidator(text.getValue()));
    }

    /** Asserts that a text line starts with the given prefix.
     * @param prefix the expected prefix of the line
     * @return this */
    public Line assertPrefix(StringData prefix) {
        return assertMatch(new PrefixValidator(prefix.getValue()));
    }

    /** Asserts that a text line ends with the given suffix.
     * @param suffix the expected suffix of the line
     * @return this */
    public Line assertSuffix(StringData suffix) {
        return assertMatch(new SuffixValidator(suffix.getValue()));
    }

    /** Asserts that a text line contains the given text.
     * @param expectedText the txt expected to be contained in the line
     * @return this */
    public Line assertContains(StringData expectedText) {
        return assertMatch(new ContainsValidator(expectedText.getValue()));
    }

    protected Line assertMatch(Validator<String> validator) {
        out.assertNextLineMatches(validator);
        return this;
    }

}
