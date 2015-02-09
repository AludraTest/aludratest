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

import org.aludratest.codecheck.rule.pmd.AbstractUsageRestrictionRule;
import org.aludratest.codecheck.rule.pmd.ClassMatcher;
import org.aludratest.codecheck.rule.pmd.UsageRestrictionCheck;
import org.aludratest.service.gui.web.page.Page;
import org.aludratest.service.gui.web.page.PageHelper;
import org.aludratest.service.gui.web.page.PageUtility;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;

/**
 * See <code>src/main/resources/pmd-rules-aludra.xml</code> or the project Site
 * for rule description.
 * 
 * @author falbrech
 * 
 */
public class PageUtilityUsageRestriction extends AbstractUsageRestrictionRule {

    @Override
    protected UsageRestrictionCheck createUsageRestrictionCheck() {
        UsageRestrictionCheck pageHelperUsageCheck = new UsageRestrictionCheck(new ClassMatcher() {
            @Override
            public boolean matches(Class<?> clazz) {
                return isPageUtilityClass(clazz);
            }
        });

        pageHelperUsageCheck.addAllowedUserClass(Page.class);
        pageHelperUsageCheck.addAllowedUserClass(PageHelper.class);
        pageHelperUsageCheck.addAllowedUserClass(PageUtility.class);
        return pageHelperUsageCheck;
    }

    @Override
    protected String getImportViolationMessage() {
        return "PageHelper classes must only be used by Pages and other PageHelpers";
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (!isPageHelperClass(node) && !isPageUtilityClass(node) && !isPageClass(node)) {
            return super.visit(node, data);
        }

        return null;
    }
}
