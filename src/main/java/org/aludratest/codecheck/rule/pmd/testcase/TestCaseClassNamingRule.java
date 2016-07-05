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

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;

import org.aludratest.codecheck.rule.pmd.AbstractRegexNamingRule;

/** Requires the names of test case methods to match a regular expression.
 * @author Volker Bergmann */
public class TestCaseClassNamingRule extends AbstractRegexNamingRule {

    private static final String DEFAULT_REGEX = "[A-Z][A-Za-z0-9]*";
    private static final String DEFAULT_MESSAGE = "Test case class names should start with an uppercase letter "
            + "and contain only letters and digits";

    /** Default constructor, setting the default regular expression */
    public TestCaseClassNamingRule() {
        super(DEFAULT_REGEX, DEFAULT_MESSAGE);
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (isTestCaseClass(node)) {
            assertMatch(node.getImage(), node, data);
        }
        return data;
    }

}
