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
package org.aludratest.codecheck.rule.pmd.testcase;

import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.rule.properties.StringProperty;

import org.aludratest.codecheck.rule.pmd.AbstractAludraTestRule;

/** Requires the names of test case methods to match a regular expression.
 * @author Volker Bergmann */
public class TestCaseClassNamingRule extends AbstractAludraTestRule {

    private static final String DEFAULT_REGEX = "[A-Z][A-Za-z0-9]*";

    /** The pmd property definition for the regex */
    public static final StringProperty REGEX_PROPERTY = new StringProperty("regex", "the regular expression to be matched",
            DEFAULT_REGEX, 1f);

    /** Default constructor, setting the default regular expression */
    public TestCaseClassNamingRule() {
        super.definePropertyDescriptor(REGEX_PROPERTY);
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (isTestCaseClass(node)) {
            String regex = getProperty(REGEX_PROPERTY);
            Pattern pattern = Pattern.compile(regex);
            String className = node.getImage();
            if (!pattern.matcher(className).matches()) {
                addViolationWithMessage(data, node, "Test class names should match the regular expression '" + regex + "'");
            }
        }
        return data;
    }

}
