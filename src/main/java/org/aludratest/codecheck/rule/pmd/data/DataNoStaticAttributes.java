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

import org.aludratest.codecheck.rule.pmd.AbstractAludraTestRule;

import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;

/**
 * See <code>src/main/resources/pmd-rules-aludra.xml</code> or the project Site
 * for rule description.
 * 
 * @author falbrech
 * 
 */
public class DataNoStaticAttributes extends AbstractAludraTestRule {

    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        if (!node.isStatic() || node.isFinal() || !isDataClass(node)) {
            return super.visit(node, data);
        }

        addViolationWithMessage(data, node, "Do not use static non-final attributes in Data classes");

        return super.visit(node, data);
    }

}
