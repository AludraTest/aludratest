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
package org.aludratest.codecheck.rule.pmd;

import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.ast.AbstractNode;
import net.sourceforge.pmd.lang.rule.properties.StringProperty;

/** Parent class for PMD rules that match a name against a regular expression.
 * @author Volker Bergmann */
public abstract class AbstractRegexNamingRule extends AbstractAludraTestRule {

    private static final String REGEX_PROPERTY_NAME = "regex";
    private static final String MESSAGE_PROPERTY_NAME = "violationMessage";

    /** The pmd property definition for the regex */
    public final StringProperty regexProperty;

    /** The pmd property definition for the violation message */
    public final StringProperty messageProperty;

    /** Default constructor, setting the default regular expression
     * @param defaultRegex the default regex to use if none is configured explicitly.
     * @param defaultMessage the default message to use for violation messages if none is configured explicitly. */
    public AbstractRegexNamingRule(String defaultRegex, String defaultMessage) {
        this.regexProperty = new StringProperty(REGEX_PROPERTY_NAME, "the regular expression to be matched", defaultRegex, 1f);
        super.definePropertyDescriptor(this.regexProperty);
        this.messageProperty = new StringProperty(MESSAGE_PROPERTY_NAME, "the regular expression to be matched", defaultMessage,
                1f);
        super.definePropertyDescriptor(this.messageProperty);
    }

    /** Tells if the provided name matches the configured regular expression.
     * @param name the name to check
     * @return true if the name matches the configured regex, otherwise false */
    protected boolean matches(String name) {
        Pattern pattern = Pattern.compile(getRegex());
        return pattern.matcher(name).matches();
    }

    protected void assertMatch(String name, AbstractNode node, Object data) {
        if (!matches(name)) {
            addViolationWithMessage(data, node, getViolationMessage());
        }
    }

    protected String getRegex() {
        return getProperty(regexProperty);
    }

    protected String getViolationMessage() {
        return getProperty(messageProperty);
    }

}
