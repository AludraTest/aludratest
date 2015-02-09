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

import java.util.Collection;
import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.AbstractJavaNode;

import org.aludratest.codecheck.rule.pmd.AbstractAludraTestRule;
import org.aludratest.dict.Data;
import org.jaxen.JaxenException;

/**
 * See <code>src/main/resources/pmd-rules-aludra.xml</code> or the project Site
 * for rule description.
 * 
 * @author falbrech
 * 
 */
public class DataSimpleAttribute extends AbstractAludraTestRule {

	@Override
	public Object visit(ASTFieldDeclaration node, Object data) {
		if (!isDataClass(node)) {
			return super.visit(node, data);
		}

		Class<?> fType = node.getType();
		if (fType != null) {
            // special case: Collection; check type parameter
            if (Collection.class.isAssignableFrom(fType)) {
                checkCollectionType(node, data);
            }
            else if (!String.class.isAssignableFrom(fType) && !Data.class.isAssignableFrom(fType)) {
				addViolationWithMessage(data, node,
						"Data classes must have only attributes of type String, Collection, or Data (or subtypes)");
			}
		}

		return super.visit(node, data);
	}

    private void checkCollectionType(ASTFieldDeclaration node, Object data) {
        boolean valid = false;
        try {
            List<?> lsNodes = node
                    .findChildNodesWithXPath("./Type/ReferenceType/ClassOrInterfaceType/TypeArguments/TypeArgument/ReferenceType/ClassOrInterfaceType");
            if (lsNodes != null && !lsNodes.isEmpty()) {
                ASTClassOrInterfaceType tpNode = (ASTClassOrInterfaceType) lsNodes.get(0);
                Class<?> clsTypeParam = tpNode.getType();
                // PMD seems not to set type information for generics type
                // parameters.
                // So extract this information from imports.
                if (clsTypeParam == null) {
                    clsTypeParam = resolveClassFromImports(tpNode.getImage(), node);
                }
                if (clsTypeParam != null && Data.class.isAssignableFrom(clsTypeParam)) {
                    valid = true;
                }
            }
        }
        catch (JaxenException je) {
            // treat as "not found", thus invalid
        }

        if (!valid) {
            addViolationWithMessage(data, node,
                    "Collection attribute of Data class must have a subclass of Data as Type parameter");
        }
    }

    private Class<?> resolveClassFromImports(String simpleClassName, AbstractJavaNode node) {
        String className = "." + simpleClassName;
        for (ASTImportDeclaration impDecl : getImports(node)) {
            String impName = impDecl.getImportedName();
            if (impName.endsWith(className)) {
                return impDecl.getType();
            }
        }

        // if not yet found, could be same package
        Class<?> parentClass = getClassOrInterfaceDeclaration(node).getType();
        if (parentClass != null && parentClass.getPackage() != null) {
            String clsName = parentClass.getPackage().getName() + className;
            try {
                return parentClass.getClassLoader().loadClass(clsName);
            }
            catch (Throwable t) { // NOSONAR
                return null;
            }
        }

        return null;
    }
}
