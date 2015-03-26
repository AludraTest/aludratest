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
package org.aludratest.codecheck.rule.pmd.guicomponent;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;

import org.aludratest.codecheck.rule.pmd.AbstractAludraTestRule;

/**
 * See <code>src/main/resources/pmd-rules-aludra.xml</code> or the project Site
 * for rule description.
 * 
 * @author falbrech
 * 
 */
public class OnlyUIMapsConstructGUIComponent extends AbstractAludraTestRule {

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (isUIMapClass(node) || isUIMapHelperClass(node) || isUIMapUtilityClass(node)) {
            return null;
        }

        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTName node, Object data) {
        if (node.jjtGetParent() instanceof ASTPrimaryPrefix) {
            String image = node.getImage();
            if (image != null && image.endsWith(".getComponentFactory")) {
                addViolationWithMessage(data, node, "Only UIMap related classes are allowed to access a GUI Component Factory");
            }
        }

        return super.visit(node, data);
    }

}
