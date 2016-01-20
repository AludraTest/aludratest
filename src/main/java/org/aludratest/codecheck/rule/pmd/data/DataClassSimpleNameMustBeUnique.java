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
package org.aludratest.codecheck.rule.pmd.data;

import java.util.Set;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;

import org.aludratest.codecheck.rule.pmd.AbstractMustBeUniqueRule;

/**
 * See <code>src/main/resources/pmd-rules-aludra.xml</code> or the project Site
 * for rule description.
 * 
 * @author falbrech
 * 
 */
public class DataClassSimpleNameMustBeUnique extends AbstractMustBeUniqueRule {

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (!isDataClass(node)) {
            return super.visit(node, data);
        }

        Set<String> uniqueSimpleNames = getUnqiueSimpleNames(data);
        if (uniqueSimpleNames == null) {
            return super.visit(node, data);
        }

        String simpleName = node.getImage();
        if (uniqueSimpleNames.contains(simpleName)) {
            addViolationWithMessage(data, node, "There is more than one Data class with the name " + simpleName);
        }

        uniqueSimpleNames.add(simpleName);
        return super.visit(node, data);
    }

}
