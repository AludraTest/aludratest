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
package org.aludratest.codecheck.rule.pmd.annot;

import java.util.Arrays;
import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.AbstractJavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.rule.properties.StringMultiProperty;

/**
 * See <code>src/main/resources/pmd-rules-aludra.xml</code> or the project Site for rule description.
 * 
 * @author falbrech
 * 
 */
public class NonCombinableAnnotations extends AbstractJavaRule {

    /**
     * Property descriptor for a comma-separated list of annotation names which must not be combined.
     */
    public static final StringMultiProperty ANNOTATION_NAMES_DESCRIPTOR = new StringMultiProperty("annotationNames",
            "Simple names of annotations which cannot be combined (comma-separated list)", new String[] { "After", "Before" },
            1.0f, ',');

    /**
     * Creates a new instance of this rule.
     */
    public NonCombinableAnnotations() {
        definePropertyDescriptor(ANNOTATION_NAMES_DESCRIPTOR);
    }

    @Override
    public Object visit(ASTAnnotation node, Object data) {
        // if it is one of the listed annotations, search siblings for other
        // ones
        List<String> checkNames = Arrays.asList(getProperty(ANNOTATION_NAMES_DESCRIPTOR));
        String annotName = getAnnotationName(node);

        if (checkNames.contains(annotName)) {
            AbstractJavaNode parent = (AbstractJavaNode) node.getNthParent(1);
            List<ASTAnnotation> siblings = parent.findChildrenOfType(ASTAnnotation.class);

            // search for a sibling with a name contained in checkNames
            for (ASTAnnotation annot : siblings) {
                if (annot == node) { // NOSONAR - can only be SAME object
                    continue;
                }

                if (checkNames.contains(getAnnotationName(annot))) {
                    addViolationWithMessage(data, node, "Annotations " + annotName + " and " + getAnnotationName(annot)
                            + " must not be combined.");
                    return super.visit(node, data);
                }
            }
        }

        return super.visit(node, data);
    }

    private String getAnnotationName(ASTAnnotation node) {
        return node.getFirstDescendantOfType(ASTName.class).getImage();
    }

}
