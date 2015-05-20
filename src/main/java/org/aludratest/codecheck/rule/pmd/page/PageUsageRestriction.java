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
import org.aludratest.codecheck.rule.pmd.UsageRestrictionCheck;
import org.aludratest.service.gui.web.page.Page;
import org.aludratest.service.gui.web.page.PageHelper;
import org.aludratest.service.gui.web.page.PageUtility;
import org.aludratest.testcase.AludraTestCase;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;

/**
 * See <code>src/main/resources/pmd-rules-aludra.xml</code> or the project Site
 * for rule description.
 * 
 * @author falbrech
 * 
 */
public class PageUsageRestriction extends AbstractUsageRestrictionRule {

    @Override
    protected UsageRestrictionCheck createUsageRestrictionCheck() {
        UsageRestrictionCheck pageUsageCheck = new UsageRestrictionCheck(Page.class);
        pageUsageCheck.addAllowedUserClass(Page.class);
        pageUsageCheck.addAllowedUserClass(PageHelper.class);
        pageUsageCheck.addAllowedUserClass(PageUtility.class);
        pageUsageCheck.addAllowedUserClass(AludraTestCase.class);
        return pageUsageCheck;
    }

    @Override
    protected String getImportViolationMessage() {
        return "Page classes must only be used by TestCases and other Page related classes";
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (!isPageUtilityClass(node) && !isPageHelperClass(node) && !isPageClass(node) && !isTestCaseClass(node)) {
            return super.visit(node, data);
        }

        return null;
    }

}
