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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.AbstractJavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

import org.aludratest.dict.ActionWordLibrary;
import org.aludratest.dict.Data;
import org.aludratest.service.gui.web.page.Page;
import org.aludratest.service.gui.web.page.PageHelper;
import org.aludratest.service.gui.web.page.PageUtility;
import org.aludratest.service.gui.web.uimap.UIMap;
import org.aludratest.service.gui.web.uimap.UIMapHelper;
import org.aludratest.service.gui.web.uimap.UIMapUtility;
import org.aludratest.testcase.AludraTestCase;

/**
 * Abstract base class for all AludraTest code check rules. <br>
 * An AludraTest code check rule must implement the
 * 
 * @author falbrech
 * 
 */
public abstract class AbstractAludraTestRule extends AbstractJavaRule {

    protected AbstractAludraTestRule() {
        setPriority(RulePriority.HIGH);
    }

    protected static final ASTClassOrInterfaceDeclaration getClassOrInterfaceDeclaration(AbstractJavaNode node) {
        ASTClassOrInterfaceDeclaration decl = null;
        if (!(node instanceof ASTClassOrInterfaceDeclaration)) {
            decl = node.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class);
        }
        else {
            decl = (ASTClassOrInterfaceDeclaration) node;
        }

        return decl;
    }

    /**
     * Returns <code>true</code> if the given AST node or its first
     * ClassOrInterfaceDeclaration parent node implements the given interface or
     * extends the given class. <br>
     * If there is no such parent node, or if the ClassOrInterfaceDeclaration
     * node does not contain Java type information (PMD type resolution must be
     * enabled), <code>false</code> is returned.
     * 
     * @param node
     *            AST node to check.
     * @param iface
     *            Interface or base class to check for being implemented by the
     *            node, if a ClassOrInterfaceDeclaration, or by the first AST
     *            parent of that node type.
     * 
     * @return <code>true</code> if the node or its parent type declaration
     *         declares a type implementing the given interface,
     *         <code>false</code> otherwise.
     */
    protected static final boolean isInterfaceImplemented(AbstractJavaNode node, Class<?> iface) {
        ASTClassOrInterfaceDeclaration clsDecl = getClassOrInterfaceDeclaration(node);
        if (clsDecl == null || clsDecl.getType() == null || clsDecl.getType().isInterface()) {
            return false;
        }

        return iface.isAssignableFrom(clsDecl.getType());
    }

    /** Returns <code>true</code> if and only if the given class fulfills the following criteria:
     * <ul>
     * <li>All public methods are static</li>
     * <li>The constructor is not visible (package-private or private)</li>
     * </ul>
     * 
     * @param clazz Class to check for util class structure
     * 
     * @return <code>true</code> if the class matches the util class criteria, <code>false</code> otherwise. */
    protected static final boolean isUtilClass(Class<?> clazz) {

        // check constructor first
        Constructor<?>[] cstrs = clazz.getDeclaredConstructors();

        // if there is NO constructor, we have to assume there is a public
        // implicit constructor
        if (cstrs == null || cstrs.length == 0) {
            return false;
        }

        for (Constructor<?> cstr : cstrs) {
            if (Modifier.isPublic(cstr.getModifiers()) || Modifier.isProtected(cstr.getModifiers())) {
                return false;
            }
        }

        // now all methods
        for (Method m : clazz.getDeclaredMethods()) {
            if (Modifier.isPublic(m.getModifiers()) && !Modifier.isStatic(m.getModifiers())) {
                return false;
            }
        }

        return true;
    }

    protected static final List<ASTImportDeclaration> getImports(AbstractJavaNode node) {
        ASTCompilationUnit cu = node.getFirstParentOfType(ASTCompilationUnit.class);
        if (cu == null) {
            return Collections.emptyList();
        }

        return cu.findChildrenOfType(ASTImportDeclaration.class);
    }

    protected static final boolean isTestCaseClass(AbstractJavaNode node) {
        return isInterfaceImplemented(node, AludraTestCase.class);
    }

    protected static final boolean isAWLClass(AbstractJavaNode node) {
        return isInterfaceImplemented(node, ActionWordLibrary.class);
    }

    protected static boolean isPageClass(AbstractJavaNode node) {
        return isInterfaceImplemented(node, Page.class);
    }

    protected static boolean isPageUtilityClass(AbstractJavaNode node) {
        return isInterfaceImplemented(node, PageUtility.class);
    }

    protected static boolean isPageUtilityClass(Class<?> clazz) {
        return PageUtility.class.isAssignableFrom(clazz);
    }

    protected static boolean isPageHelperClass(AbstractJavaNode node) {
        return isInterfaceImplemented(node, PageHelper.class);
    }

    protected static boolean isUIMapClass(AbstractJavaNode node) {
        return isInterfaceImplemented(node, UIMap.class);
    }

    protected static boolean isUIMapHelperClass(AbstractJavaNode node) {
        return isInterfaceImplemented(node, UIMapHelper.class);
    }

    protected static boolean isUIMapUtilityClass(AbstractJavaNode node) {
        return isInterfaceImplemented(node, UIMapUtility.class);
    }

    protected static boolean isDataClass(AbstractJavaNode node) {
        return isInterfaceImplemented(node, Data.class);
    }

    protected static boolean isDataClass(Class<?> clazz) {
        return Data.class.isAssignableFrom(clazz);
    }

}
