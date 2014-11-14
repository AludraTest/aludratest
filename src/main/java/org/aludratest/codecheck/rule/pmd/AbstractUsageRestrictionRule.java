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
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;

public abstract class AbstractUsageRestrictionRule extends AbstractAludraTestRule {

    private UsageRestrictionCheck usageCheck;

    protected AbstractUsageRestrictionRule() {
        this.usageCheck = createUsageRestrictionCheck();
    }

    protected abstract UsageRestrictionCheck createUsageRestrictionCheck();

    protected abstract String getImportViolationMessage();

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        for (ASTImportDeclaration impDecl : getImports(node)) {
            if (impDecl.getType() != null && node.getType() != null
                    && !usageCheck.isValidImport(impDecl.getType(), node.getType())) {
                addViolationWithMessage(data, impDecl, getImportViolationMessage());
            }
        }

        // do not dive deeper, as we do not care about member classes (they have
        // the same imports!).
        return null;
    }

}
