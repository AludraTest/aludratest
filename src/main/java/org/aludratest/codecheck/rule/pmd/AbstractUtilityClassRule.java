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

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;

public abstract class AbstractUtilityClassRule extends AbstractAludraTestRule {

    protected abstract Class<?> getUtilityBaseClass();

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        Class<?> utilClass = getUtilityBaseClass();
        if (node.getType() != null && isInterfaceImplemented(node, utilClass) && !isUtilClass(node.getType())) {
            // To discuss: Perhaps add marker for constructor and every single
            // method, not only one marker for whole class
            addViolationWithMessage(data, node, utilClass.getSimpleName()
                    + " class must not have visible constructor, and all public methods must be static");
        }

        return super.visit(node, data);
    }

}
