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
package org.aludratest.codecheck.rule.pmd.awl;

import org.aludratest.codecheck.rule.pmd.AbstractAludraTestRule;
import org.aludratest.dict.Data;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;

/**
 * See <code>src/main/resources/pmd-rules-aludra.xml</code> or the project Site
 * for rule description.
 * 
 * @author falbrech
 * 
 */
public class AWLActionParameterDataClass extends AbstractAludraTestRule {

	@Override
	public Object visit(ASTMethodDeclaration node, Object data) {
		if (!node.isPublic() || !isAWLClass(node)) {
			return super.visit(node, data);
		}

		ASTMethodDeclarator decl = node.getFirstDescendantOfType(ASTMethodDeclarator.class);
		if (decl != null && decl.getParameterCount() > 0) {
			// only check first parameter, as other parameters will be reported
			// by OnlyOneParameter rule
			ASTFormalParameter param = decl.getFirstDescendantOfType(ASTFormalParameter.class);
			if (param != null) {
				ASTClassOrInterfaceType pType = param.getFirstDescendantOfType(ASTClassOrInterfaceType.class);
				if (pType != null && pType.getType() != null && !Data.class.isAssignableFrom(pType.getType())) {
					addViolationWithMessage(data, param,
							"ActionWordLibrary parameters must be subclasses of class " + Data.class.getName());
				}
			}
		}

		return super.visit(node, data);
	}
}
