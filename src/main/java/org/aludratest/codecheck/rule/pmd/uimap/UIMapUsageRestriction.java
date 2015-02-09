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

import org.aludratest.codecheck.rule.pmd.AbstractUsageRestrictionRule;
import org.aludratest.codecheck.rule.pmd.UsageRestrictionCheck;
import org.aludratest.service.gui.web.page.Page;
import org.aludratest.service.gui.web.page.PageHelper;
import org.aludratest.service.gui.web.page.PageUtility;
import org.aludratest.service.gui.web.uimap.UIMap;
import org.aludratest.service.gui.web.uimap.UIMapHelper;
import org.aludratest.service.gui.web.uimap.UIMapUtility;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;

/**
 * See <code>src/main/resources/pmd-rules-aludra.xml</code> or the project Site
 * for rule description.
 * 
 * @author falbrech
 * 
 */
public class UIMapUsageRestriction extends AbstractUsageRestrictionRule {
    
    @Override
    protected UsageRestrictionCheck createUsageRestrictionCheck() {
        UsageRestrictionCheck uimapUsageCheck = new UsageRestrictionCheck(UIMap.class);
        uimapUsageCheck.addAllowedUserClass(UIMap.class);
        uimapUsageCheck.addAllowedUserClass(UIMapHelper.class);
        uimapUsageCheck.addAllowedUserClass(UIMapUtility.class);
        uimapUsageCheck.addAllowedUserClass(Page.class);
        uimapUsageCheck.addAllowedUserClass(PageHelper.class);
        uimapUsageCheck.addAllowedUserClass(PageUtility.class);
        return uimapUsageCheck;
    }

    @Override
    protected String getImportViolationMessage() {
        return "UIMap classes must only be used by UIMap related classes, Pages or Page related classes";
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        // optimization
        if (!isPageRelatedClass(node) && !isUIMapRelatedClass(node)) {
            return super.visit(node, data);
        }

        return null;
    }
    
    private boolean isPageRelatedClass(ASTClassOrInterfaceDeclaration node) {
        return isPageClass(node) || isPageHelperClass(node) || isPageUtilityClass(node);
    }

    private boolean isUIMapRelatedClass(ASTClassOrInterfaceDeclaration node) {
        return isUIMapClass(node) || isUIMapHelperClass(node) || isUIMapUtilityClass(node);
    }

}
