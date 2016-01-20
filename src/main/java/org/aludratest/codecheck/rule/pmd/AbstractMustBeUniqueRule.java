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

import java.util.HashSet;
import java.util.Set;

import net.sourceforge.pmd.RuleContext;

/** Abstract base class for rules checking uniqueness of names, spanning over multiple files, thus using the rule context to store
 * found names. <br>
 * Subclasses must implement all needed <code>visit()</code> methods and use the set returned by
 * {@link #getUnqiueSimpleNames(Object)} to check for duplicates and add found names to. See existing subclasses for examples.
 * 
 * @author falbrech */
public class AbstractMustBeUniqueRule extends AbstractAludraTestRule {

    private static final String UNIQUE_SIMPLE_NAMES_ATTRIBUTE = "uniqueSimpleDataNames";

    @Override
    public void start(RuleContext ctx) {
        ctx.setAttribute(UNIQUE_SIMPLE_NAMES_ATTRIBUTE, new HashSet<String>());
        super.start(ctx);
    }

    @Override
    public void end(RuleContext ctx) {
        // has already been consumed
        ctx.removeAttribute(UNIQUE_SIMPLE_NAMES_ATTRIBUTE);
        super.end(ctx);
    }

    @SuppressWarnings("unchecked")
    protected final Set<String> getUnqiueSimpleNames(Object data) {
        if (!(data instanceof RuleContext)) {
            return null;
        }

        return (Set<String>) ((RuleContext) data).getAttribute(UNIQUE_SIMPLE_NAMES_ATTRIBUTE);
    }

}
