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

import org.aludratest.codecheck.rule.pmd.ImportRestrictions;

/**
 * See <code>src/main/resources/pmd-rules-aludra.xml</code> or the project Site
 * for rule description.
 * 
 * @author falbrech
 * 
 */
public class TestCaseImportRestriction extends ImportRestrictions {

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isAbstract() || !isTestCaseClass(node)) {
            return null;
        }

        return super.visit(node, data);
    }

    @Override
    protected String getImportViolationMessage(String importName) {
        return "Illegal import for a TestCase class: " + importName;
    }
}
