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
package org.aludratest.codecheck.rule.pmd.page;

import org.aludratest.codecheck.rule.pmd.AbstractAludraTestRule;
import org.aludratest.service.gui.web.page.Page;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;

/**
 * See <code>src/main/resources/pmd-rules-aludra.xml</code> or the project Site
 * for rule description.
 * 
 * @author falbrech
 * 
 */
public class PageActionMethodReturnPageObject extends AbstractAludraTestRule {

	@Override
	public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
		if (!isPageClass(node)) {
			return null;
		}

		return super.visit(node, data);
	}

	@Override
	public Object visit(ASTMethodDeclaration node, Object data) {
		if (!isPageClass(node)) {
			return null;
		}

		if (node.isPublic()) {
			// return type must be instanceof AWL
			ASTClassOrInterfaceType returnType = node.getResultType().getFirstDescendantOfType(ASTClassOrInterfaceType.class);
            if (node.getResultType().isVoid()
                    || (returnType != null && returnType.getType() != null && !Page.class.isAssignableFrom(returnType.getType()))) {
                addViolationWithMessage(data, node.getFirstDescendantOfType(ASTMethodDeclarator.class),
                        "Public methods in Page classes must return Page compatible type");
			}
		}

		return super.visit(node, data);
	}
}
