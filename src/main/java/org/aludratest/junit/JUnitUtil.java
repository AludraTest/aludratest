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
package org.aludratest.junit;

import org.aludratest.scheduler.node.RunnerGroup;
import org.aludratest.scheduler.node.RunnerLeaf;
import org.aludratest.scheduler.node.RunnerNode;
import org.junit.runner.Description;

/**
 * Utility class that provides general JUnit related testing features. 
 * @author Volker Bergmann
 */
public class JUnitUtil {

    /** Private constructor of utility class preventing instantiation by other classes */
    private JUnitUtil() {
    }

    /**
     * Creates a JUnit {@link Description} object for a {@link RunnerNode}.
     * For a {@link RunnerGroup}, a suite is created, for atomic tests a 
     * simple description object.
     * @param node the RunnerNode object to be wrapped
     * @param testClass the class which triggered JUnit execution 
     * @return a description of of the node
     */
    public static Description createDescription(RunnerNode node, Class<?> testClass) {
        if (node instanceof RunnerLeaf) {
            return Description.createTestDescription(testClass, node.getName());
        } else {
            RunnerGroup group = (RunnerGroup) node;
            Description suite = Description.createSuiteDescription(node.getName());
            for (RunnerNode child : group.getChildren()) {
                suite.addChild(createDescription(child, testClass));
            }
            return suite;
        }
    }

}
