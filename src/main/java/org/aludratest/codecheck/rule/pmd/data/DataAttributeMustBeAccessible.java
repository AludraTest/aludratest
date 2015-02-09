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

import net.sourceforge.pmd.Report.ProcessingError;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;

import org.aludratest.codecheck.rule.pmd.AbstractAludraTestRule;
import org.databene.commons.StringUtil;
import org.jaxen.JaxenException;

/**
 * See <code>src/main/resources/pmd-rules-aludra.xml</code> or the project Site
 * for rule description.
 * 
 * @author falbrech
 * 
 */
public class DataAttributeMustBeAccessible extends AbstractAludraTestRule {

    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        if (!(isDataClass(node))) {
            return super.visit(node, data);
        }

        // get class information of containing class. If none, abort.
        ASTClassOrInterfaceDeclaration clsDecl = getClassOrInterfaceDeclaration(node);
        if (clsDecl == null || clsDecl.getType() == null) {
            return super.visit(node, data);
        }

        String attributeName = node.getVariableName();

        checkReadWriteProperty(attributeName, node, data);
        return super.visit(node, data);
    }

    private void checkReadWriteProperty(String attrName, ASTFieldDeclaration node, Object data) {
        ASTClassOrInterfaceDeclaration classDecl = node.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class);
        String attributeName = attrName;
        // only capitalize if second letter is lowercase or number (or only one
        // letter attribute)
        if (attributeName.length() == 1 || Character.isLowerCase(attributeName.charAt(1))
                || Character.isDigit(attributeName.charAt(1))) {
            attributeName = StringUtil.capitalize(attributeName);
        }
        
        // we can safely ignore the convention for "isXYZ" for boolean values,
        // as boolean values are not allowed in Data classes anyway
        try {
            if (classDecl.findChildNodesWithXPath(".//MethodDeclaration[@MethodName='get" + attributeName + "']").isEmpty()) {
                addViolationWithMessage(data, node,
                        "Attributes of Data classes must have a getter and a setter method. Method not found: get"
                                + attributeName);
                return;
            }
            if (classDecl.findChildNodesWithXPath(".//MethodDeclaration[@MethodName='set" + attributeName + "']").isEmpty()) {
                addViolationWithMessage(data, node,
                        "Attributes of Data classes must have a getter and a setter method. Method not found: set"
                                + attributeName);
            }
        }
        catch (JaxenException je) {
            ((RuleContext) data).getReport().addError(
                    new ProcessingError(je.getMessage(), ((RuleContext) data).getSourceCodeFilename()));
        }
    }

}
