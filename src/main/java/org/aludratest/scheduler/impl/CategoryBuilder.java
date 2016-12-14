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
package org.aludratest.scheduler.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.aludratest.scheduler.RunnerTree;
import org.aludratest.scheduler.node.ExecutionMode;
import org.aludratest.scheduler.node.RunnerGroup;
import org.aludratest.scheduler.node.RunnerNode;
import org.aludratest.scheduler.util.TestAttributeUtil;
import org.aludratest.testcase.AludraTestCase;

public class CategoryBuilder {

    private String removePackagePrefix;

    private List<String> categoryOrder;

    public CategoryBuilder(List<String> categoryOrder) {
        this.categoryOrder = categoryOrder;
    }

    public CategoryBuilder(String removePackagePrefix) {
        this.removePackagePrefix = removePackagePrefix;
        this.categoryOrder = Collections.emptyList();
    }

    public RunnerGroup getParentRunnerGroup(RunnerTree tree, Class<? extends AludraTestCase> clazz) {
        if (!categoryOrder.isEmpty()) {
            List<String> categories = new ArrayList<String>();
            StringBuilder prefix = new StringBuilder();
            for (String cat : categoryOrder) {
                List<String> catVals = TestAttributeUtil.getTestAttributes(clazz).get(cat);
                String catVal = catVals == null || catVals.isEmpty() ? null : catVals.get(0);
                if (catVal == null) {
                    catVal = cat + " unknown";
                }
                categories.add(prefix + catVal);
                prefix.append(catVal).append(".");
            }
            return forceGetRunnerGroup(tree, categories);
        }
        else {
            String className = clazz.getName();
            if (!"".equals(removePackagePrefix) && className.startsWith(removePackagePrefix)) {
                className = className.substring(removePackagePrefix.length());
                if (className.startsWith(".")) {
                    className = className.substring(1);
                }
            }

            // remove class itself
            if (className.contains(".")) {
                className = className.substring(0, className.lastIndexOf('.'));
            }
            else {
                // no package available
                return tree.getRoot();
            }

            List<String> groups = new ArrayList<String>();
            int i = 0;
            while (i < className.length()) {
                int nextIndex = className.indexOf('.', i);
                if (nextIndex == -1) {
                    groups.add(className);
                    break;
                }
                groups.add(className.substring(0, nextIndex));
                i = nextIndex + 1;
            }

            return forceGetRunnerGroup(tree, groups);
        }
    }

    private RunnerGroup forceGetRunnerGroup(RunnerTree tree, List<String> pathSegments) {
        RunnerGroup group = tree.getRoot();

        for (String seg : pathSegments) {
            boolean found = false;
            for (RunnerNode node : group.getChildren()) {
                if (node instanceof RunnerGroup && seg.equals(node.getName())) {
                    group = (RunnerGroup) node;
                    found = true;
                    break;
                }
            }
            if (!found) {
                // TODO here would be the place to select execution mode based on whatever information
                group = tree.createGroup(seg, ExecutionMode.PARALLEL, group);
            }
        }

        return group;
    }
}