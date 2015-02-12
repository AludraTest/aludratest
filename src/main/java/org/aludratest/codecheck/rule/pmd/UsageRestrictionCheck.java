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

public final class UsageRestrictionCheck {
    
    private List<Class<?>> allowedUserClasses = new ArrayList<Class<?>>();
    
    private List<Class<?>> forbiddenUserClasses = new ArrayList<Class<?>>();

    private ClassMatcher importChecker;
    
    
    public UsageRestrictionCheck(Class<?> importParentClassToCheck) {
        this(new ParentClassMatcher(importParentClassToCheck));
    }

    public UsageRestrictionCheck(ClassMatcher importChecker) {
        this.importChecker = importChecker;
    }
    
    public void addAllowedUserClass(Class<?> clazz) {
        if (!allowedUserClasses.contains(clazz)) {
            allowedUserClasses.add(clazz);
        }
    }
    
    public void addForbiddenUserClass(Class<?> clazz) {
        if (!forbiddenUserClasses.contains(clazz)) {
            forbiddenUserClasses.add(clazz);
        }
    }
    
    public boolean isValidImport(Class<?> importedClass, Class<?> importingClass) {
        // is this check responsible?
        if (!importChecker.matches(importedClass)) {
            return true;
        }

        // importingClass must not be in forbidden
        for (Class<?> clazz : forbiddenUserClasses) {
            if (clazz.isAssignableFrom(importingClass)) {
                return false;
            }
        }

        // if allowed are specified, importingClass must be in allowed
        if (!allowedUserClasses.isEmpty()) {
            for (Class<?> clazz : allowedUserClasses) {
                if (clazz.isAssignableFrom(importingClass)) {
                    return true;
                }
            }

            // not found in allowed
            return false;
        }

        // only here if passed forbidden and no allowed present, so...
        return true;
    }

    private static class ParentClassMatcher implements ClassMatcher {

        private Class<?> requiredParentClass;

        public ParentClassMatcher(Class<?> requiredParentClass) {
            this.requiredParentClass = requiredParentClass;
        }

        @Override
        public boolean matches(Class<?> clazz) {
            return requiredParentClass.isAssignableFrom(clazz);
        }
    }
}
