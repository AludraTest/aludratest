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
import org.aludratest.service.gui.component.GUIComponent;
import org.aludratest.service.gui.web.uimap.UIMap;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;

/**
 * See <code>src/main/resources/pmd-rules-aludra.xml</code> or the project Site
 * for rule description.
 * 
 * @author falbrech
 * 
 */
public class UIMapMethodReturnComponent extends AbstractAludraTestRule {

	@Override
	public Object visit(ASTMethodDeclaration node, Object data) {
		if (!isUIMapClass(node)) {
			return null;
		}

		if (node.isPublic()) {
			// return type must be instanceof GUIComponent or UIMap
			ASTClassOrInterfaceType returnType = node.getResultType().getFirstDescendantOfType(ASTClassOrInterfaceType.class);
            if (node.getResultType().isVoid() || !isUIMapOrGUIComponentClass(returnType)) {
				addViolationWithMessage(data, node,
						"Public methods in UIMap classes must return UIMap or GUIComponent compatible types");
			}
		}

		return super.visit(node, data);
	}

    private boolean isUIMapOrGUIComponentClass(ASTClassOrInterfaceType checkType) {
        if (checkType == null) {
            return true; // means "ignore class"
        }
        Class<?> clazz = checkType.getType();
        if (clazz == null) {
            return true; // means "ignore class"
        }

        return UIMap.class.isAssignableFrom(clazz) || GUIComponent.class.isAssignableFrom(clazz);
    }
}
