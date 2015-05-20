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

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;

import org.aludratest.codecheck.rule.pmd.AbstractAludraTestRule;
import org.databene.commons.BeanUtil;

/**
 * See <code>src/main/resources/pmd-rules-aludra.xml</code> or the project Site
 * for rule description.
 * 
 * @author falbrech
 * 
 */
public class DataNoCircularGraphs extends AbstractAludraTestRule {

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (!isDataClass(node)) {
            return super.visit(node, data);
        }

        Class<?> clazz = node.getType();
        if (clazz != null) {
            List<String> messages = new ArrayList<String>();
            checkDependencies(clazz, new Stack<Class<?>>(), new Stack<String>(), messages);
            if (messages.size() > 0) {
                addViolationWithMessage(data, node, messages.get(0));
            }
        }

        return super.visit(node, data);
    }

    private boolean checkDependencies(Class<?> clazz, Stack<Class<?>> classStack, Stack<String> path, List<String> messages) {
        boolean circular = classStack.contains(clazz);
        classStack.push(clazz);
        if (circular) {
            messages.add("There is a circular dependency of Data attributes: " + path);
            return false;
        }
        else {
            for (Field field : clazz.getFields()) {
                Class<?> fieldType = field.getType();
                if (isDataClass(fieldType) && Modifier.isPublic(field.getModifiers())) {
                    path.push(field.toString());
                    boolean doContinue = checkDependencies(fieldType, classStack, path, messages);
                    path.pop();
                    if (!doContinue) {
                        return false;
                    }
                }
            }
            for (PropertyDescriptor property : BeanUtil.getPropertyDescriptors(clazz)) {
                Class<?> propertyType = property.getPropertyType();
                if (isDataClass(propertyType)) {
                    path.push(property.getShortDescription());
                    boolean doContinue = checkDependencies(propertyType, classStack, path, messages);
                    path.pop();
                    if (!doContinue) {
                        return false;
                    }
                }
            }
        }
        classStack.pop();
        return true;
    }

}
