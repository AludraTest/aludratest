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

import java.util.concurrent.atomic.AtomicBoolean;

import org.aludratest.codecheck.rule.pmd.AbstractAludraTestRule;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

/**
 * See <code>src/main/resources/pmd-rules-aludra.xml</code> or the project Site
 * for rule description.
 * 
 * @author falbrech
 * 
 */
public class DataClassImplementToString extends AbstractAludraTestRule {

	@Override
	public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
		if (!isDataClass(node)) {
			return super.visit(node, data);
		}

		AtomicBoolean toStringImplemented = new AtomicBoolean(false);
		node.childrenAccept(new FindToStringVisitor(), toStringImplemented);
		if (!toStringImplemented.get()) {
			addViolationWithMessage(data, node, "Data class must implement toString()");
		}

		return super.visit(node, data);
	}

	private static class FindToStringVisitor extends AbstractJavaRule {

		@Override
		public Object visit(ASTMethodDeclaration node, Object data) {
			if (!(data instanceof AtomicBoolean)) {
				return null;
			}

			if ("toString".equals(node.getMethodName())
					&& node.getFirstDescendantOfType(ASTMethodDeclarator.class).getParameterCount() == 0) {
				// return type needs not to be checked because other than String
				// would cause compile error
				((AtomicBoolean) data).set(true);
			}

			return super.visit(node, data);
		}
	}

}
