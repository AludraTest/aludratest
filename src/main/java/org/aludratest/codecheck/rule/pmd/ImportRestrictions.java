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

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.rule.properties.BooleanProperty;
import net.sourceforge.pmd.lang.rule.properties.StringProperty;

import org.aludratest.dict.ActionWordLibrary;
import org.aludratest.dict.Data;
import org.aludratest.service.AludraService;
import org.aludratest.service.ComponentId;
import org.aludratest.testcase.AludraTestCase;

/**
 * Configurable and extensible rule for restricting imports. When not subclassing this class, it will check the imports in ALL
 * classes under test. Subclasses override the implemented <code>visit()</code> method to first check the class if the imports
 * shall be checked, and only if conditions are met, invoke the super visit() method.
 * 
 * @author falbrech
 * 
 */
public class ImportRestrictions extends AbstractAludraTestRule {

    /**
     * Property descriptor for the configuration property which import prefixes to allow. This is a comma-separated list of
     * prefixes to allow. Please note that e.g. <code>"java.util."</code> would match not only all <code>java.util.*</code>
     * classes, but also all <code>java.util.regex.*</code> classes! Note also that you must not include the asterisk (*) in the
     * prefix value here, because it is a string-based prefix, not a package name.
     */
    public static final StringProperty ALLOWED_IMPORT_PREFIXES_DESCRIPTOR = new StringProperty("allowedImportPrefixes",
            "Comma-separated list of import prefixes to allow", "java.util.,java.lang.", 1.0f);

    /**
     * Property descriptor for the configuration property if imports of annotation classes should generally be ignored. If set to
     * <code>true</code>, all kinds of annotation classes are allowed to be imported, otherwise, they would have to be enabled
     * using the other configuration properties of this rule, or would raise a rule violation.
     */
    public static final BooleanProperty ANNOTATIONS_ALLOWED_DESCRIPTOR = new BooleanProperty("annotationsAllowed",
            "Allow imports of Annotation Classes always", Boolean.TRUE, 2.0f);

    /**
     * Property descriptor for the configuration property if imports of utility classes should generally be ignored. <br>
     * A utility class is defined as a class with all public methods being static and no public constructor available. <br>
     * If set to <code>true</code>, all kinds of utility classes are allowed to be imported, otherwise, they would have to be
     * enabled using the other configuration properties of this rule, or would raise a rule violation.
     */
    public static final BooleanProperty UTIL_CLASSES_ALLOWED_DESCRIPTOR = new BooleanProperty("utilClassesAllowed",
            "Allow imports of arbitrary Utility Classes (having only static methods) always", Boolean.FALSE, 3.0f);

    private static final Class<?>[] DEFAULT_ALLOWED_IMPORT_PARENTS = { ActionWordLibrary.class, Data.class, AludraService.class,
        AludraTestCase.class, ComponentId.class };

    private String allowedImportPrefixes;

    private String[] allowedImportPrefixArray;

    /**
     * Creates a new instance of this rule.
     */
    public ImportRestrictions() {
        // super calls to avoid override of methods
        super.definePropertyDescriptor(ALLOWED_IMPORT_PREFIXES_DESCRIPTOR);
        super.definePropertyDescriptor(ANNOTATIONS_ALLOWED_DESCRIPTOR);
        super.definePropertyDescriptor(UTIL_CLASSES_ALLOWED_DESCRIPTOR);
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        // check all imports
        for (ASTImportDeclaration impNode : getImports(node)) {
            checkImport(impNode, data);
        }

        return null;
    }

    private void checkImport(ASTImportDeclaration node, Object data) {
        // first check for allowed prefixes - is faster
        String impName = node.getImportedName();
        Class<?> importClass = node.getType();

        if (!isAllowedImport(impName, importClass)) {
            addViolationWithMessage(data, node, getImportViolationMessage(impName));
        }
    }

    private String[] getAllowedImportPrefixArray() {
        String allowedImportPrefixesProperty = getProperty(ALLOWED_IMPORT_PREFIXES_DESCRIPTOR);
        if (allowedImportPrefixesProperty != null && allowedImportPrefixesProperty.equals(this.allowedImportPrefixes)
                && allowedImportPrefixArray != null) {
            return allowedImportPrefixArray;
        }

        if (allowedImportPrefixesProperty == null) {
            return new String[0];
        }
        String[] split = allowedImportPrefixesProperty.trim().split(",");
        List<String> prefixes = new ArrayList<String>(split.length);
        for (String s : split) {
            s = s.trim();
            if (s.endsWith("*")) {
                s = s.substring(0, s.length() - 1);
            }
            if (s.length() > 0) {
                prefixes.add(s);
            }
        }

        this.allowedImportPrefixes = allowedImportPrefixesProperty;
        this.allowedImportPrefixArray = prefixes.toArray(new String[0]);
        return allowedImportPrefixArray;
    }

    protected Class<?>[] getAllowedImportParents() {
        return DEFAULT_ALLOWED_IMPORT_PARENTS;
    }

    protected boolean isAllowedImport(String importName, Class<?> importClass) {
        for (String allowedPrefix : getAllowedImportPrefixArray()) {
            if (importName.startsWith(allowedPrefix)) {
                return true;
            }
        }

        // now, class based checks
        // Enums are always allowed
        if (importClass == null || (importClass.isAnnotation() && getProperty(ANNOTATIONS_ALLOWED_DESCRIPTOR))
                || importClass.isEnum()) {
            return true;
        }

        for (Class<?> clz : getAllowedImportParents()) {
            if (clz.isAssignableFrom(importClass)) {
                return true;
            }
        }

        // now most complex checks
        return (getProperty(UTIL_CLASSES_ALLOWED_DESCRIPTOR) && isUtilClass(importClass));
    }

    protected String getImportViolationMessage(String importName) {
        return "Illegal import for this kind of class: " + importName;
    }

}
