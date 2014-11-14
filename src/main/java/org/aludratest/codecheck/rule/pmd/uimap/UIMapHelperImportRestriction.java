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
package org.aludratest.codecheck.rule.pmd.uimap;

import org.aludratest.codecheck.rule.pmd.AbstractAludraTestRule;
import org.aludratest.dict.ActionWordLibrary;
import org.aludratest.service.gui.web.page.PageHelper;
import org.aludratest.service.gui.web.page.PageUtility;
import org.aludratest.testcase.AludraTestCase;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;

/**
 * See <code>src/main/resources/pmd-rules-aludra.xml</code> or the project Site
 * for rule description.
 * 
 * @author falbrech
 * 
 */
public class UIMapHelperImportRestriction extends AbstractAludraTestRule {
    
    private static final Class<?>[] FORBIDDEN_IMPORT_PARENTS = { PageHelper.class, PageUtility.class, AludraTestCase.class,
            ActionWordLibrary.class };

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (!isUIMapHelperClass(node) && !isUIMapUtilityClass(node)) {
            return null;
        }

        // check all imports
        for (ASTImportDeclaration impNode : getImports(node)) {
            checkImport(impNode, data);
        }

        return null;
    }

    private void checkImport(ASTImportDeclaration importNode, Object data) {
        Class<?> importClass = importNode.getType();

        for (Class<?> clazz : FORBIDDEN_IMPORT_PARENTS) {
            if (clazz.isAssignableFrom(importClass)) {
                addViolationWithMessage(data, importNode, "Illegal import for a UIMapHelper or UIMapUtility class");
                return;
            }
        }
    }

}
